package com.project.movingclosetapp.models;

import java.io.Serializable;

public class MoyoDTO implements Serializable {

    private String m_idx;
    private String m_name;
    private String m_addr;
    private String m_lat;
    private String m_lon;
    private String m_goal;
    private String m_dday;
    private String m_desc;
    private String m_start;
    private String m_end;
    private String m_status;
    private String m_ofile;
    private String m_sfile;

    public MoyoDTO() {}
    public MoyoDTO(String m_idx, String m_name, String m_addr, String m_lat, String m_lon, String m_goal, String m_dday, String m_desc, String m_start, String m_end, String m_status, String m_ofile, String m_sfile) {
        this.m_idx = m_idx;
        this.m_name = m_name;
        this.m_addr = m_addr;
        this.m_lat = m_lat;
        this.m_lon = m_lon;
        this.m_goal = m_goal;
        this.m_dday = m_dday;
        this.m_desc = m_desc;
        this.m_start = m_start;
        this.m_end = m_end;
        this.m_status = m_status;
        this.m_ofile = m_ofile;
        this.m_sfile = m_sfile;
    }

    public String getM_idx() {
        return m_idx;
    }

    public void setM_idx(String m_idx) {
        this.m_idx = m_idx;
    }

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public String getM_addr() {
        return m_addr;
    }

    public void setM_addr(String m_addr) {
        this.m_addr = m_addr;
    }

    public String getM_lat() {
        return m_lat;
    }

    public void setM_lat(String m_lat) {
        this.m_lat = m_lat;
    }

    public String getM_lon() {
        return m_lon;
    }

    public void setM_lon(String m_lon) {
        this.m_lon = m_lon;
    }

    public String getM_goal() {
        return m_goal;
    }

    public void setM_goal(String m_goal) {
        this.m_goal = m_goal;
    }

    public String getM_dday() {
        return m_dday;
    }

    public void setM_dday(String m_dday) {
        this.m_dday = m_dday;
    }

    public String getM_desc() {
        return m_desc;
    }

    public void setM_desc(String m_desc) {
        this.m_desc = m_desc;
    }

    public String getM_start() {
        return m_start;
    }

    public void setM_start(String m_start) {
        this.m_start = m_start;
    }

    public String getM_end() {
        return m_end;
    }

    public void setM_end(String m_end) {
        this.m_end = m_end;
    }

    public String getM_status() {
        return m_status;
    }

    public void setM_status(String m_status) {
        this.m_status = m_status;
    }

    public String getM_ofile() {
        return m_ofile;
    }

    public void setM_ofile(String m_ofile) {
        this.m_ofile = m_ofile;
    }

    public String getM_sfile() {
        return m_sfile;
    }

    public void setM_sfile(String m_sfile) {
        this.m_sfile = m_sfile;
    }

    @Override
    public String toString() {
        return "MoyoDTO{" +
                "m_idx='" + m_idx + '\'' +
                ", m_name='" + m_name + '\'' +
                ", m_addr='" + m_addr + '\'' +
                ", m_lat='" + m_lat + '\'' +
                ", m_lon='" + m_lon + '\'' +
                ", m_goal='" + m_goal + '\'' +
                ", m_dday='" + m_dday + '\'' +
                ", m_desc='" + m_desc + '\'' +
                ", m_start='" + m_start + '\'' +
                ", m_end='" + m_end + '\'' +
                ", m_status='" + m_status + '\'' +
                ", m_ofile='" + m_ofile + '\'' +
                ", m_sfile='" + m_sfile + '\'' +
                '}';
    }
}
