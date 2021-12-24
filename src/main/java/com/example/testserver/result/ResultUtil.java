package com.example.testserver.result;

public class ResultUtil {

    public String code;

    public String msg;

    public Object data;

    public static ResultUtil success(Object data) {
        return resultData(CodeEnum.SUCCESS.val(), CodeEnum.SUCCESS.msg(), data);
    }

    public static ResultUtil success(Object data, String msg) {
        return resultData(CodeEnum.SUCCESS.val(), msg, data);
    }

    public static ResultUtil fail(String code, String msg) {
        return resultData(code, msg, null);
    }

    public static ResultUtil fail(String code, String msg, Object data) {
        return resultData(code, msg, data);
    }

    private static ResultUtil resultData(String code, String msg, Object data) {
        ResultUtil resultData = new ResultUtil();
        resultData.setCode(code);
        resultData.setMsg(msg);
        resultData.setData(data);
        return resultData;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
enum CodeEnum {
    /** 成功 */
    SUCCESS("200", "成功"),

    /** 操做失败 */
    ERROR("500", "操作失败");

    CodeEnum(String value, String msg){
        this.val = value;
        this.msg = msg;
    }

    public String val() {
        return val;
    }

    public String msg() {
        return msg;
    }

    private String val;
    private String msg;
}
//public class Result { //should be an enum
//    private int code;
//
//    public Result(int code){
//        this.code=code;
//    }
//
//    public int getCode(){
//        return code;
//    }
//
//    public void setCode(int code){
//        this.code=code;
//    }
//}
