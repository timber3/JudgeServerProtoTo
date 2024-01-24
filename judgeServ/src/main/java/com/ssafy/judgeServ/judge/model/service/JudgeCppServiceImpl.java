package com.ssafy.judgeServ.judge.model.service;

import com.ssafy.judgeServ.judge.mapper.JudgeMapper;
import com.ssafy.judgeServ.judge.model.dto.JudgeResponseDto;
import com.ssafy.judgeServ.judge.model.dto.JudgeResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class JudgeCppServiceImpl implements JudgeService{

    private JudgeMapper mapper;
    JudgeResponseDto judgeResponseDto = new JudgeResponseDto();


    @Override
    public JudgeResponseDto judge(HashMap<String, String> map) {
        String submitNo = map.get("submitNo");
        String problemNo = map.get("problemNo");
        String userCode = map.get("code");
        String lt = map.get("limitTime");
        Long LimitTime = Long.parseLong(lt);

        // 시스템 콜 문장이 포함 되어있으면 종료시키기
        if (checkSystemCallInCode(userCode) ) {
            return judgeResponseDto;
        }

        try {
            // Process Builder 로 실행 해보기
            String uuid = UUID.randomUUID().toString();

            // 테스트 케이스와 제한 시간을 가져온다. (하나로 묶어보기)
            ArrayList<HashMap<String, String>> testCaseList = mapper.getTestCase(problemNo);
            String timelimit = mapper.getProblemTime(problemNo);
            String submitStatus = "맞았습니다.";

            String fileName = uuid;
            String compileCmd = "g++ " + uuid + ".cpp -o " + uuid;
            String executeCmd = "./" + uuid;

            // cpp 는 숫자로 시작해도 컴파일 되네? ㅋㅋ
            createCodeFile(userCode, fileName);

            // 컴파일 해주기
            ProcessBuilder pb = new ProcessBuilder(compileCmd);
            Process process = pb.start();
            process.waitFor();

            // 에러 발생 시
            if (process.exitValue() != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String error = errorReader.readLine();

                System.out.println("error = " + error);

//                String[] frags = error.split(" ");
//                isError = true;
//
//                switch (frags[0]) {
//                    case "Exception" :
//                        System.out.println(error);
//                        submitStatus = "Exception : " + frags[4];
//                        break;
//                    case "Error:" :
//                        System.out.println("error = " + error);
//                        submitStatus = "컴파일 에러";
//                        break;
//                }
//                break;
            }

            // ==================== 실행 시간 측정 =====================
            long startTime = System.currentTimeMillis();

            pb = new ProcessBuilder(executeCmd);
            process = pb.start();
            process.waitFor();

            boolean finished = process.waitFor(LimitTime, TimeUnit.MILLISECONDS);

            long finishTime = System.currentTimeMillis();

            long usedTime = finishTime - startTime;

            // ==================== 실행 시간 측정 =======================

            if (!finished) {

            }


        } catch (Exception e) {

        }

        return judgeResponseDto;
    }

    // 시스템콜 검사
    public boolean checkSystemCallInCode(String code) {
        Pattern pattern = Pattern.compile("(?<!\\w)Runtime\\.getRuntime\\(\\)\\.exec\\(\"[^\"]+\"\\)");
        Matcher matcher = pattern.matcher(code);

        judgeResponseDto.setStatus("500");
        judgeResponseDto.setMsg("시스템 접근 확인");
        judgeResponseDto.setData(null);

        // 만약 코드에서 SystemCall 패턴을 확인하면 True를 반환합니다.
        return (matcher.find());
    }

    // C++ 코드를 .cpp 파일로 만들기
    public void createCodeFile(String code, String fileName) {

        File file = new File(fileName + ".cpp");
        System.out.println(".cpp 파일 생성..");
        System.out.println("filename : " + fileName);

        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(code);
            fw.close();
        } catch (Exception e) {
            System.out.println("파일 생성 에러 발생");
            e.printStackTrace();
        }
    }
}
