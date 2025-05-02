package dk.dtu.compute.se.pisd.roborally.exceptions;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class ImpossibleMoveException extends Exception {
    private final Player player;
    private final Space space;
    private final Heading heading;

    public ImpossibleMoveException(Player player, Space space, Heading heading) {
        super("Player " + player.getName() + " cannot move to " + space + " in direction " + heading);
        this.player = player;
        this.space = space;
        this.heading = heading;
    }

    public Player getPlayer() {
        return player;
    }

    public Space getSpace() {
        return space;
    }

    public Heading getHeading() {
        return heading;
    }
}
