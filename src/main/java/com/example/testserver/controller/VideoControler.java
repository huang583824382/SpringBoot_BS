package com.example.testserver.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.testserver.pojo.Publish;
import com.example.testserver.pojo.Task;
import com.example.testserver.result.ResultUtil;
import com.example.testserver.service.PublishService;
import com.example.testserver.service.TaskService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;


@Controller
@RestController
public class VideoControler {
    @Autowired
    TaskService taskService;
    @Autowired
    PublishService publishService;
    private String PATH="D:\\testUpload\\tempFile\\";
    private String PATHtask="D:\\testUpload\\";
    @RequestMapping(value = "/api/handlevideo", method = {RequestMethod.POST})
    public ResultUtil handlevideo(@RequestParam(required = false) MultipartFile video, @RequestParam int num){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String path = PATH+uuid+"\\"; //设置项目文件夹
        File f = new File(path);
        if (!f.isDirectory()) {
            f.mkdir();
        }
        try {
            video.transferTo(new File(path, "testvideo.mp4"));
        } catch (IOException e){
            e.printStackTrace();
        }
        JSONArray jarray = new JSONArray();
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(path+"testvideo.mp4");
        try{
            grabber.start();
            System.out.println(grabber.getImageWidth()+" "+grabber.getImageHeight()+" "+grabber.getLengthInVideoFrames()+" "+num);
            Frame frame = null;
            int frames = grabber.getLengthInVideoFrames();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bi;
            for(int n=0; n<num; n++){
                for(int i=0; i<grabber.getLengthInVideoFrames()/num; i++){
                    frame=grabber.grabImage();
                }
                bi = converter.getBufferedImage(frame);
                File output = new File(path,""+n+".jpg");
                ImageIO.write(bi, "jpg", output);
                System.out.println(n);
                JSONObject img = new JSONObject();
                img.put("name", n+".jpg");
                img.put("url", "http://localhost:8443/temp/"+uuid+"/"+n+".jpg");
                jarray.add(img);
            }
            System.out.println("finish");
            grabber.close();
            grabber.stop();
        } catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtil.success(jarray, "hello");

    }

    @RequestMapping(value = "/api/addtaskfromvid", method = {RequestMethod.POST})
    public ResultUtil upload( @RequestParam(required = false) String title, @RequestParam(required = false) String abs, @RequestParam(required = false) String[] urls, HttpServletRequest Request){
//        String[] tmp = urls[0].split("/temp/");
//        System.out.println(tmp[1]);
        String oldpath="";
        String despath="";
        String[] tmp={""};
        String[] info={""};
        FileOutputStream out = null;
        FileInputStream in = null;
        int num=0;
        try {
            for(String item : urls) {
                tmp = item.split("/temp/");
                info = tmp[1].split("/");
                oldpath = PATH + info[0] + "\\";
                despath = PATHtask + info[0]+"\\";
                File p = new File(despath);
                p.mkdir();
                in = new FileInputStream(oldpath+info[1]);
                out = new FileOutputStream(new File(despath+num+".jpg"));
                IOUtils.copy(in, out);
                COSController.upload(info[0]+"/"+num+".jpg", new FileInputStream(new File(despath+num+".jpg")));
                num++;
                in.close();
                out.close();
            }
        } catch (IOException e){
            e.printStackTrace();
            return ResultUtil.fail("500", "文件读写失败");
        }

//        System.out.println(oldpath+"   "+despath);
        File de2 = new File(oldpath);
        deleteDir(de2);
        int taskid = taskService.add(new Task(info[0], abs, title, 0));
        publishService.add(new Publish(getcookie_userid(Request), taskid));

        return ResultUtil.success("上传成功","test");
    }


    private int getcookie_userid(HttpServletRequest Request){
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
    public static boolean deleteDir(File dir) {
        // 如果是文件夹
        if (dir.isDirectory()) {
            // 则读出该文件夹下的的所有文件
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                // File f=new File（String parent ，String child）
                // parent抽象路径名用于表示目录，child 路径名字符串用于表示目录或文件。
                // 连起来刚好是文件路径
                boolean isDelete = deleteDir(new File(dir, children[i]));
                // 如果删完了，没东西删，isDelete==false的时候，则跳出此时递归
                if (!isDelete) {
                    return false;
                }
            }
        }
        // 读到的是一个文件或者是一个空目录，则可以直接删除
        return dir.delete();
    }
//    public static void copyFolder(String oldPath, String newPath) {
//        try {
//            // 如果文件夹不存在，则建立新文件夹
//            (new File(newPath)).mkdirs();
//            // 读取整个文件夹的内容到file字符串数组，下面设置一个游标i，不停地向下移开始读这个数组
//            File filelist = new File(oldPath);
//            String[] file = filelist.list();
//            // 要注意，这个temp仅仅是一个临时文件指针
//            // 整个程序并没有创建临时文件
//            File temp = null;
//            for (int i = 0; i < file.length; i++) {
//                // 如果oldPath以路径分隔符/或者\结尾，那么则oldPath/文件名就可以了
//                // 否则要自己oldPath后面补个路径分隔符再加文件名
//                // 谁知道你传递过来的参数是f:/a还是f:/a/啊？
//                if (oldPath.endsWith(File.separator)) {
//                    temp = new File(oldPath + file[i]);
//                } else {
//                    temp = new File(oldPath + File.separator + file[i]);
//                }
//
//                // 如果游标遇到文件
//                if (temp.isFile()) {
//                    FileInputStream input = new FileInputStream(temp);
//                    // 复制并且改名
//                    FileOutputStream output = new FileOutputStream(newPath
//                            + "/" + (temp.getName()).toString());
//                    byte[] bufferarray = new byte[1024 * 64];
//                    int prereadlength;
//                    while ((prereadlength = input.read(bufferarray)) != -1) {
//                        output.write(bufferarray, 0, prereadlength);
//                    }
//                    output.flush();
//                    output.close();
//                    input.close();
//                }
//                // 如果游标遇到文件夹
//                if (temp.isDirectory()) {
//                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("复制整个文件夹内容操作出错");
//        }
//    }

}
