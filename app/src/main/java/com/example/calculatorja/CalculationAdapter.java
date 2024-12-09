package com.example.calculatorja;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder> {
    private List<Calculation> history;

    public CalculationAdapter(List<Calculation> history) {
        this.history = history;
    }

    @NonNull
    @Override
    public CalculationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new CalculationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculationViewHolder holder, int position) {
        Calculation calculation = history.get(position);
        holder.expression.setText(calculation.getExpression());
        holder.result.setText("Result: " + calculation.getResult());
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public static class CalculationViewHolder extends RecyclerView.ViewHolder {
        TextView expression, result;

        public CalculationViewHolder(@NonNull View itemView) {
            super(itemView);
            expression = itemView.findViewById(android.R.id.text1);
            result = itemView.findViewById(android.R.id.text2);
        }
    }
}
