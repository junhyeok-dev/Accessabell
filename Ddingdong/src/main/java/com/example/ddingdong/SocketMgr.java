package com.example.ddingdong;

import com.google.cloud.speech.v1.*;
import com.google.common.primitives.Bytes;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SocketMgr extends Thread {
    ServerSocket serverSocket;
    int portnum;
    TTSModule ttsModule;

    public SocketMgr(int num) {
        portnum = num;

        ttsModule = new TTSModule();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(portnum);
            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            while (true) {
                InputStream input = socket.getInputStream();
                byte[] data = new byte[1024];
                input.read(data);

                System.out.println(new String(data));
                ttsModule.tts(new String(data));

                AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
                TargetDataLine mic;
                String sttresult = "";

                boolean stt_succeed = false;
                while (!stt_succeed) {
                    try {
                        mic = AudioSystem.getTargetDataLine(format);

                        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                        mic = (TargetDataLine) AudioSystem.getLine(info);
                        mic.open(format);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        int numBytesRead;
                        int bytesRead = 0;
                        int CHUNK_SIZE = 1024;
                        byte[] audiodat = new byte[mic.getBufferSize() / 5];
                        mic.start();
                        System.out.println("지금 말하세요.");

                        while (bytesRead < 200000) {
                            numBytesRead = mic.read(audiodat, 0, CHUNK_SIZE);
                            bytesRead += numBytesRead;
                            out.write(audiodat, 0, numBytesRead);
                        }

                        System.out.println(Arrays.toString(audiodat));

                        mic.close();

                        byte[] audioByte = out.toByteArray();

                        List<SpeechRecognitionResult> results = stt(audioByte);

                        System.out.println("Number of results: " + results.size());

                        for (SpeechRecognitionResult result: results) {
                            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                            System.out.println(alternative.getTranscript());
                        }

                        if (results.size() != 0) {
                            sttresult = results.get(0).getAlternativesList().get(0).getTranscript();
                            stt_succeed = true;
                        } else {
                            System.out.println("알아듣지 못했습니다. 다시 말씀해주세요.");
                        }

                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }

                OutputStream output = socket.getOutputStream();
                output.write(sttresult.getBytes());
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<SpeechRecognitionResult> stt(byte[] audio) {
        List<SpeechRecognitionResult> results = new ArrayList<SpeechRecognitionResult>();

        try {
            SpeechClient speechClient = SpeechClient.create();

            ByteString audioBytes = ByteString.copyFrom(audio);
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("ko-KR")
                    .build();
            RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder().
                    setContent(audioBytes).build();

            System.out.println("STT configuration finished.");

            RecognizeResponse response = speechClient.recognize(config, recognitionAudio);

            System.out.println("Get response from GCP");

            results = response.getResultsList();

            speechClient.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }
}
