package com.tonyjs.frogger;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

/**
 * Created by tonysaavedra on 6/24/16.
 */
public class Turtle {
    private Sprite[] turtleSprites;

    public Turtle(String filePath, int cols) {
        this.turtleSprites = getTurtleFromSpriteSheet(filePath, cols);
    }

    private Sprite[] getTurtleFromSpriteSheet(String filePath, int cols) {
        Sprite[] arr = new Sprite[3];
        BufferedImage spriteSheet = SwingFXUtils.fromFXImage(
                new Image(getClass()
                        .getResource(filePath)
                        .toExternalForm()), null);

        final int width;
        if (filePath == "/images/turtle_2_sprites.png") {
            width = 70;
        } else {
            width = 100;
        }

        final int height = 40;
        final int rows = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                arr[j] = new Sprite();
                arr[j].setImage(spriteSheet.getSubimage(
                        j * width,
                        i * height,
                        width,
                        height)
                );
            }
        }
        return arr;
    }

    public Sprite[] getTurtleSprites() {
        return turtleSprites;
    }
}
