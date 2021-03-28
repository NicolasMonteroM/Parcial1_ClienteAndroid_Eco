package com.example.parcial1_clienteandroid_eco;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements OnMessageListener {

    private Button confirmBtn, previewBtn;
    private TextView reminderTxtInput, xPosInput, yPosInput;
    private View lowImpBtn, mediumImpBtn, highImpBtn;
    private boolean previewMode;
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
                    createReminder();
                }
        );

        previewBtn.setOnClickListener(
                (v) -> {
                    createPreview();
                }
        );
    }

    // <–– Get and send reminder's info to create a preview ––>
    public void createPreview() {

        previewMode = true;

        if (validateCompletion() && previewMode) {
            String reminder = reminderTxtInput.getText().toString();
            int posX = Integer.parseInt(xPosInput.getText().toString());
            int posY = Integer.parseInt(yPosInput.getText().toString());
            String reminderInfo = posX + "," + posY + "," + reminder + "," + impLevel + "," + "1";

            Log.e("Reminder ", reminderInfo);
            Gson gson = new Gson();

            String msg = gson.toJson(reminderInfo);

            tcpSingleton.sendMessage(msg);
        }
    }

    // <–– Get and send reminder's info to make a reminder ––>
    public void createReminder() {

        previewMode = false;

        if (validateCompletion() && previewMode == false) {
            String reminder = reminderTxtInput.getText().toString();
            int posX = Integer.parseInt(xPosInput.getText().toString());
            int posY = Integer.parseInt(yPosInput.getText().toString());

            String reminderInfo = posX + "," + posY + "," + reminder + "," + impLevel + "," + "0";

            Log.e("Reminder ", reminderInfo);
            Gson gson = new Gson();

            String msg = gson.toJson(reminderInfo);

            tcpSingleton.sendMessage(msg);
            clearReminderForm();

        }
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
                if (impLevel == 0) {
                    runOnUiThread(
                            () -> {
                                lowImpBtn.setBackgroundResource(R.drawable.greenbutton_inactive);
                                mediumImpBtn.setBackgroundResource(R.drawable.yellowbutton_inactive);
                                highImpBtn.setBackgroundResource(R.drawable.redbutton_inactive);
                            }
                    );
                } else if (impLevel == 1) {
                    runOnUiThread(
                            () -> {
                                lowImpBtn.setBackgroundResource(R.drawable.greenbutton_active);
                                mediumImpBtn.setBackgroundResource(R.drawable.yellowbutton_inactive);
                                highImpBtn.setBackgroundResource(R.drawable.redbutton_inactive);
                            }
                    );
                } else if (impLevel == 2) {

                    runOnUiThread(
                            () -> {
                                lowImpBtn.setBackgroundResource(R.drawable.greenbutton_inactive);
                                mediumImpBtn.setBackgroundResource(R.drawable.yellowbutton_active);
                                highImpBtn.setBackgroundResource(R.drawable.redbutton_inactive);
                            }
                    );

                } else if (impLevel == 3) {
                    runOnUiThread(
                            () -> {
                                lowImpBtn.setBackgroundResource(R.drawable.greenbutton_inactive);
                                mediumImpBtn.setBackgroundResource(R.drawable.yellowbutton_inactive);
                                highImpBtn.setBackgroundResource(R.drawable.redbutton_active);
                            }
                    );
                }
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