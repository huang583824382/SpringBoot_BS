package com.example.testserver.dao;

import com.example.testserver.pojo.Do;
import com.example.testserver.pojo.Publish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoDAO extends JpaRepository<Do, Integer> {
    Do findByTaskid(int taskid);
    List<Do> findAllByUserid(int userid);
}
