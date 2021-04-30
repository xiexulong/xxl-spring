package com.xxl.controller;

import com.xxl.entity.AuthenticationInfo;
import com.xxl.entity.po.Authority;
import com.xxl.entity.po.Role;
import com.xxl.entity.po.RolesUrlGroupsMapping;
import com.xxl.entity.po.UrlGroup;
import com.xxl.entity.po.User;
import com.xxl.entity.vo.AddUserVO;
import com.xxl.entity.vo.ModifyUserVO;
import com.xxl.entity.vo.ReturnCodeVO;
import com.xxl.entity.vo.SearchVO;
import com.xxl.entity.vo.UserDetailForDisplayVO;
import com.xxl.entity.vo.UserDetailVO;
import com.xxl.entity.vo.UserVO;
import com.xxl.service.AuthorityService;
import com.xxl.service.RolesUrlGroupsMappingService;
import com.xxl.service.UrlGroupsService;
import com.xxl.service.UsersService;
import com.xxl.util.FileUtil;
import com.xxl.util.ResolveTokenGetAuthInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Api(tags = "Users", value = "User's Rest Controller")
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    private ResolveTokenGetAuthInfoUtil resolveTokenGetAuthInfoUtil;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private RolesUrlGroupsMappingService rolesUrlGroupsMappingService;

    @Autowired
    private UrlGroupsService urlGroupsService;


    /**
     * http://127.0.0.1:9110/AuthCenter/swagger-ui.html
     * http://192.168.0.108:9110/AuthCenter/api/users/hello
     * @return
     */
    @GetMapping("/hello")
    public String getHello() {
        logger.info("call business-a getHello(), response hello xxl");
        return "hello xxl!";
    }


    /**
     * Get current user.
     * @return user.
     */
    @ApiOperation(
            value = "api to get self's information",
            notes = "The api is used to get self's information.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<UserVO> getCurrentLoginUser(HttpServletRequest request) {

        AuthenticationInfo authenticationInfo = resolveTokenGetAuthInfoUtil.resolveTokenGetAuthInfoUtil(request);

        String username = authenticationInfo.username;

        User user = usersService.getUserByUsername(username);

        UserVO usersVO = new UserVO();
        usersVO.setUsername(user.getUsername());

        usersVO.setDisplayName(user.getDisplayName() == null ? username : user.getDisplayName());

        return ResponseEntity.ok(usersVO);
    }

    /**
     * get enabled url group ids.
     * @param userName user name.
     * @return enabled url group ids.
     */
    @ApiOperation(
            value = "api to get enabled URL groups",
            notes = "The api is used to get enabled URL groups for a specified user, that is, "
                    + "the user has specified groups' permissions")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Resource not find, such as the given user is not "
                    + "exist"),
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @GetMapping(value = "/{userName}/enabledUrlGroupIds")
    public ResponseEntity<List<Long>> getEnabledUrlGroupIds(
            @ApiParam(value = "user name")
            @PathVariable String userName) {
        /**
         * Mainly to identify if there is a {@link User} named ${userName}.
         */
        User user = usersService.getUserByUsername(userName);

        List<Authority> authorities = authorityService.findRolesByUsername(user.getUsername());

        List<UrlGroup> urlGroups = urlGroupsService.findAll();

        List<Long> urlGroupIds = new ArrayList<>();
        if (!authorities.isEmpty()) {
            for (Authority authority: authorities) {
                List<RolesUrlGroupsMapping> rolesUrlGroupsMappings
                        = rolesUrlGroupsMappingService.findByRoleId(authority.getRole().getId());

                for (RolesUrlGroupsMapping rolesUrlGroupsMapping: rolesUrlGroupsMappings) {
                    boolean granting = rolesUrlGroupsMapping.getGranting();
                    if (granting) {
                        for (UrlGroup urlGroup: urlGroups) {
                            if (urlGroup.getId().longValue()
                                    == rolesUrlGroupsMapping.getUrlgroupid().longValue()) {
                                urlGroupIds.add(urlGroup.getUid());
                            }
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok(urlGroupIds);
    }

    /**
     * Get User List.
     * @param offset offset
     * @param limit limit
     * @param username username
     * @param order  order
     * @param sort sort
     * @param displayName displayName
     * @param role role
     * @param email email
     * @return  user list.
     */
    @ApiOperation(
            value = "api to get user list",
            notes = "The api is used to all users' by specified key information.")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity<SearchVO> getUserList(
            @ApiParam(value = "the offset from beginning, should with limit param")
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,

            @ApiParam(value = "the size per page")
            @RequestParam(value = "limit", defaultValue = "0") Integer limit,

            @ApiParam(value = "search string")
            @RequestParam(value = "userName", defaultValue = "") String username,

            @ApiParam(value = "sort order", allowableValues = "'asc','desc'")
            @RequestParam(value = "order", defaultValue = "asc") String order,

            @ApiParam(value = "sort by...")
            @RequestParam(value = "sort", defaultValue = "") String sort,

            @ApiParam(value = "display name")
            @RequestParam(value = "displayName", defaultValue = "") String displayName,

            @ApiParam(value = "role name")
            @RequestParam(value = "role", defaultValue = "ALL") String role,

            @ApiParam(value = "email address")
            @RequestParam(value = "email", defaultValue = "") String email) {
        Set<User> users = usersService.getUsersList(offset, limit, username, order, sort, displayName, role, email);
        Long total = usersService.count(username, displayName, role, email);
        List<UserDetailForDisplayVO> rows = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            rows.addAll(convertPoUserToDtoUser(users, order, total, offset));
        }

        SearchVO searchVO = new SearchVO();

        searchVO.setRows(rows);
        searchVO.setTotal(total);
        return ResponseEntity.ok(searchVO);
    }

    /**
     * Add User.
     * @return user map.
     */
    @ApiOperation(
            value = "api to add a user",
            notes = "The api is used to add a new user into the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Input parameters not meet the requirement"),
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<ReturnCodeVO> addUser(@Validated @RequestBody AddUserVO addUserVO) {

        usersService.addUser(addUserVO);

        ReturnCodeVO returnCodeVO = new ReturnCodeVO();
        returnCodeVO.setCode(200);
        returnCodeVO.setData("save user success.");

        //System.out.println(addUserVO.getUsername());
        //FileUtil.saveFile(addUserVO.getUsername(), "/Users/user/xxl/log/test.ini");
        return ResponseEntity.ok(returnCodeVO);
    }

    /**
     * get user info.
     * @param userId user id
     * @return map
     */
    @ApiOperation(
            value = "api to get a user",
            notes = "The api is used to get a user in the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Resource not find, such as the given user id is "
                    + "not exist"),
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @GetMapping(value = "/{user_id}", produces = "application/json")
    public ResponseEntity<UserDetailVO> getUserInfo(
            @ApiParam(value = "user id ")
            @PathVariable("user_id") Long userId) {
        User usr = usersService.getUserByUserId(userId);

        UserDetailVO userDetailVO = new UserDetailVO();

        userDetailVO.setUserId(usr.getId());
        userDetailVO.setUsername(usr.getUsername());
        userDetailVO.setDisplayName(usr.getDisplayName());
        userDetailVO.setEnabled(usr.isEnabled());
        userDetailVO.setEmail(usr.getEmail());
        userDetailVO.setAddedTime(usr.getAddedTime());

        List<String> strList = new ArrayList<>();
        List<Role> authorityTypes = usr.getRoles();
        if (authorityTypes != null) {
            for (Role role : authorityTypes) {
                strList.add(role.getRole());
            }
        }

        userDetailVO.setRoles(strList);

        return ResponseEntity.ok(userDetailVO);
    }

    /**
     * Modify User.
     * @param userId userId
     */
    @ApiOperation(
            value = "api to modify a user",
            notes = "The api is used to modify a user in the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Input parameters not meet the requirement"),
            @ApiResponse(code = 404, message = "Resource not find, such as the given user id is "
                    + "not exist"),
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @PostMapping(value = "/{user_id}", produces = "application/json")
    public void modifyUserInfo(
            @ApiParam(value = "user id ")
            @PathVariable("user_id") Long userId,
            @Validated @RequestBody ModifyUserVO modifyUserVO) {

        logger.info("modifyUserInfo id:" + userId + " userInfo:" + modifyUserVO.toString());
        usersService.modifySpecifiedUser(userId, modifyUserVO);
    }

    /**
     * remove User Info.
     * @param userId userId
     */
    @ApiOperation(
            value = "api to delete a user",
            notes = "The api is used to delete a user in the system.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Resource not find, such as the given user id is "
                    + "not exist"),
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @DeleteMapping(value = "{user_id}", produces = "application/json")
    public void removeUserInfo(
            @ApiParam(value = "user id ")
            @PathVariable("user_id") Long userId) {
        logger.info("removeUserInfo id:" + userId);
        usersService.removeUserByUserId(userId);
    }

    /**
     * Reset Password.
     * @param userId userId
     */
    @ApiOperation(
            value = "api to reset a user's password",
            notes = "The api is used to reset a user's password.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Input parameters not meet the requirement"),
            @ApiResponse(code = 404, message = "Resource not find, such as the given user id is "
                    + "not exist"),
            @ApiResponse(code = 500, message = "Internal server error, such as DB error or "
                    + "service crashed etc")
    })
    @PostMapping(value = "{user_id}/password", produces = "application/json")
    public void resetPassword(
            @ApiParam(value = "user id")
            @PathVariable("user_id") Long userId,
            @ApiParam(value = "password")
            @RequestParam String password) {
        logger.info("resetPassword userId:" + userId);
        usersService.resetPassword(userId, password);
    }

    private List<UserDetailForDisplayVO> convertPoUserToDtoUser(Set<User> users, String order,
                                                                Long total, Integer offset) {

        List<UserDetailForDisplayVO> dtoUsers = new ArrayList<>();

        int index = 0;
        for (User user : users) {
            String username = user.getUsername();

            UserDetailForDisplayVO userDetailForDisplayVO = new UserDetailForDisplayVO();
            userDetailForDisplayVO.setUsername(username);
            userDetailForDisplayVO.setDisplayName(user.getDisplayName());
            userDetailForDisplayVO.setEmail(user.getEmail());
            userDetailForDisplayVO.setUserId(user.getId());
            userDetailForDisplayVO.setEnabled(user.getEnabled());

            StringBuilder buildRoles = new StringBuilder();
            List<Role> roles = user.getRoles();

            for (Role role : roles) {
                buildRoles.append(role.getRole());
                buildRoles.append(" | ");
            }

            String strRoles = "";
            if (!TextUtils.isEmpty(buildRoles)) {
                strRoles = buildRoles.substring(0, buildRoles.lastIndexOf("|"));
            }

            userDetailForDisplayVO.setRoles(strRoles);

            dtoUsers.add(userDetailForDisplayVO);
            index++;
        }
        return dtoUsers;
    }
}
