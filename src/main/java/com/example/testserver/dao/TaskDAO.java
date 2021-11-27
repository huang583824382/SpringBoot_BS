package com.example.testserver.dao;

import com.example.testserver.pojo.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//查找任务
public interface TaskDAO extends JpaRepository<Task, Integer> {
    Task findByTaskid(int taskid);
    List<Task> findAllByState(int state);
    //title查找
}
