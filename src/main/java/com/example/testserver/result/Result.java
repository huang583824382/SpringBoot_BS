package com.example.testserver.result;

public class Result { //should be an enum
    private int code;

    public Result(int code){
        this.code=code;
    }

    public int getCode(){
        return code;
    }

    public void setCode(int code){
        this.code=code;
    }
}
