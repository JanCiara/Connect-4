public class debug {
    public static void main(String[] ages)
    {
        Connect4Bitboard game = new Connect4Bitboard();
        game.printBoard();

        game.dropPiece(4);
        game.dropPiece(4);
        game.dropPiece(4);
        game.dropPiece(4);
        game.printBoard();
        System.out.println(game.hashPosition());
    }
}
