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
                // No obstacles for now

                // Checkpoints
                Space space = board.getSpace(4, 0);
                space.getActions().add(new CheckPoint(1, false));

                space = board.getSpace(5, 0);
                space.getActions().add(new CheckPoint(2, false));

                space = board.getSpace(6, 0);
                space.getActions().add(new CheckPoint(3, true));

            } else if (name.equals(ADVANCED_BOARD_NAME)) {
                board = new Board(15, 8, name);

                // Obstacles
                Space space = board.getSpace(0, 0);
                space.getWalls().add(Heading.SOUTH);
                ConveyorBelt action = new ConveyorBelt();
                action.setHeading(Heading.WEST);
                space.getActions().add(action);

                space = board.getSpace(1, 0);
                space.getWalls().add(Heading.NORTH);
                action = new ConveyorBelt();
                action.setHeading(Heading.WEST);
                space.getActions().add(action);

                space = board.getSpace(1, 1);
                space.getWalls().add(Heading.WEST);
                action = new ConveyorBelt();
                action.setHeading(Heading.NORTH);
                space.getActions().add(action);

                space = board.getSpace(5, 5);
                space.getWalls().add(Heading.SOUTH);
                action = new ConveyorBelt();
                action.setHeading(Heading.WEST);
                space.getActions().add(action);

                space = board.getSpace(6, 5);
                action = new ConveyorBelt();
                action.setHeading(Heading.WEST);
                space.getActions().add(action);

                // Checkpoints
                space = board.getSpace(4, 0);
                space.getActions().add(new CheckPoint(1, false));

                space = board.getSpace(5, 0);
                space.getActions().add(new CheckPoint(2, false));

                space = board.getSpace(6, 0);
                space.getActions().add(new CheckPoint(3, true));
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

}
