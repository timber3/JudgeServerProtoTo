package com.ssafy.judgeServ.judge.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface JudgeMapper {
    ArrayList<HashMap<String, String>> getTestCase(String problem_no);
}
