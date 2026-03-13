package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Login;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LoginMapper {

    // 插入登录记录
    @Insert("insert into login(attempt, time) values(#{attempt}, now())")
    public Integer insert(Login login);

    // 查询登录记录
    @Select("select * from login order by `time` desc")
    public List<Login> findAll();
}
