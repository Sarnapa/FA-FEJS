package DataService;

/**
 * Own interruption flag due to problem associated with built-in mechanism
 */

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
