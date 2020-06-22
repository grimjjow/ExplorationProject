package Group10.Agents.AgentActions;

import Interop.Action.Action;

/**
 * A queue of actions to execute one after another.
 */
public class ActionsQueue<T extends Action> {

    private final String source;
    private final T action;

    public ActionsQueue(String source, T action) {
        this.source = source;
        this.action = action;
    }

    public static <A extends Action> ActionsQueue<A> add(Object source, A action) {
        return add(source.getClass(), action);
    }

    public static <A extends Action> ActionsQueue<A> add(Class<?> source, A action) {
        return add(source.getName(), action);
    }

    public static <A extends Action> ActionsQueue<A> add(String source, A action) {
        return new ActionsQueue<>(source, action);
    }

    public String getSource() {
        return this.source;
    }

    public T getAction() {
        return this.action;
    }

}
