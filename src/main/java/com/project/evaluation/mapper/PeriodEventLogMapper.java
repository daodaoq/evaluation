package com.project.evaluation.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PeriodEventLogMapper {

    @Insert("""
            INSERT INTO evaluation_period_event_log (period_id, operator_user_id, event_code, detail, create_time)
            VALUES (#{periodId}, #{operatorUserId}, #{eventCode}, #{detail}, NOW())
            """)
    int insert(@Param("periodId") Long periodId,
               @Param("operatorUserId") Integer operatorUserId,
               @Param("eventCode") String eventCode,
               @Param("detail") String detail);
}
