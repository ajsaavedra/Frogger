package com.tonyjs.frogger;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

/**
 * Created by tonysaavedra on 6/23/16.
 */
public class Frog {
    private Sprite frog;
    private Sprite[] frogSprites;
    private Sprite[] upwardFrog, downwardFrog,
                     leftwardFrog, rightwardFrog,
                     deadFrog;

    public Frog() {
        this.frog = new Sprite();
        this.frogSprites = getFrogsFromSpriteSheet();
        setUpwardFrog();
        setRightwardFrog();
        setDownwardFrog();
        setLeftwardFrog();
        setDeadFrog();
    }

    private Sprite[] getFrogsFromSpriteSheet() {
        Sprite[] arr = new Sprite[11];
        BufferedImage spriteSheet = SwingFXUtils.fromFXImage(
                new Image(getClass()
                        .getResource("/images/frog_sprites.png")
                        .toExternalForm()), null);

        final int width = 40;
        final int height = 40;
        final int rows = 1;
        final int cols = 11;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                arr[(i * cols) + j] = new Sprite();
                arr[(i * cols) + j].setImage(spriteSheet.getSubimage(
                        j * width,
                        i * height,
                        width,
                        height)
                );
            }
        }
        return arr;
    }

    public Sprite[] getFrogSprites() {
        return frogSprites;
    }

    public void setUpwardFrog() {
        upwardFrog = new Sprite[2];
        upwardFrog[0] = frogSprites[0];
        upwardFrog[1] = frogSprites[1];
    }

    public void setRightwardFrog() {
        rightwardFrog = new Sprite[2];
        rightwardFrog[0] = frogSprites[2];
        rightwardFrog[1] = frogSprites[3];
    }

    public void setDownwardFrog() {
        downwardFrog = new Sprite[2];
        downwardFrog[0] = frogSprites[4];
        downwardFrog[1] = frogSprites[5];
    }

    public void setLeftwardFrog() {
        leftwardFrog = new Sprite[2];
        leftwardFrog[0] = frogSprites[6];
        leftwardFrog[1] = frogSprites[7];
    }

    public void setDeadFrog() {
        deadFrog = new Sprite[3];
        deadFrog[0] = frogSprites[8];
        deadFrog[1] = frogSprites[9];
        deadFrog[2] = frogSprites[10];
    }

    public Sprite[] getUpwardFrog() {
        return upwardFrog;
    }

    public Sprite[] getDownwardFrog() {
        return downwardFrog;
    }

    public Sprite[] getLeftwardFrog() {
        return leftwardFrog;
    }

    public Sprite[] getRightwardFrog() {
        return rightwardFrog;
    }

    public Sprite[] getDeadFrog() {
        return deadFrog;
    }

    public Sprite getFrog() {
        return frog;
    }
}
