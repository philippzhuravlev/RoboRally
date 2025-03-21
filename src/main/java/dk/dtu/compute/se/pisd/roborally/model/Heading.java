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

/**
 * Represents the four possible headings (directions) of a robot:
 * {@code SOUTH}, {@code WEST}, {@code NORTH}, and {@code EAST}.
 * The methods {@code next()} and {@code prev()} allow for
 * cyclic navigation through the headings in clockwise and
 * counterclockwise directions, respectively.
 * The {@code opposite()} method returns the heading that is
 * directly opposite to the current heading.
*/
public enum Heading {

    SOUTH, WEST, NORTH, EAST;

    public Heading next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public Heading prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }

    public Heading opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }
}
