package com.example.testserver.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "do")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Do {
    @Column(name="userid")
    int userid;
    @Id
    @Column(name = "taskid")
    int taskid;
    @Column
    Date date;
    public Do(){}
    public Do(int userid, int taskid){
        this.userid=userid;
        this.taskid=taskid;
        date=new Date();
    }


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
