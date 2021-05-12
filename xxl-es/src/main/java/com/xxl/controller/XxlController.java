package com.xxl.controller;

import com.xxl.entity.User;
import com.xxl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class XxlController {

    @Autowired
    private UserService userService;

    /**
     * 查询一个： http://127.0.0.1:9200/test/user/30
     * 查询all： http://127.0.0.1:9200/test/user/_serrch
     * @param user
     * @return
     */
    @RequestMapping("/addUser")
    public User addUser(@RequestBody User user) {
        return userService.save(user);
    }

    @RequestMapping("/findUser")
    public List<User> findUser(String name) {
        return userService.getByName(name);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<User> getAll() {
        return userService.getAll();
    }

    /*@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBook(@PathVariable("id") String id) {
        userService.delete(userService.getByName());
        return new ResponseEntity<>("delete execute!", HttpStatus.OK);
    }*/
}
