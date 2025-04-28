import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Connect4Bitboard {
    private final int boardWidth = 7;
    private final int boardHeight = 6;
    private long player1Bitboard = 0L;
    private long player2Bitboard = 0L;
    private int currentTurn = 1;
    private int bestMove = 0;
    private final int depth = 16;
    private final int[] moveOrder = {3, 2, 4, 1, 5, 0, 6};
    private final Map<Long, Integer> transpositionTable = new HashMap<>();
    private final long[][][] zobristTable;
    public static long counter = 0;

    public Connect4Bitboard() {
        zobristTable = new long[boardWidth][boardHeight][2];
        initializeZobristTable();
    }

    private void initializeZobristTable() {
        Random random = new Random();
        for (int col = 0; col < boardWidth; col++) {
            for (int row = 0; row < boardHeight; row++) {
                zobristTable[col][row][0] = random.nextLong();
                zobristTable[col][row][1] = random.nextLong();
            }
        }
    }

    public long hashPosition() {
        long hash = 0L;
        for (int col = 0; col < boardWidth; col++) {
            for (int row = 0; row < boardHeight; row++) {
                long mask = 1L << (col + row * boardWidth);
                if ((player1Bitboard & mask) != 0) {
                    hash ^= zobristTable[col][row][0];
                } else if ((player2Bitboard & mask) != 0) {
                    hash ^= zobristTable[col][row][1];
                }
            }
        }
        return hash;
    }

    public void dropPiece(int column) {
        if (column < 0 || column >= boardWidth) {
            throw new IllegalArgumentException("Invalid column");
        }

        long mask = 1L << column;
        for (int row = 0; row < boardHeight; row++) {
            if ((player1Bitboard & mask) == 0 && (player2Bitboard & mask) == 0) {
                if (currentTurn == 1) {
                    player1Bitboard |= mask;
                } else {
                    player2Bitboard |= mask;
                }
                currentTurn = 3 - currentTurn; // Switch turn
                return;
            }
            mask <<= boardWidth; // Move to the next row in the column
        }

        throw new IllegalArgumentException("Column is full");
    }

    public boolean isValidMove(int column) {
        if (column < 0 || column >= boardWidth) {
            return false;
        }
        long mask = 1L << (column + (boardHeight - 1) * boardWidth);
        return (player1Bitboard & mask) == 0 && (player2Bitboard & mask) == 0;
    }

    private boolean checkWinner(long bitboard) {
        // Check horizontal, vertical, and diagonal connections
        int[] directions = {1, boardWidth, boardWidth - 1, boardWidth + 1};
        for (int direction : directions) {
            long bb = bitboard & (bitboard >> direction);
            if ((bb & (bb >> (2 * direction))) != 0) {
                return true;
            }
        }
        return false;
    }

    public int getWinner() {
        if (checkWinner(player1Bitboard)) {
            return 1;
        }
        if (checkWinner(player2Bitboard)) {
            return 2;
        }
        return 0;
    }

    public void printBoard() {
        for (int row = boardHeight - 1; row >= 0; row--) {
            for (int col = 0; col < boardWidth; col++) {
                long mask = 1L << (col + row * boardWidth);
                if ((player1Bitboard & mask) != 0) {
                    System.out.print("X ");
                } else if ((player2Bitboard & mask) != 0) {
                    System.out.print("O ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    public int minimax(boolean maximizingPlayer, int depth, int alpha, int beta) {
        counter++;
        if (depth == 0 || getWinner() != 0) {
            return evaluateBoard();
        }
        long curr_hash = hashPosition();
        if (transpositionTable.containsKey(curr_hash)) {
            if (depth != this.depth)
                return transpositionTable.get(curr_hash);
        }
        if (maximizingPlayer) {
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < boardWidth; i++) {
                int move = moveOrder[i];
                if (!isValidMove(move))
                    continue;
                dropPiece(move);
                int score = minimax(false, depth - 1, alpha, beta);
                undoMove(move);
                if (score > maxScore) {
                    maxScore = score;
                    if (depth == this.depth) {
                        bestMove = move;
                    }
                }
                if (maxScore >= beta) {
                    return maxScore;
                }
                alpha = Math.max(alpha, score);
            }
            transpositionTable.put(curr_hash, maxScore);
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < boardWidth; i++) {
                int move = moveOrder[i];
                if (!isValidMove(move))
                    continue;
                dropPiece(move);
                int score = minimax(true, depth - 1, alpha, beta);
                undoMove(move);
                if (score < minScore) {
                    minScore = score;
                }
                if (minScore <= alpha) {
                    return minScore;
                }
                beta = Math.min(beta, score);
            }
            transpositionTable.put(curr_hash, minScore);
            return minScore;
        }
    }

    public void undoMove(int column) {
        if (column < 0 || column >= boardWidth) {
            throw new IllegalArgumentException("Invalid column");
        }

        long mask = 1L << (column + (boardHeight - 1) * boardWidth);
        for (int row = boardHeight - 1; row >= 0; row--) {
            if ((player1Bitboard & mask) != 0) {
                player1Bitboard ^= mask;
                currentTurn = 1;
                return;
            }
            if ((player2Bitboard & mask) != 0) {
                player2Bitboard ^= mask;
                currentTurn = 2;
                return;
            }
            mask >>= boardWidth; // Move to the previous row in the column
        }

        throw new IllegalArgumentException("Column is empty");
    }

    public int evaluateBoard() {
        int score = 0;
        // Human player
        score -= evaluateBitboard(player1Bitboard);
        // AI player
        score += evaluateBitboard(player2Bitboard);
        return score;
    }

    public int evaluateBitboard(long bb) {
        int score = 0;
        int[] directions = {1, boardWidth, boardWidth - 1, boardWidth + 1};

        for (int direction : directions) {
            long bb2 = bb & (bb >> direction);
            // 4 in a row
            if ((bb2 & (bb2 >> (2 * direction))) != 0) {
                score += 10000;
            }
            // 3 in a row
            if ((bb2 & (bb2 >> direction)) != 0) {
                score += 100;
            }
            // 2 in a row
            if (bb2 != 0) {
                score += 10;
            }
        }

        // Check for pieces in middle column
        for (int row = boardHeight - 1; row >= 0; row--) {
            long mask = 1L << (3 + row * boardWidth);
            if ((bb & mask) != 0) {
                score += 10;
            }
        }
        return score;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connect4Bitboard game = new Connect4Bitboard();
        System.out.println("Player 1: X, Player 2: O");
        System.out.println("Enter column number (0-6) to drop a piece");

        while (game.getWinner() == 0) {

            // Human (Player 1)
            game.printBoard();
            System.out.println("Player " + game.currentTurn + "'s turn");
            int column = Integer.parseInt(scanner.nextLine());
            while (!game.isValidMove(column)) {
                System.out.println("Invalid move. Try again.");
                column = Integer.parseInt(scanner.nextLine());
            }
            game.dropPiece(column);

            // AI (Player 2)
            long startTime = System.currentTimeMillis();
            game.minimax(true, game.depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
            long endTime = System.currentTimeMillis();
            System.out.println("Time taken to calculate best move: " + (endTime - startTime) + " ms");

            column = game.bestMove;
            if (!game.isValidMove(column)) {
                throw new IllegalArgumentException("AI tried to make invalid move");
            }
            game.dropPiece(column);
            System.out.println(counter + " positions evaluated");
            counter = 0;
        }
        game.printBoard();
        System.out.println("Player " + game.getWinner() + " wins!");
    }
}