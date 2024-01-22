package com.ssafy.judgeServ.judge.model.service;

import com.ssafy.judgeServ.judge.model.dto.JudgeResultDto;

import java.util.HashMap;

public interface JudgeService {

    JudgeResultDto judge(HashMap<String, String> map);
}
