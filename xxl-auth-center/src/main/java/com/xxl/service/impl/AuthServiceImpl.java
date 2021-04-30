package com.xxl.service.impl;

import com.xxl.entity.AuthenticationInfo;
import com.xxl.entity.CustomConfigAttribute;
import com.xxl.entity.po.Role;
import com.xxl.entity.po.RolesUrlGroupsMapping;
import com.xxl.entity.po.Url;
import com.xxl.entity.po.UrlGroup;
import com.xxl.exception.HttpPermissionDenyException;
import com.xxl.exception.HttpUnauthorizedException;
import com.xxl.repository.AuthorityTypeRepository;
import com.xxl.service.AuthService;
import com.xxl.service.AuthorityService;
import com.xxl.service.RolesService;
import com.xxl.service.RolesUrlGroupsMappingService;
import com.xxl.util.ResolveTokenGetAuthInfoUtil;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final String UNLIMITED = "Unlimited";
    private static final String SUPER_USER = "admin";

    private volatile Map<Url, Collection<CustomConfigAttribute>> resourceMap = null;


    @Value("${auth.jwt.header.name}")
    private String headerName;

    @Value("${auth.jwt.header.prefix}")
    private String headerPrefix;

    @Value("${auth.jwt.cert.public}")
    private String publicKeyPath;

    @Value("${auth.jwt.algorithm.type}")
    private String algorithmType;

    @Value("${auth.jwt.cookie.name}")
    private String cookieName;

    @Autowired
    private ResolveTokenGetAuthInfoUtil resolveTokenGetAuthInfoUtil;

    @Autowired
    private AuthorityTypeRepository authorityTypeRepository;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RolesUrlGroupsMappingService rolesUrlGroupsMappingService;

    @Autowired
    private RolesService rolesService;

    private static final String REGEX = "(^(?!(/2dimages))(/.*)*(.(wasm|gltf|glb|js|css|png|gif|jpg|jpeg|eot|svg|ttf|woff|woff2|map|ico))(\\?.*)?$"
            + "|/+(visualmap|index|login|header|region)\\.html(\\?.*)?"
            + "|/)";

    //for region test  add "|region" into REGEX.

    private static final String URL_REGEX = "^.*(/Visual2DImageService/images_2d/range/visualization.*)"
            + "|(/debugdb/qualities/DivisionsPaint.*)"
            + "|(/debugdb/get_division_length.*)"
            + "|(/2dimages/.*)"
            + "|(/range/visualization.*)"
            //for region test
            + "|(/RegionManager/.*)"
            //--end for region test
            + "|(/Visual2DImageService/images_2d/evp.*)$";



    /**
     * Itinial.
     */
    @PostConstruct
    public void init() {
        logger.debug("loadResource in Postctruct.");
        loadUrlResources();
    }


    @Override
    public void auth(HttpServletRequest request, String uri, String method) {

        //record whether the request resource is static resource.
        boolean resourceIsStatic = allowAccessIfStaticResouce(uri,method);

        //Allows by returning 200 if the resource is static
        if (resourceIsStatic) {
            return;
        }

        if (allowAccessResouce(uri)) {
            return;
        }

        //Construct an object that contains the
        // requester's token,username, and his identity information
        AuthenticationInfo authenticationInfo = resolveTokenGetAuthInfoUtil.resolveTokenGetAuthInfoUtil(request);


        if (TextUtils.isEmpty(authenticationInfo.username)) {
            logger.error("username is null or empty.");
            throw new HttpUnauthorizedException("username is null or empty");
        }

        if (null == authenticationInfo.roles || authenticationInfo.roles.isEmpty()) {
            logger.info("No roles find for {}", authenticationInfo.username);
            throw new HttpUnauthorizedException("No roles find for " + authenticationInfo.username);
        }

        //Call the method to determine the authority, return the result of the authority judgment
        authenticationFromUrlAndMethod(uri,method,authenticationInfo);

    }


    private boolean allowAccessIfStaticResouce(String uri, String method) {
        if (!TextUtils.isEmpty(uri)
                && Pattern.matches(REGEX, uri)
                && method.equals("GET")) {
            logger.info("URI no need to do authentication, {}", uri);
            return true;
        } else {
            return false;
        }

    }

    private boolean allowAccessResouce(String uri) {

        if (!TextUtils.isEmpty(uri) && Pattern.matches(URL_REGEX, uri)) {
            logger.info("URI no need to do authentication, {}", uri);
            return true;
        } else {
            return false;
        }

    }

    /**
     * This is a method for authenticationFromUrlAndMethod.
     * @param uri uri.
     * @param method method.
     * @param auth auth.
     */
    private void authenticationFromUrlAndMethod(
            String uri, String method, AuthenticationInfo auth) {
        String username = auth.username;

        //If username is "admin" and belongs to the backdoor super user, simply return 200
        if (username.equals(SUPER_USER)) {
            logger.info("SUPERUSER   ");
            return ;
        }

        //Query the list of users for the current user
        List<String> listRole = auth.getRoles();

        //just for debug
        logger.info("Enter authenticationFromUrlAndMethod  method:   " + method + " uri:    " + uri);

        long startTimeRegex2 = System.currentTimeMillis();

        Map<Url, Collection<CustomConfigAttribute>> resourceMapTempInUse = resourceMap;

        for (Url url : resourceMapTempInUse.keySet()) {
            String urlPattern = url.getUrlPattern();
            String httpMethod = url.getHttpMethod();
            Collection<CustomConfigAttribute> matchResourceRoleGrantingList;

            Pattern p = Pattern.compile(urlPattern);

            //The regular match here is a full match
            //Anything that doesn't match will return 403
            if ((httpMethod.equals(method) || httpMethod.equals("ALL")) && p.matcher(uri).matches()) {

                //Gets all permissions for this url,
                // which is used to determine permission return
                matchResourceRoleGrantingList = resourceMapTempInUse.get(url);

                //just for debug
                logger.info("Already match regex urlPattern: {}, method: {}, uri: {}, "
                        + "matchResourceRoleGrantingList: {}",urlPattern , method, uri,
                        matchResourceRoleGrantingList.toString());

                //Traversing the list of roles, returns 200 if any match passes. Returns 403 if none passes
                for (String role : listRole) {
                    for (CustomConfigAttribute customConfigAttribute : matchResourceRoleGrantingList) {
                        if (role.equals(customConfigAttribute.getAttribute())) {
                            if (customConfigAttribute.isGranted()) {
                                logger.info("finally Hit.  role: {}, resource: {}, method: {}", role, uri, method);

                                long endTimeRegex2 = System.currentTimeMillis();
                                logger.info("@@@ Regex2 use time {} ms", (endTimeRegex2 - startTimeRegex2));
                                return;

                            }
                        }
                    }
                }


                logger.info("finally Reject. No roles match Granting resource: {}, method: {}", uri, method);
                throw new HttpPermissionDenyException(
                        "finally Reject. No roles match Granting"
                                + " resource: " + uri + " method: " + method);
            }
        }

        //Anything that doesn't match will return 403
        logger.info("Not match any ");
        throw new HttpPermissionDenyException(
                "Not match any ");
    }




    /**
     * Load the database resources to resourceMap.
     * This method adopts the operation of union,
     * where a url belongs to more than one group.
     * As long as any one of the groups meets the requirement,
     * the url is allowed to pass.
     */
    private void loadUrlResources() {

        List<Role> authorityTypes = authorityTypeRepository.findAll();

        //Deleted users are not loaded
        List<String> roles = authorityTypes.stream().filter(role -> (!role.isRemoved())).map(Role::getRole)
                .collect(Collectors.toList());

        Map<Url, Collection<CustomConfigAttribute>> resourceMapTempInload = new LinkedHashMap<>();


        List<UrlGroup> urlGroups = authorityService.findAllUrlGroups();
        List<Role> allRoles = rolesService.findAll();

        //Loading is handled as a group by group
        for (UrlGroup urlGroup : urlGroups) {

            Collection<CustomConfigAttribute> configAttributes =
                    new ArrayList<>();

            //According to the groupId query the corresponding RolesUrlGroupsMapping list
            List<RolesUrlGroupsMapping> rolesUrlGroupsMapping =
                    rolesUrlGroupsMappingService.findRolesByUrlGroupId(urlGroup.getId());


            //Walk through the RolesUrlGroupsMapping
            // list to query for role information related to the permission relationship group
            for (RolesUrlGroupsMapping i : rolesUrlGroupsMapping) {
                Role role = rolesService.findRoleById(i.getRoleid());

                //Construct configAttributes, giving a role permission or no rights to the group
                if (!role.isRemoved()) {
                    CustomConfigAttribute configAttribute =
                            new CustomConfigAttribute(role.getRole(), i.getGranting());
                    configAttributes.add(configAttribute);
                }
            }


            configAttributes = handleUlimitedGroup(urlGroup,roles,configAttributes);

            if (!configAttributes.isEmpty()) {

                //Gets all urls under the current group
                Set<Url> urls = urlGroup.getUrls();

                //Iterate through each url under the current group, updating resourceMap
                for (Url url : urls) {

                    logger.info("loadUrlResources method:  url {}", url.getUrlPattern());

                    Collection<CustomConfigAttribute> existingConfigAttributes = resourceMapTempInload.get(url);
                    Collection<CustomConfigAttribute> updatedConfigAttributes = new ArrayList<>();


                    /**
                     *iterating through each urlgroup, each time taking the existing resourceMap,
                     * comparing it to the current resourceMap, for any "granting", take the collection,
                     * direct it to resourceMap, and update.
                     */
                    for (Role role : allRoles) {
                        if (!role.isRemoved()) {
                            logger.info("loadUrlResources method:  role {}", role.getRole());
                            boolean flag = false;

                            //Query existing resourceMap, flag set to true if granted with "granting"
                            if (existingConfigAttributes != null) {
                                for (CustomConfigAttribute existingAttribute : existingConfigAttributes) {
                                    if (existingAttribute.getAttribute().equals(role.getRole())) {
                                        if (existingAttribute.isGranted()) {
                                            logger.info("loadUrlResources method: existingConfigAttributes "
                                                    + "has this url,and is Granting", role.getRole());
                                            flag = true;
                                        }
                                        break;
                                    }
                                }
                            }

                            //Query the current resourceMap, flag set to true if there is "granting", again
                            if (!flag) {
                                for (CustomConfigAttribute attribute : configAttributes) {
                                    if (attribute.getAttribute().equals(role.getRole())) {
                                        if (attribute.isGranted()) {
                                            logger.info("loadUrlResources method: "
                                                    + "thisAttributes has this url,and is Granting", role.getRole());
                                            flag = true;
                                        }
                                        break;
                                    }
                                }
                            }

                            //just for debug
                            logger.info("loadUrlResources method, ROLE: {}, FLAG: {} ", role.getRole(), flag);

                            //Update the updatedConfigAttributes
                            CustomConfigAttribute grantingConfigAttribute =
                                    new CustomConfigAttribute(role.getRole(), flag);
                            logger.info("loadUrlResources method:  Now grantingConfigAttribute  is updated {}",
                                    grantingConfigAttribute.toString());
                            updatedConfigAttributes.add(grantingConfigAttribute);

                        }

                        //just for debug
                        logger.info("loadUrlResources method: Now update updatedConfigAttributes");

                        //For the url update resourceMap under the current group

                        //resourceMap.put(url, updatedConfigAttributes);
                        resourceMapTempInload.put(url, updatedConfigAttributes);

                        logger.info("url pattern {}, url method {} ,configAttr {}",
                                url.getUrlPattern(), url.getHttpMethod(), updatedConfigAttributes);
                    }
                }
            }
        }



        //For this group, after all resourceMap updates,just for debug
        for (Map.Entry<Url, Collection<CustomConfigAttribute>> entry : resourceMapTempInload.entrySet()) {
            logger.info("Key = Url: {}, Method : {} ", entry.getKey().getUrlPattern(), entry.getKey().getHttpMethod());
            for (CustomConfigAttribute attribute : entry.getValue()) {
                logger.info("Value =   " + attribute.toString());
            }

        }

        resourceMap = resourceMapTempInload;

    }


    private Collection<CustomConfigAttribute> handleUlimitedGroup(
            UrlGroup urlGroup , List<String> roles,
            Collection<CustomConfigAttribute> configAttributes) {

        if (urlGroup.getName().equals(UNLIMITED)) {
            for (String role : roles) {
                CustomConfigAttribute configAttribute =
                        new CustomConfigAttribute(role, true);
                configAttributes.add(configAttribute);
            }
            return configAttributes;
        }

        return configAttributes;
    }


    /**
     * Reset resource function.*
     * Any changes to the permissions relationship,
     * this method is called, empties the memory resourceMap,
     * and reloads it next time
     */
    @Override
    public void resetResourceMap() {
        logger.info("resetResourceMap");
        loadUrlResources();
    }
}
