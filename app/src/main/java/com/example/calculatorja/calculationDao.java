package com.example.calculatorja;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface calculationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCalculation(Calculation calculation);

    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    List<Calculation> getAllCalculations();

    @Query("DELETE FROM calculation_history")
    void clearHistory();
}
