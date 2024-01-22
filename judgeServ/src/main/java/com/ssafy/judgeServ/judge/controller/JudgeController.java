package com.ssafy.judgeServ.judge.controller;

import com.ssafy.judgeServ.judge.model.dto.JudgeResultDto;
import com.ssafy.judgeServ.judge.model.service.JudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/judge")
@Slf4j
public class JudgeController {
    /*
    map =>
        1. 사용자가 푼 문제 번호
        2. 사용자가 제출한 코드
     */

    JudgeService judgeService;

    public JudgeController(JudgeService judgeService) {
        this.judgeService = judgeService;
    }

    /*
    Json 형식
    {
        problem_no : 문제 번호
        code : 제출 받은 코드
    }
     */
    @PostMapping
    public JudgeResultDto judge(@RequestBody HashMap<String, String> map) {
        return judgeService.judge(map);
    }
}
