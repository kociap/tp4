package checkers.client;

import checkers.Dimensions2D;
import checkers.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.layout.Pane;

public class Board extends Pane {
    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();
    private final List<ClientPiece> pieces = new ArrayList<>();
    private int size = 0;
    private final RequestService requester;
    private Dimensions2D dimensions = null;

    public Board(RequestService requester) {
        this.requester = requester;
        getChildren().addAll(tileGroup, pieceGroup);
    }

    public void initialise(final Dimensions2D dimensions, final int size) {
        this.size = size;
        this.dimensions = dimensions;

        setPrefSize(dimensions.width * size, dimensions.height * size);

        tileGroup.getChildren().clear();
        pieceGroup.getChildren().clear();
        pieces.clear();

        for(int y = 0; y < dimensions.height; y++) {
            for(int x = 0; x < dimensions.width; x++) {
                final Tile tile = new Tile(x, y, size);
                tile.setColor((x + y) % 2 == 0 ? ColorPalette.squareLight
                                               : ColorPalette.squareDark);
                tileGroup.getChildren().add(tile);
            }
        }
    }

    public void addPiece(final ClientPiece piece) {
        pieces.add(piece);
        pieceGroup.getChildren().add(piece);

        piece.resize(size);
        relocatePiece(piece);

        piece.setOnMousePressed(e -> {
            // Move the piece to the front of the scenegraph in order to make it
            // appear above all other pieces while it's being dragged.
            piece.toFront();
            piece.relocate(e.getSceneX() - size / 2, e.getSceneY() - size / 2);
        });

        piece.setOnMouseDragged(e -> {
            piece.relocate(e.getSceneX() - size / 2, e.getSceneY() - size / 2);
        });

        piece.setOnMouseReleased(e -> {
            final Point targetPosition =
                getCoordinatesUnderCursor(e.getSceneX(), e.getSceneY());
            requester.requestMove(piece.getID(), targetPosition);
        });
    }

    public void handleRequestMove(final int pieceID, final Point position) {
        final ClientPiece piece = findPieceWithID(pieceID);
        if(piece == null) {
            return;
        }

        if(position.x == -1 || position.y == -1) {
            relocatePiece(piece);
        } else {
            piece.setPosition(position);
            relocatePiece(piece);
        }
    }

    public void handleRequestTake(final int pieceID) {
        final ClientPiece piece = findPieceWithID(pieceID);
        if(piece == null) {
            return;
        }

        pieces.remove(piece);
        pieceGroup.getChildren().remove(piece);
    }

    public void handleRequestPromote(final int pieceID) {
        final ClientPiece piece = findPieceWithID(pieceID);
        if(piece == null) {
            return;
        }

        piece.promote();
    }

    private ClientPiece findPieceWithID(final int pieceID) {
        for(final ClientPiece piece: pieces) {
            if(piece.getID() == pieceID) {
                return piece;
            }
        }
        return null;
    }

    private Point getCoordinatesUnderCursor(final double x, final double y) {
        final int cx = (int)(x / size);
        final int cy = (int)(y / size);
        return new Point(cx, cy);
    }

    private void relocatePiece(final ClientPiece piece) {
        final Point position = piece.getPosition();
        piece.relocate(size * position.x, size * position.y);
    }
}
