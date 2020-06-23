package Group10.Agents.GuardMode;

import Interop.Action.Action;
import Interop.Percept.Percepts;

public interface Mode {

    Action getAction(Percepts percepts);

    boolean checkUpdate(Percepts percepts);

    void updateAgent(Percepts percepts);
}
