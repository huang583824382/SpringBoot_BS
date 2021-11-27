package com.example.testserver.service;

import com.example.testserver.dao.UserDAO;
import com.example.testserver.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    public boolean isExist(String name){
        User user = getByUsername(name);
        return null!=user;
    }

    public User getByUsername(String name){
        return userDAO.findByUsername(name);
    }

    public User get(String name, String password){
        System.out.println(name+" "+password);
        return userDAO.getByUsernameAndPassword(name, password);
    }

    public void add(User user){
        userDAO.save(user);
    }
}
