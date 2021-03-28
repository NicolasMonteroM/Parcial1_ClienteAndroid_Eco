package com.example.parcial1_clienteandroid_eco;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TCPSingleton extends Thread {

    private static TCPSingleton instance;

    public static TCPSingleton getInstance() {
        if (instance == null) {
            instance = new TCPSingleton();
            instance.start();
        }
        return instance;
    }

    private BufferedWriter bwriter;
    private OnMessageListener observer;

    public void setObserver(OnMessageListener observer) {
        this.observer = observer;
    }

    @Override
    public void run() {
        try {
            // <–– PC address -->
            Socket socket = new Socket("10.0.2.2", 5000);

            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            bwriter = new BufferedWriter(osw);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String msg) {

        new Thread(() -> {
            try {
                bwriter.write(msg + "\n");
                bwriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
