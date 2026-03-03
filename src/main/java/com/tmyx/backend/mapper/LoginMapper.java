package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Login;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface LoginMapper {

    // 插入登录记录
    @Insert("insert into login(attempt, time) values(#{attempt}, now())")
    public Integer insert(Login login);
}
