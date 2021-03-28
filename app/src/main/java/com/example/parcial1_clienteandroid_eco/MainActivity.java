package com.example.parcial1_clienteandroid_eco;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements OnMessageListener {

    private Button confirmBtn, previewBtn;
    private TextView reminderTxtInput, xPosInput, yPosInput;
    private View lowImpBtn, mediumImpBtn, highImpBtn;
    private int impLevel;

    private TCPSingleton tcpSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        confirmBtn = findViewById(R.id.confirmBtn);
        previewBtn = findViewById(R.id.previewBtn);
        reminderTxtInput = findViewById(R.id.reminderTxtInput);
        xPosInput = findViewById(R.id.xPosInput);
        yPosInput = findViewById(R.id.yPosInput);
        lowImpBtn = findViewById(R.id.lowImpBtn);
        mediumImpBtn = findViewById(R.id.mediumImpBtn);
        highImpBtn = findViewById(R.id.highImpBtn);

        tcpSingleton = TCPSingleton.getInstance();
        tcpSingleton.setObserver(this);

        setImportance();

        confirmBtn.setOnClickListener(
                (v) -> {
                    if (validateCompletion()) {
                        sendInfo(0);
                        clearReminderForm();
                    }
                }
        );

        previewBtn.setOnClickListener(
                (v) -> {
                    if (validateCompletion()) {
                        // Sending "1" to make a preview on server
                        sendInfo(1);
                    }
                }
        );
    }

    public void sendInfo(int i) {

        String reminder = reminderTxtInput.getText().toString();
        int posX = Integer.parseInt(xPosInput.getText().toString());
        int posY = Integer.parseInt(yPosInput.getText().toString());

        // The "i" states if it's a preview or not
        String reminderInfo = posX + "," + posY + "," + reminder + "," + impLevel + "," + i;

        Log.e("Reminder ", reminderInfo);
        Gson gson = new Gson();

        String msg = gson.toJson(reminderInfo);

        tcpSingleton.sendMessage(msg);
    }

    public void setImportance() {

        lowImpBtn.setOnClickListener(
                (v) -> {
                    impLevel = 1;
                }
        );

        mediumImpBtn.setOnClickListener(
                (v) -> {
                    impLevel = 2;
                }
        );

        highImpBtn.setOnClickListener(
                (v) -> {
                    impLevel = 3;
                }
        );

        setColor();
    }

    public void setColor() {

        new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(
                        () -> {
                            if (impLevel == 1) {
                                lowImpBtn.setBackgroundResource(R.drawable.greenbutton_active);
                            } else if (impLevel != 1) {
                                lowImpBtn.setBackgroundResource(R.drawable.greenbutton_inactive);
                            }
                            if (impLevel == 2) {
                                mediumImpBtn.setBackgroundResource(R.drawable.yellowbutton_active);
                            } else if (impLevel != 2) {
                                mediumImpBtn.setBackgroundResource(R.drawable.yellowbutton_inactive);
                            }
                            if (impLevel == 3) {
                                highImpBtn.setBackgroundResource(R.drawable.redbutton_active);
                            } else if (impLevel != 3) {
                                highImpBtn.setBackgroundResource(R.drawable.redbutton_inactive);
                            }
                        });
            }
        }).start();

    }

    public boolean validateCompletion() {

        boolean validate;

        if (reminderTxtInput.getText().toString().isEmpty() || xPosInput.getText().toString().isEmpty() || yPosInput.getText().toString().isEmpty() || impLevel == 0) {
            validate = false;
            Toast.makeText(this, "Llene todos los campos para poder continuar", Toast.LENGTH_SHORT).show();
        } else {
            validate = true;
        }

        return validate;
    }

    public void clearReminderForm() {

        reminderTxtInput.setText("");
        xPosInput.setText("");
        yPosInput.setText("");
        impLevel = 0;

    }

    @Override
    public void OnMessage(String msg) {
        runOnUiThread(
                () -> {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
        );
    }
}