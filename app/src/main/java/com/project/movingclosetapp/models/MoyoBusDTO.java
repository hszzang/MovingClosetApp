package com.project.movingclosetapp.models;

import java.io.Serializable;

public class MoyoBusDTO implements Serializable {

    private String m_idx;
    private String busid;
    private String buspass;
    private String mb_lat;
    private String mb_lon;
    private String mb_addr;
    private String mb_num;
    private String mb_status;
    private String mb_lastupdate;

    public String getM_idx() {
        return m_idx;
    }

    public void setM_idx(String m_idx) {
        this.m_idx = m_idx;
    }

    public String getBusid() {
        return busid;
    }

    public void setBusid(String busid) {
        this.busid = busid;
    }

    public String getBuspass() {
        return buspass;
    }

    public void setBuspass(String buspass) {
        this.buspass = buspass;
    }

    public String getMb_lat() {
        return mb_lat;
    }

    public void setMb_lat(String mb_lat) {
        this.mb_lat = mb_lat;
    }

    public String getMb_lon() {
        return mb_lon;
    }

    public void setMb_lon(String mb_lon) {
        this.mb_lon = mb_lon;
    }

    public String getMb_num() {
        return mb_num;
    }

    public void setMb_num(String mb_num) {
        this.mb_num = mb_num;
    }

    public String getMb_status() {
        return mb_status;
    }

    public void setMb_status(String mb_status) {
        this.mb_status = mb_status;
    }

    public String getMb_addr() {
        return mb_addr;
    }

    public void setMb_addr(String mb_addr) {
        this.mb_addr = mb_addr;
    }

    public String getMb_lastupdate() {
        return mb_lastupdate;
    }

    public void setMb_lastupdate(String mb_lastupdate) {
        this.mb_lastupdate = mb_lastupdate;
    }
}
