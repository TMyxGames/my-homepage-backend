package com.tmyx.backend.dto;

public class LoginDTO {
    private String captcha;

    public String getCaptcha() { return captcha; }

    public void setCaptcha(String captcha) { this.captcha = captcha; }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "captcha='" + captcha + '\'' +
                '}';
    }
}
