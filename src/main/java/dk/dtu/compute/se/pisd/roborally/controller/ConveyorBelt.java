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
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a conveyor belt on a space.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
// XXX A3
public class ConveyorBelt extends FieldAction {

    private Heading heading;


    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    /**
     * Executes the conveyor belt action for the space.
     *
     * <p>If a player is on the conveyor belt, they are pushed one space in the
     * belt's direction, provided there are no obstacles such as walls or the edge
     * of the board blocking the movement.</p>
     *
     * <p>If movement is not possible due to a wall or an out-of-bounds situation,
     * the player remains in place.</p>
     *
     * @param gameController the game controller managing the game logic
     * @param space the space where the conveyor belt action occurs
     * @return {@code true} if the player was moved successfully; {@code false} otherwise
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if (space.getPlayer() == null) {
            return false; // No player on the conveyor belt, nothing happens
        }

        Heading direction = this.getHeading(); // Get conveyor belt direction
        Space nextSpace = gameController.board.getNeighbour(space, direction); // Get next space (already checks walls & bounds)

        if (nextSpace != null) {
            try {
                gameController.moveToSpace(space.getPlayer(), nextSpace, direction);
                return true; // Action successful, player moved
            } catch (ImpossibleMoveException e) {
                return false; // Movement blocked (wall, out of bounds, etc.)
            }
        }

        return false; // No valid space to move into
    }


}
