package com.xxl.service;

import com.xxl.entity.po.Role;
import com.xxl.entity.po.User;
import com.xxl.entity.vo.AddUserVO;
import com.xxl.entity.vo.ModifyUserVO;
import com.xxl.exception.UserNotFoundException;

import java.util.List;
import java.util.Set;

public interface UsersService {
    /**
     * To find a user instance by username.
     * @param username username
     * @return a user instance by username.
     * @throws @UserNotFoundException if there is no
     * {@link User} named ${username}.
     */
    User getUserByUsername(String username);

    /**
     * Get User List.
     *
     * @param offset      offset
     * @param limit       limit
     * @param username    username
     * @param order       order
     * @param sort        sort
     * @param displayName displayName
     * @param role        role
     * @param email       email
     * @return user list.
     */
    Set<User> getUsersList(Integer offset, Integer limit, String username, String order,
                           String sort, String displayName, String role, String email);

    /**
     * To return the count number of result of ConditionSearch.The count number will return to front end.
     * @param username ConditionSearch username.
     * @param displayName ConditionSearch displayName.
     * @param role ConditionSearch role.
     * @param email ConditionSearch email.
     * @return count number of result of ConditionSearch.
     */
    Long count(String username, String displayName, String role, String email);

    /**
     * To add a new User and it's authority relationship by JPA default method.
     *
     */
    void addUser(AddUserVO addUserVO);


    /**
     * To get a user instance by userId.JPA default method.
     * @param userId userId.
     * @return user instance by userId.
     * @throws @UserNotFoundException if there is no
     * {@link User} id is ${userId}.
     */
    User getUserByUserId(Long userId);

    /**
     * To judge a user if it is removable.when the user has role of ADMIN,it is not removable.
     * @param userRoles the role list of the user.
     * @return if it is removable,return true.
     */
    Boolean isUserRemovable(List<Role> userRoles);

    /**
     * To modify a user.you must provide the userId you want to modify and user information including
     * it's authority relationship.
     * @param userId the userId you want to modify.if the userId is not exist,then will return ERROR.
     * @throws UserNotFoundException if there is no
     * {@link User} id is ${userId}.

     */
    void modifySpecifiedUser(Long userId, ModifyUserVO modifyUserVO);

    /**
     * To remove a user instance by userId.use JPA default method.
     * @param userId the userId you want to delete.
     * @throws UserNotFoundException if there is no
     * {@link User} id is ${userId}.
     */
    void removeUserByUserId(Long userId);

    /**
     * To change a user's password .you must provide the userId and new password.
     * @param userId the userId you want to change it's password.
     * @param password new password,it will be encoded.
     * @throws UserNotFoundException if there is no
     * {@link User} id is ${userId}.
     */
    void resetPassword(Long userId, String password);

    /**
     * get all Role according to ASC order of 'sequence'.use JPA default method.
     * @return return all Role after sorted according ASC of'sequence'.
     */
    Set<Role> getAuthorityType();
}
