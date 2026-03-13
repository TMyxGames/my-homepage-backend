package com.tmyx.backend.controller;

import com.tmyx.backend.common.Result;
import com.tmyx.backend.dto.LoginDTO;
import com.tmyx.backend.mapper.LoginMapper;
import com.tmyx.backend.service.LoginService;
import com.tmyx.backend.service.MailService;
import com.tmyx.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class LoginController {
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private MailService mailService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private LoginService loginService;

    // 发送验证码
    @PostMapping("/sendCaptcha")
    public Result sendCaptcha() {
        String email = "tmyx_games@qq.com";
        // 生成验证码
        String captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        // 保存验证码到redis
        redisTemplate.opsForValue().set("CAPTCHA:" + email, captcha, 5, TimeUnit.MINUTES);
        // 发送邮件
        mailService.sendCaptchaMail(email, captcha);
        // 返回结果
        return Result.success();
    }

    // 登录
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String email = "tmyx_games@qq.com";
        String inputCode = loginDTO.getCaptcha();
        String realCode = redisTemplate.opsForValue().get("CAPTCHA:" + email);
        // 检查验证码是否有效
        if (realCode == null) {
            loginService.insertLoginRecord(0);
            return Result.error(500,"验证码已失效，请重新获取");
        }
        // 检查验证码是否正确
        if (!realCode.equals(inputCode)) {
            loginService.insertLoginRecord(0);
            return Result.error(500,"验证码错误");
        }
        loginService.insertLoginRecord(1);
        redisTemplate.delete("CAPTCHA:" + email);
        // 生成JWT token
        String token = JwtUtil.createToken(1);

        return Result.success(token);

    }

    // 获取所有登录记录
    @GetMapping("/all")
    public Result getAllLoginRecords(@RequestAttribute Integer userId) {
        return Result.success(loginMapper.findAll());
    }


}
