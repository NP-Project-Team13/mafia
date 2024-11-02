import java.net.Socket;
import java.io.*;
import java.util.*;

public class MafiaRouletteClientThread implements Runnable {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    @Override
    public void run() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Connected to the game server.");

            // 서버로부터 메시지 수신 및 처리
            while (true) {
                String serverMessage = in.readLine();
                if (serverMessage == null) break; // 서버가 연결을 끊음
                System.out.println(serverMessage);

                // "Choose a player to shoot" 메시지를 수신하면 입력 대기
                if (serverMessage.contains("Choose a player to shoot")) {
                    int target = scanner.nextInt();
                    out.println(target); // 선택한 타겟을 서버로 전송
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
