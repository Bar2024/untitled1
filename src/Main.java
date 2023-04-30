import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;

    /** This game is battleshipGame bla bla bla .. (Don't forget to summarize in the future) */

    public static void battleshipGame() {

        /** In this part we get the board size from use and saving the information. n = rows , m = cols */

        System.out.println("Enter the board size:");
        String[] sizeStr = scanner.nextLine().split("X");
        int n = Integer.parseInt(sizeStr[0]);
        int m = Integer.parseInt(sizeStr[1]);


        /** making game board and guessing board for both the user and the computer. */

        String[][] userGuessBoard = makeBoard(n, m);
        String[][] compGuessBoard = makeBoard(n, m);
        String[][] userBoard = makeBoard(n, m);
        String[][] compBoard = makeBoard(n, m);


        /** Making array of battleships, according to the user's input.
         * Each entry of the array contains a string in the following format:
         * number X size. number represents the amount of the Battleships and the size represents their length. */

        System.out.println("Enter the battleships sizes:");
        String[] battleships = scanner.nextLine().split(" ");


        /** Placing the starting battleships in both the user and the computer board.
         *  Additionally, initialize a variable to be the total amount of battleships played in the current game. */

        int totalBattleships = initializeUserBoard(userBoard, battleships, n, m);
        initializeComputerBoard(compBoard, battleships, n, m);


        /** Creates an array that contains the information about the number of submarines
         that are still in the game at any given moment.
         battleshipState[0] for the user, battleshipState[1] for the computer. */

        int[] battleshipState = {totalBattleships, totalBattleships};


        /** The user and the computer start attacking each other, until one of them loses. The user starts first. */
        while(true) {

            /** The user is attacking the computer now. */
            userAttackComputer(userGuessBoard, compBoard, compGuessBoard, battleshipState, n, m);

            /** This block checks if the computer has lost all its battleships */
            if(battleshipState[1] == 0){
                System.out.println("You won the game!");
                break;
            }

            /** The computer is attacking the user now. */
            computerAttackUser(compGuessBoard, userBoard, userGuessBoard, battleshipState, n, m);

            /** This block checks if the user has lost all its battleships */
            if(battleshipState[0] == 0){
                System.out.println("You lost ):");
                break;
            }
        }

    }


    /** The function places the battleships on the user's game board according to the user's input which the
     *  information is stored in the given string named battleships.
     *  returning the total amount of battleship in the current game. */

    public static int initializeUserBoard (String[][] userBoard, String[] battleships, int n, int m){


        int totalBattleships = 0;

        /** This loop is running over each entry of the string of battleship. */
        for (String s : battleships) {
            String[] currentBattleship = s.split("X");
            // get the number and sizes of the current battleships
            int numCurrentBattleship = Integer.parseInt(currentBattleship[0]);
            int currentSizeBattleship = Integer.parseInt(currentBattleship[1]);

            /** calculating the total amount of battleships in the game. */
            totalBattleships += numCurrentBattleship;

            /** .*/
            for (int i = 0; i < numCurrentBattleship; i++) {
                int orientation;
                boolean tile;
                boolean boundaries;
                boolean overlap;
                boolean adjacent;
                printGameBoard(userBoard, n, m);
                System.out.println("Enter location and orientation for battleship size " + currentSizeBattleship);

                // the next do while will continue run till all the three parameters of the ship are correct
                do {

                    String[] battleshipInfo = scanner.nextLine().split(", ");
                    int rowBattleship = Integer.parseInt(battleshipInfo[0].trim());
                    int colBattleship = Integer.parseInt(battleshipInfo[1].trim());
                    orientation = Integer.parseInt(battleshipInfo[2].trim());


                    orientation = checkOrientation(orientation);
                    tile = checkStartingTile(n, m, rowBattleship, colBattleship);
                    boundaries = checkBoardBoundaries(n, m, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    overlap = checkOverlap(userBoard, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    adjacent = checkAdjacent(userBoard, rowBattleship, colBattleship);


                    if (orientation == -1) {
                        System.out.println("Illegal orientation, try again!");
                        continue;

                    } else if (!tile) {
                        System.out.println("Illegal tile, try again!");
                        continue;

                    } else if (!boundaries) {
                        System.out.println("Battleship exceeds the boundaries of the board, try again!");
                        continue;

                    } else if (!overlap) {
                        System.out.println("Battleship overlaps another battleship, try again!");
                        continue;

                    } else if (!adjacent) {
                        System.out.println("Adjacent battleship detected, try again!");
                        continue;
                    }

                    putInBoard(userBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                    System.out.println("Your current game board:");
                    printGameBoard(userBoard, n, m);

                } while (orientation == -1 || !boundaries || !overlap || !adjacent || !tile);
            }
        }
        return totalBattleships;
    }


    /** The computer is attacking the user's battleships.
     * This function is activated when the computer's turn has come, and in addition the game has not yet ended,
     *  and operates according to the game's instructions */
    public static void computerAttackUser(String[][] userBoard, String[][] userGuessBoard, String[][] compGuessBoard,
                                          int[] battleshipState, int n, int m) {

        boolean validScan = false;



        do {

            /** draw coordinates to attack.*/
            int rowBattleship = rnd.nextInt(n);
            int colBattleship = rnd.nextInt(m);


            /** checks if those coordinates are already been drawn.*/
            if (!isAlreadyBeenAttacked(compGuessBoard, rowBattleship, colBattleship)) {
                validScan = true;
            }

            /** If we enter this block, We know that the coordinates are valid.
             * prints a message to the user to tell him that the computer attacked those coordinates.
             * If the computer missed, printing a message and updating the computer guess board
             * If the computer hit a user's battleship, printing a message and updating both the game and guessing boards
             * If a battleship had drown, telling the user that and the amount of left battleships he holds
             * Updating the array that holds the information about the amount of battleships left in the game.*/

            if (validScan) {
                System.out.println("The computer attacked (" + rowBattleship + ", " + colBattleship + ")");

                if (isAttackMissed(userBoard, rowBattleship, colBattleship)) {
                    System.out.println("That is a miss!");
                    updateBoard(compGuessBoard, rowBattleship, colBattleship, "X");
                } else {
                    System.out.println("That is a hit!");
                    updateBoard(compGuessBoard, rowBattleship, colBattleship, "V");
                    updateBoard(userBoard, rowBattleship, colBattleship, "X");
                }

                if (battleshipDrown(rowBattleship, colBattleship, userBoard, userGuessBoard)) {
                    System.out.println("Your battleship has been drowned, You have left " + (--battleshipState[0]) + " more battleships!");
                }

            }

        } while (!validScan);

    }



    /** The user is attacking the computer's battleships.
     * This function is activated when the user's turn has come, and in addition the game has not yet ended,
     *  and operates according to the game's instructions */

    public static void userAttackComputer(String[][] userGuessBoard, String[][] compBoard, String[][] compGuessBoard,
                                          int[] battleshipState, int n, int m) {

        /** prints a message for the user.
         * Asking for coordinates that the user want to attack .*/

        System.out.println("Your current guessing board:");
        printGameBoard(userGuessBoard, n, m);
        System.out.println("Enter a tile to attack");
        boolean validScan = false;

        while (!validScan) {

            String[] userAttackCord = scanner.nextLine().split(", ");
            int rowAttack = Integer.parseInt(userAttackCord[0]);
            int colAttack = Integer.parseInt(userAttackCord[1]);

            /** In case the coordinates deviate from the board, printing error message. */
            if (!checkStartingTile(n, m, rowAttack, colAttack)) {
                System.out.println("Illegal tile, try again!");


            /** In case the coordinates have already been attacked, printing error message. */
            } else if (isAlreadyBeenAttacked(userGuessBoard, rowAttack, colAttack)) {
                System.out.println("Tile already attacked, try again!");
            }

            /** Now we know that the user had entered valid coordinates. */
            else {
                validScan= true;

                /** If the user's attack missed a battleship, printing a message, and updating the guess board.*/
                if (isAttackMissed(compBoard, rowAttack, colAttack)) {
                    System.out.println("That is a miss!");
                    updateBoard(userGuessBoard, rowAttack, colAttack, "X");

                /** Now we know that user hit a computer's battleship
                 * printing a message
                 * updating the computer's game board that his battleship had injured.
                 * updating the user's guess board according to the coordinates.
                 * Checks whether the user has sunk the ship of the computer.
                 * updates the total amount of computer's battleships that are still in the game.*/

                } else {
                    System.out.println("That is a hit!");
                    updateBoard(userGuessBoard, rowAttack, colAttack, "V");
                    updateBoard(compBoard, rowAttack, colAttack, "X");
                    if (battleshipDrown(rowAttack, colAttack, compBoard, compGuessBoard)) {
                        System.out.println("The computer's battleship has been drowned, " + (--battleshipState[1])
                                + " more battleships to go!");
                    }
                }

            }
        }
    }

    /** This function places the computer battleships on the board, according to random draw and the rules of the game */
    public static void initializeComputerBoard(String[][] compBoard,String[] battleships,int n, int m) {

        for (String s : battleships) {
            String[] currentBattleship = s.split("X");

            // get the number and sizes of the current battleships
            int numCurrentBattleship = Integer.parseInt(currentBattleship[0]);
            int currentSizeBattleship = Integer.parseInt(currentBattleship[1]);
            // make another loop for the number of the current size
            for (int i = 0; i < numCurrentBattleship; i++) {
                int orientation;
                boolean tile;
                boolean boundaries;
                boolean overlap;
                boolean adjacent;
                // the next do while will continue run till all the three parameters of the ship are correct
                do {
                    Random rnd = new Random();
                    int rowBattleship = rnd.nextInt(n);
                    int colBattleship = rnd.nextInt(m);
                    orientation = rnd.nextInt(2);
                    boundaries = checkBoardBoundaries(n, m, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    overlap = checkOverlap(compBoard, currentSizeBattleship, rowBattleship, colBattleship, orientation);
                    adjacent = checkAdjacent(compBoard, rowBattleship, colBattleship);

                    if (!boundaries)
                        continue;
                    if (!overlap)
                        continue;
                    if (!adjacent)
                        continue;

                    putInBoard(compBoard, rowBattleship, colBattleship, orientation, currentSizeBattleship);
                } while (!boundaries || !overlap || !adjacent);
            }
        }
    }


    /** Printing a board. */
    public static void printGameBoard(String[][]board, int rows, int cols){
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    /** This function places a battleship on the game board. */
    public static void putInBoard(String[][] board,int rowBattleship,int colBattleship,int orientation, int currentSizeBattleship){
        int HORIZONTAL = 0;
        if(orientation == HORIZONTAL){
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship][colBattleship + i] = "#";
            }
        }
        else {
            // Now we know the orientation is vertical
            for(int i=0; i<currentSizeBattleship; i++) {
                board[rowBattleship + i][colBattleship] = "#";
            }
        }
    }



    /** This function receives coordinates and checks if they are in the boundary of the board. */
    public static boolean checkStartingTile(int row, int col, int rowBattleship, int colBattleship) {
        if (rowBattleship < 0 || rowBattleship >= row || colBattleship < 0 || colBattleship >= col)
            return false;
        return true;
    }


    /** This function updates a given board according to the coordinate and the sign. */
    public static void updateBoard(String[][] board, int row, int col, String sign){
        board[row][col] = sign;
    }

    /** This function checks if the coordinates have already been attacked in the past. */
    public static boolean isAlreadyBeenAttacked(String[][] board, int row, int col){
        return board[row][col] != "-";
    }

    /** Checks if the attack missed a battleship. */
    public static boolean isAttackMissed(String[][] board, int row, int col){
        return board[row][col] == "-";
    }


    // count how much digit in n to know how spaces to put on board
    public static int digitCount(int num) {
        int count = 0;
        while (num != 0) {
            num /= 10;
            count++;
        }
        return count;
    }

    // make the board
    public static String[][] makeBoard(int n, int m) {
        // m+1 and n+1 because the first row and column of the board are used for labels
        // fill the board with "-"
        String[][] board = new String[n+1][m+1];
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < m+1; j++) {
                board[i][j] = "-";
            }
        }
        int space_Num = digitCount(n);
        // first tile as length of digit of n
        board[0][0] = "";
        for (int i = 0; i < space_Num; i++) {
            board[0][0] += " ";
        }
        // number the first row
        for (int j = 0; (j + 1) < (m + 1); j++) {
            board[0][j + 1] = j + "";
        }
        // number the first col
        for (int i = 0; (i + 1) < (n + 1); i++) {
            // count the spaces before each number
            int sumSpaces = space_Num - digitCount(i);
            board[i + 1][0] = "";
            // row number and spaces
            for (int j = 0; j < sumSpaces; j++) {
                board[i + 1][0] += " ";
            }
            board[i + 1][0] += i + "";
        }
        return board;
    }

    // check for correct orientation
    public static int checkOrientation(int input) {
        int HORIZONTAL = 0;
        int VERTICAL = 1;
        // if the orientation is not 1/0 it will return -1 which is an error
        int ERROR = -1;
        if (input == HORIZONTAL) {
            return HORIZONTAL;
        } else if (input == VERTICAL) {
            return VERTICAL;
        } else {
            return ERROR;
        }
    }

    // check overlap of battleships.
    // use the board, the size of ship, the tiles and the orientation
    public static boolean checkOverlap(String[][] board, int sizeShip, int row, int col,  int orientation) {
        int HORIZONTAL = 0;
        String ALREADY_PLACED = "#";
        // check for horizontal ship
        if (orientation == HORIZONTAL) {
            // use the size of the ship to calculate the space
            for (int i = 0; i < sizeShip; i++) {
                if (board[row][col + 1].equals(ALREADY_PLACED)) {
                    return false;
                }
            }
            // check for vertical ships
        } else {
            for (int j = 0; j < sizeShip; j++) {
                if (board[row + j][col].equals(ALREADY_PLACED)) {
                    return false;
                }
            }
        }
        return true;
    }

    // check if the battleship is inside the board
    // n,m are the sizes of the board
    // rowBattleship and colBattleship belongs for the battleship
    public static boolean checkBoardBoundaries(int n, int m, int sizeBattleship, int rowBattleship, int colBattleship, int orientation) {
        int HORIZONTAL = 0;
        // check for horizontal ship
        if (orientation == HORIZONTAL) {
            for (int i = 0; i < sizeBattleship; i++) {
                if ((colBattleship + i) > m) {
                    return false;
                }
            }
            // check for vertical ship
        } else {
            for (int i = 0; i < sizeBattleship; i++) {
                if ((rowBattleship + i) > n) {
                    return false;
                }
            }
        }
        return true;
    }


    // check adjacent of battleships
    public static boolean checkAdjacent(String[][] board, int row, int col) {
        // MIN, MAX : the range of the board
        int MIN = 0;
        int MAX = (board.length - 1);
        int TOP = -1;
        int LEFT = -1;
        int BOT = 1;
        int RIGHT = 1;
        // check in range of 1
        for (int i = row + TOP; i <= row + BOT; i++) {
            for (int j = col + LEFT; j <= row + RIGHT; j++ ) {
                if ((i >= MIN) && (i <= MAX) && (j >= MIN) && (j <= MAX)){
                    if (board[i][j].equals("v")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    // check if hit a battleship
    public static boolean hitBattleship(int rowBattleship, int colBattleship,String[][] board) {
        String A_BATTLESHIP = "#";
        if (board[rowBattleship][colBattleship].equals(A_BATTLESHIP))
            return true;
        return false;
    }

    // check if a battleship sunk
    // it checks if after the attack around the tile the same tiles on the guessing board are the same the player's board
    public static boolean battleshipDrown(int rowBattleship, int colBattleship, String[][] board, String[][] guessBoard) {
        int MIN = 0;
        // check horizontal to right
        for (int i = colBattleship; i < board[MIN].length; i++){
            if (board[rowBattleship][i].equals("#") && !guessBoard[rowBattleship][i].equals("V"))
                return false;
        }
        // check horizontal to left
        for (int i = colBattleship; i >= MIN; i--){
            if (board[rowBattleship][i].equals("#") && !guessBoard[rowBattleship][i].equals("V"))
                return false;
            //avoid negative indices
            if (i == MIN)
                break;
        }
        // check vertical to top
        for (int j = rowBattleship; j >= MIN; j--){
            if (board[j][colBattleship].equals("#") && !guessBoard[j][colBattleship].equals("V"))
                return false;
        }
        // check vertical to bot
        for (int j = rowBattleship; j < board.length; j++){
            if (board[j][colBattleship].equals("#") && !guessBoard[j][colBattleship].equals("V"))
                return false;
            //avoid negative indices
            if (j == MIN)
                break;
        }
        return true;
    }




    public static void main(String[] args) throws IOException {
        String path = args[0];
        scanner = new Scanner(new File(path));
        int numberOfGames = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Total of " + numberOfGames + " games.");

        for (int i = 1; i <= numberOfGames; i++) {
            scanner.nextLine();
            int seed = scanner.nextInt();
            rnd = new Random(seed);
            scanner.nextLine();
            System.out.println("Game number " + i + " starts.");
            battleshipGame();
            System.out.println("Game number " + i + " is over.");
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("All games are over.");
    }
}