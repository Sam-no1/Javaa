import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author: Samyak Gurung
 * Description: The JavaFX front end for the Cryptogram game. The board is a GridPane of
 * VBoxes (a 1-char TextField over the encrypted letter). Typing a letter goes
 * View -> Controller -> Model, and the Model tells this View which letter changed
 * through the Observer pattern.
 * */
public class CryptogramGUIView extends Application implements Observer {
    private static final int COLUMNS = 30;

    private CryptogramModel model;
    private CryptogramController controller;

    private GridPane gridPane;
    private GridPane freqTable;
    private Button hintButton;

    // every editable TextField on the board, in order (for moving the cursor)
    private List<TextField> letterFields = new ArrayList<>();
    // encrypted letter -> all the TextFields for that letter
    private ArrayMap<Character, List<TextField>> letterBoxes = new ArrayMap<>();
    private boolean gameOver;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        model = new CryptogramModel();
        controller = new CryptogramController(model);
        model.addObserver(this);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        gridPane = new GridPane();
        gridPane.setHgap(4);
        gridPane.setVgap(12);
        gridPane.setPadding(new Insets(10));
        root.setCenter(gridPane);
        root.setRight(rightPanel());

        showPuzzle();

        Scene scene = new Scene(root, 1250, 500);
        primaryStage.setTitle("Cryptograms");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.toFront();
    }

    /**
     * Fills the GridPane with one VBox per character of the encrypted quote,
     * at most COLUMNS per row, breaking rows between words.
     */
    private void setupBoard() {
        gridPane.getChildren().clear();
        letterFields.clear();
        letterBoxes = new ArrayMap<>();

        List<String> lines = controller.splitIntoLines(controller.getEncryptedQuote(), COLUMNS);
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int col = 0; col < line.length(); col++) {
                createLetterBox(line.charAt(col), gridPane, col, row);
            }
        }
    }

    /**
     * Makes the VBox for one character: a TextField on top of a Label with the encrypted letter
     * @param eChar encrypted character
     * @param gridPane board to add it to
     * @param col column in the board
     * @param row row in the board
     * @return the VBox that was added
     */
    public VBox createLetterBox(char eChar, GridPane gridPane, int col, int row) {
        TextField text = new TextField();
        text.setPrefColumnCount(1);
        text.setPrefWidth(30);
        text.setAlignment(Pos.CENTER);

        Label label = new Label(String.valueOf(eChar));
        label.setStyle("-fx-font-weight: bold;");

        if (eChar < 'A' || eChar > 'Z') {
            text.setText(String.valueOf(eChar));
            text.setDisable(true);
        } else {
            letterFields.add(text);
            if (!letterBoxes.containsKey(eChar)) {
                letterBoxes.put(eChar, new ArrayList<>());
            }
            letterBoxes.get(eChar).add(text);
            // Only update() changes the text, so eat the typed characters
            text.addEventFilter(KeyEvent.KEY_TYPED, Event::consume);
            text.setOnKeyPressed(event -> handleKey(event, eChar, text));
        }

        VBox vbox = new VBox(4, text, label);
        vbox.setAlignment(Pos.CENTER);
        gridPane.add(vbox, col, row);
        return vbox;
    }

    /**
     * KeyPressed handler for a letter box. Sends the guess to the controller,
     * the model then notifies update() which fills the boxes.
     * @param event the key event
     * @param eChar the encrypted letter this box stands for
     * @param text the box that was typed in
     */
    private void handleKey(KeyEvent event, char eChar, TextField text) {
        KeyCode code = event.getCode();
        if (code == KeyCode.TAB || code.isArrowKey()) {
            return;
        }
        event.consume();
        if (gameOver || event.isShortcutDown()) {
            return;
        }

        if (code.isLetterKey()) {
            controller.makeReplacement(eChar, code.getChar().charAt(0));
            focusNext(text);
            checkForWin();
        } else if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
            controller.makeReplacement(eChar, ' ');
        }
    }

    /**
     * Moves the cursor to the next box that can be typed in
     * @param text the current box
     */
    private void focusNext(TextField text) {
        int i = letterFields.indexOf(text);
        if (i >= 0 && i + 1 < letterFields.size()) {
            letterFields.get(i + 1).requestFocus();
        }
    }

    /**
     * If the puzzle is solved, lock the board and show the win Alert
     */
    private void checkForWin() {
        if (gameOver || !controller.isGameOver()) {
            return;
        }
        gameOver = true;
        for (TextField field : letterFields) {
            field.setEditable(false);
        }
        hintButton.setDisable(true);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText("Message");
        alert.setContentText("You won!");
        alert.showAndWait();
    }

    /**
     * Picks a new quote and redraws the board and frequency table
     */
    private void newPuzzle() {
        try {
            controller.newGame();
        } catch (FileNotFoundException e) {
            new Alert(AlertType.ERROR, "Could not find quotes.txt").showAndWait();
            return;
        }
        showPuzzle();
    }

    /**
     * Draws the models current puzzle and unlocks the board
     */
    private void showPuzzle() {
        gameOver = false;
        hintButton.setDisable(false);
        setupBoard();
        updateFrequencies();
    }

    /**
     * Builds the right side: New Puzzle and Hint buttons, the frequency checkbox,
     * and the (hidden at first) frequency table
     * @return the panel
     */
    public VBox rightPanel() {
        Button newButton = new Button("New Puzzle");
        newButton.setOnAction(event -> newPuzzle());

        hintButton = new Button("Hint");
        hintButton.setOnAction(event -> {
            if (!gameOver) {
                controller.giveHint();
                checkForWin();
            }
        });

        freqTable = new GridPane();
        freqTable.setHgap(15);
        freqTable.setVgap(2);
        freqTable.setVisible(false);
        freqTable.managedProperty().bind(freqTable.visibleProperty());

        CheckBox freqBox = new CheckBox("Show Freqs");
        freqBox.setSelected(false);
        freqBox.setOnAction(event -> freqTable.setVisible(freqBox.isSelected()));

        VBox panel = new VBox(10, newButton, hintButton, freqBox, freqTable);
        panel.setPadding(new Insets(10, 0, 10, 20));
        panel.setAlignment(Pos.TOP_LEFT);
        return panel;
    }

    /**
     * Fills the frequency table with A-Z counts from the encrypted quote, in two columns
     */
    private void updateFrequencies() {
        freqTable.getChildren().clear();
        for (char c = 'A'; c <= 'Z'; c++) {
            int i = c - 'A';
            Label label = new Label(c + ": " + controller.getFrequency(c));
            label.setStyle("-fx-font-family: monospace;");
            freqTable.add(label, i / 13, i % 13);
        }
    }

    /**
     * Called by the model after a guess. arg is the encrypted letter that changed,
     * so only the boxes for that letter are updated.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof Character)) {
            return;
        }
        char changed = (Character) arg;
        Character guess = controller.getGuess(changed);
        String newText = (guess == null) ? "" : String.valueOf(guess);
        List<TextField> boxes = letterBoxes.get(changed);
        if (boxes != null) {
            for (TextField box : boxes) {
                box.setText(newText);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
