package com.project.evaluation.mapper;

import com.project.evaluation.entity.College;
import com.project.evaluation.vo.College.AddCollegeReq;
import com.project.evaluation.vo.College.UpdateCollegeReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CollegeMapper {
    /**
     * 添加学院
     *
     * @param addCollegeReq
     */
    @Insert("INSERT INTO `sys_college`" +
            "(college_name, status, create_time, update_time)" +
            "VALUES(#{collegeName}, #{status}, NOW(), NOW())")
    void addCollege(AddCollegeReq addCollegeReq);

    /**
     * 删除学院
     *
     * @param id
     * @return
     */
    @Delete("DELETE FROM `sys_college` WHRER id = #{id}")
    int deleteCollege(Integer id);

    /**
     * 更新学院信息
     *
     * @param id
     * @param updateCollegeReq
     */
    @Update("UPDATE `sys_college` SET" +
            "college_name = #{updateCollegeReq.collegeName}," +
            "status = #{updateCollegeReq.status}," +
            "update_time = NOW() WHERE id = #{id}")
    void updateCollege(Integer id, UpdateCollegeReq updateCollegeReq);

    /**
     * 根据学院名称查找学院
     *
     * @param name
     * @return
     */
    @Select("SELECT *FROM `sys_college` WHERE college_name = #{name}")
    College findCollegeByName(String name);

    /**
     * 通过id查找学院
     *
     * @param id
     * @return
     */
    @Select("SELECT * FROM `sys_college` WHERE id = #{id}")
    College findCollegeById(Integer id);

    /**
     * 批量获取学院列表
     *
     * @return
     */
    @Select("SELECT * FROM `sys_college`")
    List<College> collegeList();

    /**
     * 分页条件查询学院
     *
     * @param status
     * @return
     */
    @Select("SELECT * FROM `sys_college` WHERE status=#{status}")
    List<College> paginationQuery(Integer status);
}
