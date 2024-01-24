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

@Service
@RequiredArgsConstructor
public class JudgeJavaServiceImpl implements JudgeService {
    private JudgeMapper mapper;

    @Override
    public JudgeResponseDto judge(HashMap<String, String> map) {
        JudgeResponseDto judgeResponseDto = new JudgeResponseDto();
        // 채점 코드 실행하기 전 TC 가져오기.
        
        try {
            // 테스트 케이스들을 전부 가져온다.
            ArrayList<HashMap<String, String>> testCaseList = mapper.getTestCase(map.get("problem_no"));

            for (int i = 0 ; i < testCaseList.size(); i ++) {
                System.out.println("testCaseList.get(i).get(\"input\") = " + testCaseList.get(i).get("input"));
            }

            String timelimit = mapper.getProblemTime(map.get("problem_no"));

            long tl = Long.parseLong(timelimit);

            String submitStatus = "맞았습니다.";

            String id = UUID.randomUUID().toString();

            //"C:\\Program Files\\Java\\jdk-17.0.1\\lib\\" +
            String path = id; //폴더 경로
//            String cmd = "javac \"C:\\Program Files\\Java\\jdk-17.0.1\\lib\\" + id + "\\solution.java\"";
//            String cmd2 = "java -cp \"C:\\Program Files\\Java\\jdk-17.0.1\\lib\\" + id + "\" solution";
            String cmd2 = "java " + id + "/solution.java";

            File Folder = new File(path);

            // 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
            if (!Folder.exists()) {
                try{
                    System.out.println(Folder.mkdir()); //폴더 생성합니다.
                    System.out.println("폴더가 생성되었습니다.");
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }else {
                System.out.println("이미 폴더가 생성되어 있습니다.");
            }

            // 2. 코드 가져와서 파일로 생성하기
            createCodeFile(map.get("code"), path);

            Runtime runtime = Runtime.getRuntime();
//            Process process = runtime.exec(cmd);

//            process.waitFor();
//            process.destroy();

//            System.out.println(cmd);

            Double timeSum = 0.0;

            boolean isError = false;

            // TC 불러왔으면 검사하는 로직 수행하기
            for (int tc = 0 ; tc < testCaseList.size() ; tc++) {
                // 컴파일 하고 실행시키기
                Process process2 = runtime.exec(cmd2);

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process2.getOutputStream()));
                bw.write(testCaseList.get(tc).get("input"));
                bw.flush();
                double beforeTime = System.currentTimeMillis();
                bw.close();

                if (!process2.waitFor(tl + 2000 + 1000, TimeUnit.MILLISECONDS)) {
                    submitStatus = "시간 초과";
                    isError = true;
                    break;
                }
                double afterTime = System.currentTimeMillis();

                BufferedReader bf = new BufferedReader(new InputStreamReader(process2.getInputStream(), "MS949"));
                String str = bf.readLine();

                timeSum += (afterTime-beforeTime)/1000;

                System.out.printf("tc :%d  시간 측정 결과 : %.3f\n", tc, (afterTime-beforeTime)/1000);
                System.out.println(str);

//                BufferedReader errorReader = process2.errorReader();
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process2.getErrorStream()));

                if (process2.exitValue() != 0) {
                    String error = errorReader.readLine();
                    String[] frags = error.split(" ");
                    isError = true;

                    switch (frags[0]) {
                        case "Exception" :
                            System.out.println(error);
                            submitStatus = "Exception : " + frags[4];
                            break;
                        case "Error:" :
                            System.out.println("error = " + error);
                            submitStatus = "컴파일 에러";
                            break;
                    }
                    break;
                }

                if (!testCaseList.get(tc).get("output").equals(str) ) {
                    submitStatus = "틀렸습니다";
                    isError = true;
                    break;
                }

                System.out.println(errorReader.readLine());
                System.out.println("process2.exitValue() = " + process2.exitValue());

                // 결과 값 출력 & 저장
                System.out.println("str = " + str);
            }

            String timeResult;

            if(isError) {
                timeResult = null;
            } else {
                timeSum *= 1000;
                timeSum /= testCaseList.size();
                timeResult = timeSum + "";
            }

            // 정답 여부를 판단 했으면 저장하기
            HashMap<String, String> result = new HashMap<>();

            result.put("submit_no", map.get("submit_no"));
            result.put("result" , submitStatus);
            result.put("time", timeResult);

            mapper.setSubmitStatus(result);
            System.out.println("=========DB input done========");

            File dirFile = new File(path);
            File javaFile = new File(path, "solution.java");

            // 결과 반영 했으면 디렉토리 삭제하기
            // 내부 파일부터 삭제하고 디렉토리 삭제
            javaFile.delete();
            dirFile.delete();

            judgeResponseDto.setStatus("200");
            judgeResponseDto.setMsg("채점 성공");
            judgeResponseDto.setData(null);

        } catch (Exception e) {
            judgeResponseDto.setStatus("500");
            judgeResponseDto.setMsg("테케 가져오기 실패");
            judgeResponseDto.setData(null);

            e.printStackTrace();
        }
        return judgeResponseDto;
    }

    public void createCodeFile(String code, String path) throws IOException {
        File file = new File(path, "solution.java");
        System.out.println("파일 생성 중..");
        System.out.println("path : " + path);

        if(file.createNewFile());
        FileWriter fw = new FileWriter(file);
        fw.write(code);
        fw.close();
    }

    // 코드에 SystemCall 호출 검사

//    public SolveResult solve(SolveInfo solveInfo, String type, String no, String INTEXT, String OUTTEXT) throws IOException, InterruptedException{
//        if (checkSystemCallInCode(solveInfo.getCode())) {
//            System.out.println("시스템 콜 함수 사용");
//            return new SolveResult(0, "시스템 콜 함수 사용", 0);
//        }
//        System.out.println("codeExecutor 실행 !");
//        return codeExecutor(solveInfo, type, no,INTEXT, OUTTEXT);
//    }

}
