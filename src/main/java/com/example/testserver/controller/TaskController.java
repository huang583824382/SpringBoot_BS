package com.example.testserver.controller;

import com.example.testserver.ZipUtils.ZipUtils;
import com.example.testserver.buildDataset.COCOBuilder;
import com.example.testserver.buildDataset.PVOCBuilder;
import com.example.testserver.dao.DoDAO;
import com.example.testserver.pojo.Do;
import com.example.testserver.pojo.Publish;
import com.example.testserver.pojo.Task;
import com.example.testserver.result.Result;
import com.example.testserver.result.ResultUtil;
import com.example.testserver.service.DoService;
import com.example.testserver.service.PublishService;
import com.example.testserver.service.TaskService;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class TaskController {
    @Autowired
    TaskService taskService;
    @Autowired
    PublishService publishService;
    @Autowired
    DoService doService;

//    @GetMapping("/api/tasks/{taskid}")
//    public Publish findTaskByTaskid(@PathVariable("taskid") int taskid) throws Exception {
//        return publishService.getByTaskid(taskid);
//    }
//
//    @GetMapping("/api/user/tasks/{userid}")
//    public List<Publish> findTaskByUserid(@PathVariable("userid") int userid) throws Exception {
//        return publishService.getByUserid(userid);
//    }

    @GetMapping("/api/alltasks")
    public List<Task> list(HttpServletRequest Request) throws Exception {
//        Cookie[] cookies=Request.getCookies();
//        System.out.println(cookies.length+" "+cookies[1].getValue());
        FormatSrc(taskService.list());
        return taskService.list();
    }

    private List<Task> FormatSrc(List<Task> tmp){ //get the first pic in the dir as the cover
//        String path = "D:\\testUpload\\";
//        for(int i=0; i<tmp.size(); i++){
//            Task t=tmp.get(i);
//            String pathi=path+t.getImgsrc()+"\\";
//            File file = new File(pathi);
//            File[] files = file.listFiles();
//            t.setImgsrc(t.getImgsrc()+"/"+files[0].getName());
//        }
        String path = "D:\\testUpload\\";
        for(int i=0; i<tmp.size(); i++){
            Task t=tmp.get(i);
            String pathi=path+t.getImgsrc()+"\\";
            File file = new File(pathi);
            File[] files = file.listFiles();
            t.setImgsrc("https://huangzhiwei-1304995366.cos-website.ap-nanjing.myqcloud.com/"+t.getImgsrc()+"/"+files[0].getName());
        }
        return tmp;
    }

    @GetMapping("/api/notclaimedtasks")
    public List<Task> notclaimedtasks(HttpServletRequest Request) throws Exception {
        return FormatSrc(taskService.getAllBystate(0));
    }

    @GetMapping("/api/owntasks")
    public List<Task> owntasks(HttpServletRequest Request) throws Exception {
        return getUserTask(Request);
    }

    @GetMapping("/api/reviewingtasks")
    public List<Task> reviewingtasks(HttpServletRequest Request) throws Exception {
        return getUserTaskByState(Request, 2);
    }

    @GetMapping("/api/unchecktasks")
    public List<Task> unchecktasks(HttpServletRequest Request) throws Exception {
        FormatSrc(taskService.getAllBystate(2));
        return taskService.getAllBystate(2);
    }

    @GetMapping("/api/donetasks")
    public List<Task> donetasks(HttpServletRequest Request) throws Exception {
        return getUserTaskByState(Request, 3);
    }

    @GetMapping("/api/waitingtasks")
    public List<Task> waitingtasks(HttpServletRequest Request) throws Exception {
        return getUserTaskByState(Request, 1);
    }

    @PostMapping("/api/submit")
    public ResultUtil submitTask(HttpServletRequest Request,@RequestParam String taskid){
        int userid = getcookie_userid(Request);
        taskService.updateTaskState(Integer.parseInt(taskid), 2); //state = 1, claimed task
        System.out.println("submit"+taskid+" by "+userid);
        return ResultUtil.success("提交成功");
    }

    @PostMapping("/api/reviewdone")
    public ResultUtil reviewDoneTask(HttpServletRequest Request,@RequestParam String taskid){
        int userid = getcookie_userid(Request);
        taskService.updateTaskState(Integer.parseInt(taskid), 3); //state = 1, claimed task
        System.out.println("reviewed "+taskid+" by "+userid);
        return ResultUtil.success("复核成功");
    }
    @PostMapping("/api/exportdata")
    public void exportdata(HttpServletRequest Request, HttpServletResponse response, @RequestParam String taskid, @RequestParam String type) throws IOException {
        Task t = taskService.getByTaskid(Integer.parseInt(taskid));
        String folderpath = "D:\\testUpload\\"+t.getImgsrc()+"\\";
        if(type.equals("0")){
            PVOCBuilder.createPVOC(folderpath); //生成数据集
            File zip = new File(folderpath+"PVOC.zip");
            if(zip.exists()){
                zip.delete();
            }
            FileOutputStream fos2 = new FileOutputStream(new File(folderpath+"PVOC.zip"));
            ZipUtils.toZip(folderpath+"VOC2007\\", fos2, true);
            downFile(Request, response, "PVOC.zip", zip);
            System.out.println("Pascal VOC");
        }
        else if(type.equals("1")){
            COCOBuilder.createCOCO(folderpath);
            //createCOCO(taskid);
            File zip = new File(folderpath+"COCO.zip");
            if(zip.exists()){
                zip.delete();
            }
            FileOutputStream fos2 = new FileOutputStream(new File(folderpath+"COCO.zip"));
            ZipUtils.toZip(folderpath+"COCO\\", fos2, true);
            downFile(Request, response, "COCO.zip", zip);
            System.out.println("COCO");
        }
//        return ResultUtil.success("");
    }

    public static void downFile(HttpServletRequest request, HttpServletResponse response, String filename, File file) throws IOException {
        //  文件存在才下载
        if (file.exists()) {
            OutputStream out = null;
            FileInputStream in = null;
            try {
                // 1.读取要下载的内容
                in = new FileInputStream(file);

                // 2. 告诉浏览器下载的方式以及一些设置
                // 解决文件名乱码问题，获取浏览器类型，转换对应文件名编码格式，IE要求文件名必须是utf-8, firefo要求是iso-8859-1编码
                String agent = request.getHeader("user-agent");
                if (agent.contains("FireFox")) {
                    filename = new String(filename.getBytes("UTF-8"), "iso-8859-1");
                } else {
                    filename = URLEncoder.encode(filename, "UTF-8");
                }
                // 设置下载文件的mineType，告诉浏览器下载文件类型
                String mineType = request.getServletContext().getMimeType(filename);
                response.setContentType(mineType);
                // 设置一个响应头，无论是否被浏览器解析，都下载
                response.setHeader("Content-disposition", "attachment; filename=" + filename);
                // 将要下载的文件内容通过输出流写到浏览器
                out = response.getOutputStream();
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
        }
    }

    private List<Task> getUserTask(HttpServletRequest Request){
        Cookie[] cookies=Request.getCookies();
        if(cookies==null){
            System.out.println("nocookie");
        }
        else{
            int userid;
            if((userid=getcookie_userid(Request))>=0) {
                FormatSrc(taskService.getTasksByUserid(userid));
                return taskService.getTasksByUserid(userid);
            }
        }
        return null;
    }

    private List<Task> getUserTaskByState(HttpServletRequest Request, int state){
        Cookie[] cookies=Request.getCookies();
        if(cookies==null){
            System.out.println("nocookie");
        }
        else {
            int userid;
            if((userid=getcookie_userid(Request))>=0) {
                FormatSrc(taskService.getTasksByUserid(userid, state));
                return taskService.getTasksByUserid(userid, state);
            }
        }
        return null;
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
    @PostMapping(value = "/api/taskdetail")
    @ResponseBody
    public List<String> getAllImgTask(@RequestParam String taskid){
        System.out.println("taskdetail of "+taskid);
        List<String> imgs = new LinkedList<>();
        Task t=taskService.getByTaskid(Integer.parseInt(taskid));
        String path = "D:\\testUpload\\";
        String pathi=path+t.getImgsrc()+"\\";
        File file = new File(pathi);
        File[] files = file.listFiles();
        for(File f : files){
            String name = f.getName();
            System.out.println("name "+name);
            String[] temp = name.split("\\.");
            if(f.isDirectory()) continue;
            String extension = temp[1];
            if(extension.equals("jpg")||extension.equals("png")||extension.equals("bmp")||extension.equals("jpeg"))
                imgs.add("https://huangzhiwei-1304995366.cos-website.ap-nanjing.myqcloud.com/"+t.getImgsrc()+"/"+f.getName());
        }
        return imgs;
    }

    @GetMapping("/api/doingtasks")
    public List<Task> doingtasks(HttpServletRequest Request) throws Exception {
        Cookie[] cookies=Request.getCookies();
        if(cookies==null){
            System.out.println("nocookie");
        }
        else{
            System.out.println(cookies[0].getValue());
        }
        FormatSrc(taskService.getAllBystate(1));
        return taskService.getAllBystate(1);
    }

    @PostMapping(value = "/api/claimtask")
    @ResponseBody
    public ResultUtil claim(HttpServletRequest Request, @RequestParam String taskid){
        int userid = getcookie_userid(Request);
        doService.add(new Do(userid, Integer.parseInt(taskid)));
        taskService.updateTaskState(Integer.parseInt(taskid), 1); //state = 1, claimed task
        System.out.println("claim"+taskid+" from "+userid);
        return ResultUtil.success("领取成功");
    }

    @PostMapping(value = "/api/checkpass")
    @ResponseBody
    public ResultUtil checkpass(HttpServletRequest Request, @RequestParam String taskid){
        int userid = getcookie_userid(Request);
        taskService.updateTaskState(Integer.parseInt(taskid), 3); //state = 3, task done
        System.out.println("check pass "+taskid+" by "+userid);
        return ResultUtil.success("复核成功");
    }

    @PostMapping(value = "/api/checkfail")
    @ResponseBody
    public ResultUtil checkfail(HttpServletRequest Request, @RequestParam String taskid){
        int userid = getcookie_userid(Request);
        taskService.updateTaskState(Integer.parseInt(taskid), -1); //state = -1, task redo
        System.out.println("check pass "+taskid+" by "+userid);
        return ResultUtil.success("返工成功");
    }

    @GetMapping("/api/testCOSupload")
    public ResultUtil COSUpload(HttpResponse response) throws Exception {
        File f = new File("D:\\Unicorn\\DESKTOP\\Personal Files\\05f6e79668a759abd6d41b0d41cbe97.jpg");
        System.out.println("COS:"+f.getPath());
        COSController.upload("testfolder1/test.jpg", f.getPath());
        System.out.println("COS:finish");
        return ResultUtil.success("上传成功");
    }

    @GetMapping("/api/testCOSget")
    public List<String> COSGet(@RequestParam String taskid){
        Task t=taskService.getByTaskid(Integer.parseInt(taskid));
        System.out.println("COSGet:"+t.getImgsrc()+"/");
        return COSController.getObjlist(t.getImgsrc()+"/");
    }

    @RequestMapping(value = "/api/addtask", method = {RequestMethod.POST})
    public ResultUtil upload(@RequestParam(required = false) String title, @RequestParam(required = false) String abs, @RequestParam(required = false) MultipartFile[] files, HttpServletRequest Request) {

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String path = "D:\\testUpload\\"+uuid+"\\"; //设置项目文件夹
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("创建" + uuid);
            f.mkdir();
        }
        try{
            for(int i=0; i<files.length; i++){
                MultipartFile file=files[i];
//                file.getInputStream();

                //获取上传文件的后缀名
                String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
                // 获取上传文件的名称
//            String filename = upload.getOriginalFilename();
                // 把文件的名称设置唯一值，uuid
                String filename = i + fileType;
                //上传对象存储
                COSController.upload(uuid+"/"+filename, file.getInputStream());
                file.transferTo(new File(path, filename));
            }
            int taskid = taskService.add(new Task(uuid, abs, title, 0));
            publishService.add(new Publish(getcookie_userid(Request), taskid));
//            publishService.add(new Publish());
            return ResultUtil.success("上传成功", uuid);
        }catch (Exception var3) {
            var3.printStackTrace();
            return ResultUtil.fail("400", var3.getMessage());
        }
    }
}
