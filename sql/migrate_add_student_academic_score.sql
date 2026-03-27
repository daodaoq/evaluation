-- 学业水平（智育）由管理端维护：新增智育成绩表
CREATE TABLE IF NOT EXISTS evaluation_student_academic_score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    period_id BIGINT NOT NULL COMMENT '综测周期ID',
    student_no VARCHAR(64) NOT NULL COMMENT '学号',
    class_name VARCHAR(100) NOT NULL COMMENT '班级',
    student_name VARCHAR(64) NOT NULL COMMENT '姓名',
    intellectual_score DECIMAL(18,8) NOT NULL COMMENT '智育分（高精度小数）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_period_student_no (period_id, student_no),
    KEY idx_period_id (period_id),
    KEY idx_student_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生学业水平智育成绩表';
