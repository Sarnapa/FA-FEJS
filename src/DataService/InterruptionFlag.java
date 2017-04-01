package DataService;

// Due to problem associated with interrupt method
class InterruptionFlag {
    private boolean isInterrupted;

    InterruptionFlag(boolean isInterrupted) {
        this.isInterrupted = isInterrupted;
    }

    void setFlag() {
        isInterrupted = true;
    }

    boolean getFlag() {
        return isInterrupted;
    }
}
