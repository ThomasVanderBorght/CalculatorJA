package com.example.calculatorja;

import java.util.List;

public class CalculatorRepository {

    private calculationDao calculationDao;

    public CalculatorRepository(calculationDao calculationDao) {
        this.calculationDao = calculationDao;
    }

    public void saveCalculation(Calculation calculation) {
        calculationDao.insertCalculation(calculation);
    }
    public List<Calculation> getHistory() {
        return calculationDao.getAllCalculations();
    }

    public void clearHistory() {
        new Thread(calculationDao::clearHistory).start();
    }
}
