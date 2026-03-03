package com.tmyx.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendCaptchaMail(String to, String code) {
        // 创建邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 邮件发送人
        message.setFrom(from);
        // 邮件接收人
        message.setTo(to);
        // 邮件主题
        message.setSubject("个人站点后台登录验证");
        // 邮件内容
        message.setText("您的验证码为：" + code + "，有效期为5分钟。");
        // 发送邮件
        mailSender.send(message);
    }
}
