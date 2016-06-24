package com.tonyjs.frogger;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

/**
 * Created by tonysaavedra on 6/23/16.
 */
public class Car {
    private Sprite car;

    public Car(int type) {
        this.car = getCarFromSpriteSheet(type);
    }

    private Sprite getCarFromSpriteSheet(int type) {
        Sprite car = new Sprite();
        BufferedImage spriteSheet = SwingFXUtils.fromFXImage(
                new Image(getClass()
                        .getResource("/images/car_sprites.png")
                        .toExternalForm()), null);

        final int width = 40;
        final int height = 40;
        final int rows = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = type; j < type + 1; j++) {
                car = new Sprite();
                car.setImage(spriteSheet.getSubimage(
                        j * width,
                        i * height,
                        width,
                        height)
                );
            }
        }
        return car;
    }

    public Sprite getCar() {
        return car;
    }
}
