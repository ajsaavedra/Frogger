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
    private Sprite[] currentFrogAnimation, firstRowTurtles, secondRowTurtles,
            thirdRowTurtles, twoTurtleGroup, threeTurtleGroup;
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
        initializeTurtles();
        setTurtles();

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
            setupSprite(firstRow.get(i).getCar(), x, y, -vehicleVelocityX, 0, gc);
            setupSprite(secondRow.get(i).getCar(), x, y - 45, vehicleVelocityX, 0, gc);
            setupSprite(thirdRow.get(i).getCar(), x, y - 90, -vehicleVelocityX, 0, gc);
            if (i < 2) {
                setupSprite(fourthRow.get(i).getCar(), x, y - 135, vehicleVelocityX * 3, 0, gc);
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
            setupSprite(trucks.get(i).getTruck(), x, y, -vehicleVelocityX, 0, gc);
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
                setupSprite(trees.get(tree).getTree(), x, y, 50, 0, riverGC);
                x = 40;
            } else if (tree < 7) {
                y = 220;
                setupSprite(trees.get(tree).getTree(),
                        trees.get(tree-1).getTree().getPositionX() - (tree * 50), y,
                        150, 0, riverGC);
                x = 40; y -= 80;
            } else {
                setupSprite(trees.get(tree).getTree(), x, y, 50, 0, riverGC);
                x += 200;
            }
        }
    }

    private void initializeTurtles() {
        Turtle groupOfTwo = new Turtle("/images/turtle_2_sprites.png", 3);
        Turtle groupOfThree = new Turtle("/images/turtle_3_sprites.png", 3);
        twoTurtleGroup = groupOfTwo.getTurtleSprites();
        threeTurtleGroup = groupOfThree.getTurtleSprites();
        thirdRowTurtles = new Sprite[4];
        firstRowTurtles = new Sprite[4];
        secondRowTurtles = new Sprite[3];
    }

    private void setTurtles() {
        double x = 40;
        for (int j = 0; j < 4; j++) {
            thirdRowTurtles[j] = new Sprite();
            thirdRowTurtles[j].setImage(threeTurtleGroup[0].getImage());
            setupSprite(thirdRowTurtles[j], x, 330, -80, 0, riverGC);

            if (j < 3) {
                secondRowTurtles[j] = new Sprite();
                secondRowTurtles[j].setImage(threeTurtleGroup[0].getImage());
                setupSprite(secondRowTurtles[j], x, 255, -100, 0, riverGC);
            }

            firstRowTurtles[j] = new Sprite();
            firstRowTurtles[j].setImage(twoTurtleGroup[0].getImage());
            setupSprite(firstRowTurtles[j], x, 180, -80, 0, riverGC);
            x += 180;
        }
    }

    private void setupSprite(Sprite sprite, double x, double y, double veloX, double veloY, GraphicsContext g) {
        sprite.setPositionXY(x, y);
        sprite.setVelocity(veloX, veloY);
        sprite.render(g);
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
                animateTurtles();
                checkVehicleLocation();
                checkTreeLocation();
                checkTurtleLocation();

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
        renderAndUpdateSprite(frogSprite, gc);
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
            renderAndUpdateSprite(firstRow.get(i).getCar(), gc);
            renderAndUpdateSprite(secondRow.get(i).getCar(), gc);
            renderAndUpdateSprite(thirdRow.get(i).getCar(), gc);
            if (i < 2) {
                renderAndUpdateSprite(fourthRow.get(i).getCar(), gc);
            }
            if (i < 3) {
                renderAndUpdateSprite(trucks.get(i).getTruck(), gc);
            }
        }
    }

    private void animateTrees() {
        for (Tree t : trees) {
            renderAndUpdateSprite(t.getTree(), riverGC);
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

    private void animateTurtles() {
        for (int j = 0; j < 4; j++) {
            if (j < 3) {
                renderAndUpdateSprite(secondRowTurtles[j], riverGC);
            }
            renderAndUpdateSprite(thirdRowTurtles[j], riverGC);
            renderAndUpdateSprite(firstRowTurtles[j], riverGC);
        }
    }

    private void checkTurtleLocation() {
        for (int j = 0; j < 4; j++) {
            if (j < 3 && secondRowTurtles[j].getPositionX() < -secondRowTurtles[j].getWidth()) {
                secondRowTurtles[j].setPositionXY(APP_WIDTH, secondRowTurtles[j].getPositionY());
            }
            if (thirdRowTurtles[j].getPositionX() < -thirdRowTurtles[j].getWidth()) {
                thirdRowTurtles[j].setPositionXY(APP_WIDTH, thirdRowTurtles[j].getPositionY());
            }
            if (firstRowTurtles[j].getPositionX() < -firstRowTurtles[j].getWidth()) {
                firstRowTurtles[j].setPositionXY(APP_WIDTH, firstRowTurtles[j].getPositionY());
            }
        }
    }

    private void checkVehicleLocation() {
        for (int i = 0; i < 4; i++) {
            if (i < 2) {
                resetVehicle(fourthRow.get(i).getCar());
            }
            if (i < 3) {
                resetVehicle(trucks.get(i).getTruck());
            }
            resetVehicle(firstRow.get(i).getCar());
            resetVehicle(secondRow.get(i).getCar());
            resetVehicle(thirdRow.get(i).getCar());
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
        return checkCarGroupHitFrogger(firstRow) || checkCarGroupHitFrogger(thirdRow) ||
                checkCarGroupHitFrogger(secondRow) || checkCarGroupHitFrogger(fourthRow) ||
                checkTruckGroupHitFrogger(trucks);
    }

    private boolean checkCarGroupHitFrogger(ArrayList<Car> toCheck) {
        for (Car v : toCheck) {
            if (v.getCar().intersectsSprite(frogSprite)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkTruckGroupHitFrogger(ArrayList<Truck> toCheck) {
        for (Truck v : toCheck) {
            if (v.getTruck().intersectsSprite(frogSprite)) {
                return true;
            }
        }
        return false;
    }

    private void moveFrogUp() {
        if (currentFrogAnimation != frogger.getUpwardFrog()) {
            currentFrogAnimation = frogger.getUpwardFrog();
            updateFrogSprite();
        }
        setFrogVelocity(0, -220);
    }

    private void moveFrogDown() {
        if (currentFrogAnimation != frogger.getDownwardFrog()) {
            currentFrogAnimation = frogger.getDownwardFrog();
            updateFrogSprite();
        }
        setFrogVelocity(0, 220);
    }

    private void moveFrogLeft() {
        if (currentFrogAnimation != frogger.getLeftwardFrog()) {
            currentFrogAnimation = frogger.getLeftwardFrog();
            updateFrogSprite();
        }
        setFrogVelocity(-220, 0);
    }

    private void moveFrogRight() {
        if (currentFrogAnimation != frogger.getRightwardFrog()) {
            currentFrogAnimation = frogger.getRightwardFrog();
            updateFrogSprite();
        }
        setFrogVelocity(220, 0);
    }

    private void updateFrogSprite() {
        temp = frogSprite;
        frogSprite = currentFrogAnimation[0];
        frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
    }

    private void setFrogVelocity(double x, double y) {
        frogSprite.setVelocity(x, y);
        renderAndUpdateSprite(frogSprite, gc);
    }

    private void stopFrog() {
        frogSprite.setVelocity(0, 0);
        renderAndUpdateSprite(frogSprite, gc);
        CLICKED = false;
    }

    private void renderAndUpdateSprite(Sprite sprite, GraphicsContext g) {
        sprite.render(g);
        sprite.update(elapsedTime);
    }

    private void keepFrogWithinCanvas() {
        if (frogSprite.getPositionX() < 5) {
            frogSprite.setPositionXY(frogSprite.getPositionX() + 5, frogSprite.getPositionY());
            stopFrog();
        } else if (frogSprite.getPositionX() > APP_WIDTH - 25) {
            frogSprite.setPositionXY(frogSprite.getPositionX() - 25, frogSprite.getPositionY());
            stopFrog();
        } else if (frogSprite.getPositionY() > APP_HEIGHT - 25) {
            frogSprite.setPositionXY(frogSprite.getPositionX(), frogSprite.getPositionY() - 25);
            stopFrog();
        } else if (frogSprite.getPositionY() < 80) {
            frogSprite.setPositionXY(frogSprite.getPositionX(), frogSprite.getPositionY() + 5);
            stopFrog();
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
