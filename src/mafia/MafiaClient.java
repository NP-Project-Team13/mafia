package mafia;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MafiaClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;

    public MafiaClient(String serverAddress, int serverPort, String nickname) {
        try {
            this.nickname = nickname; // 닉네임 설정
            socket = new Socket(serverAddress, serverPort); // 서버 연결
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 닉네임 전송
            out.println(this.nickname);
            out.flush();
            System.out.println("닉네임 전송 완료: " + this.nickname);
        } catch (IOException e) {
            System.out.println("서버에 연결할 수 없습니다.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {

                String serverMessage = in.readLine();
                if (serverMessage.startsWith("{")) {
                    ServerResponse response = JsonUtil.jsonToResponse(serverMessage);
                    System.out.println("행동: " + response.getAction());
                    System.out.println("결과: " + response.getResult());
                    System.out.println("메시지: " + response.getMessage());
                } else {
                    System.out.println("서버: " + serverMessage);
                }

                if (serverMessage.startsWith("당신의 턴")) {
                    System.out.print("행동을 선택하세요 ('총 쏘기' 또는 '능력 사용'): ");
                    String actionType = scanner.nextLine();
                    String target = null;

                    if ("shoot".equalsIgnoreCase(actionType)) {
                        System.out.print("쏘고 싶은 플레이어의 닉네임을 입력하세요: ");
                        target = scanner.nextLine();
                    }else if ("useAbility".equalsIgnoreCase(actionType)) {
                        // 능력 사용일 경우 타겟 입력(필요할 때만)
                        System.out.print("능력을 사용할 타겟의 닉네임을 입력하세요 (없으면 엔터): ");
                        target = scanner.nextLine();
                        if (target.isEmpty()) target = null;
                    } else {
                        System.out.println("잘못된 행동입니다. 다시 선택해주세요.");
                        continue;
                    }

                    ClientAction action = new ClientAction(actionType, target);
                    out.println(JsonUtil.actionToJson(action));
                }
                else if (serverMessage.startsWith("1)")) {
                    System.out.print("제일 활약이 좋았던 플레이어의 번호를 선택하세요 : ");
                    String voteNum = scanner.nextLine();
                    out.println(voteNum);
                }
            }
        } catch (IOException e) {
            System.out.println("서버와의 연결이 끊어졌습니다.");
            e.printStackTrace();
        }
    }

    private static void printHistory() {
        String filepath = "src/mafia/history.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) { // 한 줄씩 읽기
                String newLine;

                String[] result = line.split(" ");
                String date = result[0];
                String P1 = result[1];
                String P2 = result[2];
                String P3 = result[3];
                String P4 = result[4];
                String winningTeam = result[5];
                String MVP = result[6];

                newLine = i + ") " + date + "   " + "Team A (" + P1 + ", " + P2 + ")   ";
                if(winningTeam.equals("A")) { newLine += "Win : Lose   "; }
                else { newLine += "Lose : Win   ";}
                newLine += "Team B (" + P3 + ", " + P4 + ")   MVP : " + MVP;
                System.out.println(newLine);
                i++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void gameHistory() {
        // 텍스트 파일, 데이터 파일을 읽어 명예의 전당, 업적 출력
        System.out.println("==== 게임 히스토리 ====");
        printHistory();
    }

    private static void showGameRules() {
        System.out.println("==== 게임 규칙 ====");
        System.out.println("4명의 플레이어가 순서대로 번호를 부여받고, 모두 접속 시 게임이 시작됩니다.");
        System.out.println("게임 규칙 열람을 완료했습니다.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("서버 주소를 입력하세요 (기본값: localhost): ");
        String serverAddress = scanner.nextLine();
        if (serverAddress.isEmpty()) serverAddress = "localhost";

        System.out.print("서버 포트를 입력하세요 (기본값: 12345): ");
        String portInput = scanner.nextLine();
        int serverPort = portInput.isEmpty() ? 12345 : Integer.parseInt(portInput);

        System.out.print("닉네임을 입력하세요: ");
        String nickname = scanner.nextLine().trim();

        while (true) {
            System.out.println("==== 메인메뉴 ====");
            System.out.println("1. 게임 참가");
            System.out.println("2. 게임 규칙");
            System.out.println("3. 게임 히스토리");
            System.out.println("4. 게임 종료");
            System.out.print("메뉴 선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // 개행 문자 제거

            switch (choice) {
                case 1:
                    MafiaClient client = new MafiaClient(serverAddress, serverPort, nickname);
                    client.start();
                    break;
                case 2:
                    showGameRules();
                    break;
                case 3:
                    gameHistory();
                    break;
                case 4:
                    System.out.println("게임 종료...");
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 선택하세요.");
            }
        }
    }
}
