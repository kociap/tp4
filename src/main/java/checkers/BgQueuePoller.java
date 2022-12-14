package checkers;

import javafx.animation.AnimationTimer;

public class BgQueuePoller extends AnimationTimer {
    private final CommandReceiver receiver;
    private final CommandQueue queue;
    private final int socketID;

    public BgQueuePoller(int socketID, CommandReceiver receiver,
                         CommandQueue queue) {
        this.socketID = socketID;
        this.receiver = receiver;
        this.queue = queue;
    }

    @Override
    public void handle(long now) {
        if(queue.poll()) {
            final String command = queue.pop();
            receiver.receiveCommand(socketID, command);
        }
    }
}