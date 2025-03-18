package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.exceptions.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    /**
     * Test for Assignment V1 (can be deleted later once V1 was shown to the teacher)
     */
    @Test
    void testV1() {
        Board board = gameController.board;

        Player player = board.getCurrentPlayer();
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName() + " should be on Space (0,4)!");
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void testMoveForwardWithWall() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.getSpace().getWalls().add(Heading.SOUTH);
        gameController.moveForward(player);
        assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not have moved due to wall");
    }

    @Test
    void testFastForward() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        gameController.fastForward(player);
        assertEquals(board.getSpace(0, 2), player.getSpace(), "Player should have moved forward to (0,2)");
    }

    @Test
    void testFastForwardWithWall() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.getSpace().getWalls().add(Heading.SOUTH);
        gameController.fastForward(player);
        assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not have moved due to wall");
    }

    @Test
    void testTurnRight() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        gameController.turnRight(player);
        assertEquals(Heading.WEST, player.getHeading(), "Player should have turned right to face WEST");
    }

    @Test
    void testTurnLeft() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        gameController.turnLeft(player);
        assertEquals(Heading.EAST, player.getHeading(), "Player should have turned left to face EAST");
    }

    @Test
    void testMoveForwardOutOfBounds() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.setSpace(board.getSpace(0, TEST_HEIGHT - 1)); // Place player at the bottom edge
        player.setHeading(Heading.SOUTH); // Set heading to SOUTH

        gameController.moveForward(player);

        assertEquals(board.getSpace(0, TEST_HEIGHT - 1), player.getSpace(), "Player should not move out of bounds");
    }

    @Test
    void testFastForwardOutOfBounds() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.setSpace(board.getSpace(0, TEST_HEIGHT - 2)); // Place player near the bottom edge
        player.setHeading(Heading.SOUTH); // Set heading to SOUTH

        gameController.fastForward(player);

        // The player should move one space forward and stop at (0, TEST_HEIGHT - 1)
        assertEquals(board.getSpace(0, TEST_HEIGHT - 1), player.getSpace(), "Player should move one space forward and stop at the edge");
    }

    @Test
    void testUTurn() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        gameController.turnU(player);
        assertEquals(Heading.NORTH, player.getHeading(), "Player should have turned around to face NORTH");
    }

    @Test
    void testBackwards() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.setSpace(board.getSpace(0, 1)); // Set initial position
        player.setHeading(Heading.SOUTH); // Set heading to SOUTH

        gameController.turnRight(player);
        gameController.turnRight(player);
        gameController.moveForward(player);

        assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should have moved backwards to (0,0)");
    }

    @Test
    void testBackwardsWithWall() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.getSpace().getWalls().add(Heading.NORTH);
        gameController.moveBackward(player);
        assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not have moved backwards due to wall");
    }

    @Test
    void testBackwardsOutOfBounds() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.setSpace(board.getSpace(0, 0)); // Set initial position
        player.setHeading(Heading.SOUTH); // Set heading to SOUTH

        gameController.moveBackward(player);

        assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not move out of bounds");
    }

    @Test
    void testPushSingleRobot() {
        Board board = gameController.board;
        Player pusher = board.getPlayer(0);
        Player pushed = board.getPlayer(1);

        // Set up players in a row, pusher behind pushed
        pusher.setSpace(board.getSpace(2, 0));
        pushed.setSpace(board.getSpace(3, 0));
        pusher.setHeading(Heading.EAST);

        // Move pusher forward (should push the other player)
        gameController.moveForward(pusher);

        assertEquals(board.getSpace(4, 0), pushed.getSpace(), "Pushed robot should be at (4,0)");
        assertEquals(board.getSpace(3, 0), pusher.getSpace(), "Pusher should be at (3,0)");
    }

    @Test
    void testPushMultipleRobots() {
        Board board = gameController.board;
        Player pusher = board.getPlayer(0);
        Player pushed1 = board.getPlayer(1);
        Player pushed2 = board.getPlayer(2);

        // Set up three players in a row
        pusher.setSpace(board.getSpace(1, 0));
        pushed1.setSpace(board.getSpace(2, 0));
        pushed2.setSpace(board.getSpace(3, 0));

        // Ensure the pusher is facing EAST (towards the pushed players)
        pusher.setHeading(Heading.EAST);

        // Move pusher forward (should push all)
        gameController.moveForward(pusher);

        // Expected positions based on EAST movement
        assertEquals(board.getSpace(4, 0), pushed2.getSpace(), "Pushed robot 2 should be at (4,0)");
        assertEquals(board.getSpace(3, 0), pushed1.getSpace(), "Pushed robot 1 should be at (3,0)");
        assertEquals(board.getSpace(2, 0), pusher.getSpace(), "Pusher should be at (2,0)");
    }

    @Test
    void testPushBlockedByWall() {
        Board board = gameController.board;
        Player pusher = board.getPlayer(0);
        Player pushed = board.getPlayer(1);

        // Set up two players in a row, with a wall blocking the pushed player
        pusher.setSpace(board.getSpace(2, 0));
        pushed.setSpace(board.getSpace(3, 0));

        pusher.setHeading(Heading.EAST);

        pushed.getSpace().getWalls().add(Heading.EAST); // Add a wall blocking movement

        // Move pusher forward (should fail)
        gameController.moveForward(pusher);

        assertEquals(board.getSpace(2, 0), pusher.getSpace(), "Pusher should not have moved");
        assertEquals(board.getSpace(3, 0), pushed.getSpace(), "Pushed player should not have moved");
    }

    @Test
    void testConveyorBeltMovement() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space startSpace = board.getSpace(2, 2);
        Space endSpace = board.getSpace(3, 2);

        player.setSpace(startSpace);

        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST);
        startSpace.getActions().add(belt);

        gameController.executeFieldActions();

        assertEquals(endSpace, player.getSpace(), "Player should have moved to (3,2) due to conveyor belt");
    }

    @Test
    void testConveyorBeltBlockedByWall() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space startSpace = board.getSpace(2, 2);
        Space endSpace = board.getSpace(3, 2);

        player.setSpace(startSpace);

        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST);
        startSpace.getActions().add(belt);

        endSpace.getWalls().add(Heading.WEST); // Wall blocking the conveyor movement

        gameController.executeFieldActions();

        assertEquals(startSpace, player.getSpace(), "Player should not have moved due to wall");
    }

    @Test
    void testConveyorBeltBlockedByBoardEdge() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space startSpace = board.getSpace(TEST_WIDTH - 1, 2); // Rightmost column

        player.setSpace(startSpace);

        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST); // Moves off the board
        startSpace.getActions().add(belt);

        gameController.executeFieldActions();

        assertEquals(startSpace, player.getSpace(), "Player should not move off the board");
    }

    @Test
    void testCheckpointReachedInOrder() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space checkpointSpace = board.getSpace(2, 2);

        CheckPoint checkpoint = new CheckPoint(1, false);
        checkpointSpace.getActions().add(checkpoint);

        player.setSpace(checkpointSpace);
        gameController.executeFieldActions();

        assertEquals(1, player.getCheckpointsReached(), "Player should have reached checkpoint 1");
    }

    @Test
    void testCheckpointSkipped() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space checkpointSpace = board.getSpace(2, 2);

        CheckPoint checkpoint = new CheckPoint(2, false); // Player hasn't reached checkpoint 1 yet
        checkpointSpace.getActions().add(checkpoint);

        player.setSpace(checkpointSpace);
        gameController.executeFieldActions();

        assertEquals(0, player.getCheckpointsReached(), "Player should not be able to skip checkpoints");
    }

    @Test
    void testFinalCheckpointTriggersGameEnd() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        Space checkpointSpace = board.getSpace(2, 2);

        CheckPoint checkpoint = new CheckPoint(3, true); // Final checkpoint
        checkpointSpace.getActions().add(checkpoint);

        // Ensure player has reached previous checkpoints
        player.setCheckpointsReached(2);

        player.setSpace(checkpointSpace);
        gameController.executeFieldActions();

        assertEquals(3, player.getCheckpointsReached(), "Player should have reached the final checkpoint");
        assertEquals(Phase.FINISHED, board.getPhase(), "Game should transition to FINISHED phase");
    }

    @Test
    void testPlayerInteractionCorrectlyResumesGame() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();

        // Set game to PLAYER_INTERACTION phase
        board.setPhase(Phase.PLAYER_INTERACTION);

        gameController.executeNextStep(Command.RIGHT);

        assertEquals(Phase.ACTIVATION, board.getPhase(), "Game should return to ACTIVATION phase after interaction");
    }

    @Test
    void testExecuteNextStepWaitsForPlayerInteraction() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        CommandCard commandCard = new CommandCard(Command.LEFT_OR_RIGHT);

        player.getProgramField(0).setCard(commandCard);
        board.setStep(0);
        board.setPhase(Phase.ACTIVATION);

        gameController.executeNextStep(null); // No interactive input

        assertEquals(Phase.PLAYER_INTERACTION, board.getPhase(), "Game should enter PLAYER_INTERACTION phase.");
    }

    @Test
    void testPlayersCannotShareSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);
        Space targetSpace = board.getSpace(2, 2);

        player1.setSpace(targetSpace);
        player2.setSpace(targetSpace);

        assertNotEquals(player1.getSpace(), player2.getSpace(), "Two players should not occupy the same space");
    }

    @Test
    void testStartProgrammingPhase() {
        gameController.startProgrammingPhase();

        assertEquals(Phase.PROGRAMMING, gameController.board.getPhase(), "Phase should be PROGRAMMING");

        for (int i = 0; i < gameController.board.getPlayersNumber(); i++) {
            Player player = gameController.board.getPlayer(i);

            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                assertNull(player.getProgramField(j).getCard(), "Program fields should be empty");
                assertTrue(player.getProgramField(j).isVisible(), "Program fields should be visible");
            }
            for (int j = 0; j < Player.NO_CARDS; j++) {
                assertNotNull(player.getCardField(j).getCard(), "Players should receive random command cards");
            }
        }
    }

    @Test
    void testMoveToSpace() {
        Player player = gameController.board.getCurrentPlayer();
        Space startSpace = player.getSpace();
        Space targetSpace = gameController.board.getNeighbour(startSpace, player.getHeading());

        assertDoesNotThrow(() -> gameController.moveToSpace(player, targetSpace, player.getHeading()),
                "Player should be able to move to an empty space");

        assertEquals(targetSpace, player.getSpace(), "Player should have moved to the target space");
    }

    @Test
    void testMoveToSpaceBlockedByWall() {
        Player player = gameController.board.getCurrentPlayer();
        Space startSpace = player.getSpace();
        Space targetSpace = gameController.board.getNeighbour(startSpace, player.getHeading());

        startSpace.getWalls().add(player.getHeading());

        assertThrows(ImpossibleMoveException.class, () -> gameController.moveToSpace(player, targetSpace, player.getHeading()),
                "Movement should fail when blocked by a wall");
    }

    @Test
    void testMoveCardsSuccess() {
        Player player = gameController.board.getCurrentPlayer();
        CommandCardField source = player.getCardField(0);
        CommandCardField target = player.getProgramField(0);

        source.setCard(new CommandCard(Command.FORWARD));

        boolean moved = gameController.moveCards(source, target);

        assertTrue(moved, "Card should be successfully moved");
        assertNull(source.getCard(), "Source should be empty");
        assertNotNull(target.getCard(), "Target should contain the moved card");
    }

    @Test
    void testMoveCardsFailTargetOccupied() {
        Player player = gameController.board.getCurrentPlayer();
        CommandCardField source = player.getCardField(0);
        CommandCardField target = player.getProgramField(0);

        source.setCard(new CommandCard(Command.FORWARD));
        target.setCard(new CommandCard(Command.RIGHT));

        boolean moved = gameController.moveCards(source, target);

        assertFalse(moved, "Card should NOT move if the target is occupied");
        assertNotNull(source.getCard(), "Source should still have its card");
        assertNotNull(target.getCard(), "Target card should remain unchanged");
    }

    @Test
    void testProceedToNextPlayer() {
        Player firstPlayer = gameController.board.getCurrentPlayer();
        gameController.proceedToNextPlayer();
        Player nextPlayer = gameController.board.getCurrentPlayer();

        assertNotEquals(firstPlayer, nextPlayer, "Game should switch to the next player");
    }

    @Test
    void testProceedToNextPlayerResetsAfterLastPlayer() {
        Board board = gameController.board;
        int lastPlayerIndex = board.getPlayersNumber() - 1;
        board.setCurrentPlayer(board.getPlayer(lastPlayerIndex));

        gameController.proceedToNextPlayer();

        assertEquals(board.getPlayer(0), board.getCurrentPlayer(), "After the last player, game should reset to first player.");
    }

    @Test
    void testProceedToNextPlayerTriggersProgrammingPhaseAfterFinalStep() {
        Board board = gameController.board;
        board.setStep(Player.NO_REGISTERS - 1); // Set to last step
        board.setCurrentPlayer(board.getPlayer(board.getPlayersNumber() - 1)); // Last player

        gameController.proceedToNextPlayer();

        assertEquals(Phase.PROGRAMMING, board.getPhase(), "After all steps, the game should restart the programming phase.");
    }

    @Test
    void testExecuteNextStepActivatesNextCommand() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        CommandCard commandCard = new CommandCard(Command.FORWARD);

        // Assign a command to the first step
        player.getProgramField(0).setCard(commandCard);
        board.setStep(0);
        board.setPhase(Phase.ACTIVATION);

        // Execute steps for each player in the round
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            gameController.executeNextStep(null);
        }

        assertEquals(1, board.getStep(), "Game should move to the next step after all players have played.");
    }


    // BOARD CREATION TESTS

    /**
     * Utility method to check if a space contains an action of a given class.
     */
    private boolean spaceHasAction(Space space, Class<? extends FieldAction> actionClass) {
        return space.getActions().stream().anyMatch(actionClass::isInstance);
    }

    @Test
    void testSingletonInstance() {
        BoardFactory instance1 = BoardFactory.getInstance();
        BoardFactory instance2 = BoardFactory.getInstance();
        assertSame(instance1, instance2, "BoardFactory should follow the singleton pattern.");
    }

    @Test
    void testCreateDefaultBoard() {
        BoardFactory factory = BoardFactory.getInstance();
        Board board = factory.createBoard(null);

        assertNotNull(board, "Board should be created.");
        assertEquals(8, board.width, "Default board should be 8x8.");
        assertEquals(8, board.height, "Default board should be 8x8.");
        assertEquals("<none>", board.boardName, "Default board name should be <none>.");
    }

    @Test
    void testCreateSimpleBoard() {
        BoardFactory factory = BoardFactory.getInstance();
        Board board = factory.createBoard(BoardFactory.SIMPLE_BOARD_NAME);

        assertNotNull(board, "Board should be created.");
        assertEquals(8, board.width, "Simple board should be 8x8.");
        assertEquals(8, board.height, "Simple board should be 8x8.");
        assertEquals(BoardFactory.SIMPLE_BOARD_NAME, board.boardName, "Board name should be 'simple'.");

        // Test conveyor belts
        assertTrue(spaceHasAction(board.getSpace(0, 2), ConveyorBelt.class), "Conveyor belt should be at (0,2).");
        assertTrue(spaceHasAction(board.getSpace(7, 5), ConveyorBelt.class), "Conveyor belt should be at (7,5).");

        // Test walls
        assertTrue(board.getSpace(2, 2).getWalls().contains(Heading.SOUTH), "Wall should be at (2,2) SOUTH.");
        assertTrue(board.getSpace(5, 5).getWalls().contains(Heading.WEST), "Wall should be at (5,5) WEST.");

        // Test checkpoints
        assertTrue(spaceHasAction(board.getSpace(1, 6), CheckPoint.class), "Checkpoint should be at (1,6).");
        assertTrue(spaceHasAction(board.getSpace(6, 1), CheckPoint.class), "Checkpoint should be at (6,1).");
    }

    @Test
    void testCreateAdvancedBoard() {
        BoardFactory factory = BoardFactory.getInstance();
        Board board = factory.createBoard(BoardFactory.ADVANCED_BOARD_NAME);

        assertNotNull(board, "Board should be created.");
        assertEquals(15, board.width, "Advanced board should be 15x8.");
        assertEquals(8, board.height, "Advanced board should be 15x8.");
        assertEquals(BoardFactory.ADVANCED_BOARD_NAME, board.boardName, "Board name should be 'advanced'.");

        // Test conveyor belts
        assertTrue(spaceHasAction(board.getSpace(13, 6), ConveyorBelt.class), "Conveyor belt should be at (13,6).");
        assertTrue(spaceHasAction(board.getSpace(4, 5), ConveyorBelt.class), "Conveyor belt should be at (4,5).");

        // Test walls
        assertTrue(board.getSpace(3, 0).getWalls().contains(Heading.SOUTH), "Wall should be at (3,0) SOUTH.");
        assertTrue(board.getSpace(7, 2).getWalls().contains(Heading.SOUTH), "Wall should be at (7,2) SOUTH.");

        // Test checkpoints
        assertTrue(spaceHasAction(board.getSpace(9, 2), CheckPoint.class), "Checkpoint should be at (9,2).");
        assertTrue(spaceHasAction(board.getSpace(13, 7), CheckPoint.class), "Final checkpoint should be at (13,7).");
    }

    @Test
    void testCreateUnknownBoard() {
        BoardFactory factory = BoardFactory.getInstance();
        Board board = factory.createBoard("unknown");

        assertNotNull(board, "Board should be created.");
        assertEquals(8, board.width, "Unknown board should default to 8x8.");
        assertEquals(8, board.height, "Unknown board should default to 8x8.");
        assertEquals("unknown", board.boardName, "Board name should match input.");
    }

    @Test
    void testAvailableBoardNames() {
        List<String> names = BoardFactory.getAvailableBoardNames();
        assertNotNull(names, "Board names list should not be null.");
        assertTrue(names.contains("simple"), "List should contain 'simple'.");
        assertTrue(names.contains("advanced"), "List should contain 'advanced'.");
    }
}