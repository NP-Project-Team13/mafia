public class MafiaRouletteClient {
    private static final int CLIENT_COUNT = 4;

    public static void main(String[] args) {
        for (int i = 0; i < CLIENT_COUNT; i++) {
            new Thread(new MafiaRouletteClientThread()).start();
        }
    }
}
