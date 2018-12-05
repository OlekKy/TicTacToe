import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private boolean playable = true; // game plays
    private boolean turnX = true; // set to start always with X !!!!@@@!!!!
    private Tile[][] board = new Tile[3][3];
    private List<Combo> combos = new ArrayList<>(); // List of combos
    private Line line;
    private Label labelStart;
    private Pane root = new Pane();

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


        // all possible combos
        // horizontal
        for (int y = 0; y < 3; y++) {
            combos.add(new Combo(board[0][y], board[1][y], board[2][y]));
        }

        // vertical
        for (int x = 0; x < 3; x++) {
            combos.add(new Combo(board[x][0], board[x][1], board[x][2]));
        }

        // diagonals
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

//      play with computer game


        });
        root.getChildren().add(btnSelectStart);

        // button exit app
        Button btnExit = new Button("EXIT");
        btnExit.setTranslateX(650);
        btnExit.setTranslateY(200);
        btnExit.setOnAction(event -> {
            primaryStage.close();
        });
        root.getChildren().add(btnExit);

    }

    private void checkState() { // check the combo is up
        for (Combo combo : combos) {
            if (combo.isComplete()) {
                playable = false;
                playWinAnimation(combo);
                break;
            }
        }
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

                if (event.getButton() == MouseButton.PRIMARY) { // primary = left mouse button
                    if (!turnX) // if turnX = false then do nothing on left clicks
                        return;

                    drawX(); // draw X
                    turnX = false; // set next move to O
                    checkState(); // after draw X check state of game
                }
                else if (event.getButton() == MouseButton.SECONDARY) { // secondary = right mouse button
                    if (turnX) // if turnX = true then do nothing on right click
                        return;

                    drawO(); // draw O
                    turnX = true;   // set next move to X
                    checkState(); // after draw O check state of game
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

        private void drawX() {
            text.setText("X");
        }

        private void drawO() {
            text.setText("O");
        }
    }


    public static void main(String[] args) {
        launch(args);
    } // app start
}
