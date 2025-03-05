package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        Assertions.assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName() + " should be on Space (0,4)!");
    }

    // The following tests should be used later for assignment V2

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    // TODO and there should be more tests added for the different assignments eventually

    @Test
    void testMoveForwardWithWall() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.getSpace().getWalls().add(Heading.SOUTH);
        gameController.moveForward(player);
        Assertions.assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not have moved due to wall");
    }

    @Test
    void testFastForward() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        gameController.fastForward(player);
        Assertions.assertEquals(board.getSpace(0, 2), player.getSpace(), "Player should have moved forward to (0,2)");
    }

    @Test
    void testFastForwardWithWall() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.getSpace().getWalls().add(Heading.SOUTH);
        gameController.fastForward(player);
        Assertions.assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not have moved due to wall");
    }

    @Test
    void testTurnRight() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        gameController.turnRight(player);
        Assertions.assertEquals(Heading.WEST, player.getHeading(), "Player should have turned right to face WEST");
    }

    @Test
    void testTurnLeft() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        gameController.turnLeft(player);
        Assertions.assertEquals(Heading.EAST, player.getHeading(), "Player should have turned left to face EAST");
    }

    @Test
    void testMoveForwardOutOfBounds() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.setSpace(board.getSpace(0, TEST_HEIGHT - 1)); // Place player at the bottom edge
        player.setHeading(Heading.SOUTH); // Set heading to SOUTH

        gameController.moveForward(player);

        Assertions.assertEquals(board.getSpace(0, TEST_HEIGHT - 1), player.getSpace(), "Player should not move out of bounds");
    }

    @Test
    void testFastForwardOutOfBounds() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.setSpace(board.getSpace(0, TEST_HEIGHT - 2)); // Place player near the bottom edge
        player.setHeading(Heading.SOUTH); // Set heading to SOUTH

        gameController.fastForward(player);

        // The player should move one space forward and stop at (0, TEST_HEIGHT - 1)
        Assertions.assertEquals(board.getSpace(0, TEST_HEIGHT - 1), player.getSpace(), "Player should move one space forward and stop at the edge");
    }

    @Test
    void testUTurn() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        gameController.turnRight(player);
        gameController.turnRight(player);
        Assertions.assertEquals(Heading.NORTH, player.getHeading(), "Player should have turned around to face NORTH");
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

        Assertions.assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should have moved backwards to (0,0)");
    }

    @Test
    void testBackwardsWithWall() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.getSpace().getWalls().add(Heading.NORTH);
        gameController.turnRight(player);
        gameController.turnRight(player);
        gameController.moveForward(player);
        Assertions.assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not have moved backwards due to wall");
    }

    @Test
    void testBackwardsOutOfBounds() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        player.setSpace(board.getSpace(0, 0)); // Set initial position
        player.setHeading(Heading.SOUTH); // Set heading to SOUTH

        gameController.turnRight(player);
        gameController.turnRight(player);
        gameController.moveForward(player);

        Assertions.assertEquals(board.getSpace(0, 0), player.getSpace(), "Player should not move out of bounds");
    }

}