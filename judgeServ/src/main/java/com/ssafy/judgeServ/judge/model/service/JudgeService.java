package com.ssafy.judgeServ.judge.model.service;

import com.ssafy.judgeServ.judge.model.dto.JudgeResponseDto;
import com.ssafy.judgeServ.judge.model.dto.JudgeResultDto;

import java.util.HashMap;

public interface JudgeService {
    JudgeResponseDto judge(HashMap<String, String> map);
}
