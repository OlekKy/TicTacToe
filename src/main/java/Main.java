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
 * @author Aleksander Kempa, EiT E1
 * @version 1.0
 */
public class Main extends Application {

    /**
     * Flaga ustawiająca/sprawdzająca czy gra (zaznaczanie "X" lub "O") jest możliwa
     */
    private boolean playable = true;

    /**
     * Flaga ustawiająca/sprawdzająca czy gra toczy się przeciwko komputerowi.
     */
    private boolean gameVsAI = false;

    /**
     * Flaga ustawiająca/sprawdzająca czy komputer ma pierwszy ruch.
     */
    private boolean isFirstAIMove = true;

    /**
     * Flaga ustawiająca/sprawdzająca czy gracz "X" ma pierwszy ruch.
     */
    private boolean turnX = true;

    /**
     * Tablica 3x3 odpowiadajaca za pola planszy gry.
     */
    private Tile[][] board = new Tile[3][3];

    /**
     * Lista wszystkich zwycięskich ułożen znaków "X" lub "O".
     */
    private List<Combo> combos = new ArrayList<>();

    /**
     * pole typu Line, jest to linia, która będzie rysowana po wygraniu gry przez któregoś gracza.
     */
    private Line line;

    /**
     * Pole tekstowe, w którym umieszczona jest informacja o tym, który z graczy rozpoczyna grę.
     */
    private Label labelStart;

    /**
     * Pole tekstowe, w którym umieszczona jest informacja o tym, który gracz ma teraz swoją turę.
     */
    private Label labelTurn;

    /**
     * Główny widok aplikacji, zawiera wszystie przyciski, plansze gry oraz pola tekstowe, które są do niego
     * dodawane jako elementy-dzieci.
     */
    private Pane root = new Pane();

    /**
     * Metoda createContent odpowiada za tworzenie zawartości głównego okna programu. Stworzone
     * zostaje okno o podanych wymiarach. Następnie do okna głównego dodawana jest plansza gry, składająca się
     * z 9 identycznych, pustych kwadratów w odpowiednim położeniu. Ustawione są wartości i parametry pól tekstowych.
     * Dodawana jest liczba wszystkich ułożeń znaków, które wygrywają rundę.
     * @return Metoda zwraca nadrzędny element aplikacji wraz z wszystkimi obiektami, które zostaly do niego dodane
     * wewnątrz tej metody.
     */
    private Parent createContent() {
        root.setPrefSize(800, 700);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Tile tile = new Tile();
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

        createCombosList();
        return root;
    }

    /**
     * Metoda dodająca do listy combos wszystkie zwycięskie ułożenia znaków na podstawie położenia elementów.
     * Pierwsza pętla dodaje 3 ułożenia poziome, druga 3 ułożenia pionowe, a dodatkowo na końcu dodawane są obie
     * przekątne.
     */
    public void createCombosList(){
        for (int y = 0; y < 3; y++) {
            combos.add(new Combo(board[0][y], board[1][y], board[2][y]));
        }
        for (int x = 0; x < 3; x++) {
            combos.add(new Combo(board[x][0], board[x][1], board[x][2]));
        }
        combos.add(new Combo(board[0][0], board[1][1], board[2][2]));
        combos.add(new Combo(board[2][0], board[1][1], board[0][2]));
    }

    /**
     * Metoda startująca działanie aplikacji. Umieszczono w niej ustawienie głównej sceny programu treścią wypełnioną
     * w metodzie createContent(). Dodatkowo zaimplementowano przyciski i realizowane przez nie zadania. Elementy te
     * dodowane są do nadrzędnego elementu aplikacji "root".
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();

        /**
         * Implementacja menu, w którym można wybrać czy grę rozpoczyna "X" czy "O". Flaga "turnX" ustawianiaa jest
         * zależnie od dokonanego wyboru.
         */
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


        /**
         * Implementacja przycisku, ktory po naciśnięciu inicjalizuje grę z innym graczem. Flaga gry z AI jest
         * ustawiana na False, a następnie czyszczona jest plansza gry. Dodatkowo ustawiane są flagi "turnX" oraz
         * "playable" na wartośc True, by umożliwić rozpoczęcie rozgrywki.
         */
        Button btnRestart = new Button("Start with Player");
        btnRestart.setTranslateX(650);
        btnRestart.setTranslateY(50);
        btnRestart.setOnAction(event -> {
            gameVsAI = false;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[j][i].text.setText(null);
                    root.getChildren().remove(line);
                }
            }
            turnX = true;
            playable = true;
        });
        root.getChildren().add(btnRestart);

        /**
         * Implementacja przycisku odpowiedzialnego za rozpoczęcie gry przeciwko komputerowi. Ustawia flagę
         * gameVsAI na True, czyści planszę gry, a następnie ustawia odpowiednio pozostałe flagi i zmienia
         * treść pola tekstowego.
         */
        Button btnSelectStart = new Button("Start with AI");
        btnSelectStart.setTranslateX(650);
        btnSelectStart.setTranslateY(100);
        btnSelectStart.setOnAction(event -> {
            gameVsAI = true;
            root.getChildren().remove(line);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[j][i].text.setText(null);
                }
            }
            isFirstAIMove = true;
            root.getChildren().remove(line);
            labelTurn.setText("Turn: X - Player");
            turnX = true;
            playable = true;
        });
        root.getChildren().add(btnSelectStart);

        /**
         * Implementacja przycisku zamykającego aplikację. Po jego kliknięciu zamykany jest primaryStag.
         */
        Button btnExit = new Button("Exit");
        btnExit.setTranslateX(650);
        btnExit.setTranslateY(200);
        btnExit.setOnAction(event -> {
            primaryStage.close();
        });
        root.getChildren().add(btnExit);

    }

    /** Metoda sprawdzająca czy któryś z graczy wygrał rundę. Jeśli tak, dalsza rozgrywka jest blokowana
     * i zostaje narysowana linia przebiegająca po polach planszy z wygrywającym układem.
     */
    private void checkState() {
        if (!isGameOver()){
            for (Combo combo : combos) {
                if (combo.isComplete()) {
                    playable = false;
                    playWinAnimation(combo);
                    break;
                }
            }
        }
    }

    /**
     * Metoda sprawdzająca czy gra została ukończona, poprzez sprawdzenie zawartości wszystkich pól na planszy.
     * @return Zwraca True jeżeli w każdym z 9ciu pól znajduje się znak "X" lub "O".
     */
    private boolean isGameOver(){
        int x = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((board[j][i].text.getText().equals("X")) || (board[j][i].text.getText().equals("O"))){
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

    /**
     * Metoda odpowiadająca za rysowanie animacji kreślenia linii po ułożonym, zwycięskim układzie znaków na planszy.
     * @param combo Zwycięskie ułożenie znaków "X" lub "O" na polach planszy.
     */
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


    /**
     * Klasa prywatna Combo, posiadająca konstruktor oraz metodę isComplete. Sklada się z pól planszy gry, które
     * nalężą (lub mogą nalezeć) do zwycięskiego ułożenia układu znaków.
     */
    private class Combo {
        /**
         *  Tablica pol planszy gry, składających się na zwycięskie ułożenie układu znaków. Ma 3 elementy.
         */
        private Tile[] tiles;

        /**
         * Konstruktor klasy Combo.
         * @param tiles Przypisanie elementów do tablicy Tile
         */
        public Combo(Tile... tiles) {
            this.tiles = tiles;
        }

        /**
         * Metoda sprawdzająca czy zwycięskie ułożenie znaków "X" lub "O" zostało spełnione
         * @return Zwraca False jeśli element jest pusty. Natomiast jeśli zawartości wszystkich pól w układzie są
         * takie same, metoda zwraca True.
         */
        public boolean isComplete() {
            if (tiles[0].getValue().isEmpty())
                return false;

            return tiles[0].getValue().equals(tiles[1].getValue())
                    && tiles[0].getValue().equals(tiles[2].getValue());
        }
    }

    /**
     * Prywatna Klasa Tile, która rozszerza klasę StackPane, dzięki czemu możliwa jest konfiguracja
     * pól składających się na planszę gry,
     */
    private class Tile extends StackPane {

        /**
         * Pole tekstowe, które jest tworzone podczas startu aplikacji. Domyślnie jest puste.
         */
        private Text text = new Text();

        /**
         * Konstruktor klasy Tile. Odpowiada za stworzenie pojedynczego elementu (Kwadratu) planszy.
         * Tworzy kwadrat o wymiarach 200x200, o przeźroczystym wypełnieniu i czarnym kolorze krawędzi.
         * Zawiera implementację operacji wykonywach po kliknięciu na dany kwadrat.
         */
        public Tile() {
            Rectangle border = new Rectangle(200, 200);
            border.setFill(null);
            border.setStroke(Color.BLACK);

            text.setFont(Font.font(72));

            setAlignment(Pos.CENTER);
            getChildren().addAll(border, text);

            /**
             * Po kliknięciu na dany kwadrat wykonuje operacje zaleznie od spełnionych warunków. Jeśli gra jest w trybie
             * "playable" to wykonaj akcje zależnie czy gra toczy się przeciwko AI lub przeciwko Graczowi 2.
             */
            setOnMouseClicked(event -> {
                if (!playable)
                    return;

                /**
                 * Gdy gra toczy się przeciwko komputerowi to gracz wykonuje swój ruch za pomocą lewego klawisza myszy.
                 * Następuje zmiana statusu tury oraz sprawdzany jest stan gry. Ruch wykonywane przez komputer jest
                 * zgodny z zaimplementowanym algorytmem w metodzie AIMove().
                 */
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
                        if (!turnX) {
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

                /**
                 * Jeśli gra toczy się przeciwko graczowi 2, to po każdym wykonaniu ruchu tura przechodzi do przeciwnego gracza.
                 * Pierwszy gracz rysuje "X" za pomocą lewego klawisza myszy, a drugi za pomocą prawego. Odpowiednio z turami
                 * zmieniane są również wartości pola tekstowego labelTurn.
                 */
                if (!gameVsAI) {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (!turnX)
                            return;

                        if (drawX()) {
                            turnX = false;
                            labelTurn.setText("Turn: O");
                            checkState();
                        } else {
                            turnX = true;
                        }

                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        if (turnX)
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

        /**
         * Metoda zwracająca środek w osi X.
         * @return Srodek osi X po przetworzeniu.
         */
        public double getCenterX() {
            return getTranslateX() + 100;
        }

        /**
         * Metoda zwracająca środek w osi Y.
         * @return Srodek osi Y po przetworzeniu.
         */
        public double getCenterY() {
            return getTranslateY() + 100;
        }

        /**
         * Metoda zwracająca wartość tekstu z pola text.
         * @return Zwraca tekst z pola text.
         */
        public String getValue() {
            return text.getText();
        }

        /**
         * Metoda odpowiadająca za narysowanie znaku "X" przez gracza, jesli wybrane pole nie jest już zajęte.
         * @return True lub False, zależnie czy operacja narysowania się powiedzie lub nie.
         */
        private boolean drawX() {
            if ((!text.getText().equals("X")) && !(text.getText().equals("O"))){
                text.setText("X");
                checkState();
                return true;
            } else {
                return false;
            }
        }

        /**
         * Metoda odpowiadająca za narysowanie znaku "O" przez gracza, jesli wybrane pole nie jest już zajęte.
         * @return True lub False, zależnie czy operacja narysowania się powiedzie lub nie.
         */
        private boolean drawO() {
            if ((!text.getText().equals("X")) && !(text.getText().equals("O"))) {
                text.setText("O");
                checkState();
                return true;
            } else {
                return false;
            }
        }

        /**
         * Metoda odpowiedzialna za narysowanie znaku "O" przez komputer z wybranym, pustym polu na planszy.
         * @param x Określa położenie pola planszy w orientacji poziomej.
         * @param y Określa położenie pola planszy w orinetacji pionowej.
         * @return True lub false, zależnie czy operacja narysowania znaku "O" się powiedzie lub nie.
         */
        private boolean drawOAI(int x, int y) {
            if (!(board[x][y].text.getText().equals("X")) && !(board[x][y].text.getText().equals("O"))){
                board[x][y].text.setText("O");
                return true;
            } else {
                return false;
            }
        }

        /**
         * Metoda wpisująca znak "O" podczas gry z komputerem w wylosowane, niezajęte pole na planszy.
         */
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

        /**
         * Metoda odpowiadająca za wykonanie ruchu przez komputer. Została zaimplementowana zgodnie z algorytmem,
         * który ma za zadanie takie wykonywanie ruchów przez komputer, by komputer nie przegrał rundy. W pierwszej
         * kolejności sprawdzane jest czy wykonywany ruch jest pierwszym, a jeśli tak, to wykonywany jest taki ruch,
         * by zablokować graczowi jak najwięcej możliwości ułożenia układu. Jeśli wykonywany ruch jest drugim lub
         * kolejnym, komputer w pierwszej kolejności przeszukuje plansze gry w poszukiwaniu dwóch swoich znaków "O"
         * i gdy je znajdzie, stara się wrysować trzeci tak, by ułożyć układ i wygrać rundę. Jeśli nie znajduje, wtedy
         * przeszukuje planszę w poszukiwaniu dwóch znaków gracza i wpisuje swój "O" w takie pole, by uniemożliwić graczowi
         * wygranie gry w kolejnej rundzie. Jeśli żaden z powyższych scenariuszy nie jest spełniony to znak "O" jest
         * rysowany w wylosowanym, niezajętym polu.
         * @return Zwraca True jeśli narysowanie znaku "O" sie powiedzie, jeśli nie - zwraca False.
         */
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

            if ((board[0][0].text.getText().equals("O")) && (board[1][1].text.getText().equals("O"))){
                if (drawOAI(2, 2)) return true;
            }
            if ((board[0][0].text.getText().equals("O")) && (board[2][2].text.getText().equals("O"))){
                if (drawOAI(1, 1)) return true;
            }
            if ((board[1][1].text.getText().equals("O")) && (board[2][2].text.getText().equals("O"))){
                if (drawOAI(0, 0)) return true;
            }

            if ((board[2][0].text.getText().equals("O")) && (board[1][1].text.getText().equals("O"))){
                if (drawOAI(0, 2)) return true;
            }
            if ((board[2][0].text.getText().equals("O")) && (board[0][2].text.getText().equals("O"))){
                if (drawOAI(1, 1)) return true;
            }
            if ((board[1][1].text.getText().equals("O")) && (board[0][2].text.getText().equals("O"))){
                if (drawOAI(2, 0)) return true;
            }

            for (int i = 0; i < 3; i++) {
                if ((board[i][0].text.getText().equals("X")) && (board[i][1].text.getText().equals("X"))) {
                    if (drawOAI(i, 2)) return true;
                }
                if ((board[i][0].text.getText().equals("X")) && (board[i][2].text.getText().equals("X"))) {
                    if (drawOAI(i, 1)) return true;
                }
                if ((board[i][1].text.getText().equals("X")) && (board[i][2].text.getText().equals("X"))) {
                    if (drawOAI(i,0)) return true;
                }

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


            if ((board[0][0].text.getText().equals("X")) && (board[1][1].text.getText().equals("X"))){
                if (drawOAI(2, 2)) return true;
            }
            if ((board[0][0].text.getText().equals("X")) && (board[2][2].text.getText().equals("X"))){
                if (drawOAI(1, 1)) return true;
            }
            if ((board[1][1].text.getText().equals("X")) && (board[2][2].text.getText().equals("X"))){
                if (drawOAI(0, 0)) return true;
            }

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

    /**
     * Metoda rozpoczynająca działąnie aplikacji.
     * @param args Argumenty.
     */
    public static void main(String[] args) {
        launch(args);
    }
}