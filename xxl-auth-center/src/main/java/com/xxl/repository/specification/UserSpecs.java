package com.xxl.repository.specification;

import com.xxl.entity.po.Role;
import com.xxl.entity.po.User;
import org.apache.http.util.TextUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


public class UserSpecs {

    /**
     * condition search.
     * @param username username
     * @param displayName display name
     * @param role role
     * @param email email
     * @return specification
     */
    public static Specification<User> conditionSearch(final String username, final String displayName,
                                                      final String role, final String email) {
        return (root, query, cb) -> {
            List<Predicate> predicate = new ArrayList<>();
            if (!TextUtils.isEmpty(username)) {
                predicate.add(cb.like(root.get("username").as(String.class), "%" + username + "%"));
            }
            if (!TextUtils.isEmpty(displayName)) {
                predicate.add(cb.like(root.get("displayName").as(String.class), "%" + displayName + "%"));
            }
            if (!TextUtils.isEmpty(role) && !role.equals("ALL")) {
                ListJoin<User, Role> authJoin = root.join(
                        root.getModel().getList("roles", Role.class), JoinType.LEFT);
                predicate.add(cb.equal(authJoin.get("role").as(String.class), role));
            }
            if (!TextUtils.isEmpty(email)) {
                predicate.add(cb.like(root.get("email").as(String.class), "%" + email + "%"));
            }
            //predicate.add(cb.notEqual(root.get("username").as(String.class), "admin"));
            Predicate[] pre = new Predicate[predicate.size()];
            return cb.and(predicate.toArray(pre));
        };
    }
}
