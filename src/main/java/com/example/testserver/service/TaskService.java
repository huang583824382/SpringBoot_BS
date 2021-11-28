package com.example.testserver.service;

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

    public List<Task> list(){
        return taskDAO.findAll(Sort.by(Sort.Direction.ASC, "state"));
    }

    public Task getByTaskid(int taskid){return taskDAO.findByTaskid(taskid); }

    public List<Task> getAllBystate(int state){ return taskDAO.findAllByState(state); }

    public void add(Task task){taskDAO.saveAndFlush(task); }
}
