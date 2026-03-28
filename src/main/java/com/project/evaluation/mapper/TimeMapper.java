package com.project.evaluation.mapper;


import com.project.evaluation.entity.Time;
import com.project.evaluation.vo.Time.AddTimeReq;
import com.project.evaluation.vo.Time.UpdateTimeReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 周期 mapper层
 */
@Mapper
public interface TimeMapper {


    /**
     * 添加
     * @param addTimeReq
     */
    @Insert("""
            INSERT INTO evaluation_period
            (period_name, start_time, end_time, status, archived,
             application_start_time, application_end_time, review_end_time,
             public_notice_start, public_notice_end, objection_end_time,
             create_time, update_time)
            VALUES (#{periodName}, #{startTime}, #{endTime}, #{status}, #{archived},
             #{applicationStartTime}, #{applicationEndTime}, #{reviewEndTime},
             #{publicNoticeStart}, #{publicNoticeEnd}, #{objectionEndTime},
             NOW(), NOW())
            """)
    void addTime(AddTimeReq addTimeReq);

    /**
     * 删除周期
     * @param id
     */
    @Delete("DELETE FROM `evaluation_period` WHERE id = #{id}")
    int deleteTime(Integer id);

    /**
     * 更新周期信息
     * @param updateTimeReq
     */
    @Update("""
            UPDATE evaluation_period SET
            period_name = #{updateTimeReq.periodName},
            start_time = #{updateTimeReq.startTime},
            end_time = #{updateTimeReq.endTime},
            status = #{updateTimeReq.status},
            archived = #{updateTimeReq.archived},
            application_start_time = #{updateTimeReq.applicationStartTime},
            application_end_time = #{updateTimeReq.applicationEndTime},
            review_end_time = #{updateTimeReq.reviewEndTime},
            public_notice_start = #{updateTimeReq.publicNoticeStart},
            public_notice_end = #{updateTimeReq.publicNoticeEnd},
            objection_end_time = #{updateTimeReq.objectionEndTime},
            update_time = NOW()
            WHERE id = #{id}
            """)
    void updateTime(Integer id, UpdateTimeReq updateTimeReq);

    /**
     *根据周其名称查找周期
     * @param name
     * @rerurn
     */
    @Select("SELECT * FROM `evaluation_period` WHERE period_name = #{name}")
    Time findTimeByName(String name);

    /**
     * 通过id查找周期
     * @param id
     * @return
     */
    @Select("SELECT * FROM `evaluation_period` WHERE id = #{id}")
    Time findTimeById(Integer id);

    /**
     * 批量获取周期列表
     * @return
     */
    @Select("SELECT * FROM `evaluation_period`")
    List<Time> timeList();

    /**
     *
     * @param periodName
     * @param status
     * @return
     */
    List<Time> paginationQuery(String periodName, Integer status);
}
