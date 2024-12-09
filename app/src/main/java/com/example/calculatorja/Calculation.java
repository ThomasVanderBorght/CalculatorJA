package com.example.calculatorja;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "calculation_history")
public class Calculation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String expression;
    private String result;
    private long timestamp;

    public Calculation(String expression, String result, long timestamp) {
        this.expression = expression;
        this.result = result;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
