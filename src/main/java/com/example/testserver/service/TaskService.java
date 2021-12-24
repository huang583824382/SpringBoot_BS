package com.example.testserver.service;

import com.alibaba.fastjson.JSONObject;
import com.example.testserver.dao.DoDAO;
import com.example.testserver.dao.PublishDAO;
import com.example.testserver.dao.TaskDAO;
import com.example.testserver.pojo.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskDAO taskDAO;
    @Autowired
    PublishDAO publishDAO;
    @Autowired
    DoDAO doDAO;

//    public List<Task> getTasksByUserid(int userid, int state){
//        List<Do> mydo = doDAO.findAllByUserid(userid);
//        List<Task> mytask = new LinkedList<>();
//
//    }

    public List<Task> getTasksByUserid(int userid, int state){
        return taskDAO.getTaskDoList(userid, state);
    }

    public List<Task> getTasksByUserid(int userid){
        return taskDAO.getTaskPublishList(userid);
    }

    public List<Task> list(){
        return taskDAO.findAll(Sort.by(Sort.Direction.ASC, "state"));
    }

    public Task getByTaskid(int taskid){return taskDAO.findByTaskid(taskid); }

    public List<Task> getAllBystate(int state){ return taskDAO.findAllByState(state); }

    public int add(Task task){return taskDAO.saveAndFlush(task).getTaskid();}

    public void updateTaskState(int taskid, int state){
        taskDAO.UpdateTaskState(taskid, state);
    }

//    public List<JSONObject> getTasksWithDo(){
//
//    }
}
