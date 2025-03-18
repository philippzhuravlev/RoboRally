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
     * Creates a new game board based on the specified board name.
     * The board name determines the type and layout of the board,
     * including obstacles, conveyor belts, walls, and checkpoints.
     *
     * <p>If the name is {@code null}, a default 8x8 board is created.</p>
     *
     * <p>Available board options:</p>
     * <ul>
     *     <li>{@code SIMPLE_BOARD_NAME} - An 8x8 board with a basic layout of conveyor belts, walls, and checkpoints.</li>
     *     <li>{@code ADVANCED_BOARD_NAME} - A 15x8 board with additional obstacles, complex conveyor belt paths, and multiple checkpoints.</li>
     *     <li>Any other name - Creates an empty 8x8 board.</li>
     * </ul>
     *
     * @param name the name of the board type to create
     * @return the generated {@link Board} instance corresponding to the given name
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

    /**
     * Adds a conveyor belt to the specified space on the board.
     *
     * @param board   the game board
     * @param x       the x-coordinate of the space
     * @param y       the y-coordinate of the space
     * @param heading the direction the conveyor belt moves
     */
    private void addConveyorBelt(Board board, int x, int y, Heading heading) {
        Space space = board.getSpace(x, y);
        ConveyorBelt action = new ConveyorBelt();
        action.setHeading(heading);
        space.getActions().add(action);}

    /**
     * Adds walls to a specific space on the board in the given directions.
     *
     * @param board    the game board
     * @param x        the x-coordinate of the space
     * @param y        the y-coordinate of the space
     * @param headings the directions in which walls should be placed
     */
    private void addWalls(Board board, int x, int y, Heading... headings) {
        Space space = board.getSpace(x, y);
        for (Heading heading : headings) {
            space.getWalls().add(heading);
        }
    }

    /**
     * Places a checkpoint on the specified space of the board.
     *
     * @param board            the game board
     * @param x                the x-coordinate of the space
     * @param y                the y-coordinate of the space
     * @param checkpointNumber the number assigned to the checkpoint
     * @param isFinalCheckpoint whether this is the final checkpoint
     */
    private void addCheckpoints(Board board, int x, int y, int checkpointNumber, boolean isFinalCheckpoint) {
        Space space = board.getSpace(x, y);
        space.getActions().add(new CheckPoint(checkpointNumber, isFinalCheckpoint));
    }

}
