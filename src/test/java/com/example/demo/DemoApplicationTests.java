package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }


    public static void main(String[] args) {
        String s = "{\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\":\"用户通话 API 请求模式\",\"description\":\"用于验证 /user/{userId}/call/{callId} GET API 请求参数的模式\",\"type\":\"object\",\"properties\":{\"userId\":{\"type\":\"string\",\"description\":\"用户的唯一标识符\",\"pattern\":\"^[1-9]\\\\d*$\",\"errorMessage\":\"userId不能为空，且必须为正整数\"},\"callId\":{\"type\":\"string\",\"description\":\"通话的唯一标识符\",\"pattern\":\"^[1-9]\\\\d*$\",\"errorMessage\":\"callId不能为空，且必须为正整数\"},\"pageNum\":{\"type\":\"string\",\"pattern\":\"^[1-9]\\\\d*$\",\"default\":\"1\",\"errorMessage\":\"pageNum必须是正整数，必须从1开始\"},\"pageSize\":{\"type\":\"string\",\"pattern\":\"^[1-9]\\\\d*$\",\"default\":\"10\",\"errorMessage\":\"pageSize必须是正整数，必须从1开始\"},\"callStatus\":{\"type\":[\"string\",\"null\"],\"enum\":[\"IN_PROGRESS\",\"COMPLETED\",\"CANCELLED\",null],\"errorMessage\":\"callStatus 值必须为 IN_PROGRESS、COMPLETED、CANCELLED其中之一 \"}},\"required\":[\"userId\",\"callId\"],\"additionalProperties\":false}";
    }

}
