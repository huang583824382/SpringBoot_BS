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
        return taskService.list(); //返回state排序的所有task
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

    //    @PostMapping("/api/addtask")
//    public void addtask(@RequestBody body)
    @RequestMapping(value = "/api/addtask", method = {RequestMethod.POST})
    public ResultUtil upload(@RequestParam(required = false) String title, @RequestParam(required = false) String abs, @RequestParam(required = false) MultipartFile file) {
        System.out.println(">>" + title + " " + abs);
        if (file.isEmpty()) {
            System.out.println("文件为空");
        }
        String path = "D:\\testUpload\\";
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("创建" + f.mkdirs());

        }
        //获取上传文件的后缀名
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
        // 获取上传文件的名称
        //String filename = upload.getOriginalFilename();
        // 把文件的名称设置唯一值，uuid
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String filename = uuid + fileType;

        try {
            file.transferTo(new File(path, filename));
//            System.out.println("upload dir = " + dir);
//            String videoUrl = uploadFile(file, dir);
//            System.out.println("upload end = " + System.currentTimeMillis());

            return ResultUtil.success("上传成功", path + filename);
        } catch (Exception var3) {
            return ResultUtil.fail("400", var3.getMessage());
        }
    }
}
