package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.*;

public class IntegerDecodeTest {
    //Тест 0: Пустая строка
    @Test
    void empty() {
        assertThrows(NumberFormatException.class, () -> {
            Integer.decode("");
        });
    }

    // Тест 1: Неправильный формат
    @ParameterizedTest
    @ValueSource(strings = { "abc", "0x", "0xGG", "-12-", "++" })
    void testDecodeThrowsNumberFormatException(String invalidValue) {
        assertThrows(NumberFormatException.class, () -> {
            Integer.decode(invalidValue);
        });
    }

    // Тест 2: С пробелами и отступами
    @Test
    void testDecodeWithWhitespace() {
        assertThrows(NumberFormatException.class, () -> Integer.decode(" 123"));
        assertThrows(NumberFormatException.class, () -> Integer.decode("123 "));
        assertThrows(NumberFormatException.class, () -> Integer.decode(" 0xFF "));
    }

    // Тест 3: Десятичные числа
    @ParameterizedTest
    @CsvSource({
            "123, 123",
            "-456, -456",
            "0, 0"
    })
    void testDecodeDecimalNumbers(String input, int expected) {
        assertEquals(Integer.valueOf(expected), Integer.decode(input));
    }

    // Тест 4: Шестнадцатеричные числа (0x, 0X, #)
    @ParameterizedTest
    @CsvSource({
            "0x18, 24",
            "0xFF, 255",
            "#FF, 255",
            "#ff, 255",
    })
    void testDecodeHexadecimal(String input, int expected) {
        assertEquals(Integer.valueOf(expected), Integer.decode(input));
    }

    // Тест 5: Восьмеричные числа (0 префикс)
    @ParameterizedTest
    @CsvSource({
            "017, 15",
            "0777, 511",
            "017777777777, 2147483647"
    })
    void testDecodeOctal(String input, int expected) {
        assertEquals(Integer.valueOf(expected), Integer.decode(input));
    }

    // Тест 6: Отрицательные числа в разных системах счисления
    @Test
    void testDecodeNegativeNumbers() {
        assertEquals(Integer.valueOf(-255), Integer.decode("-0xFF"));
        assertEquals(Integer.valueOf(-255), Integer.decode("-0XFF"));
        assertEquals(Integer.valueOf(-255), Integer.decode("-#FF"));
        assertEquals(Integer.valueOf(-15), Integer.decode("-017"));
    }

    // Тест 7: Числа со знаком +
    @Test
    void testPlusCases() {
        assertEquals(Integer.valueOf(123), Integer.decode("+123"));
        assertEquals(Integer.valueOf(255), Integer.decode("+0xFF"));
        assertEquals(Integer.valueOf(15), Integer.decode("+017"));
    }

    // Тест 8: Граничные значения
    @Test
    void testDecodeSpecialCases() {
        assertEquals(Integer.valueOf(2147483647), Integer.decode("2147483647"));
        assertEquals(Integer.valueOf(-2147483648), Integer.decode("-2147483648"));
        assertEquals(Integer.valueOf(2147483647), Integer.decode("0x7FFFFFFF"));
        assertEquals(Integer.valueOf(-2147483648), Integer.decode("-0x80000000"));
    }

    // Тест 9: Проверка переполнения
    @Test
    void testDecodeOverflow() {
        // Переполнение произойдет
        assertThrows(NumberFormatException.class, () -> Integer.decode("2147483648"));
        assertThrows(NumberFormatException.class, () -> Integer.decode("-2147483649"));
        assertThrows(NumberFormatException.class, () -> Integer.decode("0x80000000"));
        assertThrows(NumberFormatException.class, () -> Integer.decode("020000000000"));

        // Переполнение НЕ произойдет
        assertDoesNotThrow(() -> Integer.decode("-0x80000000"));
        assertDoesNotThrow(() -> Integer.decode("2147483647"));
    }
}
