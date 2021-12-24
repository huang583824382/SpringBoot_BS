package com.example.testserver.controller;
import com.alibaba.fastjson.JSON;
import com.example.testserver.controller.TaskController;
import com.alibaba.fastjson.JSONArray;
import com.example.testserver.CookieHandler.CookieHandler;

import com.alibaba.fastjson.JSONObject;
import com.example.testserver.pojo.Task;
import com.example.testserver.service.DoService;
import com.example.testserver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

@CrossOrigin
@RestController
public class LabelController {
    @Autowired
    TaskService taskService;

    @Autowired
    DoService doService;

    @PostMapping("/api/label/loadtask")
    public JSONArray loadtask(@RequestParam String taskid) throws Exception{
        System.out.println("loadtask:"+taskid);
        Task t = taskService.getByTaskid(Integer.parseInt(taskid));
        String res=readjson(t.getImgsrc());
        JSONArray array = JSON.parseArray(res);
        System.out.println(array);
        return array;
    }

    @PostMapping("/api/label/save")
    public void saveLabel(@RequestParam String data, @RequestParam String taskid) throws IOException{

        Task t = taskService.getByTaskid(Integer.parseInt(taskid));
        String path = "D:\\testUpload\\";
        String pathi=path+t.getImgsrc()+"\\";
        System.out.println("recieved "+data);
        savejson(pathi+"label.json", data);
    }
    private List<Task> FormatSrc(List<Task> tmp){ //get the first pic in the dir as the cover
        String path = "D:\\testUpload\\";
        for(int i=0; i<tmp.size(); i++){
            Task t=tmp.get(i);
            String pathi=path+t.getImgsrc()+"\\";
            File file = new File(pathi);
            File[] files = file.listFiles();
            t.setImgsrc(t.getImgsrc()+"/"+files[0].getName());
        }
        return tmp;
    }
//    private List<Task> getUserTaskByState(HttpServletRequest Request, int state){
//        Cookie[] cookies=Request.getCookies();
//        if(cookies==null){
//            System.out.println("nocookie");
//        }
//        else if(cookies.length==2){
//            System.out.println(cookies[0].getName());
//            for(Cookie c:cookies){
//                System.out.println(c.getName());
//                if(c.getName().equals("cookie_userid")){
//                    System.out.println(c.getValue());
//                    FormatSrc(taskService.getTasksByUserid(Integer.parseInt(c.getValue()), state));
//                    return taskService.getTasksByUserid(Integer.parseInt(c.getValue()), state);
//                }
//            }
//        }
//        return null;
//    }
    private String readjson(String tasksrc) {
        String Path = "D:\\testUpload\\";
        BufferedReader reader = null;
        String laststr = "";
        File file = new File(Path+tasksrc+"\\"+"label.json");
        if(!file.exists()){
            try {
                file.createNewFile();
                //遍历
                File f=new File(Path+tasksrc);
                File[] fs = f.listFiles();
                JSONArray init = new JSONArray();
                for(File item : fs){
                    String extension = "";
                    int i = item.getName().lastIndexOf('.');
                    if (i > 0) {
                        extension = item.getName().substring(i+1);
                    }
                    if(extension.equals("jpg")||extension.equals("png")||extension.equals("bmp")||extension.equals("jpeg")){
                        JSONObject jobj = new JSONObject();
                        jobj.put("polygon", new JSONArray());
                        jobj.put("circle", new JSONArray());
                        jobj.put("rectangular", new JSONArray());
                        jobj.put("url", "https://huangzhiwei-1304995366.cos-website.ap-nanjing.myqcloud.com/"+tasksrc+"/"+item.getName());
                        init.add(jobj);
                    }
                }
//                System.out.println("init: "+init.toString());
                savejson(Path+tasksrc+"\\"+"label.json", init.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(Path+tasksrc+"\\"+"label.json");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    public static void savejson(String fileName,String data) {
        BufferedWriter writer = null;
        File file = new File(fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
