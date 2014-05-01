package linebooth;

public interface IPipelineAction<T> {
    /**
     * Perform an action on a pipline state. It does not have to make a copy of the state.
     * @return [description]
     */
    public T action(T state);
}