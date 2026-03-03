package com.tmyx.backend.entity;

import java.util.Date;

public class Login {
    private Integer id;
    private Integer attempt;
    private Date time;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getAttempt() { return attempt; }

    public void setAttempt(Integer attempt) { this.attempt = attempt; }

    public Date getTime() { return time; }

    public void setTime(Date time) { this.time = time; }

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", attempt=" + attempt +
                ", time=" + time +
                '}';
    }
}
