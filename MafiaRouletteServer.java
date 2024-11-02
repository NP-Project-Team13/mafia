import java.io.*;
import java.net.*;
import java.util.Random;

public class MafiaRouletteServer {
    private static final int PORT = 12345;
    private static final int HEALTH = 3;
    private static final int CYLINDER_SIZE = 5;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for players...");

            Socket[] players = new Socket[4];
            BufferedReader[] inStreams = new BufferedReader[4];
            PrintWriter[] outStreams = new PrintWriter[4];

            // 4명의 플레이어 접속 대기
            for (int i = 0; i < players.length; i++) {
                players[i] = serverSocket.accept();
                inStreams[i] = new BufferedReader(new InputStreamReader(players[i].getInputStream()));
                outStreams[i] = new PrintWriter(players[i].getOutputStream(), true);
                outStreams[i].println("Welcome Player " + (i + 1) + "!");
                System.out.println("Player " + (i + 1) + " connected.");
            }

            int bulletPosition = new Random().nextInt(CYLINDER_SIZE);
            int[] playerHealth = {HEALTH, HEALTH, HEALTH, HEALTH};

            // 팀 나누기: 0과 1은 팀 A, 2와 3은 팀 B
            int[][] teams = {{0, 1}, {2, 3}};
            int round = 1;

            // 게임 루프
            while (true) {
                System.out.println("Round " + round + " 시작!");

                // 라운드 진행: 각 플레이어의 턴
                for (int i = 0; i < players.length; i++) {
                    if (playerHealth[i] <= 0) continue; // 체력이 0인 플레이어는 스킵

                    System.out.println("Player " + (i + 1) + "의 턴 시작");
                    outStreams[i].println("It's your turn! Choose a player to shoot (1-4):");
                    int target = Integer.parseInt(inStreams[i].readLine()) - 1;
                    System.out.println("플레이어 " + (i + 1) + "가 플레이어 " + (target + 1) + "에게 총을 쏘기 시도 중...");

                    // 유효한 타겟인지 확인
                    if (target < 0 || target >= players.length || playerHealth[target] <= 0 || target == i) {
                        outStreams[i].println("Invalid target. Turn skipped.");
                        continue;
                    }

                    int shootResult = new Random().nextInt(CYLINDER_SIZE);
                    if (shootResult == bulletPosition) {
                        playerHealth[target]--;
                        outStreams[target].println("You've been shot! Health: " + playerHealth[target]);
                        outStreams[i].println("You shot Player " + (target + 1) + "!");
                        System.out.println("플레이어 " + (i + 1) + "가 플레이어 " + (target + 1) + "에게 총을 맞췄습니다!");

                        if (playerHealth[target] <= 0) {
                            outStreams[target].println("You are dead.");
                            System.out.println("Player " + (target + 1) + " is out of the game.");
                        }
                    } else {
                        outStreams[i].println("Missed the shot.");
                        outStreams[target].println("Player " + (i + 1) + " tried to shoot you but missed.");
                        System.out.println("플레이어 " + (i + 1) + "가 플레이어 " + (target + 1) + "에게 총을 빗맞췄습니다.");
                    }
                    System.out.println("Player " + (i + 1) + "의 턴 종료");
                }

                // 생존 팀 확인
                int[] teamStatus = {0, 0}; // 0번 팀과 1번 팀의 생존자 수를 셈
                for (int i = 0; i < teams.length; i++) {
                    for (int player : teams[i]) {
                        if (playerHealth[player] > 0) {
                            teamStatus[i]++;
                        }
                    }
                }

                // 팀이 전멸했는지 확인
                if (teamStatus[0] == 0 || teamStatus[1] == 0) {
                    String winningTeam = (teamStatus[0] > 0) ? "Team A" : "Team B";
                    System.out.println(winningTeam + "이(가) 승리했습니다!");
                    for (int j = 0; j < players.length; j++) {
                        if (playerHealth[j] > 0) {
                            outStreams[j].println("Congratulations! Your team is the last surviving team.");
                        }
                        players[j].close();
                    }
                    System.out.println("Game over.");
                    return;
                }

                System.out.println("Round " + round + " 종료");
                round++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
