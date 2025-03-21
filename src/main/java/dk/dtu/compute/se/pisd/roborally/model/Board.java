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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * This represents a RoboRally game board. Which gives access to
 * all the information of current state of the games. Note that
 * the terms board and game are used almost interchangeably.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int counter = 0;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * Gets the number of players on the board.
     *
     * @return the number of players
     */
    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Gets the current player on the board.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Sets the current player on the board.
     * If the player is different from the current player and is part of the players list,
     * the current player is updated and observers are notified of the change.
     *
     * @param player the player to set as the current player
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces) and is within the board's bounds;
     * otherwise, the current space is returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; the current space if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        // TODO A3: This implementation needs to be adjusted so that walls on
        //          spaces (and maybe other obstacles) are taken into account
        //          (see above JavaDoc comment for this method) -- done
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = y + 1;
                break;
            case WEST:
                x = x - 1;
                break;
            case NORTH:
                y = y - 1;
                break;
            case EAST:
                x = x + 1;
                break;
        }

        // Check for out-of-bounds before applying modulo
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return getSpace(space.x, space.y);
        }

        // Apply modulo to wrap around
        x = (x + width) % width;
        y = (y + height) % height;

        Space neighbour = getSpace(x, y);
        if (neighbour != null) {
            if (space.getWalls().contains(heading) || neighbour.getWalls().contains(heading.opposite())) {
                return getSpace(space.x, space.y);
            }
        }
        return neighbour;
    }

    /**
     * Returns the current status message of the board.
     *
     * <p>The status message includes:</p>
     * <ul>
     *     <li>The current game phase</li>
     *     <li>The name of the current player</li>
     *     <li>The total number of moves made</li>
     *     <li>The current program register step</li>
     *     <li>The number of checkpoints reached by the current player</li>
     * </ul>
     *
     * @return a formatted string representing the current game status
     */
    public String getStatusMessage() {
        // TODO V1: add the move count to the status message -- done
        // TODO V2: changed the status so that it shows the phase, the current player, and the current register -- done
        return "Phase = " + getPhase() + ", Player = " + getCurrentPlayer().getName() + ", Moves = " + getCounter() +
                ", Register = " + getStep() + " Checkpoints = " + getCurrentPlayer().getCheckpointsReached();
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        if (this.counter != counter) {
            this.counter = counter;
            notifyChange();
        }
    }

}
