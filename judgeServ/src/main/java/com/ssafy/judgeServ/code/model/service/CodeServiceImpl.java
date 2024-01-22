package com.ssafy.judgeServ.code.model.service;

import com.ssafy.judgeServ.code.mapper.CodeMapper;
import com.ssafy.judgeServ.code.model.dto.CodeResultDto;
import org.springframework.stereotype.Service;

@Service
public class CodeServiceImpl implements CodeService{
    CodeMapper mapper;

    public CodeServiceImpl(CodeMapper mapper) {
        this.mapper = mapper;
    }
    @Override
    public CodeResultDto submit(String code) {


        return null;
    }
}
