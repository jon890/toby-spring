package chapter3.template_and_callback;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalcSumTest {

    static Calculator calculator;
    static String numberFilePath;

    @BeforeAll
    static void setUp() {
        calculator = new Calculator();
        numberFilePath = CalcSumTest.class.getResource("/numbers.txt").getPath();
    }

    @Test
    public void 숫자들의_합() throws IOException {
        int sum = calculator.calcSum(numberFilePath);
        assertEquals(sum, 10);
    }

    @Test
    public void 숫자들의_곱() throws IOException {
        int multiply = calculator.calcMultiply(numberFilePath);
        assertEquals(multiply, 24);
    }

    @Test
    public void 숫자들의_concat() throws IOException {
        String concat = calculator.concatenate(numberFilePath);
        assertEquals(concat, "1234");
    }
}
