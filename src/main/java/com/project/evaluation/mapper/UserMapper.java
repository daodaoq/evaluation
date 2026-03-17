package com.project.evaluation.mapper;

import com.project.evaluation.entity.MyUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查对象
     * @param username
     * @return
     */
    MyUser selectByUsername(@Param("username") String username);
}
