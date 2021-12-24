package com.example.testserver.CookieHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieHandler {
    public int getcookie_userid(HttpServletRequest Request){
        Cookie[] cookies=Request.getCookies();
        if(cookies==null){
            System.out.println("nocookie");
        }
        else {
            for(Cookie c:cookies){
                if(c.getName().equals("cookie_userid")){
                    System.out.println(c.getValue());
                    return Integer.parseInt(c.getValue());
                }
            }
        }
        return -1;
    }
}
