package com.ssafy.judgeServ.code.model.dto;

import lombok.Data;

@Data
public class CodeResultDto {
    private String status;
    private String msg;
    private Object data;
}
