package com.ssafy.judgeServ.judge.model.service;

import com.ssafy.judgeServ.judge.mapper.JudgeMapper;
import com.ssafy.judgeServ.judge.model.dto.JudgeResultDto;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class JudgeServiceImpl implements JudgeService{
    JudgeMapper mapper;

    public JudgeServiceImpl(JudgeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public JudgeResultDto judge(HashMap<String, String> map) {
        JudgeResultDto judgeResultDto = new JudgeResultDto();
        // 채점 코드 실행하기 전 TC 가져오기.
        
        try {

            // 테스트 케이스들을 전부 가져온다.
            ArrayList<HashMap<String, String>> testCaseList = mapper.getTestCase(map.get("problem_no"));
            ArrayList<String> userResultList = new ArrayList<>();
            String timelimit = mapper.getProblemTime(map.get("problem_no"));

            long tl = Long.parseLong(timelimit);

            String submitStatus = "맞았습니다.";

            String id = UUID.randomUUID().toString();

            String path = "C:\\Program Files\\Java\\jdk-17\\lib\\" + id; //폴더 경로
            String cmd = "javac \"C:\\Program Files\\Java\\jdk-17\\lib\\" + id + "\\solution.java\"";
            String cmd2 = "java -cp \"C:\\Program Files\\Java\\jdk-17\\lib\\" + id + "\" solution";

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
            Process process = runtime.exec(cmd);

            process.waitFor();
            process.destroy();

            System.out.println(cmd);

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

                BufferedReader errorReader = process2.errorReader();



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
                System.out.println(process2.exitValue());

                // 결과 값 출력 & 저장
                System.out.println(str);
                userResultList.add(str);
            }

            // 일단 단순하게 맞 or 틀 로 구분
//            boolean correct = true;

//            // 사용자 결과와 DB의 결과를 비교해서 DB에 반영하기
//            for (int i = 0 ; i < testCaseList.size(); i ++) {
//                // user가 제출한 코드의 답과, 테케의 답이 다를 경우
//                if (!testCaseList.get(i).get("output").equals(userResultList.get(i))) {
//                    correct = false;
//                    break;
//                }
//            }


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

        } catch (Exception e) {
            judgeResultDto.setStatus("500");
            judgeResultDto.setMsg("테케 가져오기 실패");
            judgeResultDto.setData(null);

            e.printStackTrace();
        }
        return judgeResultDto;
    }

    public void createCodeFile(String code, String path) throws IOException {
        File file = new File(path + "\\solution.java");
        System.out.println("파일 생성 중..");
        System.out.println(path);
        if(file.createNewFile());
        FileWriter fw = new FileWriter(file);
        fw.write(code);
        fw.close();
    }

}
