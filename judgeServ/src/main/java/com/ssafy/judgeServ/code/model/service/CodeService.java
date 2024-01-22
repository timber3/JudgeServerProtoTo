package com.ssafy.judgeServ.code.model.service;

import com.ssafy.judgeServ.code.model.dto.CodeResultDto;

public interface CodeService {

    CodeResultDto submit(String code);
}
