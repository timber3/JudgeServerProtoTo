package com.ssafy.judgeServ.code.controller;


import com.ssafy.judgeServ.code.model.dto.CodeResultDto;
import com.ssafy.judgeServ.code.model.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/code")
@Slf4j
public class CodeController {

    CodeService codeService;

    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @PostMapping("/submit")
    public CodeResultDto submit(@RequestBody HashMap<String, String> map) {
        CodeResultDto codeResultDto = new CodeResultDto();



        return codeResultDto;
    }

}
