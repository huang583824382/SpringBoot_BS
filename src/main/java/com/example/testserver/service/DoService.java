package com.example.testserver.service;

import com.example.testserver.dao.DoDAO;
import com.example.testserver.dao.PublishDAO;
import com.example.testserver.pojo.Do;
import com.example.testserver.pojo.Publish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoService {
    @Autowired
    DoDAO doDAO;

    public Do getByTaskid(int taskid){return doDAO.findByTaskid(taskid);}
    public List<Do> getByUserid(int userid){return doDAO.findAllByUserid(userid);}

    public void add(Do d){doDAO.saveAndFlush(d); }

}
