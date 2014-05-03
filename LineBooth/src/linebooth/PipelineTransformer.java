package linebooth;

import java.util.ArrayList;

public class PipelineTransformer<T> {
    private ArrayList<IPipelineAction<T>> actions = new ArrayList<IPipelineAction<T>>();

    public PipelineTransformer<T> action(IPipelineAction<T> action) {
        actions.add(action);

        return this;
    }

    public T result(T state) {
        for(IPipelineAction<T> actioner : actions) {
            actioner.action(state);
        }

        return state;
    }
}