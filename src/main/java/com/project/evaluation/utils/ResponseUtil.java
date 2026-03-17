package com.project.evaluation.utils;

import com.alibaba.fastjson2.JSON;
import com.project.evaluation.entity.Result;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtil {

    public static void write(HttpServletResponse response, Result result) throws IOException {
        // 响应头设置 JSON
        response.setContentType("application/json;charset=utf-8");
        // 创建输出流对象
        PrintWriter writer = response.getWriter();
        // 创建 JSON 格式的数据
        String json = JSON.toJSONString(result);
        // 输出 JSON 格式的数据
        writer.println(json);
    }
}
