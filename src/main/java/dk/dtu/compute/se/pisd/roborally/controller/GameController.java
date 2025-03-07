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
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * Moves the current player to the specified space if the space is unoccupied.
     * Updates the current player to the next player in the sequence and increments the move counter.
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        // TODO V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free())
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if and when the player is moved (the counter and the status line
        //     message needs to be implemented at another place) -- done

        // moves player to space when clicked on; Probably should be replaced later!
        Player currentPlayer = board.getCurrentPlayer(); // gets current space of a specific player
        
        if (space.getPlayer() == null) { // if getplayer returns null the space does not contain a player
            currentPlayer.setSpace(space); // and if empty we set current player at this space
            
            int currentPlayerNumber = board.getPlayerNumber(currentPlayer); //get current Player number
            int nextPlayerNumber = (currentPlayerNumber + 1) % board.getPlayersNumber(); // move on to the next player
            
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber)); // set current player as the "next player"
            

            board.setCounter(board.getCounter() + 1); // Increment the counter when a move is made .
        }
    }

    // XXX V2
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

    // XXX V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    private void executeFieldActions() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            Space space = player.getSpace();

            if (space != null) {
                // Iterate over all field actions assigned to this space
                for (FieldAction action : space.getActions()) {
                    action.doAction(this, space); // Call doAction() for each field action
                }
            }
        }
    }

    // XXX V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    // All players finished their commands → Trigger field actions
                    executeFieldActions();

                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX V2
    /**
     * Executes the given command for the specified player.
     * Depending on the command, the player will move or rotate accordingly.
     *
     * @param player  the player executing the command
     * @param command the command to be executed
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

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
     * If another player is in the target space, they will be pushed.
     * If movement is not possible, an exception is thrown.
     *
     * @param pusher  the player attempting to move
     * @param space   the target space
     * @param heading the direction of movement
     * @throws ImpossibleMoveException if movement is blocked (e.g., by walls or board limits)
     */
    void moveToSpace(@NotNull Player pusher, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        if (space == pusher.getSpace()) {
            throw new ImpossibleMoveException(pusher, space, heading); // Out of bounds or invalid space
        }

        // Ensure walls block movement
        Space currentSpace = pusher.getSpace();
        Space neighbour = board.getNeighbour(currentSpace, heading);

        if (neighbour == null || currentSpace.getWalls().contains(heading)) {
            throw new ImpossibleMoveException(pusher, space, heading); // Blocked by a wall
        }

        // check if the target space is occupied by another player
        Player pushed = space.getPlayer();
        if (pushed != null) {
            Space nextSpace = board.getNeighbour(space, heading);

            // Throw exception instead of stopping silently**
            if (nextSpace == null || space.getWalls().contains(heading)) {
                throw new ImpossibleMoveException(pusher, space, heading); // Can't push, movement fails
            }


            moveToSpace(pushed, nextSpace, heading); // Recursively move the pushed player

            // If the space is still occupied, the push failed**
            if (space.getPlayer() != null) {
                throw new ImpossibleMoveException(pusher, space, heading);
            }
        }

        pusher.setSpace(space); // Move only if everything succeeded
    }

    // TODO V2 -- done
    /**
     * Moves the player one space forward in the direction they are currently heading.
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
                    moveToSpace(player, target, heading); // Now it also pushes other robots
                    board.setCounter(board.getCounter() + 1); // Increment counter here
                } catch (ImpossibleMoveException e) {
                    // Don't do anything if the movement fails
                }
            }
        }
    }

    /**
     * Moves the player one space backward (opposite of their current heading).
     * If movement is blocked, the player remains in place.
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
                    board.setCounter(board.getCounter() + 1); // Increment counter here
                } catch (ImpossibleMoveException e) {
                    // Don't do anything if the movement fails
                }
            }
        }
    }

    // TODO V2 -- done
    /**
     * Moves the player two spaces forward in the direction they are currently heading.
     *
     * @param player the player to move forward
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    // TODO V2 -- done
    /**
     * Rotates the player 90 degrees to the right.
     *
     * @param player the player to rotate
     */
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
        board.setCounter(board.getCounter() + 1); // Increment counter here
    }

    // TODO V2 -- done
    /**
     * Rotates the player 90 degrees to the left.
     *
     * @param player the player to rotate
     */
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
        board.setCounter(board.getCounter() + 1); // Increment counter here
    }

    /**
     * Rotates the player 180 degrees to face the opposite direction.
     * This is equivalent to making a U-turn.
     *
     * @param player the player to rotate
     */
    public void turnU(@NotNull Player player) {
        player.setHeading(player.getHeading().opposite());
        board.setCounter(board.getCounter() + 1); // Increment counter here
    }

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
     * A method called when no corresponding controller operation is implemented yet.
     * This should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
