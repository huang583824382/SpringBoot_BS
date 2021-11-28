package com.example.testserver.service;

import com.example.testserver.dao.PublishDAO;
import com.example.testserver.pojo.Publish;
import com.example.testserver.pojo.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublishService {
    @Autowired
    PublishDAO publishDAO;

    public Publish getByTaskid(int taskid){return publishDAO.findByTaskid(taskid);}
    public List<Publish> getByUserid(int userid){return publishDAO.findAllByUserid(userid);}

    public void add(Publish publish){publishDAO.saveAndFlush(publish); }
}
