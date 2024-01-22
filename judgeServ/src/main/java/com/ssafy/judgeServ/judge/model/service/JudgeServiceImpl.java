package com.ssafy.judgeServ.judge.model.service;

import com.ssafy.judgeServ.judge.mapper.JudgeMapper;
import com.ssafy.judgeServ.judge.model.dto.JudgeResultDto;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
            ArrayList<HashMap<String, String>> list = mapper.getTestCase(map.get("problem_no"));
            // TC 불러왔으면 검사하는 로직 수행하기

            String id = UUID.randomUUID().toString();

            String path = "C:\\Program Files\\Java\\jdk-17.0.1\\lib\\" + id; //폴더 경로
            String cmd = "javac \"C:\\Program Files\\Java\\jdk-17.0.1\\lib\\" + id + "\\solution.java\"";
            String cmd2 = "java -cp \"C:\\Program Files\\Java\\jdk-17.0.1\\lib\\" + id + "\" solution";

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

            Process process2 = runtime.exec(cmd2);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process2.getOutputStream()));
            bw.write(list.get(0).get("input"));
            bw.flush();
            bw.close();
            BufferedReader bf =new BufferedReader(new InputStreamReader(process2.getInputStream(), "MS949"));
            String str = bf.readLine();
            System.out.println(str);

            // 3. 파일 실행시켜서 입력버퍼에 input 값 넣기


            // 4. 출력 값 받아오고 output 값 비교하기


            // 5. 결과 답기


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
