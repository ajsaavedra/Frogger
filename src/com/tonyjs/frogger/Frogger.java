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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Created by tonysaavedra on 6/23/16.
 */
public class Frogger extends Application {
    private int APP_WIDTH = 650;
    private int APP_HEIGHT = 650;
    private int currentAnimation = 0;
    private double elapsedTime, motionTime;
    private int vehicleVelocityX = 50;
    private int totalScore = 0;
    private int totalLives = 3;
    private boolean CLICKED, GAME_START, GAME_OVER, ON_RIVER_OBJ;
    private Rectangle[] winningTiles;
    private Group root, rects;
    private GraphicsContext gc, riverGC;
    private Frog frogger;
    private Sprite frogSprite, temp;
    private Sprite[] currentFrogAnimation, firstRowTurtles, secondRowTurtles,
            twoTurtleGroup, threeTurtleGroup, bonusFrogs;
    private ArrayList<Car> firstRow, secondRow, thirdRow, fourthRow;
    private ArrayList<Truck> trucks;
    private ArrayList<Tree> trees;
    private AnimationTimer timer;
    private LongValue startNanoTime;
    private Text scoreLabel;
    private SoundEffect jump, squash, coin, extra, plunk, time;

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
        setLabels();
        setFrogger();
        initializeCars();
        placeCars();
        setTrucks();
        initializeTrees();
        setTrees();
        initializeTurtles();
        setTurtles();
        setWinningTiles();
        setSounds();
        bonusFrogs = new Sprite[5];

        root.getChildren().addAll(background, rects, scoreLabel, treeCanvas, canvas);

        return root;
    }

    private void setKeyFunctions(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                GAME_START = true;
                coin.playClip();
            } else if (GAME_START && e.getCode() == KeyCode.UP) {
                CLICKED = true;
                moveFrogUp();
                totalScore += 10;
                updateScoreLabel();
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
                if (frogSprite != null){
                    jump.playClip();
                    setFrogVelocity(0, 0);
                }
                CLICKED = false;
            }
        });
    }

    private ImageView setBackground() {
        ImageView bg = new ImageView(new Image(getClass().getResource("/images/background.png").toExternalForm()));
        bg.setFitWidth(APP_WIDTH);
        bg.setFitHeight(APP_HEIGHT);
        return bg;
    }

    private void setLabels() {
        scoreLabel = new Text("SCORE: " + Integer.toString(totalScore));
        scoreLabel.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 25));
        scoreLabel.setFill(Color.RED);
        scoreLabel.setStroke(Color.BLACK);
        scoreLabel.setLayoutX(20);
        scoreLabel.setLayoutY(30);
    }

    private void updateScoreLabel() {
        scoreLabel.setText("SCORE: " + Integer.toString(totalScore));
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
        double y = 560;
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
        double y = 390;
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
                trees.add(new Tree("/images/tree_1.png"));
                trees.add(new Tree("/images/tree_1.png"));
                trees.add(new Tree("/images/tree_1.png"));
                trees.add(new Tree("/images/tree_1.png"));
            } else if (row < 2) {
                trees.add(new Tree("/images/tree_2.png"));
                trees.add(new Tree("/images/tree_2.png"));
            } else {
                trees.add(new Tree("/images/tree_3.png"));
                trees.add(new Tree("/images/tree_3.png"));
                trees.add(new Tree("/images/tree_3.png"));
                trees.add(new Tree("/images/tree_3.png"));
            }
        }
    }

    private void setTrees() {
        double x = 40;
        double y = 260;
        for (int tree = 0; tree < 10; tree++) {
            if (tree < 4) {
                x += 200 * tree;
                setupSprite(trees.get(tree).getTree(), x, y, 50, 0, riverGC);
                x = 40;
            } else if (tree < 6) {
                y = 210;
                setupSprite(trees.get(tree).getTree(),
                        trees.get(tree-1).getTree().getPositionX() - (tree * 100), y,
                        100, 0, riverGC);
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
        firstRowTurtles = new Sprite[4];
        secondRowTurtles = new Sprite[4];
    }

    private void setTurtles() {
        double x = 0;
        for (int j = 0; j < 4; j++) {
            secondRowTurtles[j] = new Sprite();
            secondRowTurtles[j].setImage(threeTurtleGroup[0].getImage());
            setupSprite(secondRowTurtles[j], x, 300, -80, 0, riverGC);

            firstRowTurtles[j] = new Sprite();
            firstRowTurtles[j].setImage(twoTurtleGroup[0].getImage());
            setupSprite(firstRowTurtles[j], x, 170, -80, 0, riverGC);
            x += 200;
        }
    }

    private void setupSprite(Sprite sprite, double x, double y, double veloX, double veloY, GraphicsContext g) {
        sprite.setPositionXY(x, y);
        sprite.setVelocity(veloX, veloY);
        sprite.render(g);
    }

    private void setWinningTiles() {
        rects = new Group();
        winningTiles = new Rectangle[5];
        double x = 20; double y = 85;
        for (int i = 0; i < 5; i++) {
            Rectangle r = new Rectangle();
            r.setHeight(40); r.setWidth(55);
            r.setLayoutX(x); r.setLayoutY(y);
            r.setFill(Color.TRANSPARENT);
            x+= 140;
            rects.getChildren().add(r);
            winningTiles[i] = r;
        }
    }

    private void setSounds() {
        jump = new SoundEffect("/sounds/hop.mp3");
        squash = new SoundEffect("/sounds/squash.mp3");
        plunk = new SoundEffect("/sounds/plunk.mp3");
        coin = new SoundEffect("/sounds/coin.mp3");
        extra = new SoundEffect("/sounds/extra.mp3");
    }

    private void startGame() {
        startNanoTime = new LongValue(System.nanoTime());
        timer = new AnimationTimer() {
            public void handle(long now) {
                elapsedTime = (now - startNanoTime.value) / 1000000000.0;
                startNanoTime.value = now;

                gc.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);
                riverGC.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);

                if (frogSprite != null) {
                    animateFrog();
                }

                if (totalLives == 0) {
                    GAME_OVER = true;
                }

                animateVehicles();
                animateTrees();
                animateTurtles();
                checkVehicleLocation();
                checkTreeLocation();
                checkTurtleLocation();
                isOnRiverObject();
                renderBonuses();

                if (CLICKED && frogSprite != null) {
                    keepFrogWithinCanvas();
                }

                if (isOnWinningTile()) {
                    if (allTilesFull()) {
                        extra.playClip();
                    } else {
                        coin.playClip();
                    }
                    totalScore += 500;
                    updateScoreLabel();
                    setFrogger();
                } else if (frogWasHit()) {
                    squash.playClip();
                    totalLives--;
                    setFrogger();
                } else if (isInRiver()) {
                    plunk.playClip();
                    totalLives--;
                    setFrogger();
                }

                if (GAME_OVER) {
                    GAME_START = false;
                    frogSprite = null;
                    timer.stop();
                }
            }
        };
        timer.start();
    }

    private void animateFrog() {
        renderAndUpdateSprite(frogSprite, gc);
        motionTime += 0.20;
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
            renderAndUpdateSprite(firstRowTurtles[j], riverGC);
        }
    }

    private void checkTurtleLocation() {
        for (int j = 0; j < 4; j++) {
            if (j < 3 && secondRowTurtles[j].getPositionX() < -secondRowTurtles[j].getWidth()) {
                secondRowTurtles[j].setPositionXY(APP_WIDTH, secondRowTurtles[j].getPositionY());
            }
            if (firstRowTurtles[j].getPositionX() < -firstRowTurtles[j].getWidth()) {
                firstRowTurtles[j].setPositionXY(APP_WIDTH, firstRowTurtles[j].getPositionY());
            }
        }
    }

    private boolean isInRiver() {
        return frogSprite.getPositionY() <= 325 && !ON_RIVER_OBJ;
    }

    private void isOnRiverObject() {
        ON_RIVER_OBJ = addFrogToRiverObject();
    }

    private boolean addFrogToRiverObject() {
        for (Tree t : trees) {
            if (t.getTree().intersectsSprite(frogSprite)) {
                setFrogVelocity(t.getTree().getVelocityX()/2, 0);
                return true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (frogSprite.intersectsSprite(firstRowTurtles[i])) {
                setFrogVelocity(firstRowTurtles[i].getVelocityX()/2, 0);
                return true;
            } else if (frogSprite.intersectsSprite(secondRowTurtles[i])) {
                setFrogVelocity(secondRowTurtles[i].getVelocityX()/2, 0);
                return true;
            }
        }
        return false;
    }

    private boolean isOnWinningTile() {
        Rectangle r;
        for (int i = 0; i < winningTiles.length; i++) {
            r = winningTiles[i];
            if (frogSprite.getPositionY() <= 85 + r.getHeight() &&
                    frogSprite.getPositionX() >= r.getLayoutX() &&
                    frogSprite.getPositionX() <= r.getLayoutX() + r.getWidth()) {
                placeBonusFrog(i);
                return true;
            }
        }
        return false;
    }

    private void placeBonusFrog(int index) {
        Sprite bonus = new Sprite();
        bonus.setImage("/images/bonus.png");
        bonus.setPositionXY(winningTiles[index].getLayoutX(), 85);
        bonus.render(riverGC);
        bonusFrogs[index] = bonus;
    }

    private void renderBonuses() {
        for (Sprite bonus : bonusFrogs) {
            if (bonus != null) {
                bonus.render(riverGC);
            }
        }
    }

    private boolean allTilesFull() {
        for (Sprite bonus : bonusFrogs) {
            if (bonus == null) {
                return false;
            }
        }
        totalLives++;
        return true;
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
        if (ON_RIVER_OBJ || frogSprite.getPositionY() <= 385) {
            setFrogVelocity(0, -1375);
        } else {
            setFrogVelocity(0, -250);
        }
    }

    private void moveFrogDown() {
        if (currentFrogAnimation != frogger.getDownwardFrog()) {
            currentFrogAnimation = frogger.getDownwardFrog();
            updateFrogSprite();
        }
        if (ON_RIVER_OBJ && frogSprite.getPositionY() < 285) {
            setFrogVelocity(0, 1225);
        } else {
            setFrogVelocity(0, 250);
        }
    }

    private void moveFrogLeft() {
        if (currentFrogAnimation != frogger.getLeftwardFrog()) {
            currentFrogAnimation = frogger.getLeftwardFrog();
            updateFrogSprite();
        }
        if (ON_RIVER_OBJ) {
            setFrogVelocity(-1000, 0);
        } else {
            setFrogVelocity(-250, 0);
        }
    }

    private void moveFrogRight() {
        if (currentFrogAnimation != frogger.getRightwardFrog()) {
            currentFrogAnimation = frogger.getRightwardFrog();
            updateFrogSprite();
        }
        if (ON_RIVER_OBJ) {
            setFrogVelocity(1000, 0);
        } else {
            setFrogVelocity(250, 0);
        }
    }

    private void updateFrogSprite() {
        temp = frogSprite;
        frogSprite = currentFrogAnimation[0];
        frogSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
        frogSprite.setVelocity(temp.getVelocityX(), temp.getVelocityY());
    }

    private void setFrogVelocity(double x, double y) {
        frogSprite.setVelocity(x, y);
        renderAndUpdateSprite(frogSprite, gc);
    }

    private void renderAndUpdateSprite(Sprite sprite, GraphicsContext g) {
        sprite.render(g);
        sprite.update(elapsedTime);
    }

    private void keepFrogWithinCanvas() {
        if (frogSprite.getPositionX() < 5) {
            frogSprite.setPositionXY(frogSprite.getPositionX() + 5, frogSprite.getPositionY());
            setFrogVelocity(0, 0);
        } else if (frogSprite.getPositionX() > APP_WIDTH - 25) {
            frogSprite.setPositionXY(frogSprite.getPositionX() - 25, frogSprite.getPositionY());
            setFrogVelocity(0, 0);
        } else if (frogSprite.getPositionY() > APP_HEIGHT - 25) {
            frogSprite.setPositionXY(frogSprite.getPositionX(), frogSprite.getPositionY() - 25);
            setFrogVelocity(0, 0);
        } else if (frogSprite.getPositionY() < 80) {
            frogSprite.setPositionXY(frogSprite.getPositionX(), frogSprite.getPositionY() + 5);
            setFrogVelocity(0, 0);
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
