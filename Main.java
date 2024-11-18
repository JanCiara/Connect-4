import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class Main {
    static char[][] board = new char[6][7];
    // Two players
    static final char HUMAN = 'H';
    static final char AI = 'A';
    static final char EMPTY = 'O';

    // Game state
    static char current_player = HUMAN;
    static final byte depth = 7;

    // Scanner
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        prepareBoard();
        do {
            move();
            message(HUMAN);
            change_player();
            if (!game_over()) {
                bestmove();
                message(AI);
                change_player();
            }
        } while (!game_over());
    }

    static void message(char player) {
        if (doesXwin(player)) {
            printBoard();
            System.out.println("\nPlayer: " + current_player + " won!");
        }
        if (isDraw(board)) {
            printBoard();
            System.out.println("\nDraw!");
        }
    }

    static void bestmove() {
        // Record the start time
        long startTime = System.nanoTime();

        // Call nagamax to get the best move
        Map.Entry<Integer, Integer> eval_bestMove = Minimax.nagamax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, -1);
        int res = eval_bestMove.getValue(); // best move

        // Record the end time
        long endTime = System.nanoTime();

        // Calculate the elapsed time
        long duration = endTime - startTime; // Duration in nanoseconds
        double seconds = duration / 1_000_000_000.0; // Convert to seconds for readability

        // Print the results
        System.out.println();
        System.out.print("AI made move: " + res);
        System.out.printf("\nTime taken to calculate the move: %.3f seconds%n", seconds);

        place(res, AI); // Make the move
    }

    static void move() {
        printBoard();
        int column_number;

        System.out.println("Where do place your piece?\n " +
                "Type column number (0-6): ");
        column_number = sc.nextInt();
        while (!isValid(column_number)) {
            System.out.println("Incorrect move! Try again: ");
            column_number = sc.nextInt();
        }

        place(column_number, current_player);
        doesXwin(current_player);
        printBoard();
    }

    static void place(int column_number, char player) {
        for (int row = 0; row < board.length; row++) {
            if (board[row][column_number] != EMPTY) {
                board[row - 1][column_number] = player;
                break;
            }
            if (row == board.length - 1) {
                board[row][column_number] = player;
                break;
            }
        }

    }

    static void remove(int column_number) {
        for (int row = 0; row < board.length; row++) {
            if (board[row][column_number] != EMPTY) {
                board[row][column_number] = EMPTY;
                break;
            }
        }
    }

    static void change_player() {
        if (current_player == HUMAN)
            current_player = AI;
        else
            current_player = HUMAN;
    }

    static boolean doesXwin(char player) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length - 3; col++) {
                if (board[row][col] == player &&
                        board[row][col + 1] == player &&
                        board[row][col + 2] == player &&
                        board[row][col + 3] == player) {
                    return true;
                }
            }
        }
        //check for 4 up and down
        for (int row = 0; row < board.length - 3; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == player &&
                        board[row + 1][col] == player &&
                        board[row + 2][col] == player &&
                        board[row + 3][col] == player) {
                    return true;
                }
            }
        }
        //check upward diagonal
        for (int row = 3; row < board.length; row++) {
            for (int col = 0; col < board[0].length - 3; col++) {
                if (board[row][col] == player &&
                        board[row - 1][col + 1] == player &&
                        board[row - 2][col + 2] == player &&
                        board[row - 3][col + 3] == player) {
                    return true;
                }
            }
        }
        //check downward diagonal
        for (int row = 0; row < board.length - 3; row++) {
            for (int col = 0; col < board[0].length - 3; col++) {
                if (board[row][col] == player &&
                        board[row + 1][col + 1] == player &&
                        board[row + 2][col + 2] == player &&
                        board[row + 3][col + 3] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isDraw(char[][] board) {
        boolean draw = true;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == EMPTY) {
                    draw = false;
                    break;
                }
            }
        }
        return draw;
    }

    static void printBoard() {
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.print(i + "| ");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.print("  ");
        for (int j = 0; j < 15; j++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.print("   ");
        for (int j = 0; j < 7; j++) {
            System.out.print(j + " ");
        }
        System.out.println();
    }

    static boolean isValid(int column_number) {
        return (column_number >= 0 && column_number <= 6 && board[0][column_number] == EMPTY);
    }

    static void prepareBoard() {
        for (char[] row : board) {
            Arrays.fill(row, EMPTY);
        }
    }

    // returns if it is draw OR someone won
    static boolean game_over() {
        return isDraw(board) || (LookForWinner(board) != ' ');
    }

    // returns winner
    public static char LookForWinner(char[][] board) {
        char winner = ' ';
        if (doesXwin(AI))
            winner = AI;
        else if (doesXwin(HUMAN))
            winner = HUMAN;
        return winner;
    }
}