package Group10;

import Group10.Agents.Factories.MainFactory;
import Group10.Engine.Callback;
import Group10.Engine.Game;
import Group10.World.Reader.Reader;

import java.io.File;

public class Performance {
    private static File mapFile = new File("src/main/java/Group10/World/Maps/test_3.map");
    private static Game game;
    private static boolean[] intruderWin;
    private static int[] turns;
    private static int numberOfIntruderWins;
    private static int numberOfGuardWins;
    private static int totalIntruderTurn;
    private static int totalGuardTurn;

    public static void main(String[] args) {
        int iteration = 10;
        intruderWin = new boolean[iteration];
        turns = new int[iteration];

        for (int i = 0; i < iteration; i++) {
            System.out.println("Iteration #" + i);
            game = new Game(Reader.parseFile(mapFile.getAbsolutePath()), new MainFactory(), false, 15, new Callback<Game>() {
                @Override
                public void call(Game v) {
                }
            });
            Thread thread = new Thread(game);
            thread.start();
            while (thread.isAlive()) {
            }
            System.out.println("Finish with winner: " + game.getWinner() + " in " + game.getNumberOfTurn() + " turns.");

            turns[i] = game.getNumberOfTurn();
            intruderWin[i] = game.getWinner() == Game.Team.INTRUDERS;

            if (intruderWin[i]) {
                numberOfIntruderWins++;
                totalIntruderTurn += turns[i];
            } else {
                numberOfGuardWins++;
                totalGuardTurn += turns[i];
            }
        }

        System.out.println("\nAfter " + iteration + " iterations:");
        System.out.println("\tIntruders have wins " + numberOfIntruderWins + " games");
        if (numberOfIntruderWins > 0)
            System.out.println("\twith an average of " + totalIntruderTurn / numberOfIntruderWins + " turns.");
        System.out.println("\tGuards have wins " + numberOfGuardWins + " games");
        if (numberOfGuardWins > 0)
            System.out.println("\twith an average of " + totalGuardTurn / numberOfGuardWins + " turns.");
    }
}
