package com.example.janari.SimpleDailyBudgetApp;

import org.junit.Test;

import static org.junit.Assert.*;


public class CalculationUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        CalculateDailySumClass calculateDailySumClass = new CalculateDailySumClass();
        double result = calculateDailySumClass.calculateSum(12.0, 4.0, 2.0);
        int expected = 4;
        int res = (int) result;
        assertEquals(expected, res);
    }
}