package com.example.testserver.dao;

import com.example.testserver.pojo.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

//查找任务
public interface TaskDAO extends JpaRepository<Task, Integer> {
    Task findByTaskid(int taskid);
    List<Task> findAllByState(int state);

    @Query(value = "select t.taskid, t.imgsrc, t.abs, t.title, t.state from do d, task t where d.taskid=t.taskid and d.userid=?1 and t.state=?2", nativeQuery = true)
    List<Task> getTaskDoList(int userid, int state);

    @Query(value = "select t.taskid, t.imgsrc, t.abs, t.title, t.state from publish p, task t where p.taskid=t.taskid and p.userid=?1", nativeQuery = true)
    List<Task> getTaskPublishList(int userid);
    //title查找

    @Transactional
    @Modifying
    @Query(value = "UPDATE task SET state = ?2 WHERE taskid = ?1",nativeQuery = true)
    void UpdateTaskState(int taskid, int state);
}
