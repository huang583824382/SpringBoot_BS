package com.example.testserver.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name="task")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskid")
    int taskid;
    @Column(name = "imgsrc")
    String imgsrc;
    @Column(name = "abs")
    String abs;
    @Column(name = "title")
    String title;
    @Column(name = "state")
    Integer state; //0 waiting, 1 doing, 2 reviewing, 3 done
    public Task(){};
    public Task(String imgsrc, String abs, String title, int state){
        this.imgsrc=imgsrc;
        this.abs=abs;
        this.title=title;
        this.state=state;
    }

    public int getTaskid(){
        return taskid;
    }

    public void setTaskid(int id){
        taskid=id;
    }

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
