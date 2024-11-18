public class Minimax {
    public static int minimax(char[][] board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        // base case
        if (depth == 0 || Main.game_over()) {
            return evaluation(board);
        }
        char player = maximizingPlayer ? Main.AI : Main.HUMAN;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < board[0].length; i++) {
                if (!Main.isValid(i))
                    continue;
                Main.place(i, player);
                maxEval = Math.max(maxEval, minimax(board, depth - 1, alpha, beta, false));
                Main.remove(i);

                // Alpha-beta pruning
                if (maxEval > beta)
                    break;
                alpha = Math.max(alpha, maxEval);
            }
            return maxEval;
        }
        else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < board[0].length; i++) {
                if (!Main.isValid(i))
                    continue;
                Main.place(i, player);
                minEval = Math.min(minEval, minimax(board, depth - 1, alpha, beta, true));
                Main.remove(i);

                // Alpha-beta pruning
                if (minEval < alpha)
                    break;
                beta = Math.min(beta, minEval);
            }
            return minEval;
        }
    }

    static int bestmove(char[][] board, int depth) {
        int move = -1, score, bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < board[0].length; i++) {
            if (!Main.isValid(i))
                continue;
            Main.place(i, Main.AI);
            score = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            Main.remove(i);

            if (score > bestScore) {
                bestScore = score;
                move = i;
            }
            System.out.println("Column: " + i + " Score: " + score);
        }
        return move;
    }

    static int evaluation(char[][] board) {
        char winner = Main.LookForWinner(board);
        int score = 0;

        // If board in won my AI
        if (winner == Main.AI)
            return 1000;
            // If it is won by human
        else if (winner != ' ')
            return -1000;
        // If it is not a terminated position

        // We calculate AI pieces - Human pieces
        int centerDifferene = countCenterPieces(board);
        score += centerDifferene * 4;

        int AI_linesOfTwo = countLinesOfTwo(board, Main.AI);
        int HUMAN_linesOfTwo = countLinesOfTwo(board, Main.HUMAN);

        score += AI_linesOfTwo * 2;
        score -= HUMAN_linesOfTwo * 2;

        int AI_linesOfThree = countLinesOfThree(board, Main.AI);
        int HUMAN_linesOfThree = countLinesOfThree(board, Main.HUMAN);

        score += AI_linesOfThree * 5;
        score -= HUMAN_linesOfThree * 5;

        return score;
    }

    static int countCenterPieces(char[][] board) {
        int res = 0;
        int centerColumn = 3;
        for (int row = 0; row < board.length; row++) {
            if (board[row][centerColumn] == Main.AI)
                res++;
            else if (board[row][centerColumn] == Main.HUMAN)
                res--;
        }
        return res;
    }

    static int countLinesOfTwo(char[][] board, char player) {
        int res = 0;
        int rows = board.length;
        int cols = board[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Horizontal
                if (col + 1 < cols && board[row][col] == player && board[row][col + 1] == player)
                    res++;

                // Vertical
                if (row + 1 < rows && board[row][col] == player && board[row + 1][col] == player)
                    res++;

                // Upward Diagonal
                if (row - 1 >= 0 && col + 1 < cols && board[row][col] == player && board[row - 1][col + 1] == player)
                    res++;

                // Downward Diagonal
                if (row + 1 < rows && col + 1 < cols && board[row][col] == player && board[row + 1][col + 1] == player)
                    res++;
            }
        }
        return res;
    }

    static int countLinesOfThree(char[][] board, char player) {
        int res = 0;
        int rows = board.length;
        int cols = board[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Horizontal check
                if (col + 2 < cols &&
                        board[row][col] == player &&
                        board[row][col + 1] == player &&
                        board[row][col + 2] == player) {
                    res++;
                }

                // Vertical check
                if (row + 2 < rows &&
                        board[row][col] == player &&
                        board[row + 1][col] == player &&
                        board[row + 2][col] == player) {
                    res++;
                }

                // Upward Diagonal check
                if (row - 2 >= 0 && col + 2 < cols &&
                        board[row][col] == player &&
                        board[row - 1][col + 1] == player &&
                        board[row - 2][col + 2] == player) {
                    res++;
                }

                // Downward Diagonal check
                if (row + 2 < rows && col + 2 < cols &&
                        board[row][col] == player &&
                        board[row + 1][col + 1] == player &&
                        board[row + 2][col + 2] == player) {
                    res++;
                }
            }
        }
        return res;
    }

}
