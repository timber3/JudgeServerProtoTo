package com.ssafy.judgeServ.judge.model.dto;

import lombok.Data;

@Data
public class JudgeResponseDto {
    private String status;
    private String msg;
    private Object data;
}
