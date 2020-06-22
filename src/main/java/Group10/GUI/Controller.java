package Group10.GUI;

import Group10.Engine.Callback;
import Group10.Engine.Game;
import Group10.Agents.Container.GuardContainer;
import Group10.Agents.Container.IntruderContainer;
import Group10.Agents.Factories.MainFactory;
import Group10.World.Dynamic.DynamicObject;
import Group10.World.Reader.Reader;
import javafx.animation.AnimationTimer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Controller implements Runnable {

    private final Gui gui;
    private final Game game;

    private AtomicInteger historyViewIndex = new AtomicInteger(-1);

    private int historyIndex = -1;
    private final List<History> history = new LinkedList<>();

    private AnimationTimer animator;
    private final boolean generateHistory;

    public Controller(Gui gui, File mapFile, boolean generateHistory){
        this.gui = gui;
        this.generateHistory = generateHistory;
        game = new Game(Reader.parseFile(mapFile.getAbsolutePath()), new MainFactory(), false, 15, new Callback<Game>() {
            @Override
            public void call(Game game) {
                if(generateHistory){
                    synchronized (history)
                    {
                        historyIndex++;
                        History entry = new History();
                        history.add(historyIndex, entry);

                        entry.guardContainers = game.getGuards().stream().map(e -> e.clone(game)).collect(Collectors.toList());
                        entry.intruderContainers = game.getIntruders().stream().map(e -> e.clone(game)).collect(Collectors.toList());


                        entry.dynamicObjects = game.getGameMap().getDynamicObjects().stream()
                                .map(DynamicObject::clone)
                                .collect(Collectors.toList());

                    }
                }
            }
        });

        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    public Game getGame() {
        return game;
    }

    public History getCurrentHistory() {
        int index = historyViewIndex.get() == -1 ? (historyIndex == history.size() ? historyIndex - 1 : historyIndex) :
                (historyViewIndex.get() == history.size() ? historyViewIndex.get() - 1 : historyViewIndex.get());
        return history.get(index).clone();
    }

    public void kill(){
        game.getRunningLoop().set(false);
        if(animator!=null){
            animator.stop();
        }
    }
    public void updateGameSpeed(int gameSpeed){
        game.getTicks().set(gameSpeed);
    }

    public AtomicInteger getHistoryViewIndex() {
        return historyViewIndex;
    }

    public int getHistoryIndex() {
        return historyIndex;
    }

    @Override
    public void run() {
        animator = new AnimationTimer(){
            @Override
            public void handle(long now){
                if(generateHistory)
                {
                    if(game.getWinner()!=null){
                        gui.activateHistory();
                    }
                    synchronized (history)
                    {
                        if(!history.isEmpty())
                        {
                            History entry = getCurrentHistory();
                            gui.drawMovables(entry.guardContainers, entry.intruderContainers, entry.dynamicObjects);
                        }
                    }
                }
                else
                {
                    game.query((lock) -> {
                        gui.drawMovables(new ArrayList<>(game.getGuards()), new ArrayList<>(game.getIntruders()),
                                new ArrayList<>(game.getGameMap().getDynamicObjects()));
                    }, true);
                }
        }};
        animator.start();
    }

    public static class History implements Cloneable {

        public List<GuardContainer> guardContainers = new ArrayList<>();
        public List<IntruderContainer> intruderContainers = new ArrayList<>();
        public List<DynamicObject<?>> dynamicObjects = new ArrayList<>();

        public History() {}

        @Override
        protected History clone() {
            History history = new History();
            history.guardContainers.addAll(guardContainers);
            history.intruderContainers.addAll(intruderContainers);
            history.dynamicObjects.addAll(dynamicObjects);
            return history;
        }
    }
}