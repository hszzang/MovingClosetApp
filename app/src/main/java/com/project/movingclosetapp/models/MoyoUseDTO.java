package com.project.movingclosetapp.models;

public class MoyoUseDTO {

    private String m_idx;
    private String userid;
    private String mu_name;
    private String mu_phone;
    private String mu_email;
    private String mu_time;
    private String mu_regidate;

    public MoyoUseDTO() {}
    public MoyoUseDTO(String m_idx, String userid, String mu_name, String mu_phone, String mu_email, String mu_time, String mu_regidate) {
        this.m_idx = m_idx;
        this.userid = userid;
        this.mu_name = mu_name;
        this.mu_phone = mu_phone;
        this.mu_email = mu_email;
        this.mu_time = mu_time;
        this.mu_regidate = mu_regidate;
    }

    public String getM_idx() {
        return m_idx;
    }

    public void setM_idx(String m_idx) {
        this.m_idx = m_idx;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMu_name() {
        return mu_name;
    }

    public void setMu_name(String mu_name) {
        this.mu_name = mu_name;
    }

    public String getMu_phone() {
        return mu_phone;
    }

    public void setMu_phone(String mu_phone) {
        this.mu_phone = mu_phone;
    }

    public String getMu_email() {
        return mu_email;
    }

    public void setMu_email(String mu_email) {
        this.mu_email = mu_email;
    }

    public String getMu_time() {
        return mu_time;
    }

    public void setMu_time(String mu_time) {
        this.mu_time = mu_time;
    }

    public String getMu_regidate() {
        return mu_regidate;
    }

    public void setMu_regidate(String mu_regidate) {
        this.mu_regidate = mu_regidate;
    }
}
