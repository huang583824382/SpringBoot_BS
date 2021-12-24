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

    public int isConflict(User user){
        User t1=getByUsername(user.getUsername());
        User t2=getByEmail(user.getEmail());
        if(t2!=null){
            return -1; //邮箱重复
        }
        else{
            if(t1!=null){
                return -2; //用户名重复
            }
            else{
                return 1; //不重复
            }
        }
    }

    public User getByUsername(String name){
        return userDAO.findByUsername(name);
    }
    public User getByEmail(String email){
        return userDAO.findByEmail(email);
    }


    public User get(String name, String password){
        System.out.println(name+" "+password);
        return userDAO.getByUsernameAndPassword(name, password);
    }

    public int add(User user){
        return userDAO.saveAndFlush(user).getUserid();
    }
}
