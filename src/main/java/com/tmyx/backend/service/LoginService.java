package com.tmyx.backend.service;

import com.tmyx.backend.entity.Login;
import com.tmyx.backend.mapper.LoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    public LoginMapper loginMapper;

    public void insertLoginRecord(Integer attempt) {
        // 插入登录记录
        Login login = new Login();
        login.setAttempt(attempt);
        loginMapper.insert(login);

    }
}
