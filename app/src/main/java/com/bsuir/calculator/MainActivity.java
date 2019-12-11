package com.bsuir.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final List<String> operators =
            Arrays.asList("+", "-", "/", "*", "^");
    private static final List<String> functions =
            Arrays.asList("sin", "cos", "log", "sqrt");
    private static final String ERROR_TEXT = "Invalid expression";
    private static final String DECIMAL_FORMAT = "0.########";

    private TextView resultDisplay, inputDisplay;

    private String lastElement = "";
    private Boolean isCalculationDone = false;
    private Boolean isErrorOccurred = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultDisplay = findViewById(R.id.resultDisplay);
        inputDisplay = findViewById(R.id.inputDisplay);

        Button buttonCE = findViewById(R.id.buttonCE);
        Button buttonC = findViewById(R.id.buttonC);

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearViewDisplays();
                isCalculationDone = false;
                lastElement = "";
            }
        });
        buttonCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCalculationDone) {
                    clearViewDisplays();
                    isCalculationDone = false;
                    lastElement = "";
                } else {
                    String newText = inputDisplay.getText().toString();

                    if (newText.equals("")) {
                        return;
                    } else if (newText.length() == 1) {
                        newText = "";
                        inputDisplay.setText(newText);
                        resultDisplay.setText("");
                        lastElement = "";
                    } else {
                        newText = StringUtils.chop(newText);

                        inputDisplay.setText(newText);
                        lastElement = newText.substring(newText.length() - 1);

                        String result = getCalculatedResult();

                        if (!isErrorOccurred) {
                            resultDisplay.setText(result);
                        }
                    }
                }
            }
        });
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("input", inputDisplay.getText().toString());
        savedInstanceState.putString("result", resultDisplay.getText().toString());
        savedInstanceState.putString("lastElement", lastElement);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        resultDisplay.setText(savedInstanceState.getString("result"));
        inputDisplay.setText(savedInstanceState.getString("input"));
        lastElement = savedInstanceState.getString("lastElement");
    }

    public void appendElement(View v) {
        Button buttonPressed = (Button) v;
        String buttonPressedText = buttonPressed.getText().toString();
        String inputDisplayText = inputDisplay.getText().toString();
        String resultDisplayText = resultDisplay.getText().toString();

        if ("√".equals(buttonPressedText)) {
            buttonPressedText = "sqrt";
        }

        if ("π".equals(buttonPressedText)) {
            buttonPressedText = "π";
        }

        if (isCalculationDone) {
            isCalculationDone = false;

            if (isErrorOccurred) {
                clearViewDisplays();
                inputDisplayText = "";
                lastElement = "";
                isErrorOccurred = false;
            } else {
                inputDisplay.setText(resultDisplayText);
                lastElement = String.valueOf(resultDisplayText.charAt(resultDisplayText.length() - 1));
                inputDisplayText = resultDisplayText;
            }
        }

        if (isOperator(buttonPressedText)) {
            if (isOperator(lastElement)) {
                inputDisplay.setText(StringUtils.chop(inputDisplayText).concat(buttonPressedText));
                lastElement = buttonPressedText;
            } else if (!"".equals(lastElement)) {
                inputDisplay.append(buttonPressedText);
                lastElement = buttonPressedText;
            }
        } else if (isFunction(buttonPressedText)) {
            inputDisplay.append(buttonPressedText + "(");
            lastElement = buttonPressedText;
        } else if (".".equals(buttonPressedText) && lastElement.equals(buttonPressedText)) {
            return;
        } else {
            appendToInputDisplay(buttonPressedText);
            lastElement = buttonPressedText;
            String result = getCalculatedResult();

            if (!isErrorOccurred) {
                resultDisplay.setText(result);
            }
        }
    }

    public String getCalculatedResult() {
        try {
            Expression expression = new ExpressionBuilder(inputDisplay.getText().toString()).build();
            DecimalFormatSymbols separators = new DecimalFormatSymbols(Locale.GERMAN);
            separators.setDecimalSeparator('.');
            separators.setGroupingSeparator(' ');
            Double value = expression.evaluate();
            isErrorOccurred = false;

            return new DecimalFormat(DECIMAL_FORMAT, separators).format(value);
        } catch (Exception e) {
            isErrorOccurred = true;

            return ERROR_TEXT;
        }
    }

    public void calculate(View v) {
        String inputDisplayText = inputDisplay.getText().toString();
        String resultDisplayText = resultDisplay.getText().toString();
        boolean isValid = !inputDisplayText.equals("")
                            && !inputDisplayText.endsWith("=")
                            && !resultDisplayText.equals(inputDisplayText);

        if (isValid) {
            resultDisplay.setText(getCalculatedResult());
            inputDisplay.append(" =");
            isCalculationDone = true;
        }
    }

    private void appendToInputDisplay(String value) {
        inputDisplay.append(value);
    }

    private void clearViewDisplays() {
        resultDisplay.setText("");
        inputDisplay.setText("");
    }

    public boolean isOperator(String s) {
        return operators.contains(s);
    }

    public boolean isFunction(String s) {
        return functions.contains(s);
    }
}
