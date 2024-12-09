package com.example.calculatorja;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText inputExpression;
    private LineChart lineChart;
    private Button recognizeButton;

    private TextView resultView;
    private Button calculateButton;
    private RecyclerView historyRecyclerView;
    private CalculationAdapter adapter;
    private DrawingView drawingView;
    private CalculatorRepository repository;
    private List<Calculation> historyList = new ArrayList<>();
    private LruCache<String, String> calculationCache;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        lineChart = findViewById(R.id.lineChart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        calculationCache = new LruCache<>(50);
        inputExpression = findViewById(R.id.inputExpression);
        drawingView = findViewById(R.id.drawingView);
        resultView = findViewById(R.id.resultView);
        calculateButton = findViewById(R.id.calculateButton);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        recognizeButton = findViewById(R.id.recognizeButton);
        recognizeButton.setOnClickListener(v -> recognizeHandwriting());
        repository = new CalculatorRepository(
                CalculatorDataBase.getInstance(this).calculationDao()
        );

        adapter = new CalculationAdapter(historyList);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(adapter);

        loadHistory();

        calculateButton.setOnClickListener(view -> {
            String expression = inputExpression.getText().toString();
            evaluateExpressionAsync(expression);
        });
    }

    private void recognizeHandwriting() {

        Bitmap bitmap = getBitmapFromView(drawingView);
        if (bitmap == null) {
            resultView.setText("Error: DrawingView is empty.");
            return;
        }


        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);


        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        recognizer.process(inputImage)
                .addOnSuccessListener(text -> {

                    String recognizedText = text.getText();
                    if (recognizedText.isEmpty()) {
                        resultView.setText("No text recognized.");
                    } else {

                        inputExpression.setText(recognizedText);
                        evaluateExpressionAsync(recognizedText);
                    }
                })
                .addOnFailureListener(e -> {

                    resultView.setText("Recognition failed: " + e.getMessage());
                });
    }

    private Bitmap getBitmapFromView(DrawingView drawingView) {
        Bitmap bitmap = Bitmap.createBitmap(drawingView.getWidth(), drawingView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawingView.draw(canvas);
        return bitmap;
    }

    private void evaluateExpressionAsync(String expression) {
        String cachedResult = calculationCache.get(expression);

        if (cachedResult != null) {
            resultView.setText("Cached: " + expression);
            plotGraph(expression);
            return;
        }

        executor.execute(() -> {
            String result;
            try {
                Expression exp = new ExpressionBuilder(expression).build();
                double evalResult = exp.evaluate();
                result = String.valueOf(evalResult);
            } catch (Exception e) {
                result = "Error";
            }

            String finalResult = result;

            runOnUiThread(() -> {
                resultView.setText(finalResult);
                calculationCache.put(expression, finalResult);
                plotGraph(expression);
                saveCalculation(expression, finalResult);
            });
        });
    }

    private void plotGraph(String expression) {
        try {
            ArrayList<Entry> entries = new ArrayList<>();

            for (float x = -10; x <= 10; x += 0.1) {
                Expression exp = new ExpressionBuilder(expression)
                        .variable("x")
                        .build();

                exp.setVariable("x", x);

                double result;
                try {
                    result = exp.evaluate();
                    if (Double.isNaN(result)) {
                        continue;
                    }

                    entries.add(new Entry(x, (float) result));
                } catch (Exception e) {
                    result = 0;

                    Log.e("GraphPlot", "Error evaluating expression for x = " + x + ": " + e.getMessage());
                }
            }

            if (entries.isEmpty()) {
                Log.e("GraphPlot", "No entries to plot. The expression might be invalid.");
                return;
            }


            LineDataSet dataSet = new LineDataSet(entries, "y = f(x)");


            dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            dataSet.setValueTextColor(getResources().getColor(android.R.color.black));


            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate();

            Log.d("GraphPlot", "Graph plotted successfully.");
        } catch (Exception e) {

            Log.e("GraphPlot", "Error in plotGraph: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void saveCalculation(String expression, String result) {
        executor.execute(() -> {
            Calculation calculation = new Calculation(expression, result, System.currentTimeMillis());

            repository.saveCalculation(calculation);


            runOnUiThread(() -> {
                historyList.add(0, calculation);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void loadHistory() {
        new Thread(() -> {
            historyList.clear();
            historyList.addAll(repository.getHistory());
            runOnUiThread(adapter::notifyDataSetChanged);
        }).start();
    }


}