package com.example.testserver.controller;
import com.example.testserver.result.Result;
import com.example.testserver.result.ResultUtil;
import com.example.testserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import com.example.testserver.pojo.User;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @CrossOrigin
    @PostMapping(value = "/api/login")
    @ResponseBody
    public ResultUtil login(@RequestBody User requestUser, HttpSession session) {
        String username = requestUser.getUsername();
        username = HtmlUtils.htmlEscape(username);

        User user = userService.get(username, requestUser.getPassword());
        if (null == user) {
            System.out.println("fail");
            return ResultUtil.fail("400", "test");
        } else {
            System.out.println("success");
            session.setAttribute("user", user);
            return ResultUtil.success("success", "test");
        }
    }

    @CrossOrigin
    @PostMapping(value = "/api/register")
    @ResponseBody
    public ResultUtil register(@RequestBody User requestUser) {

        int res = userService.isConflict(requestUser);

        if (res==1) {
            //无重复可以注册
            System.out.println("注册成功");
            userService.add(new User(requestUser.getUsername(), requestUser.getPassword(), requestUser.getEmail(), 0));
            return ResultUtil.success("success", "注册成功");
        } else if(res==-1){
            System.out.println("重复邮箱："+requestUser.getEmail());
            return ResultUtil.fail("400", "重复邮箱");
        }
        else{
            System.out.println("重复名称："+requestUser.getUsername());
            return ResultUtil.fail("400", "重复名称");
        }
    }
}