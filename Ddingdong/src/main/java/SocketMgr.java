import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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

                System.out.print("응답: ");
                Scanner scanner = new Scanner(System.in);
                String in = scanner.nextLine();
                byte[] in_bytes = in.getBytes();

                OutputStream output = socket.getOutputStream();
                output.write(in_bytes);
            }
        } catch (SocketException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
