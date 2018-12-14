import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import javafx.scene.layout.Pane;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author Aleksander Kempa
 * @version 1.0
 */
public class Main extends Application {


    private boolean playable = true; // Flaga ustawiająca czy gra jest możliwa
    private boolean gameVsAI = false; // Flaga sprawdzajaca czy gra toczy się przeciwko komputerowi
    private boolean isFirstAIMove = true;
    private boolean turnX = true; // set to start always with X !!!!@@@!!!!
    private Tile[][] board = new Tile[3][3];
    private List<Combo> combos = new ArrayList<>(); // List of combos
    private Line line;
    private Label labelStart;
    private Label labelTurn;
    private Pane root = new Pane();
    /**
     * Metoda createContent
     * @param
     */
    private Parent createContent() {
        root.setPrefSize(800, 700); // create pane with size 800 x 700s

        for (int i = 0; i < 3; i++) {   // Y location
            for (int j = 0; j < 3; j++) {   // X location
                Tile tile = new Tile(); // 9 x new tile ( tile = rectangular )
                tile.setTranslateX(j * 200);
                tile.setTranslateY(i * 200);

                root.getChildren().add(tile);

                board[j][i] = tile;
            }
        }

        labelStart = new Label("X Starts");
        labelStart.setFont(Font.font(30));
        labelStart.setTranslateX(650);
        labelStart.setTranslateY(250);
        root.getChildren().add(labelStart);

        Label labelTips = new Label("Right click: X \nLeft click:  O");
        labelTips.setTranslateX(650);
        labelTips.setTranslateY(350);
        root.getChildren().add(labelTips);

        labelTurn = new Label("Turn: X");
        labelTurn.setFont(Font.font(50));
        labelTurn.setTranslateY(630);
        labelTurn.setTranslateX(220);
        root.getChildren().add(labelTurn);
        // Wszystkie możliwe ułozenia wygrywające
        // 3 ułożenia poziome
        for (int y = 0; y < 3; y++) {
            combos.add(new Combo(board[0][y], board[1][y], board[2][y]));
        }

        // 3 ułożenia pionowe
        for (int x = 0; x < 3; x++) {
            combos.add(new Combo(board[x][0], board[x][1], board[x][2]));
        }

        // 2 przekątne
        combos.add(new Combo(board[0][0], board[1][1], board[2][2]));
        combos.add(new Combo(board[2][0], board[1][1], board[0][2]));

        return root;
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();

        MenuItem menuItemX = new MenuItem("X");
        MenuItem menuItemO = new MenuItem("O");
        MenuButton menuButton = new MenuButton("Select who starts", null, menuItemX, menuItemO);
        menuButton.setTranslateX(650);
        menuButton.setTranslateY(150);
        menuItemX.setOnAction(event -> {
            System.out.println("Option X");
            labelStart.setText("X Starts");
            turnX = true;
        });
        menuItemO.setOnAction(event -> {
            System.out.println("Option O");
            labelStart.setText("O Starts");
            turnX = false;
        });
        root.getChildren().add(menuButton);

        Button btnRestart = new Button("Start with Player");
        btnRestart.setTranslateX(650);
        btnRestart.setTranslateY(50);
        btnRestart.setOnAction(event -> {
            // clean all tiles
            gameVsAI = false;
            for (int i = 0; i < 3; i++) {   // Y location
                for (int j = 0; j < 3; j++) {
                    board[j][i].text.setText(null);
                    root.getChildren().remove(line);
                    turnX = true;
                    playable = true;
                }
            }
        });
        root.getChildren().add(btnRestart);

        Button btnSelectStart = new Button("Start with AI");
        btnSelectStart.setTranslateX(650);
        btnSelectStart.setTranslateY(100);
        btnSelectStart.setOnAction(event -> {
            // Gra przeciwko komputerowi
            gameVsAI = true;
            root.getChildren().remove(line);
            for (int i = 0; i < 3; i++) {   // Y location
                for (int j = 0; j < 3; j++) {
                    board[j][i].text.setText(null);
                    //root.getChildren().remove(line);
                    //
                }
            }
            isFirstAIMove = true;
            //line.setVisible(false);
           // line.setDisable(true);
            root.getChildren().remove(line);
            labelTurn.setText("Turn: X - Player");
            turnX = true;
            playable = true;
        });
        root.getChildren().add(btnSelectStart);

        // button exit app
        Button btnExit = new Button("Exit");
        btnExit.setTranslateX(650);
        btnExit.setTranslateY(200);
        btnExit.setOnAction(event -> {
            primaryStage.close();
        });
        root.getChildren().add(btnExit);

    }

    private void checkState() { // check the combo is up
        if (!isGameOver()){
            for (Combo combo : combos) {
                if (combo.isComplete()) {
                    playable = false;
                    playWinAnimation(combo);
                    //System.out.println("KONIEC");
                    break;
                }
            }
        }

    }

    private boolean isGameOver(){
        int x = 0;
        for (int i = 0; i < 3; i++) {   // Y location
            for (int j = 0; j < 3; j++) {
                if ((board[j][i].text.getText().equals("X")) || (board[j][i].text.getText().equals("O"))){ // jesli pole nie jest puste
                    x++;
                    if (x == 9) {
                        System.out.println("9");
                        playable = false;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void playWinAnimation(Combo combo) {
        line = new Line();
        line.setStartX(combo.tiles[0].getCenterX());
        line.setStartY(combo.tiles[0].getCenterY());
        line.setEndX(combo.tiles[0].getCenterX());
        line.setEndY(combo.tiles[0].getCenterY());

        root.getChildren().add(line);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                new KeyValue(line.endXProperty(), combo.tiles[2].getCenterX()),
                new KeyValue(line.endYProperty(), combo.tiles[2].getCenterY())));
        timeline.play();
    }

    private class Combo { // data structure
        private Tile[] tiles; // array of 3 tiles
        public Combo(Tile... tiles) {
            this.tiles = tiles;
        } // assign tiles to the array

        public boolean isComplete() { // check if combo is complete
            if (tiles[0].getValue().isEmpty()) // if tiles 0 is empty then is complete = false
                return false;

            return tiles[0].getValue().equals(tiles[1].getValue()) // if tiles 0 = tiles 1 and
                    && tiles[0].getValue().equals(tiles[2].getValue()); // tiles 0 = tiles 2 then true -> is completed
        }
    }

    private class Tile extends StackPane {  // tile class
        private Text text = new Text(); // when app starts it creates tile with empty text

        public Tile() {
            Rectangle border = new Rectangle(200, 200); // size of rectangulars
            border.setFill(null); // inside color - transparent
            border.setStroke(Color.BLACK);  // border color BLACK

            text.setFont(Font.font(72)); // size of text font

            setAlignment(Pos.CENTER);
            getChildren().addAll(border, text); // adding border and text to list of children

            setOnMouseClicked(event -> { // able to call this method because of extending StackPane and inherited all methods
                if (!playable) // if playable = false then do nothing else ( clicking disabled)
                    return;

                if (gameVsAI) {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (turnX) {
                            if (drawX()) {
                                turnX = false;
                                checkState();
                                if (!playable) return;
                                labelTurn.setText("Turn: O - Computer");
                            }
                        }
                    }

                    if (playable) {

                        if (!turnX) { // jeśli ruch komputera...

                            if (AIMove()) {
                                checkState();
                                labelTurn.setText("Turn: X - Player");
                                isFirstAIMove = false;
                                turnX = true;
                            }
                        }
                    } else {
                        return;
                    }
                }

                if (!gameVsAI) {

                    if (event.getButton() == MouseButton.PRIMARY) { // primary = left mouse button
                        if (!turnX) // if turnX = false then do nothing on left clicks
                            return;

                        if (drawX()) { // draw X
                            turnX = false;
                            labelTurn.setText("Turn: O");
                            checkState();
                        } else {
                            turnX = true;// set next move to O
                        }

                    } else if (event.getButton() == MouseButton.SECONDARY) { // secondary = right mouse button
                        if (turnX) // if turnX = true then do nothing on right click
                            return;

                        if (drawO()){
                            turnX = true;
                            labelTurn.setText("Turn: X");
                            checkState();
                        } else {
                            turnX = false;
                        }
                    }
                }
            });
        }

        public double getCenterX() {
            return getTranslateX() + 100;
        }

        public double getCenterY() {
            return getTranslateY() + 100;
        }

        public String getValue() { // get text of tile
            return text.getText();
        }

        private boolean drawX() {
            if ((!text.getText().equals("X")) && !(text.getText().equals("O"))){
                text.setText("X");
                checkState();
                return true;
            } else {
                return false;
            }
        }

        private boolean drawO() {
            if ((!text.getText().equals("X")) && !(text.getText().equals("O"))) {
                text.setText("O");
                checkState();
                return true;
            } else {
                return false;
            }
        }
        private boolean drawOAI(int x, int y) {
            if (!(board[x][y].text.getText().equals("X")) && !(board[x][y].text.getText().equals("O"))){
                board[x][y].text.setText("O");

                //turnX=true;

                return true;
            } else {
                return false;

            }
        }
        private void randomField(){
            if (playable){
                boolean done = false;
                Random r = new Random();
                int a,b;
                while (!done) {
                    a = r.nextInt(3);
                    b = r.nextInt(3);
                    if (drawOAI(a,b)) {
                        done = true;
                    }
                }
            }

        }

        private boolean AIMove(){
            if (isFirstAIMove){
                if (drawOAI(1,1)) {
                    return true;
                } else if (drawOAI(0,0)){
                    return true;
                } else if (drawOAI(2,0)){
                    return true;
                } else if (drawOAI(0,2)){
                    return true;
                } else if (drawOAI(2,2)){
                    return true;
                }

            }
            // Jesli komputer znajduje 2 swoje znaki, wtedy wpisuje trzeci i wygrywa.
            for (int i = 0; i < 3; i++) {
                if ((board[i][0].text.getText().equals("O")) && (board[i][1].text.getText().equals("O"))){
                    if (drawOAI(i, 2)) return true;
                }
                if ((board[i][0].text.getText().equals("O")) && (board[i][2].text.getText().equals("O"))){
                    if (drawOAI(i, 1)) return true;
                }
                if ((board[i][1].text.getText().equals("O")) && (board[i][2].text.getText().equals("O"))){
                    if (drawOAI(i, 0)) return true;
                }
                // Sprawdzenie w pionie
                if ((board[0][i].text.getText().equals("O")) && (board[1][i].text.getText().equals("O"))){
                    if (drawOAI(2, i)) return true;
                }
                if ((board[0][i].text.getText().equals("O")) && (board[2][i].text.getText().equals("O"))){
                    if (drawOAI(1, i)) return true;
                }
                if ((board[1][i].text.getText().equals("O")) && (board[2][i].text.getText().equals("O"))){
                    if (drawOAI(0, i)) return true;
                }
            }
            // Sprawdzanie przekatnych
            // Przekatna z lewej do prawej
            if ((board[0][0].text.getText().equals("O")) && (board[1][1].text.getText().equals("O"))){
                if (drawOAI(2, 2)) return true;
            }
            if ((board[0][0].text.getText().equals("O")) && (board[2][2].text.getText().equals("O"))){
                if (drawOAI(1, 1)) return true;
            }
            if ((board[1][1].text.getText().equals("O")) && (board[2][2].text.getText().equals("O"))){
                if (drawOAI(0, 0)) return true;
            }
            // Przekatna z prawej do lewej
            if ((board[2][0].text.getText().equals("O")) && (board[1][1].text.getText().equals("O"))){
                if (drawOAI(0, 2)) return true;
            }
            if ((board[2][0].text.getText().equals("O")) && (board[0][2].text.getText().equals("O"))){
                if (drawOAI(1, 1)) return true;
            }
            if ((board[1][1].text.getText().equals("O")) && (board[0][2].text.getText().equals("O"))){
                if (drawOAI(2, 0)) return true;
            }

            // Sprawdzanie ruchu gracza w pionie i w poziomie - Blokowanie
            for (int i = 0; i < 3; i++) {
                // sprawdzenie w poziomie
                if ((board[i][0].text.getText().equals("X")) && (board[i][1].text.getText().equals("X"))) {
                    if (drawOAI(i, 2)) return true;
                }
                if ((board[i][0].text.getText().equals("X")) && (board[i][2].text.getText().equals("X"))) {
                    if (drawOAI(i, 1)) return true;
                }
                if ((board[i][1].text.getText().equals("X")) && (board[i][2].text.getText().equals("X"))) {
                    if (drawOAI(i,0)) return true;
                }
                // sprawdzenie w pionie
                if ((board[0][i].text.getText().equals("X")) && (board[1][i].text.getText().equals("X"))) {
                    if (drawOAI(2, i)) return true;
                }
                if ((board[0][i].text.getText().equals("X")) && (board[2][i].text.getText().equals("X"))) {
                    if (drawOAI(1, i)) return true;
                }
                if ((board[1][i].text.getText().equals("X")) && (board[2][i].text.getText().equals("X"))) {
                    if (drawOAI(0,i)) return true;
                }
            }

            // Blokowanie po przekatnej z lewej do prawej
            if ((board[0][0].text.getText().equals("X")) && (board[1][1].text.getText().equals("X"))){
                if (drawOAI(2, 2)) return true;
            }
            if ((board[0][0].text.getText().equals("X")) && (board[2][2].text.getText().equals("X"))){
                if (drawOAI(1, 1)) return true;
            }
            if ((board[1][1].text.getText().equals("X")) && (board[2][2].text.getText().equals("X"))){
                if (drawOAI(0, 0)) return true;
            }
            // Blokowanie po przekatnej z prawej do lewej
            if ((board[2][0].text.getText().equals("X")) && (board[1][1].text.getText().equals("X"))){
                if (drawOAI(0, 2)) return true;
            }
            if ((board[2][0].text.getText().equals("X")) && (board[0][2].text.getText().equals("X"))){
                if (drawOAI(1, 1)) return true;
            }
            if ((board[1][1].text.getText().equals("X")) && (board[0][2].text.getText().equals("X"))){
                if (drawOAI(2, 0)) return true;
            }

            if (playable) {
                randomField();
                return true;
            }
            return false;

        }
    }


    public static void main(String[] args) {
        launch(args);
    } // app start
}
