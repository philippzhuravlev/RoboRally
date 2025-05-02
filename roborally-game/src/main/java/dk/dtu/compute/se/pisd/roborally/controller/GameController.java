/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.exceptions.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {
    private BoardView boardView;
    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    public void setBoardView(BoardView boardView) {
        this.boardView = boardView;
    }

    // ASSIGNMENT 4a
    /**
     * Moves the current player to the specified space if it is unoccupied.
     * <p>If the movement is successful, the game advances to the next player in the sequence,
     * and the move counter is incremented.</p>
     *
     * @param space the destination space for the current player
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        Player currentPlayer = board.getCurrentPlayer();

        if (space.getPlayer() == null) {
            currentPlayer.setSpace(space);

            int currentPlayerNumber = board.getPlayerNumber(currentPlayer);
            int nextPlayerNumber = (currentPlayerNumber + 1) % board.getPlayersNumber();

            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));

            board.setCounter(board.getCounter() + 1);
        }
    }

    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Executes all field actions for the current game state.
     *
     * <p>This method processes field actions such as conveyor belts and checkpoints
     * for all players on the board. It iterates through each player, retrieves their
     * current space, and triggers any assigned field actions.</p>
     *
     * <p>Each field action is executed by calling its {@code doAction()} method.</p>
     */
    void executeFieldActions() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            Space space = player.getSpace();

            if (space != null) {
                // Iterate over all field actions assigned to this space
                for (FieldAction action : space.getActions()) {
                    action.doAction(this, space);
                }
            }
        }
    }

    /**
     * Executes the next step in the activation phase of the game.
     *
     * <p>This method processes the next command card for the current player.
     * After executing the command, it moves to the next player. Once all players
     * have executed their commands for the current step, field actions (such as
     * conveyor belts) are triggered.</p>
     *
     * <p>The game then progresses to the next step or, if the last step is reached,
     * restarts the programming phase.</p>
     *
     * <p>If a command requires player interaction and no input is provided,
     * the game enters the player interaction phase until an action is taken.</p>
     *
     * @param interactiveCommand an optional interactive command issued by the player,
     *                           or {@code null} if no interaction is provided
     */
    public void executeNextStep(Command interactiveCommand) {
        Player currentPlayer = board.getCurrentPlayer();
        // ACTIVATION PHASE -> executes next step for player
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) { // ensure the step is within valid bounds
                CommandCard card = currentPlayer.getProgramField(step).getCard(); // get current command card
                if (card != null) {
                    // ? : notaton = if interactiveCommand is not null, use it, otherwise use the card command
                    Command command = interactiveCommand != null ? interactiveCommand : card.command;
                    if (command == Command.LEFT_OR_RIGHT && interactiveCommand == null) {
                        board.setPhase(Phase.PLAYER_INTERACTION); // Switch to interaction phase
                        return; // Wait for player interaction
                    }
                    executeCommand(currentPlayer, command);
                }
                proceedToNextPlayer();

            } else {
                assert false; // this should not happen; invalid step is performed
            }
            // INTERACTION PHASE -> executes interactive command for player
        } else if (board.getPhase() == Phase.PLAYER_INTERACTION && currentPlayer != null) {
            if (interactiveCommand != null) {
                executeCommand(currentPlayer, interactiveCommand);
                board.setPhase(Phase.ACTIVATION);
                proceedToNextPlayer();
            }
        } else {
            assert false; // again, this should not happen; invalid phase or no current player
        }
    }

    // Overload method for non-interactive commands
    private void executeNextStep() {
        executeNextStep(null);
    }

    /**
     * Executes the given command for the specified player.
     * Depending on the command, the player will move or rotate accordingly.
     *
     * @param player  the player executing the command
     * @param command the command to be executed
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case U_TURN:
                    this.turnU(player);
                    break;
                case BACKWARDS:
                    this.moveBackward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * Moves a player to a specified space in the given direction.
     *
     * <p>
     * If another player is already in the target space, they will be pushed
     * recursively
     * until a free space is found or movement becomes impossible (e.g., due to a
     * wall or board limits).
     * </p>
     *
     * <p>
     * If movement is not possible (e.g., the path is blocked by a wall or the
     * board's edge),
     * an {@code ImpossibleMoveException} is thrown.
     * </p>
     *
     * @param pusher  the player attempting to move
     * @param space   the target space
     * @param heading the direction of movement
     * @throws ImpossibleMoveException if movement is blocked (e.g., by walls, board
     *                                 limits, or no free space to push a player)
     */
    void moveToSpace(@NotNull Player pusher, @NotNull Space space, @NotNull Heading heading)
            throws ImpossibleMoveException {
        if (space == pusher.getSpace()) { // Out of bounds or invalid space
            throw new ImpossibleMoveException(pusher, space, heading);
        }

        // HANDLE WALLS
        Space currentSpace = pusher.getSpace();
        Space neighbour = board.getNeighbour(currentSpace, heading);
        if (neighbour == null || currentSpace.getWalls().contains(heading)) {
            throw new ImpossibleMoveException(pusher, space, heading); // Blocked by a wall
        }

        // Check if the target space is occupied by another player
        Player pushed = space.getPlayer();
        if (pushed != null) {
            Space nextSpace = board.getNeighbour(space, heading);

            // Can't push, throw exception
            if (nextSpace == null || space.getWalls().contains(heading)) {
                throw new ImpossibleMoveException(pusher, space, heading);
            }

            moveToSpace(pushed, nextSpace, heading); // Recursively move the pushed player

            // If the space is still occupied, the push failed
            if (space.getPlayer() != null) {
                throw new ImpossibleMoveException(pusher, space, heading);
            }
        }

        pusher.setSpace(space); // Move only if everything succeeded
    }

    /**
     * Moves the player one space forward in the direction they are currently
     * heading.
     *
     * <p>
     * If another player is already in the target space, they will be pushed
     * recursively
     * using {@code moveToSpace()}.
     * </p>
     *
     * <p>
     * If movement is blocked (e.g., by a wall or the edge of the board), the player
     * remains in place.
     * </p>
     *
     * @param player the player to move forward
     */
    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading);

            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                    board.setCounter(board.getCounter() + 1); // Increment counter here
                } catch (ImpossibleMoveException e) {
                    // Don't do anything if the movement fails
                }
            }
        }
    }

    /**
     * Moves the player one space backward (opposite of their current heading).
     *
     * <p>
     * If another player is already in the target space, they will be pushed
     * recursively
     * using {@code moveToSpace()}.
     * </p>
     *
     * <p>
     * If movement is blocked (e.g., by a wall or the edge of the board), the player
     * remains in place.
     * </p>
     *
     * @param player the player to move backward
     */
    public void moveBackward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading().opposite(); // Move in opposite direction
            Space target = board.getNeighbour(space, heading);

            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                    board.setCounter(board.getCounter() + 1); // Increment counter
                } catch (ImpossibleMoveException e) {
                    // Don't do anything if the movement fails
                    // Could throw exceptions in a console, but this is not part of the assignment
                }
            }
        }
    }

    /**
     * Moves the player two spaces forward in the direction they are currently
     * heading.
     *
     * @param player the player to move forward
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    /**
     * Rotates the player 90 degrees to the right.
     *
     * @param player the player to rotate
     */
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
        board.setCounter(board.getCounter() + 1); // Increment counter
    }

    /**
     * Rotates the player 90 degrees to the left.
     *
     * @param player the player to rotate
     */
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
        board.setCounter(board.getCounter() + 1); // Increment counter
    }

    /**
     * Rotates the player 180 degrees to face the opposite direction.
     * This is equivalent to making a U-turn.
     *
     * @param player the player to rotate
     */
    public void turnU(@NotNull Player player) {
        player.setHeading(player.getHeading().opposite());
        board.setCounter(board.getCounter() + 1); // Increment counter
    }

    /**
     * Moves a command card from the source field to the target field.
     *
     * @param source the command card field from which the card is moved
     * @param target the command card field to which the card is moved
     * @return {@code true} if the card was successfully moved; {@code false} otherwise
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet
     */
    public void notImplemented() {
        assert false;
    }

    /**
     * Handles an interactive command by executing the next step associated with it.
     * This method is typically used to process commands that require immediate
     * interaction or response during the game.
     *
     * @param interactiveCommand the command to be handled and executed
     */
    public void handleInteractiveCommand(Command interactiveCommand) {
        executeNextStep(interactiveCommand);
    }

    /**
     * Proceeds to the next player in the game.
     * This method updates the current player to the next player in the sequence.
     * If all players have executed their commands, it triggers the execution of field actions.
     */
    public void proceedToNextPlayer() {
        int nextPlayerNumber = board.getPlayerNumber(board.getCurrentPlayer()) + 1; // i.e. find next player
        if (nextPlayerNumber < board.getPlayersNumber()) { // Move to the next player
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            executeFieldActions();

            int nextStep = board.getStep() + 1;
            if (nextStep < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(nextStep); // Make the next step's fields visible
                board.setStep(nextStep);
                board.setCurrentPlayer(board.getPlayer(0)); // Reset to the first player
            } else {
                startProgrammingPhase(); // Restart programming phase
            }
        }
    }

    /**
     * Handles the end of the game by displaying a victory message.
     *
     * @param winner the player who won the game
     */
    public void handleGameEnd(Player winner) {
        if (boardView != null) {
            boardView.showVictoryMessage(winner);
        }
    }
}
