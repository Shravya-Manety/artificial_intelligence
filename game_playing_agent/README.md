Project description:

In this project, we will play the game of Halma, an adversarial game with some similarities to
checkers. The game uses a 16x16 checkered gameboard. Each player starts with 19 game pieces
clustered in diagonally opposite corners of the board. To win the game, a player needs to
transfer all of their pieces from their starting corner to the opposite corner, into the positions
that were initially occupied by the opponent. Note that this original rule of the game is subject
to spoiling, as a player may choose to not move some pieces at all, thereby preventing the
opponent from occupying those locations. Note that the spoiling player cannot win either
(because some pieces remain in their original corner and thus cannot be used to occupy all
positions in the opposite corner). Here, to prevent spoiling, we modify the goal of the game to
be to occupy all of the opponent’s starting positions which the opponent is not still occupying.
See http://www.cyningstan.com/post/922/unspoiling-halma for more about this rule
modification.

Setup for two players:

Note: we only consider the two-player variant here; this game can also be played by four players
but we will not explore this here.
- Simple wooden pawn-style playing pieces, often called "Halma pawns."
- The board consists of a grid of 16×16 squares.
- Each player's camp consists of a cluster of adjacent squares in one corner of the board.
These camps are delineated on the board.
- For two-player games, each player's camp is a cluster of 19 squares. The camps are in
opposite corners.
- Each player has a set of pieces in a distinct color, of the same number as squares in each
camp.
- The game starts with each player's camp filled by pieces of their own color.
- Home for black pawns is upper left corner. Home for white pawns is lower right corner.

Play sequence:

We first describe the typical play for humans. We will then describe some minor modifications
for how we will play this game with artificial agents.
- Create the initial board setup according to the above description.
- Players randomly determine who will move first.
- Pieces can move in eight possible directions (orthogonally and diagonally).
- Each player's turn consists of moving a single piece of one's own color in one of the
following plays:
  - One move to an empty square:
  - Move the piece to an empty square that is adjacent to the piece’s original
    position (with 8-adjacency).
  - This move ends the play for this player’s turn.
  - One or more jumps over adjacent pieces:
  - An adjacent piece of any color can be jumped if there is an empty square
    on the directly opposite side of that piece.
  - Place the piece in the empty square on the opposite side of the jumped
    piece.
  - The piece that was jumped over is unaffected and remains on the board.
  - After any jump, one may make further jumps using the same piece, or end
    the play for this turn.
  - In a sequence of jumps, a piece may jump several times over the same
    other piece.
- Once a piece has reached the opposing camp, a play cannot result in that piece leaving
the camp.
- If the current play results in having every square of the opposing camp that is not already
occupied by the opponent to be occupied by one's own pieces, the acting player wins.
Otherwise, play proceeds to the other player.

Playing with agents:

In this homework, your agent will play against another agent, either created by the TAs, or
created by another student in the class. For grading, we will use two scenarios:

1) Single move: your agent will be given in input.txt a board configuration, a color to play,
and some number of seconds of allowed time to play one move. Your agent should return
in output.txt the chosen move(s), before the given play time has expired. Play time is
measured as total CPU time used by your agent on all CPU threads it may spawn (so,
parallelizing your agent will not get you any free time). Your agent will play 10 single
moves, each worth one point. If your agent returns an illegal move, a badly formatted
output.txt, or does not return before its time is up, it will lose the point for that move.

2) Play against reference agent: your agent will then play 9 full games against a simple
minimax agent with no alpha-beta pruning, created by the TAs. There will be a limited
total amount of play time available to your agent for the whole game (e.g., 100 seconds),
so you should think about how to best use it throughout the game. This total amount of
time will vary from game to game. Your agent must play correctly (no illegal moves, etc)
and beat the reference minimax agent to receive 10 points per game. Your agent will be
given the first move on 5 of the 9 games. In case of a draw, the agent with more remaining
play time wins.
Note that we make a difference between single moves and playing full games because in single
moves it is advisable to use all remaining play time for that move. While playing games, however,
you should think about how to divide your remaining play time across possibly many moves
throughout the game.

Please refer example.txt for a sample execution.
