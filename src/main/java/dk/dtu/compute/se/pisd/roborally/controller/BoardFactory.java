package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import java.util.List;

/**
 * A factory for creating boards. The factory itself is implemented as a singleton.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3: might be used for creating a first slightly more interesting board.
public class BoardFactory {

    // BOARD NAMES
    static String DEFAULT_NAME = "<none>";
    static String SIMPLE_BOARD_NAME = "simple";
    static String ADVANCED_BOARD_NAME = "advanced";
    static List<String> boardNames = List.of(SIMPLE_BOARD_NAME, ADVANCED_BOARD_NAME); // We'll use this shortcut from the slides


    /**
     * The single instance of this class, which is lazily instantiated on demand.
     */
    static private BoardFactory instance = null;

    /**
     * Constructor for BoardFactory. It is private in order to make the factory a singleton.
     */
    private BoardFactory() {
    }

    /**
     * Returns the single instance of this factory. The instance is lazily
     * instantiated when requested for the first time.
     *
     * @return the single instance of the BoardFactory
     */
    public static BoardFactory getInstance() {
        if (instance == null) {
            instance = new BoardFactory();
        }
        return instance;
    }

    /**
     * Creates a new board of given name of a board, which indicates
     * which type of board should be created. For now the name is ignored.
     *
     * @param name the given name board
     * @return the new board corresponding to that name
     */
    public Board createBoard(String name) {
        Board board;
        if (name == null) {
            board = new Board(8, 8, DEFAULT_NAME);
        } else {
            if (name.equals(SIMPLE_BOARD_NAME)) {
                board = new Board(8, 8, name);
                
                // CONVEYOR BELTS
                for (int y = 2; y <= 5; y++) { // Left
                    addConveyorBelt(board, 0, y, Heading.SOUTH);
                }
                for (int y = 2; y <= 5; y++) { // Right
                    addConveyorBelt(board, 7, y, Heading.NORTH);
                }
                for (int x = 2; x <= 5; x++) { // Top
                    addConveyorBelt(board, x, 0, Heading.WEST);
                }
                for (int x = 2; x <= 5; x++) { // Bottom
                    addConveyorBelt(board, x, 7, Heading.EAST);
                }
                
                // WALLS
                addWalls(board, 2, 2, Heading.SOUTH, Heading.EAST);
                addWalls(board, 2, 5, Heading.NORTH, Heading.EAST);
                addWalls(board, 5, 2, Heading.WEST, Heading.SOUTH);
                addWalls(board, 5, 5, Heading.NORTH, Heading.WEST);

                // CHECKPOINTS 
                addCheckpoints(board, 1, 6, 1, false);
                addCheckpoints(board, 6, 6, 2, false);
                addCheckpoints(board, 6, 1, 3, true);

            } else if (name.equals(ADVANCED_BOARD_NAME)) {
                board = new Board(15, 8, name);
                Space space;

                // Walls (More obstacles across the board)
                addWalls(board, 3, 0, Heading.SOUTH);
                addWalls(board, 0, 1, Heading.NORTH);
                addWalls(board, 4, 3, Heading.WEST);
                addWalls(board, 9, 2, Heading.WEST);
                addWalls(board, 6, 6, Heading.NORTH);
                addWalls(board, 2, 5, Heading.EAST);
                addWalls(board, 7, 2, Heading.SOUTH);
                addWalls(board, 8, 4, Heading.WEST);
                addWalls(board, 10, 6, Heading.NORTH);
                addWalls(board, 12, 3, Heading.SOUTH);

                // Conveyor Belts (Rearranged to avoid specified positions)
                addConveyorBelt(board, 0, 6, Heading.NORTH);
                addConveyorBelt(board, 0, 5, Heading.NORTH);
                addConveyorBelt(board, 4, 5, Heading.EAST);
                addConveyorBelt(board, 3, 4, Heading.EAST);
                addConveyorBelt(board, 9, 3, Heading.NORTH);
                addConveyorBelt(board, 9, 4, Heading.NORTH);
                addConveyorBelt(board, 11, 6, Heading.WEST);
                addConveyorBelt(board, 13, 2, Heading.SOUTH);
                addConveyorBelt(board, 13, 3, Heading.SOUTH);
                addConveyorBelt(board, 13, 4, Heading.SOUTH);
                addConveyorBelt(board, 13, 5, Heading.SOUTH);
                addConveyorBelt(board, 13, 6, Heading.SOUTH);

                // Checkpoints (Rearranged to avoid specified positions)
                space = board.getSpace(9, 2);
                space.getActions().add(new CheckPoint(1, false));

                space = board.getSpace(5, 3);
                space.getActions().add(new CheckPoint(2, false));

                space = board.getSpace(7, 6);
                space.getActions().add(new CheckPoint(3, false));

                space = board.getSpace(13, 7);
                space.getActions().add(new CheckPoint(4, true));

            } else {
                board = new Board(8, 8, name);
            }
        }
        return board;
    }

    /**
     * Returns a list of board names in the form of a list of string
     * @return an unmodifiable list of available board names
     */
    public static List<String> getAvailableBoardNames() {
        return boardNames;
    }

    private void addConveyorBelt(Board board, int x, int y, Heading heading) {
        Space space = board.getSpace(x, y);
        ConveyorBelt action = new ConveyorBelt();
        action.setHeading(heading);
        space.getActions().add(action);}

    private void addWalls(Board board, int x, int y, Heading... headings) {
        Space space = board.getSpace(x, y);
        for (Heading heading : headings) {
            space.getWalls().add(heading);
        }
    }

    private void addCheckpoints(Board board, int x, int y, int checkpointNumber, boolean isFinalCheckpoint) {
        Space space = board.getSpace(x, y);
        space.getActions().add(new CheckPoint(checkpointNumber, isFinalCheckpoint));
    }

}
