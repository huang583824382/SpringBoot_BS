package com.example.testserver.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "publish")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Publish {

    @Column(name = "userid")
    int userid;
    @Id
    @Column(name = "taskid")
    int taskid;
    @Column(name = "date")
    Date date;

    public int getTaskid() {
        return taskid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }
}
