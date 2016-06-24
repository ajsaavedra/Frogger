package com.tonyjs.frogger;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Created by tonysaavedra on 6/23/16.
 */
public class Frogger extends Application {
    private int APP_WIDTH = 650;
    private int APP_HEIGHT = 700;
    private Group root;
    private GraphicsContext gc;
    private Frog frogger;
    private Sprite frogSprite;
    private Sprite[] currentFrogAnimation;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Frogger");
        primaryStage.setResizable(false);

        Parent root = getContent();
        Scene main = new Scene(root, APP_WIDTH, APP_HEIGHT);
        primaryStage.setScene(main);
        primaryStage.show();
    }

    private Parent getContent() {
        root = new Group();

        Canvas canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        ImageView background = setBackground();
        root.getChildren().addAll(background, canvas);

        setFrogger();
        return root;
    }

    private ImageView setBackground() {
        ImageView bg = new ImageView(new Image(getClass().getResource("/images/background.png").toExternalForm()));
        bg.setFitWidth(APP_WIDTH);
        bg.setFitHeight(APP_HEIGHT);
        return bg;
    }

    private void setFrogger() {
        frogger = new Frog();
        currentFrogAnimation = frogger.getUpwardFrog();
        frogSprite = currentFrogAnimation[1];
        frogSprite.setPositionXY(APP_WIDTH/2 - 10, APP_HEIGHT-45);
        frogSprite.render(gc);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
