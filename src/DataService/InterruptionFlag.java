package DataService;

// Due to problem associated with interrupt method
public class InterruptionFlag
{
    private boolean isInterrupted;

    InterruptionFlag(boolean isInterrupted)
    {
        this.isInterrupted = isInterrupted;
    }

    public void setFlag()
    {
        isInterrupted = true;
    }

    public boolean getFlag()
    {
        return isInterrupted;
    }
}
