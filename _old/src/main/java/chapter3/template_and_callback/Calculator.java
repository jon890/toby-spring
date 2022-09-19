package chapter3.template_and_callback;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public int calcSum(String filePath) throws IOException {
        return lineReadTemplate(filePath, (line, value) -> value + Integer.parseInt(line), 0);
    }

    public int calcMultiply(String filePath) throws IOException {
        return lineReadTemplate(filePath, (line, value) -> value * Integer.parseInt(line), 1);
    }

    public String concatenate(String filePath) throws IOException {
        return lineReadTemplate(filePath, (line, value) -> value + line, "");
    }

    public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initValue) throws IOException {
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            T res = initValue;
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                // 콜백 오브젝트 호출
                // 템플릿에서 만든 컨텍스트 정보인
                // line 과 value 를 전달하고 값을 저장한다
                res = callback.doSomethingWithLine(line, res);
            }

            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
