package com.project.evaluation.mapper;

import com.project.evaluation.entity.Authority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthorityMapper {

    /**
     * 根据用户 id 查询权限字符
     * @param id
     * @return
     */
    List<String> selectAuthorityByUserId(@Param("userid") Integer id);

    /**
     * 根据用户 id 查询权限详细信息
     * @param id
     * @return
     */
    List<Authority> selectAllAuthorityDetailsByUserId(@Param("userid") Integer id);
}
