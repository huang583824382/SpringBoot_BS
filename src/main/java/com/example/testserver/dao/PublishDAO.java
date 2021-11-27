package com.example.testserver.dao;

import com.example.testserver.pojo.Publish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PublishDAO extends JpaRepository<Publish, Integer> {
    Publish findByTaskid(int taskid);
    List<Publish> findAllByUserid(int userid);

}
