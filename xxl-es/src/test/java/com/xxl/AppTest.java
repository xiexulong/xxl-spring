package com.xxl;


import com.xxl.dao.UserRepository;
import com.xxl.entity.User;
import com.xxl.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest 
{
    @Autowired
    private UserService userService;

    @Test
    public void contextLoads() {
        System.out.println(userService.count());
    }

    @Test
    public void testInsert() {
        User user = new User("111","A",1,1);
        userService.save(user);

        User user2 = new User("112","B",1,1);
        userService.save(user2);
    }

    @Test
    public void testDelete() {
        User commodity = new User();
        commodity.setId("111");
        userService.delete(commodity);
    }

    @Test
    public void testGetAll() {
        Iterable<User> iterable = userService.getAll();
        iterable.forEach(e->System.out.println(e.toString()));
    }

    @Test
    public void testGetByName() {
        List<User> list = userService.getByName("B");
        System.out.println(list);
    }

    @Test
    public void testPage() {
        Page<User> page = userService.pageQuery(0, 10, "A");
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getContent());
    }
}
