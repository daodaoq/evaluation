package com.project.evaluation.mapper;

import com.project.evaluation.vo.Class.AddClassReq;
import com.project.evaluation.vo.Class.UpdateClassReq;
import com.project.evaluation.entity.Class;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClassMapper {

    /**
     * 添加班级
     *
     * @param addClassReq
     */
    @Insert("INSERT INTO `sys_class`" +
            "(class_name,college_id,grade_year,create_time,update_time)" +
            "VALUES(#{className},#{collegeId},#{gradeYear},NOW(),NOW())")
    void addClass(AddClassReq addClassReq);

    /**
     * 删除班级
     *
     * @param id
     * @return
     */
    @Delete("DELETE FROM `sys_class` WHERE id=#{id}")
    int deleteClass(Integer id);

    @Update("UPDATE `sys_class` SET " +
            "class_name=#{updateClassReq.className}," +
            "college_id=#{updateClassReq.collegeId}," +
            "grade_year=#{updateClassReq.gradeYear}," +
            "update_time=NOW() WHERE id=#{id}")
    void updateClass(Integer id, UpdateClassReq updateClassReq);

    /**
     * 根据班级名称查找班级
     *
     * @param name
     * @return
     */
    @Select("SELECT * FROM `sys_class` WHERE class_name=#{name}")
    Class findClassByName(String name);

    @Select("SELECT * FROM `sys_class` WHERE class_name=#{className} AND college_id=#{collegeId} LIMIT 1")
    Class findByCollegeIdAndName(@Param("collegeId") Integer collegeId, @Param("className") String className);

    /**
     * 通过id查找班级
     *
     * @param id
     * @return
     */
    @Select("SELECT * FROM `sys_class` WHERE id=#{id}")
    Class findClassById(Integer id);

    /**
     * 批量获取班级列表
     *
     * @return
     */
    @Select("SELECT * FROM `sys_class`")
    List<Class> classList();

    List<Class> selectByIds(@Param("ids") List<Integer> ids);

    /**
     * 分页条件查询班级
     *
     * @param collegeId
     * @param gradeYear
     * @return
     */
    List<Class> paginationQuery(@Param("collegeIds") List<Integer> collegeIds, @Param("gradeYears") List<Integer> gradeYears);
}
