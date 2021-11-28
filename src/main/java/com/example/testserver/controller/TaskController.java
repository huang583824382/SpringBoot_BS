package com.example.testserver.controller;

import com.example.testserver.pojo.Publish;
import com.example.testserver.pojo.Task;
import com.example.testserver.result.Result;
import com.example.testserver.result.ResultUtil;
import com.example.testserver.service.PublishService;
import com.example.testserver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class TaskController {
    @Autowired
    TaskService taskService;
    @Autowired
    PublishService publishService;

    @GetMapping("/api/tasks/{taskid}")
    public Publish findTaskByTaskid(@PathVariable("taskid") int taskid) throws Exception {
        return publishService.getByTaskid(taskid);
    }

    @GetMapping("/api/user/tasks/{userid}")
    public List<Publish> findTaskByUserid(@PathVariable("userid") int userid) throws Exception {
        return publishService.getByUserid(userid);
    }

    @GetMapping("/api/alltasks")
    public List<Task> list() throws Exception {
        List<Task> tmp = taskService.list(); //返回state排序的所有task
        System.out.println(tmp.size());
        String path = "D:\\testUpload\\";
        for(int i=0; i<tmp.size(); i++){
            Task t=tmp.get(i);
            String pathi=path+t.getImgsrc()+"\\";
//            System.out.println(pathi);
            File file = new File(pathi);
//            System.out.println(file);
            File[] files = file.listFiles();
            System.out.println(files[0].getName());
//            String fileType = files[0].getName().substring(file.getName().indexOf("."));
//            System.out.println(files.length);
//            String fileType = file.getName().substring(file.getName().indexOf("."));
            t.setImgsrc(t.getImgsrc()+"/"+files[0].getName());
        }
        return taskService.list();
    }

    @GetMapping("/api/waitingtasks")
    public List<Task> waitingtasks() throws Exception {
        return taskService.getAllBystate(0);
    }

    @GetMapping("/api/doingtasks")
    public List<Task> doingtasks() throws Exception {
        return taskService.getAllBystate(1);
    }

    @GetMapping("/api/reviewingtasks")
    public List<Task> reviewingtasks() throws Exception {
        return taskService.getAllBystate(2);
    }

    @GetMapping("/api/donetasks")
    public List<Task> donetasks() throws Exception {
        return taskService.getAllBystate(3);
    }


    @RequestMapping(value = "/api/addtask", method = {RequestMethod.POST})
    public ResultUtil upload(@RequestParam(required = false) String title, @RequestParam(required = false) String abs, @RequestParam(required = false) MultipartFile[] files) {
        System.out.println(">>" + title + " " + abs+" " +files.length);
//        if (files.isEmpty()) {
//            System.out.println("文件为空");
//        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String path = "D:\\testUpload\\"+uuid+"\\"; //设置项目文件夹
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("创建" + f.mkdirs());
        }

        try{
            for(int i=0; i<files.length; i++){
                MultipartFile file=files[i];
                //获取上传文件的后缀名
                String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
                // 获取上传文件的名称
//            String filename = upload.getOriginalFilename();
                // 把文件的名称设置唯一值，uuid
                String filename = i + fileType;
                file.transferTo(new File(path, filename));
            }
            taskService.add(new Task(uuid, abs, title, 0));
            publishService.add(new Publish());
            return ResultUtil.success("上传成功", uuid);
        }catch (Exception var3) {
            return ResultUtil.fail("400", var3.getMessage());
        }




//        try {
//            file.transferTo(new File(path, filename));
//            taskService.add(new Task(filename, abs, title, 0));
//            return ResultUtil.success("上传成功", filename);
//        } catch (Exception var3) {
//            return ResultUtil.fail("400", var3.getMessage());
//        }
//        return ResultUtil.success("上传成功", "filename");
    }
}
