package linebooth;

import java.util.ArrayList;

public class PipelineTransformer<T> {
    private T state;
    private boolean executed;

    private ArrayList<IPipelineAction<T>> actions = new ArrayList<IPipelineAction<T>>();

    public PipelineTransformer(T state) {
        reset(state);
    }

    public void reset(T state) {
        this.state = state;
        executed = false;
    }

    public PipelineTransformer<T> action(IPipelineAction<T> action) {
        actions.add(action);

        return this;
    }

    public T result() {
        if(!executed) {
            for(IPipelineAction<T> actioner : actions) {
                state = actioner.action(state);
            }
            executed = true;
        }

        return state;
    }
}