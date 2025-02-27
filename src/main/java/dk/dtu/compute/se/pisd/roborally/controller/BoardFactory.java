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
            board = new Board(8,8, DEFAULT_NAME);
        } else {
            board = new Board(8,8, name);
            
            if (name.equals(SIMPLE_BOARD_NAME)) {
                // No obstacles for now
            } else if (name.equals(ADVANCED_BOARD_NAME)) {

                // Obstacles
                Space space = board.getSpace(0,0);
                space.getWalls().add(Heading.SOUTH);
                ConveyorBelt action = new ConveyorBelt();
                action.setHeading(Heading.WEST);
                space.getActions().add(action);

                space = board.getSpace(1,0);
                space.getWalls().add(Heading.NORTH);
                action = new ConveyorBelt();
                action.setHeading(Heading.WEST);
                space.getActions().add(action);
                
                space = board.getSpace(1,1);
                space.getWalls().add(Heading.WEST);
                action = new ConveyorBelt();
                action.setHeading(Heading.NORTH);
                space.getActions().add(action);

                space = board.getSpace(5,5);
                space.getWalls().add(Heading.SOUTH);
                action = new ConveyorBelt();
                action.setHeading(Heading.WEST);
                space.getActions().add(action);

                space = board.getSpace(6,5);
                action = new ConveyorBelt();
                action.setHeading(Heading.WEST);
                space.getActions().add(action);
            }
        }
        return board;
    }

    /**
     * returns list of board names in the form of a list of string 
     * @return an unmodifiable list of available board names
     */
    public static List<String> getAvailableBoardNames() {
        return boardNames;
    }

}
