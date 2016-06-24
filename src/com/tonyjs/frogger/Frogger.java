package com.tonyjs.frogger;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by tonysaavedra on 6/23/16.
 */
public class Frogger extends Application {
    private int APP_WIDTH = 650;
    private int APP_HEIGHT = 700;
    private int currentAnimation = 0;
    private double elapsedTime, motionTime;
    private int vehicleVelocityX = 50;
    private boolean CLICKED, GAME_START;
    private Group root;
    private GraphicsContext gc;
    private Frog frogger;
    private Sprite frogSprite;
    private Sprite[] currentFrogAnimation;
    private ArrayList<Car> firstRow, secondRow, thirdRow, fourthRow;
    private ArrayList<Truck> trucks;
    private AnimationTimer timer;
    private LongValue startNanoTime;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Frogger");
        primaryStage.setResizable(false);

        Parent root = getContent();
        Scene main = new Scene(root, APP_WIDTH, APP_HEIGHT);
        setKeyFunctions(main);
        primaryStage.setScene(main);
        primaryStage.show();
        startGame();
    }

    private Parent getContent() {
        root = new Group();

        Canvas canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        ImageView background = setBackground();
        root.getChildren().addAll(background, canvas);

        setFrogger();
        initializeCars();
        placeCars();
        setTrucks();

        return root;
    }

    private void setKeyFunctions(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                GAME_START = true;
            } else if (GAME_START && e.getCode() == KeyCode.UP) {
                CLICKED = true;
                moveFrogUp();
            } else if (GAME_START && e.getCode() == KeyCode.DOWN) {
                CLICKED = true;
                moveFrogDown();
            } else if (GAME_START && e.getCode() == KeyCode.LEFT) {
                CLICKED = true;
                moveFrogLeft();
            } else  if (GAME_START && e.getCode() == KeyCode.RIGHT) {
                CLICKED = true;
                moveFrogRight();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP ||
                    e.getCode() == KeyCode.DOWN ||
                    e.getCode() == KeyCode.LEFT ||
                    e.getCode() == KeyCode.RIGHT) {
                stopFrog();
            }
        });
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
        frogSprite = currentFrogAnimation[0];
        frogSprite.setPositionXY(APP_WIDTH/2 - 10, APP_HEIGHT-45);
        frogSprite.render(gc);
    }

    private void initializeCars() {
        firstRow = new ArrayList<>();
        secondRow = new ArrayList<>();
        thirdRow = new ArrayList<>();
        fourthRow = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            firstRow.add(i, new Car(0));
            secondRow.add(i, new Car(1));
            thirdRow.add(i, new Car(3));

            if (i < 2) {
                fourthRow.add(i, new Car(2));
            }
        }
    }

    private void placeCars() {
        double x = 50;
        double y = 600;
        Sprite c;
        for (int i = 0; i < 4; i++) {
            c = firstRow.get(i).getCar();
            c.setPositionXY(x, y);
            c.setVelocity(vehicleVelocityX, 0);
            c.render(gc);

            c = secondRow.get(i).getCar();
            c.setPositionXY(x, y - 45);
            c.setVelocity(-vehicleVelocityX, 0);
            c.render(gc);

            c = thirdRow.get(i).getCar();
            c.setPositionXY(x, y - 90);
            c.setVelocity(vehicleVelocityX, 0);
            c.render(gc);

            if (i < 2) {
                c = fourthRow.get(i).getCar();
                c.setPositionXY(x, y - 135);
                c.setVelocity(vehicleVelocityX * 3, 0);
                c.render(gc);
            }

            x += 150;
        }
    }

    private void setTrucks() {
        trucks = new ArrayList<>();
        double x = 50;
        double y = 420;
        Sprite t;
        for (int i = 0; i < 3; i++) {
            trucks.add(i, new Truck());
            t = trucks.get(i).getTruck();
            t.setPositionXY(x, y);
            t.setVelocity(-vehicleVelocityX, 0);
            t.render(gc);
            x += 180;
        }
    }

    private void startGame() {
        startNanoTime = new LongValue(System.nanoTime());
        timer = new AnimationTimer() {
            public void handle(long now) {
                elapsedTime = (now - startNanoTime.value) / 1000000000.0;
                startNanoTime.value = now;

                gc.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);
                animateFrog();
                animateVehicles();
                checkVehicleLocation();
                if (CLICKED) {
                    keepFrogWithinCanvas();
                }
            }
        };
        timer.start();
    }

    private void animateFrog() {
        frogSprite.render(gc);
        frogSprite.update(elapsedTime);

        motionTime += 0.12;
        if (motionTime > 0.5 && CLICKED) {
            Sprite temp = frogSprite;
            animate();
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
            frogSprite.setVelocity(temp.getVelocityX(), temp.getVelocityY());
            motionTime = 0;
        }

    }

    private void animate() {
        if (currentAnimation == currentFrogAnimation.length) {
            currentAnimation = 0;
        }

        frogSprite = currentFrogAnimation[currentAnimation];
        currentAnimation++;
    }

    private void animateVehicles() {
        Sprite v;
        for (int i = 0; i < 4; i++) {
            if (i < 2) {
                v = fourthRow.get(i).getCar();
                v.render(gc);
                v.update(elapsedTime);
            }
            if (i < 3) {
                v = trucks.get(i).getTruck();
                v.render(gc);
                v.update(elapsedTime);
            }

            v = firstRow.get(i).getCar();
            v.render(gc);
            v.update(elapsedTime);

            v = secondRow.get(i).getCar();
            v.render(gc);
            v.update(elapsedTime);

            v = thirdRow.get(i).getCar();
            v.render(gc);
            v.update(elapsedTime);
        }
    }

    private void checkVehicleLocation() {
        Sprite v;
        for (int i = 0; i < 4; i++) {
            if (i < 2) {
                v = fourthRow.get(i).getCar();
                resetVehicle(v);
            }
            if (i < 3) {
                v = trucks.get(i).getTruck();
                resetVehicle(v);
            }

            v = firstRow.get(i).getCar();
            resetVehicle(v);

            v = secondRow.get(i).getCar();
            resetVehicle(v);

            v = thirdRow.get(i).getCar();
            resetVehicle(v);
        }
    }

    private void resetVehicle(Sprite v) {
        if (v.getPositionX() < 0 - v.getWidth()) {
            v.setPositionXY(APP_WIDTH, v.getPositionY());
        } else if (v.getPositionX() > APP_WIDTH) {
            v.setPositionXY(0, v.getPositionY());
        }
    }

    private void moveFrogUp() {
        if (currentFrogAnimation != frogger.getUpwardFrog()) {
            currentFrogAnimation = frogger.getUpwardFrog();
            Sprite temp = frogSprite;
            frogSprite = currentFrogAnimation[0];
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        }
        setFrogVelocity(0, -200);
    }

    private void moveFrogDown() {
        if (currentFrogAnimation != frogger.getDownwardFrog()) {
            currentFrogAnimation = frogger.getDownwardFrog();
            Sprite temp = frogSprite;
            frogSprite = currentFrogAnimation[0];
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        }
        setFrogVelocity(0, 200);
    }

    private void moveFrogLeft() {
        if (currentFrogAnimation != frogger.getLeftwardFrog()) {
            currentFrogAnimation = frogger.getLeftwardFrog();
            Sprite temp = frogSprite;
            frogSprite = currentFrogAnimation[0];
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        }
        setFrogVelocity(-200, 0);
    }

    private void moveFrogRight() {
        if (currentFrogAnimation != frogger.getRightwardFrog()) {
            currentFrogAnimation = frogger.getRightwardFrog();
            Sprite temp = frogSprite;
            frogSprite = currentFrogAnimation[0];
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        }
        setFrogVelocity(200, 0);
    }

    private void setFrogVelocity(double x, double y) {
        frogSprite.setVelocity(x, y);
        frogSprite.render(gc);
        frogSprite.update(elapsedTime);
    }

    private void stopFrog() {
        frogSprite.setVelocity(0, 0);
        frogSprite.render(gc);
        frogSprite.update(elapsedTime);
        CLICKED = false;
    }

    private void keepFrogWithinCanvas() {
        if (frogSprite.getPositionX() < 5) {
            frogSprite.setPositionXY(frogSprite.getPositionX() + 5, frogSprite.getPositionY());
            frogSprite.setVelocity(0, 0);
        } else if (frogSprite.getPositionX() > APP_WIDTH - 25) {
            frogSprite.setPositionXY(frogSprite.getPositionX() - 25, frogSprite.getPositionY());
            frogSprite.setVelocity(0, 0);
        } else if (frogSprite.getPositionY() > APP_HEIGHT - 25) {
            frogSprite.setPositionXY(frogSprite.getPositionX(), frogSprite.getPositionY() - 25);
            frogSprite.setVelocity(0, 0);
        } else if (frogSprite.getPositionY() < 80) {
            frogSprite.setPositionXY(frogSprite.getPositionX(), frogSprite.getPositionY() + 5);
            frogSprite.setVelocity(0, 0);
        }
    }

    public class LongValue {
        public long value;

        public LongValue(long i) {
            this.value = i;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
