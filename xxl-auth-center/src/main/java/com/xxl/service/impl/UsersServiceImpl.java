package com.xxl.service.impl;

import com.xxl.config.Const;
import com.xxl.entity.po.Authority;
import com.xxl.entity.po.Role;
import com.xxl.entity.po.User;
import com.xxl.entity.vo.AddUserVO;
import com.xxl.entity.vo.ModifyUserVO;
import com.xxl.exception.HttpBadRequestException;
import com.xxl.exception.UserNotFoundException;
import com.xxl.repository.AuthorityRepository;
import com.xxl.repository.AuthorityTypeRepository;
import com.xxl.repository.UserRepository;
import com.xxl.repository.specification.UserSpecs;
import com.xxl.service.UsersService;
import com.xxl.util.ViewHelperUtil;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsersServiceImpl implements UsersService {
    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityTypeRepository authorityTypeRepository;
    @Autowired
    private AuthorityRepository authorityRepository;


    /**
     * To find a user instance by username.
     * @param username username The User name.
     * @return Returns an object instance of User.
     */
    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (null == user) {
            throw new UserNotFoundException("user info not find for " + username);
        }
        return user;
    }

    /**
     * Fuzzy queries call this method.
     * @param offset      offset Parameters required for paging.
     * @param limit       limit Parameters required for paging.
     * @param username    username Filter required parameters.
     * @param order       order Filter required parameters.
     * @param sort        sort Parameters required for sorting.
     * @param displayName displayName Filter required parameters.
     * @param role        role Filter required parameters.
     * @param email       email Filter required parameters.
     * @return Returns a collection of User queries.
     */
    @Override
    public Set<User> getUsersList(Integer offset, Integer limit, String username,
                                  String order, String sort, String displayName,
                                  String role, String email) {
        if (limit == 0) {
            return new LinkedHashSet<>(userRepository.findAll(
                    UserSpecs.conditionSearch(username, displayName, role, email),
                    new Sort(new Sort.Order(ViewHelperUtil.getSortDirectionByString(order),
                            getSortByStringForUser(sort)))));
        } else {
            PageRequest pageRequest = new PageRequest(
                    ViewHelperUtil.getPageByOffsetAndLimit(offset, limit), limit,
                    new Sort(new Sort.Order(ViewHelperUtil.getSortDirectionByString(order),
                            getSortByStringForUser(sort))));
            Page<User> pageUser = userRepository.findAll(
                    UserSpecs.conditionSearch(username, displayName, role, email), pageRequest);
            if (pageUser != null) {
                Set<User> result = new LinkedHashSet<>();
                for (User user : pageUser.getContent()) {
                    result.add(user);
                }
                return result;
            }
        }

        return null;
    }

    @Override
    public Long count(String username, String displayName, String role, String email) {
        return userRepository.count(UserSpecs.conditionSearch(username, displayName, role, email));
    }

    private String getSortByStringForUser(String sort) {
        if (TextUtils.isEmpty(sort)) {
            return "id";
        }
        if ("roles".equals(sort)) {
            return "roles.code";
        }
        return sort;
    }


    /**
     *  Add a User to the database.
     * @param addUserVO You need to enter the AddUserVO object.
     */
    @Override
    @Transactional
    public void addUser(AddUserVO addUserVO) {
        User user = createUser(addUserVO);

        userRepository.save(user);

        List<Authority> authorities = createAuthorities(addUserVO, user);
        authorityRepository.save(authorities);
    }


    /**
     * User is created before it is added.
     * @param addUserVO You need to enter the AddUserVO object.
     * @return Returns an object for the created User.
     */
    private User createUser(AddUserVO addUserVO) {

        User user = new User();

        String username = addUserVO.getUsername();
        user.setId(null);
        user.setUsername(username);

        String displayName = addUserVO.getDisplayName();
        user.setDisplayName(displayName);
        user.setEnabled(true);


        String password = addUserVO.getPassword();
        user.setPassword(passwordEncoder.encode(password));

        Object email = addUserVO.getEmail();

        user.setEmail(email == null ? null : (String) email);
        user.setAddedTime(new Date());
        return user;
    }

    /**
     * Bind the added role to its role identity information.
     * @param addUserDTO You must pass an addUserDTO object as an input.
     * @param user User object.
     * @return Returns a list of roles bound to the user.
     */
    private List<Authority> createAuthorities(AddUserVO addUserDTO, User user) {
        List<Authority> authorities = new ArrayList<>();

        List roles = addUserDTO.getRoles();

        for (int i = 0; i < roles.size(); i++) {
            Authority authority = new Authority();
            authority.setAddedTime(new Date());
            authority.setId(null);
            authority.setUser(user);
            Role role = authorityTypeRepository
                    .findByRole((String) roles.get(i));
            authority.setRole(role);
            authorities.add(authority);
        }
        return authorities;
    }

    /**
     *  Get the User object by UserId.
     * @param userId userId.
     * @return User object.
     */
    @Override
    public User getUserByUserId(Long userId) {
        User user = userRepository.findOne(userId);
        if (null == user) {
            throw new UserNotFoundException("user info not find for " + userId);
        }
        return user;
    }

    /**
     * Returns whether the user can be deleted.
     * @param userRoles the role list of the user.
     * @return Whether it can be deleted.
     */
    @Override
    public Boolean isUserRemovable(List<Role> userRoles) {
        for (Role role : userRoles) {
            if (role.getRole().equals(Const.ADMIN_ROLE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Modify a user information.
     * @param userId the userId you want to modify.if the userId is not exist,then will return ERROR.
     * @param modifyUserVO Receive a ModifyUserVO object as the parameter.
     */
    @Override
    @Transactional
    public void modifySpecifiedUser(Long userId, ModifyUserVO modifyUserVO) {

        User usr = userRepository.findOne(userId);
        if (usr == null) {
            logger.error("no this with user id: {}", userId);
            throw new UserNotFoundException("User not exist!");
        }
        String displayName = modifyUserVO.getDisplayName();

        usr.setDisplayName(displayName);

        Object email = modifyUserVO.getEmail();

        usr.setEmail(email == null ? null : (String) email);
        if (modifyUserVO.getEnabled() != null) {
            String enable = modifyUserVO.getEnabled();
            if (enable.equals("1")) {
                usr.setEnabled(true);
            } else {
                usr.setEnabled(false);
            }
        }

        userRepository.save(usr);
        authorityRepository.deleteByUsername(usr.getUsername());

        List<Authority> authorities = new ArrayList<>();

        List<String> strList = modifyUserVO.getRoles();
        for (String str : strList) {
            Authority authority = new Authority();
            authority.setId(null);
            authority.setUser(usr);
            Role role = authorityTypeRepository
                    .findByRole(str);
            authority.setRole(role);
            authorities.add(authority);
        }
        authorityRepository.save(authorities);
    }

    /**
     * Delete a user by UserId.
     * @param userId the userId you want to delete.
     */
    @Override
    @Transactional
    public void removeUserByUserId(Long userId) {
        User user = this.getUserByUserId(userId);
        userRepository.delete(user.getId());
    }

    /**
     * This method is called when the password is changed.
     * @param userId the userId you want to change it's password.
     * @param password new password,it will be encoded.
     */
    @Override
    public void resetPassword(Long userId, String password) {
        if (password == null || password.length() == 0) {
            throw new HttpBadRequestException("password can not be empty!");
        }
        User user = this.getUserByUserId(userId);
        userRepository.resetPassword(user.getId(), passwordEncoder.encode(password));
    }


    @Override
    public Set<Role> getAuthorityType() {
        return new LinkedHashSet<>(authorityTypeRepository
                .findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "sequence"))));
    }
}
