package com.example.testserver.dao;

import com.example.testserver.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer>{
    User findByUsername(String name);

    User getByUsernameAndPassword(String name, String password);
}
