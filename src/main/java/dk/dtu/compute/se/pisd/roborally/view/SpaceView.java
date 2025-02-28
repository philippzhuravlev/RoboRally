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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 40; // 60; // 75;
    final public static int SPACE_WIDTH = 40;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();

            // Draw walls
            Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            
            for (Heading wall : space.getWalls()) {
                switch (wall) {
                    case NORTH:
                        gc.strokeLine(0, 0, SPACE_WIDTH, 0);
                        break;
                    case SOUTH:
                        gc.strokeLine(0, SPACE_HEIGHT, SPACE_WIDTH, SPACE_HEIGHT);
                        break;
                    case EAST:
                        gc.strokeLine(SPACE_WIDTH, 0, SPACE_WIDTH, SPACE_HEIGHT);
                        break;
                    case WEST:
                        gc.strokeLine(0, 0, 0, SPACE_HEIGHT);
                        break;
                }
            }
            this.getChildren().add(canvas);

            // Draw field actions
            for (FieldAction action : space.getActions()) {
                if (action instanceof ConveyorBelt) {
                    ConveyorBelt belt = (ConveyorBelt) action;
                    Polygon arrow = new Polygon(
                        SPACE_WIDTH/2 - 10, SPACE_HEIGHT/2 - 20,
                        SPACE_WIDTH/2 + 10, SPACE_HEIGHT/2 - 20,
                        SPACE_WIDTH/2, SPACE_HEIGHT/2 - 5
                    );
                    arrow.setFill(Color.LIGHTBLUE);
                    arrow.setRotate((90 * belt.getHeading().ordinal()) % 360);
                    this.getChildren().add(arrow);
                } else if (action instanceof CheckPoint) {
                    CheckPoint checkPoint = (CheckPoint) action;
                    
                    // Create checkpoint circle
                    Circle circle = new Circle(SPACE_WIDTH/3);
                    circle.setFill(Color.TRANSPARENT);
                    circle.setStroke(Color.GREEN);
                    circle.setStrokeWidth(2);
                    
                    // Add checkpoint number
                    Text number = new Text(String.valueOf(checkPoint.getNumber()));
                    number.setFill(Color.GREEN);
                    number.setStyle("-fx-font-size: 16;");
                    
                    this.getChildren().addAll(circle, number);
                }
            }

            // Draw player
            updatePlayer();
        }
    }

}
