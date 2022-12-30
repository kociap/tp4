package checkers.server;

import checkers.Dimensions2D;
import java.util.Iterator;
import java.util.List;
import checkers.Point;
import checkers.CommandBuilder;
import checkers.Piece;
import checkers.SocketWrapper;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Server
// Listens on localhost port 8080.
//
public class Server {
    private Engine engine;
    private ServerSocket socket;
    private Coordinator coordinator;

    private ClientThread clientRed;
    private ClientThread clientWhite;

    private Lock engineLock = new ReentrantLock();

    public Server(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public boolean run() {
        try {
            // TODO: Hardcoded port.
            socket = new ServerSocket(8080);
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    public void notifyEngineSelected(Engine engine) {
        this.engine = engine;
        try {
            // TODO: Move to thread;
            clientRed =
                new ClientThread(this, new SocketWrapper(socket.accept()));
            clientRed.start();
            sendHello(clientRed, Piece.Color.red);
            coordinator.notifyClientConnected();
            // clientWhite = new SocketWrapper(socket.accept());
            // sendHello(clientWhite, Piece.Color.white);
            // coordinator.notifyClientConnected();
        } catch(Exception e) {
            return;
        }
    }

    public Dimensions2D getBoardSize() {
        engineLock.lock();
        try {
            return engine.getBoardSize();
        } catch(Exception e) {
            return null;
        } finally {
            engineLock.unlock();
        }
    }

    public Iterator<Piece> listPieces() {
        engineLock.lock();
        try {
            return engine.listPieces();
        } catch(Exception e) {
            return null;
        } finally {
            engineLock.unlock();
        }
    }

    public List<Point> listMoves(Piece piece) {
        engineLock.lock();
        try {
            return engine.listMoves(piece);
        } catch(Exception e) {
            return null;
        } finally {
            engineLock.unlock();
        }
    }

    private void sendHello(ClientThread client, Piece.Color color) {
        final CommandBuilder builder = new CommandBuilder();
        builder.command("hello").parameter(color);
        client.sendCommand(builder.finalise());
    }
}