package com.project.evaluation.mapper;

import com.project.evaluation.entity.EvaluationPublicity;
import com.project.evaluation.vo.Publicity.PublicitySaveReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EvaluationPublicityMapper {

    @Insert("""
            INSERT INTO evaluation_publicity (period_id, class_id, start_time, end_time, status, create_time, update_time)
            VALUES (#{periodId}, #{classId}, #{startTime}, #{endTime}, #{status}, NOW(), NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(EvaluationPublicity row);

    @Update("""
            UPDATE evaluation_publicity SET
            period_id = #{periodId},
            class_id = #{classId},
            start_time = #{startTime},
            end_time = #{endTime},
            status = #{status},
            update_time = NOW()
            WHERE id = #{id}
            """)
    int updateById(PublicitySaveReq req);

    @Delete("DELETE FROM evaluation_publicity WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM evaluation_publicity WHERE id = #{id}")
    EvaluationPublicity selectById(@Param("id") Long id);

    @Select("SELECT * FROM evaluation_publicity WHERE period_id = #{periodId} ORDER BY id DESC")
    List<EvaluationPublicity> listByPeriod(@Param("periodId") Long periodId);

    @Select("""
            <script>
            SELECT * FROM evaluation_publicity p
            WHERE p.period_id = #{periodId}
              AND p.status = 'OPEN'
              AND NOW() BETWEEN p.start_time AND p.end_time
              AND (p.class_id IS NULL OR p.class_id = #{classId})
            ORDER BY p.id DESC
            </script>
            """)
    List<EvaluationPublicity> listActiveForStudent(@Param("periodId") Long periodId, @Param("classId") Integer classId);
}
