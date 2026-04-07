package com.project.evaluation;

import com.project.evaluation.config.DotenvBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvaluationApplication {

    public static void main(String[] args) {
        // 必须在 Spring 启动前加载，否则 spring.profiles.active 无法从 .env 生效
        DotenvBootstrap.load();
        SpringApplication.run(EvaluationApplication.class, args);
    }

}
