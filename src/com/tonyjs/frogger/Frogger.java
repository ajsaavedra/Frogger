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
    private GraphicsContext gc, riverGC;
    private Frog frogger;
    private Sprite frogSprite, temp;
    private Sprite[] currentFrogAnimation;
    private ArrayList<Car> firstRow, secondRow, thirdRow, fourthRow;
    private ArrayList<Truck> trucks;
    private ArrayList<Tree> trees;
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
        Canvas treeCanvas = new Canvas(APP_WIDTH, APP_HEIGHT);

        gc = canvas.getGraphicsContext2D();
        riverGC = treeCanvas.getGraphicsContext2D();

        ImageView background = setBackground();

        setFrogger();
        initializeCars();
        placeCars();
        setTrucks();
        initializeTrees();
        setTrees();

        root.getChildren().addAll(background, treeCanvas, canvas);

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
        for (int i = 0; i < 4; i++) {
            temp = firstRow.get(i).getCar();
            temp.setPositionXY(x, y);
            temp.setVelocity(-vehicleVelocityX, 0);
            temp.render(gc);

            temp = secondRow.get(i).getCar();
            temp.setPositionXY(x, y - 45);
            temp.setVelocity(vehicleVelocityX, 0);
            temp.render(gc);

            temp = thirdRow.get(i).getCar();
            temp.setPositionXY(x, y - 90);
            temp.setVelocity(-vehicleVelocityX, 0);
            temp.render(gc);

            if (i < 2) {
                temp = fourthRow.get(i).getCar();
                temp.setPositionXY(x, y - 135);
                temp.setVelocity(vehicleVelocityX * 3, 0);
                temp.render(gc);
            }

            x += 150;
        }
    }

    private void setTrucks() {
        trucks = new ArrayList<>();
        double x = 50;
        double y = 420;
        for (int i = 0; i < 3; i++) {
            trucks.add(i, new Truck());
            temp = trucks.get(i).getTruck();
            temp.setPositionXY(x, y);
            temp.setVelocity(-vehicleVelocityX, 0);
            temp.render(gc);
            x += 180;
        }
    }

    private void initializeTrees() {
        trees = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            if (row < 1) {
                trees.add(0, new Tree("/images/tree_1.png"));
                trees.add(1, new Tree("/images/tree_1.png"));
                trees.add(2, new Tree("/images/tree_1.png"));
                trees.add(3, new Tree("/images/tree_1.png"));
            } else if (row < 2) {
                trees.add(4, new Tree("/images/tree_2.png"));
                trees.add(5, new Tree("/images/tree_2.png"));
                trees.add(6, new Tree("/images/tree_2.png"));
            } else {
                trees.add(7, new Tree("/images/tree_3.png"));
                trees.add(8, new Tree("/images/tree_3.png"));
                trees.add(9, new Tree("/images/tree_3.png"));
                trees.add(10, new Tree("/images/tree_3.png"));
            }
        }
    }

    private void setTrees() {
        double x = 40;
        double y = 290;
        for (int tree = 0; tree < 11; tree++) {
            if (tree < 4) {
                x += 200 * tree;
                temp = trees.get(tree).getTree();
                temp.setVelocity(100, 0);
                temp.setPositionXY(x, y);
                temp.render(riverGC);
                x = 40;
            } else if (tree < 7) {
                y = 220;
                temp = trees.get(tree).getTree();
                temp.setVelocity(100, 0);
                temp.setPositionXY(trees.get(tree-1).getTree().getPositionX() - (tree * 50), y);
                temp.render(riverGC);
                x = 40; y -= 80;
            } else {
                temp = trees.get(tree).getTree();
                temp.setVelocity(100, 0);
                temp.setPositionXY(x, y);
                temp.render(riverGC);
                x += 200;
            }
        }
    }

    private void startGame() {
        startNanoTime = new LongValue(System.nanoTime());
        timer = new AnimationTimer() {
            public void handle(long now) {
                elapsedTime = (now - startNanoTime.value) / 1000000000.0;
                startNanoTime.value = now;

                gc.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);
                riverGC.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);

                animateFrog();
                animateVehicles();
                animateTrees();
                checkVehicleLocation();
                checkTreeLocation();

                if (CLICKED) {
                    keepFrogWithinCanvas();
                }

                if (frogWasHit()) {
                    GAME_START = false;
                    timer.stop();
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
            temp = frogSprite;
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
        for (int i = 0; i < 4; i++) {
            if (i < 2) {
                temp = fourthRow.get(i).getCar();
                temp.render(gc);
                temp.update(elapsedTime);
            }
            if (i < 3) {
                temp = trucks.get(i).getTruck();
                temp.render(gc);
                temp.update(elapsedTime);
            }

            temp = firstRow.get(i).getCar();
            temp.render(gc);
            temp.update(elapsedTime);

            temp = secondRow.get(i).getCar();
            temp.render(gc);
            temp.update(elapsedTime);

            temp = thirdRow.get(i).getCar();
            temp.render(gc);
            temp.update(elapsedTime);
        }
    }

    private void animateTrees() {
        for (Tree t : trees) {
            temp = t.getTree();
            temp.render(riverGC);
            temp.update(elapsedTime);
        }
    }

    private void checkTreeLocation() {
        for (Tree t : trees) {
            temp = t.getTree();
            if (temp.getPositionX() > APP_WIDTH) {
                temp.setPositionXY(-temp.getWidth(), temp.getPositionY());
            }
        }
    }

    private void checkVehicleLocation() {
        for (int i = 0; i < 4; i++) {
            if (i < 2) {
                temp = fourthRow.get(i).getCar();
                resetVehicle(temp);
            }
            if (i < 3) {
                temp = trucks.get(i).getTruck();
                resetVehicle(temp);
            }

            temp = firstRow.get(i).getCar();
            resetVehicle(temp);

            temp = secondRow.get(i).getCar();
            resetVehicle(temp);

            temp = thirdRow.get(i).getCar();
            resetVehicle(temp);
        }
    }

    private void resetVehicle(Sprite v) {
        if (v.getPositionX() < 0 - v.getWidth()) {
            v.setPositionXY(APP_WIDTH, v.getPositionY());
        } else if (v.getPositionX() > APP_WIDTH) {
            v.setPositionXY(-v.getWidth(), v.getPositionY());
        }
    }

    private boolean frogWasHit() {
        for (int i = 0; i < 4; i++) {
            if (i < 2 && fourthRow.get(i).getCar().intersectsSprite(frogSprite)) {
                    return true;
            }
            if (i < 3 && trucks.get(i).getTruck().intersectsSprite(frogSprite)) {
                    return true;
            }

            if (firstRow.get(i).getCar().intersectsSprite(frogSprite)) {
                return true;
            } else if (secondRow.get(i).getCar().intersectsSprite(frogSprite)) {
                return true;
            } else if (thirdRow.get(i).getCar().intersectsSprite(frogSprite)) {
                return true;
            }
        }
        return false;
    }

    private void moveFrogUp() {
        if (currentFrogAnimation != frogger.getUpwardFrog()) {
            currentFrogAnimation = frogger.getUpwardFrog();
            temp = frogSprite;
            frogSprite = currentFrogAnimation[0];
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        }
        setFrogVelocity(0, -220);
    }

    private void moveFrogDown() {
        if (currentFrogAnimation != frogger.getDownwardFrog()) {
            currentFrogAnimation = frogger.getDownwardFrog();
            temp = frogSprite;
            frogSprite = currentFrogAnimation[0];
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        }
        setFrogVelocity(0, 220);
    }

    private void moveFrogLeft() {
        if (currentFrogAnimation != frogger.getLeftwardFrog()) {
            currentFrogAnimation = frogger.getLeftwardFrog();
            temp = frogSprite;
            frogSprite = currentFrogAnimation[0];
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        }
        setFrogVelocity(-220, 0);
    }

    private void moveFrogRight() {
        if (currentFrogAnimation != frogger.getRightwardFrog()) {
            currentFrogAnimation = frogger.getRightwardFrog();
            temp = frogSprite;
            frogSprite = currentFrogAnimation[0];
            frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        }
        setFrogVelocity(220, 0);
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
