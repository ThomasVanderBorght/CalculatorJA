package com.example.calculatorja;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Calculation.class}, version = 1, exportSchema = false)
public abstract class CalculatorDataBase extends RoomDatabase {

    private static CalculatorDataBase instance;

    public abstract calculationDao calculationDao();

    public static synchronized CalculatorDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CalculatorDataBase.class,
                            "calculator_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
