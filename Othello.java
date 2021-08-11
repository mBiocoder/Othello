import javafx.animation.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import szte.mi.Move;
import szte.mi.Player;

import java.util.Random;

public class Othello extends Application{
    //Attributes
    static final String BOARD_GAME_NAME = "Othello";
    static final int BOARD_SIZE = 8;
    static final int BOX_SIZE = 40;
    static final int TURNCOINTIME = 500;

    Board board;

    private Player oPlayer1, oPlayer2, lastPlayer;
    private PlayerMove lastPlayerMove = null;
    private boolean bGameStarted = false;

    //Players and Getters and Setters
    private char currentOpponent = 'W';
    public char getCurrentOpponent() {
        return currentOpponent;
    }
    public void setCurrentOpponent(char currentOpponent) {
        this.currentOpponent = currentOpponent;
    }

    //Scores, getter, setter
    private int blackOwnerPoints = 0;
    private int whiteOwnerPoints = 0;
    public int getBlackOwnerPoints() {
        return blackOwnerPoints;
    }
    public void setBlackOwnerPoints(int blackOwnerPoints) {
        this.blackOwnerPoints = blackOwnerPoints;
    }
    public int getWhiteOwnerPoints() {
        return whiteOwnerPoints;
    }
    public void setWhiteOwnerPoints(int whiteOwnerPoints) {
        this.whiteOwnerPoints = whiteOwnerPoints;
    }

    char startingOwner = Board.BLACK;
    public char getCurrentTurn() {
        return currentTurn;
    }
    public void setCurrentTurn(char currentTurn) {
        this.currentTurn = currentTurn;
    }
    char currentTurn = Board.BLACK;

    static FlowPane root;
    static OthelloPane othelloPane;
    static TitleLabel ownerTurnLabel;

    MiniMaxPlayer miniMaxPlayer = new MiniMaxPlayer();

    //Constructor
    public Othello(){
        this.resetGame();
    }

    //Methods
    //Reset the game
    private void resetGame() {
        bGameStarted = false;
        board = new Board();
        board.init();
            if (othelloPane == null)
            {
                othelloPane = new OthelloPane(8, 40, 500.0);
            }

            GUIPlayerClass oGUIPlayer = new GUIPlayerClass();
            oPlayer2 = oGUIPlayer; //human player

            Random rnd = new Random();

            MiniMaxPlayer miniMaxPlayer = new MiniMaxPlayer();
            miniMaxPlayer.init(1, 0L, rnd);
            oPlayer1 = miniMaxPlayer;
            lastPlayer = oPlayer2;
            lastPlayerMove = null;
            othelloPane.update(miniMaxPlayer.board1);
    }

    //Initialize
    @Override
    public void init() {
        System.out.println("Init called");
        System.out.println(currentTurn + " is to play currently");
        board.displayBoard(board.getBoard());

        setBlackOwnerPoints(board.count(Board.BLACK));
        setWhiteOwnerPoints(board.count(Board.WHITE));

        //print on console too
        System.out.println("Black score: " + blackOwnerPoints);
        System.out.println("White score: " + whiteOwnerPoints);
        System.out.println();
    }

    @Override
    public void start(Stage stage) {
        //System.out.println("Start called");
        System.out.println("Bitte zum Spielen zum Vollbildmodus wechseln!");

        root = new FlowPane(Orientation.VERTICAL);
        root.setAlignment(Pos.CENTER);

        //Set backgound of flowPane
        Image imageBG = new Image("file:src\\resources\\Beach.png");
        BackgroundSize bSize = new BackgroundSize(700, 700, false, false, true, false);

        Background background2 = new Background(new BackgroundImage(imageBG,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bSize));

        root.setBackground(new Background(new BackgroundImage(imageBG,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bSize)));

        //Set padding to flowpane
        root.setPadding(new Insets(15, 15, 15, 15));
        root.setLayoutX(10.0);
        root.setLayoutY(10.0);

        ownerTurnLabel = new TitleLabel(0, true);
        othelloPane = new OthelloPane(BOARD_SIZE, BOX_SIZE, TURNCOINTIME);

        root.getChildren().addAll(ownerTurnLabel, othelloPane);

        //Call methods
        updateOwnerTurnTitle();
        playGUIGame(lastPlayer);
        createAndShowGUI(stage);
    }


    public void playGUIGame(Player lastPlayer) {
                    if (lastPlayer instanceof GUIPlayerClass) {//check where the lastPlayer is an instance of
                        //System.out.println("we are in guiplayer");

                        //Passing
                        if (board.getPlaceableLocations(currentTurn, currentOpponent).isEmpty() && othelloPane.isGameOver() == false) {
                            System.out.println("passing to: " + currentOpponent);
                            showMessageBox("Passing to opponent", "You can't make any possible moves.");
                            this.lastPlayer = oPlayer1;
                            playGUIGame(this.lastPlayer);
                        }

                        for (int row = 0; row < BOARD_SIZE; row++) {
                            for (int column = 0; column < BOARD_SIZE; column++) {
                                final Owner currentOwner = othelloPane.getOwner(row, column);

                                final int f_row = (row);
                                final int f_column = (column);
                                //What to do when Button on board is clicked
                                othelloPane.getBox(row, column).setOnMouseClicked(event -> {
                                    if (currentOwner.getType() == Board.EMPTY && board.isLegal(f_row, f_column, currentTurn)) {
                                        try {
                                            System.out.println("This is where all we can place: "+board.getPlaceableLocations(currentTurn, currentOpponent));
                                            board.setMove(f_row, f_column, currentTurn, currentOpponent, board.getBoard());
                                            miniMaxPlayer.board1.setMove(f_row, f_column, currentTurn, currentOpponent, miniMaxPlayer.board1.getBoard());
                                            lastPlayerMove = new PlayerMove(f_row, f_column, currentTurn);

                                            System.out.println("Setting move from button on board!");
                                            board.displayBoard(board.getBoard());

                                            setBlackOwnerPoints(board.count(Board.BLACK));
                                            setWhiteOwnerPoints(board.count(Board.WHITE));

                                            //print on console the scores
                                            System.out.println("Black score: " + blackOwnerPoints);
                                            System.out.println("White score: " + whiteOwnerPoints);
                                            System.out.println();

                                        } catch (NullPointerException e) {
                                            e.getCause();
                                        }
                                        //Update the board
                                        currentOwner.setType(currentTurn);
                                        othelloPane.updateBoardForFlips(f_row, f_column);

                                        //Check for a winner
                                        if (board.announceWinner(board.getPlaceableLocations(currentTurn, currentOpponent), board.getPlaceableLocations(currentOpponent, currentTurn)) == 0) {
                                            ownerTurnLabel.setText("It is a Draw!");
                                            System.exit(0);
                                        } else if (board.announceWinner(board.getPlaceableLocations(currentTurn, currentOpponent), board.getPlaceableLocations(currentOpponent, currentTurn)) == 1) {
                                            ownerTurnLabel.setText("White has won the game!");
                                            System.exit(0);
                                        } else if (board.announceWinner(board.getPlaceableLocations(currentTurn, currentOpponent), board.getPlaceableLocations(currentOpponent, currentTurn)) == -1) {
                                            ownerTurnLabel.setText("Black has won the game!");
                                            System.exit(0);
                                        }

                                        //Change lastPlayer
                                        this.lastPlayer = oPlayer1;

                                        //Recursive call
                                        playGUIGame(this.lastPlayer);
                                    }else {
                                        System.out.println("This is not a valid move! Try again.");
                                        showMessageBox("Invalid move!", "Try again!");
                                    }
                                });
                            }
                        }
                    }else if (lastPlayer instanceof MiniMaxPlayer){
                        //Change values to match minimaxPlayer
                        nextcurrentPlayerTurn();
                        nextopponentPlayerTurn();
                        updateOwnerTurnTitle();

                        //Passing
                        if (board.getPlaceableLocations(currentTurn, currentOpponent).isEmpty() && othelloPane.isGameOver() == false) {
                            System.out.println("passing to: " + currentOpponent);
                            showMessageBox("Passing to opponent", "Computer can't make any possible moves.");
                            this.lastPlayer = oPlayer2;
                            playGUIGame(this.lastPlayer);
                        }

                        if (currentTurn == Board.WHITE) { //AI is to play and uses the Minimax algorithm
                            Timeline timeLine = new Timeline(new KeyFrame(Duration.millis(TURNCOINTIME), ev -> {
                                miniMaxPlayer.setMyColor(currentTurn);
                                miniMaxPlayer.setOppColor(currentOpponent);

                                try{
                                    System.out.println(lastPlayerMove);
                                    System.out.println(miniMaxPlayer.board1.getPlaceableLocations(miniMaxPlayer.myColor, miniMaxPlayer.oppColor));

                                    //Call nextMove in MinimaxPlayer
                                    Move move = miniMaxPlayer.nextMove(lastPlayerMove, 0L, 0L);

                                    setBlackOwnerPoints(miniMaxPlayer.board1.count(Board.BLACK));
                                    setWhiteOwnerPoints(miniMaxPlayer.board1.count(Board.WHITE));

                                    //What to do when clicked -> Button click will be simulated by Robot.click()
                                    othelloPane.getBox(move.x, move.y).setOnMouseClicked(event -> {
                                        board.setMove(move.x, move.y, currentTurn, currentOpponent, board.getBoard());
                                        lastPlayerMove = new PlayerMove(move.x, move.y, currentTurn);

                                        setBlackOwnerPoints(board.count(Board.BLACK));
                                        setWhiteOwnerPoints(board.count(Board.WHITE));

                                        //print on console the scores
                                        System.out.println("Black score: " + blackOwnerPoints);
                                        System.out.println("White score: " + whiteOwnerPoints);
                                        System.out.println();

                                        //Update board
                                        final Owner currentOwner = othelloPane.getOwner(move.x, move.y);
                                        currentOwner.setType(currentTurn);
                                        othelloPane.updateBoardForFlips(move.x, move.y);

                                        //Change values
                                        nextcurrentPlayerTurn();
                                        nextopponentPlayerTurn();
                                        updateOwnerTurnTitle();

                                        //Check for win
                                        if (board.announceWinner(board.getPlaceableLocations(currentTurn, currentOpponent), board.getPlaceableLocations(currentOpponent, currentTurn)) == 0) {
                                            ownerTurnLabel.setText("It is a Draw!");
                                            System.exit(0);
                                        } else if (board.announceWinner(board.getPlaceableLocations(currentTurn, currentOpponent), board.getPlaceableLocations(currentOpponent, currentTurn)) == 1) {
                                            ownerTurnLabel.setText("White has won the game!");
                                            System.exit(0);
                                        } else if (board.announceWinner(board.getPlaceableLocations(currentTurn, currentOpponent), board.getPlaceableLocations(currentOpponent, currentTurn)) == -1) {
                                            ownerTurnLabel.setText("Black has won the game!");
                                            System.exit(0);
                                        }

                                        //Update lastPlayer
                                        this.lastPlayer = oPlayer2;

                                        //Recursive call
                                        playGUIGame(this.lastPlayer);
                                    });
                                    //Simulate button click
                                    Robot.click(othelloPane.getBox(move.x, move.y));
                                }catch (NullPointerException e){
                                    e.getCause();
                                }
                            }));
                            timeLine.play();
                        }
                    }
    }

    //Change values
    public void nextcurrentPlayerTurn() {
        currentTurn = (currentTurn == Board.BLACK) ? Board.WHITE : Board.BLACK;
    }
    public void nextopponentPlayerTurn(){
        currentOpponent = (currentOpponent == Board.BLACK) ? Board.WHITE : Board.BLACK;
    }
    public void updateOwnerTurnTitle() {
        ownerTurnLabel.setText(currentTurn + " Player's Turn");
    }

    //Show the board
    public void createAndShowGUI(Stage stage) {
        stage.setTitle(BOARD_GAME_NAME);
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.show();
    }

    //Print message
    public void showMessageBox(String sTitle, String sHeader){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, null, ButtonType.OK);
        alert.setTitle(sTitle);
        alert.setHeaderText(sHeader);

        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(350, 250);
        alert.showAndWait();
    }

    //Main method
    public static void main(String[] args) {
        launch(args);
    }
}

//Inner class
class Robot {
    public static void click(Node node) {
        Event.fireEvent(node, new MouseEvent(MouseEvent.MOUSE_CLICKED, node.getLayoutX()/2, node.getLayoutY()/2, node.getLayoutX()/2, node.getLayoutY()/2, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null));
    }
}

//Inner class
class OthelloPane extends GridPane {
    //Atrributes
    private int boardSize;
    private int boxSize;
    private Duration flipTime;
    private int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

    private Owner[][] owners;
    private Pane[][] boxes;

    //methods
    //Constructor
    public OthelloPane(int boardSize, int boxSize, double flipDuration) {
        super();

        owners = new Owner[boardSize][boardSize];
        boxes = new Pane[boardSize][boardSize];

        this.boardSize = boardSize;
        this.boxSize = boxSize;
        this.flipTime = Duration.millis(flipDuration);

        //Initialize the board and setup grid constaints
        for (int i = 0; i < boardSize; i++)
            getRowConstraints().add( new RowConstraints(boxSize));
        for (int i = 0; i < boardSize; i++)
            getColumnConstraints().add( new ColumnConstraints(boxSize));

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Pane box = new Pane();
                Owner owner = new Owner(boxSize, Board.EMPTY);
                box.setBackground(new Background(new BackgroundFill(Color.BURLYWOOD, null, null)));
                box.getChildren().add(owner);
                setOwner(i, j, owner);
                setBox(i, j, box);
                add(box, j, i);
            }
        }

        //Allows for a boardsize != 8 aswell
        int midPoint = boardSize/2;
        Owner bottomLeft = getOwner(midPoint, midPoint - 1);
        Owner bottomRight = getOwner(midPoint, midPoint);
        Owner topLeft = getOwner(midPoint - 1, midPoint - 1);
        Owner topRight = getOwner(midPoint - 1, midPoint);

        //Set initial black and white coins
        topLeft.setType(Board.WHITE);
        bottomRight.setType(Board.WHITE);
        topRight.setType(Board.BLACK);
        bottomLeft.setType(Board.BLACK);

        //Makes line constraints visible true
        setGridLinesVisible(true);
        setAlignment(Pos.CENTER);
    }

    //Check if game is Over
    public boolean isGameOver() {
        boolean containsBlankBox = false;

        for (Owner[] row: owners) {
            for (Owner owner: row) {
                if (owner.getType() == Board.EMPTY) {
                    containsBlankBox = true;
                }
            }
        }

        return !containsBlankBox;
    }

    //Update the board
      public void update(Board oBoard){
        Owner oPlayerOnField = null;
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                if ((oPlayerOnField != null) ){
                    oPlayerOnField.setType(oBoard.getPlayer(i,j, oBoard.getBoard())); //currentPlayer on board
                }
                this.setOwner(i,j, oPlayerOnField);
            }
        }
    }

    // flip related
    public void updateBoardForFlips(int originalRow, int originalColumn) {
        char[][] ownerTypes = new char[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                ownerTypes[i][j] = getOwner(i, j).getType();

        for (int[] directionGroup: directions) {
            int rowDirection = directionGroup[0];
            int columnDirection = directionGroup[1];
            if (flipping(originalRow, originalColumn, rowDirection, columnDirection, 'n')) {
                flipping(ownerTypes, originalRow, originalColumn, rowDirection, columnDirection);
            }
        }

    }

    //Flipping in a direction
    public void flipping(char[][] ownerTypes, int originalRow, int originalColumn, int rowDirection, int columnDirection) {
        char originalOwnerType = ownerTypes[originalRow][originalColumn];

        int row = originalRow + rowDirection;
        int column = originalColumn + columnDirection;

        while (row < boardSize && row >= 0 && column < boardSize && column >= 0) {
            char ownerType = ownerTypes[row][column];

            if (ownerType == Board.EMPTY || ownerType == originalOwnerType) {
                break;
            }

            Owner owner = getOwner(row, column);

            RotateTransition firstRotator = new RotateTransition(flipTime, owner);
            firstRotator.setAxis(Rotate.Y_AXIS);
            firstRotator.setFromAngle(0);
            firstRotator.setToAngle(90);
            firstRotator.setInterpolator(Interpolator.LINEAR);
            firstRotator.setOnFinished(e -> owner.setType(originalOwnerType));

            RotateTransition secondRotator = new RotateTransition(flipTime, owner);
            secondRotator.setAxis(Rotate.Y_AXIS);
            secondRotator.setFromAngle(90);
            secondRotator.setToAngle(180);
            secondRotator.setInterpolator(Interpolator.LINEAR);

            new SequentialTransition(firstRotator, secondRotator).play();

            row += rowDirection;
            column += columnDirection;
        }
    }

    //Is in flipdirection
    public boolean flipping(int originalRow, int originalColumn, int directionRow, int directionCOlumn, char optionalOwnerType) {
        // optional parameter
        char originalOwnerType = (optionalOwnerType == 'n') ? getOwner(originalRow, originalColumn).getType() : optionalOwnerType;
        int column = originalColumn + directionCOlumn;
        int row = originalRow + directionRow;

        int count = 0;
        while (row < boardSize && row >= 0 && column < boardSize && column >= 0) {
            Owner owner = getOwner(row, column);
            char ownerType = owner.getType();

            if (ownerType == Board.EMPTY || owner.getFill() == Color.YELLOW) {
                break;
            } else if (ownerType == originalOwnerType) {
                return count > 0;
            }

            row += directionRow;
            column += directionCOlumn;
            count++;
        }

        return false;
    }

    // getters
    public Pane getBox(int row, int column) {
        return boxes[row][column];
    }
    public Owner getOwner(int row, int column) {
        return owners[row][column];
    }

    // setters
    private void setBox(int row, int column, Pane box) {
        boxes[row][column] = box;
    }
    private void setOwner(int row, int column, Owner owner) {
        owners[row][column] = owner;
    }
}

//Inner class owner
class Owner extends Circle {
    private char type = Board.EMPTY;
    public Owner(int size, char type) {
        super();

        int center = size / 2;
        setType(type);
        setCenterX(center);
        setCenterY(center);
        setRadius(center-5);
    }
    public char getType() {
        return type;
    }
    public void setType(char type) {
        this.type = type;
        setFill(getColor(type));
    }

    public Color getColor(char player) {
        switch (player) {
            case Board.WHITE: return Color.WHITE;
            case Board.BLACK: return Color.BLACK;
            default: return null;
        }
    }

}

//Inner class
//Code obtained from https://docs.oracle.com/javafx/2/text/jfxpub-text.htm and modified
class TitleLabel extends Label {
    public TitleLabel(int fontSize, boolean bold) {
        if (bold){
            setStyle("-fx-font-weight: bold");
        }
        InnerShadow is = new InnerShadow();
        is.setOffsetX(3.0f);
        is.setOffsetY(3.0f);
        setEffect(is);

        setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        setTextFill(Color.LIGHTGOLDENRODYELLOW);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10,0,10,0));
    }
}