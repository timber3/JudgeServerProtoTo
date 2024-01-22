package com.ssafy.judgeServ;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.ssafy.judgeServ.*.mapper"})
public class JudgeServApplication {

	public static void main(String[] args) {
		SpringApplication.run(JudgeServApplication.class, args);
	}
}
