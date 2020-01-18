import java.nio.file.Paths;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.*;
class Pair {
    // Return an immutable singleton map containing only the specified
    // key-value pair mapping
    public static <T, U> Map<T, U> of(T first, U second)
    {
        return Collections.singletonMap(first, second);
    }
}
class SquareOnBoard{
    static final int ORGINAL_TILE_EMPTY = 0;
    static final int ORIGINAL_TILE_BLACK = 1;
    static final int ORIGINAL_TILE_WHITE = 2;

    static final int CURRENT_PAWN_EMPTY = 0;
    static final int CURRENT_PAWN_BLACK = 1;
    static final int CURRENT_PAWN_WHITE = 2;

    int original_tile;
    int current_pawn;
    Map<Integer, Integer> coordinates;

    SquareOnBoard(){
    }

    SquareOnBoard(int original_tile, int current_pawn, int row, int col){
        this.original_tile = original_tile;
        this.current_pawn = current_pawn;
        coordinates = Pair.of(row, col);
    }

    @Override
    public String toString() {
        return String.valueOf(original_tile);
    }
}
class PossibleMove{
    Map<Integer, Integer> src;
    LinkedList<Map<Integer, Integer>> destinations;
    Map<Map<Integer, Integer>, Map<Integer, Integer>> childParentMap;

    public PossibleMove(){

    }
    public PossibleMove(Map<Integer, Integer> src, Map<Integer, Integer> dest, Map<Map<Integer, Integer>, Map<Integer, Integer>> cpmap){
        this.src = src;
        this.destinations = new LinkedList<>();
        this.destinations.addFirst(dest);
        this.childParentMap = cpmap;
    }
    @Override
    public String toString() {
        return "PossibleMove{" +
                "src=" + src +
                ", destinations=" + destinations +
                '}';
    }

    public void setSrc(Map<Integer, Integer> src) {
        this.src = src;
    }

    public LinkedList<Map<Integer, Integer>> getDestinations() {
        return destinations;
    }

    public void setDestinations(LinkedList<Map<Integer, Integer>> destinations) {
        this.destinations = destinations;
    }

    public void setChildParentMap(Map<Map<Integer, Integer>, Map<Integer, Integer>> childParentMap) {
        this.childParentMap = childParentMap;
    }
}

public class homework {
    private static String mode;
    private static int currentPlayer, opponentPlayer;
    private static double timeForPlay;
    private static List<List<SquareOnBoard>> halmaGrid;
    private static ArrayList<SquareOnBoard> blackHomeLocations = new ArrayList<>();
    private static ArrayList<SquareOnBoard> whiteHomeLocations = new ArrayList<>();
    private static PossibleMove globalMove = null;


    private void takeInput() {

        LinkedList<String> outputLines;
        File file = new File("D:\\artifiial_intelligence\\Assignment2\\src\\input.txt");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String st;
        ArrayList <String> values = new ArrayList<>();
        try {
            while ((st = br.readLine()) != null)
                values.add(st);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<String> itr = values.iterator();
        mode = itr.next();
        currentPlayer = itr.next().equalsIgnoreCase("WHITE") ? SquareOnBoard.CURRENT_PAWN_WHITE : SquareOnBoard.CURRENT_PAWN_BLACK;
        if(currentPlayer == SquareOnBoard.CURRENT_PAWN_BLACK)
            opponentPlayer = SquareOnBoard.CURRENT_PAWN_WHITE;
        else
            opponentPlayer = SquareOnBoard.CURRENT_PAWN_BLACK;

//        if game then
//        hardcode first 6 moves for black and white
//        write to output, overwrite to the file if it exists else it will create a file
//        Playdata.txt needs to be checked here
//        if playdata.txt exists then read and overwrite it. Else after the first time create it
        if(mode.equalsIgnoreCase("GAME")){
            if(currentPlayer == SquareOnBoard.CURRENT_PAWN_WHITE){
                double whiteStart = System.currentTimeMillis()/1000.0;
                LinkedList<String>[] openingMovesWhite = new LinkedList[7];
                file = new File("D:\\artifiial_intelligence\\Assignment2\\src\\playdata.txt");

//              Generate opening moves for white only
                outputLines = new LinkedList<String>(Arrays.asList("J 13,14 11,12"));
                openingMovesWhite[0] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 13,15 11,13", "J 11,13 11,11"));
                openingMovesWhite[1] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 14,14 12,12", "J 12,12 10,12", "J 10,12 12,10"));
                openingMovesWhite[2] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 15,13 13,11","J 13,11 11,9"));
                openingMovesWhite[3] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 12,15 10,13","J 10,13 12,11","J 12,11 12,9","J 12,9 10,9"));
                openingMovesWhite[4] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 15,12 13,10","J 13,10 11,10","J 11,10 9,8"));
                openingMovesWhite[5] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 14,12 12,12","J 12,12 10,10","J 10,10 10,8","J 10,8 8,8"));
                openingMovesWhite[6] = outputLines;

                if(!file.exists()){

                    writeToOutputFile(openingMovesWhite[0], "src//output.txt");
                    writeToOutputFile(new LinkedList<String>(Arrays.asList("1")), "src//playdata.txt");
                    System.exit(0);
                }
                else {
                    br = null;
                    try {
                        br = new BufferedReader(new FileReader(file));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    int count = 0;
                    try {
                        count = Integer.parseInt(br.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (count < 7) {
                        writeToOutputFile(openingMovesWhite[count], "src//output.txt");
                        writeToOutputFile(new LinkedList<String>(Arrays.asList(String.valueOf(count + 1))), "src//playdata.txt");
                        System.exit(0);
                    }
                }
            }
            else{
                double blackStart = System.currentTimeMillis()/1000.0;
                LinkedList<String>[] openingMovesBlack = new LinkedList[7];

                file = new File("D:\\artifiial_intelligence\\Assignment2\\src\\playdata.txt");
//              Generate opening moves for black only
                outputLines = new LinkedList<String>(Arrays.asList("J 1,2 3,4"));
                openingMovesBlack[0] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 0,2 2,4", "J 2,4 4,4"));
                openingMovesBlack[1] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 1,1 3,3", "J 3,3 3,5", "J 3,5 5,3"));
                openingMovesBlack[2] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 2,0 4,2","J 4,2 6,4"));
                openingMovesBlack[3] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 0,3 2,5","J 2,5 4,3","J 4,3 6,3","J 6,3 6,5"));
                openingMovesBlack[4] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 3,0 5,2","J 5,2 5,4","J 5,4 7,6"));
                openingMovesBlack[5] = outputLines;
                outputLines = new LinkedList<String>(Arrays.asList("J 3,1 3,3","J 3,3 5,5","J 5,5 7,5","J 7,5 7,7"));
                openingMovesBlack[6] = outputLines;

                if(!file.exists()){
                    writeToOutputFile(openingMovesBlack[0], "src//output.txt");
                    writeToOutputFile(new LinkedList<String>(Arrays.asList("1")), "src//playdata.txt");
                    System.exit(0);
                }
                else{
                    br = null;
                    try {
                        br = new BufferedReader(new FileReader(file));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    int count = 0;
                    try {
                        count = Integer.parseInt(br.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(count < 7){
                        writeToOutputFile(openingMovesBlack[count], "src//output.txt");
                        writeToOutputFile(new LinkedList<String>(Arrays.asList(String.valueOf(count+1))), "src//playdata.txt");
                        System.exit(0);
                    }
                }
            }
        }

        timeForPlay = Double.parseDouble(itr.next()) + System.currentTimeMillis()/1000.0;
        halmaGrid = new ArrayList<>(16);
        List<SquareOnBoard> temp_list;
        for(int i=0; i<16;i++){
            halmaGrid.add(new ArrayList<SquareOnBoard>(16));
            temp_list = halmaGrid.get(i);
            for(int j=0; j<16; j++){
                temp_list.add(new SquareOnBoard());
            }
        }
        String row="";
        int i = 0, j = 0;
        Map<Character,Integer> pawnOptions = new HashMap<>();
        pawnOptions.put('.',SquareOnBoard.CURRENT_PAWN_EMPTY);
        pawnOptions.put('B',SquareOnBoard.CURRENT_PAWN_BLACK);
        pawnOptions.put('W',SquareOnBoard.CURRENT_PAWN_WHITE);
        while (itr.hasNext()){
            row = itr.next();
            j = 0;
            for(char val: row.toCharArray()) {
                if ((i + j) < 5 || (((i + j) == 5 && i != 0) && ((i + j) == 5 && j != 0))) {
                    SquareOnBoard sq = new SquareOnBoard(SquareOnBoard.ORIGINAL_TILE_BLACK, pawnOptions.get(val), i, j);
                    halmaGrid.get(i).set(j, sq);
                    blackHomeLocations.add(sq);
                } else if ((i + j) > 25 || (((i + j) == 25 && i != 15) && ((i + j) == 25 && j != 15))) {

                    SquareOnBoard sq = new SquareOnBoard(SquareOnBoard.ORIGINAL_TILE_WHITE, pawnOptions.get(val), i, j);
                    halmaGrid.get(i).set(j, sq);
                    whiteHomeLocations.add(sq);
                } else {

                    SquareOnBoard sq = new SquareOnBoard(SquareOnBoard.ORGINAL_TILE_EMPTY, pawnOptions.get(val), i, j);
                    halmaGrid.get(i).set(j, sq);
                }
                j++;
            }
            i++;
        }
    }

    private List<String> readInputFileIntoList(String fileName) {
        List<String> lines = Collections.emptyList();
        try
        {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return lines;
    }

    public static void main(String[] args){

        homework homework_obj = new homework();

        homework_obj.takeInput();

        int depth = 3;
        int timeOneMove = 2;
        List<SquareOnBoard> pawnsInOwnHome = new ArrayList<>();
        List<SquareOnBoard> pawnsInOpponentHome = new ArrayList<>();
        List<SquareOnBoard> pawnsInNoPawnLand = new ArrayList<>();
        int opponent = currentPlayer == 1 ? 2 : 1;

        ArrayList<PossibleMove> listAllPossibleMoves = new ArrayList<>();
        for(int row=0; row<16; row++){
            for(int col=0; col<16; col++){

                SquareOnBoard currSquare = halmaGrid.get(row).get(col);
//              1st if current pawn is not my player, not interested
                if(currSquare.current_pawn != currentPlayer)
                    continue;
//              else, we make 3 list: 1: for pawns of my player in home location need to be moved first
//                2: then pawns of my player in between will play
//                3: then finally the ones in the opponent camp will be moved
                else{
                    if(currSquare.original_tile == currentPlayer)
                        pawnsInOwnHome.add(currSquare);
                    else if(currSquare.original_tile == opponent)
                        pawnsInOpponentHome.add(currSquare);
                    else
                        pawnsInNoPawnLand.add(currSquare);
                }
            }
        }
        if(pawnsInOpponentHome.size() > 7)
            depth = 2;
        else if(pawnsInNoPawnLand.size() > 12 && timeForPlay > 20*timeOneMove)
            depth = 5;
        else if(pawnsInNoPawnLand.size() < 12 && timeForPlay > 20*timeOneMove)
            depth = 4;
        else if(pawnsInOwnHome.size() > 10 && timeForPlay > 20*timeOneMove)
            depth = 5;
        if(mode.equalsIgnoreCase("SINGLE")){
            double current_time = System.currentTimeMillis();
            double value = homework_obj.minimaxWithAlphaBetaPruning(depth, currentPlayer, timeForPlay,Double.MIN_VALUE, Double.MAX_VALUE,true, true);
            if(globalMove != null)
                homework_obj.makeOutputForFile(globalMove);
        }
        else{

            double value =  homework_obj.minimaxWithAlphaBetaPruning(depth, currentPlayer, timeForPlay,Double.MIN_VALUE, Double.MAX_VALUE,true, true);
            if(globalMove != null)
                homework_obj.makeOutputForFile(globalMove);
        }

    }

    private void makeOutputForFile(PossibleMove value) {

        Map<Integer, Integer> src = value.src != null ? value.src : null;
        Map<Integer, Integer> dest = value.destinations != null ? value.destinations.getFirst() : null ;
        LinkedList<String> lines = new LinkedList<>();
        if(dest != null && src!= null){
            Map.Entry<Integer, Integer> parentEntry = value.childParentMap.get(dest).entrySet().iterator().next();
            Map.Entry<Integer, Integer> destEntry = dest.entrySet().iterator().next();
            if(Math.abs(parentEntry.getKey()-destEntry.getKey()) <=1 && Math.abs(parentEntry.getValue()-destEntry.getValue()) <= 1){
                String out = "E " + parentEntry.getValue() + "," + parentEntry.getKey() + " " + destEntry.getValue() +","+ destEntry.getKey();
                lines.add(out);
            }
            else{
                int s1 = src.entrySet().iterator().next().getKey();
                int s2 = src.entrySet().iterator().next().getValue();
                if(parentEntry.getKey() == s1 || parentEntry.getValue() == s2){
                    String out = "J " + parentEntry.getValue() + "," + parentEntry.getKey() + " " + destEntry.getValue() +","+ destEntry.getKey();
                    lines.addFirst(out);
                }
                else{
                    while(destEntry.getKey() != s1 || destEntry.getValue() != s2){
                        Map.Entry<Integer, Integer> parent = value.childParentMap.get(dest).entrySet().iterator().next();
                        String out = "J " + parent.getValue() + "," + parent.getKey() + " " + destEntry.getValue() +","+ destEntry.getKey();
                        lines.addFirst(out);
                        dest = value.childParentMap.get(dest);
                        destEntry = parent;
                    }
                }
            }
            writeToOutputFile(lines, "src//output.txt");
        }
    }

    private void writeToOutputFile(LinkedList<String> lines, String fileName) {
        try {
            FileWriter fileWriter=new FileWriter(fileName);
            for(int j=0;j<lines.size();j++) {
                for (int i = 0; i < lines.get(j).length(); i++)
                    fileWriter.write(lines.get(j).charAt(i));
                if(j<lines.size()-1)
                    fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double minimaxWithAlphaBetaPruning(int depth, int playAs, double timeForPlay, double alpha, double beta, boolean maximize, boolean first) {

        ArrayList<PossibleMove> allPossibleMoves;
        PossibleMove bestPossibleMove = null;
        double optimalValue;
        double currentTime = System.currentTimeMillis()/1000.0;
        if(depth == 0 || isWinningBoardConfiguration() || currentTime > timeForPlay){
            return utilityFunction();
        }

        if(maximize){
            optimalValue = Double.MIN_VALUE;
            allPossibleMoves = getAllPossibleMoves(playAs, false);

        }
        else{
            optimalValue = Double.MAX_VALUE;
            allPossibleMoves = getAllPossibleMoves(opponentPlayer, false);

        }
        for(PossibleMove pmove: allPossibleMoves){
            for(Map<Integer, Integer> destCoordinate : pmove.getDestinations()){

                currentTime = System.currentTimeMillis()/1000.0;
                if(currentTime > timeForPlay)
                    return optimalValue;

                Map.Entry<Integer, Integer> srcEntry = pmove.src.entrySet().iterator().next();
                Map.Entry<Integer, Integer> destEntry = destCoordinate.entrySet().iterator().next();


                int tempPawn =  halmaGrid.get(srcEntry.getKey()).get(srcEntry.getValue()).current_pawn;
                halmaGrid.get(srcEntry.getKey()).get(srcEntry.getValue()).current_pawn = SquareOnBoard.CURRENT_PAWN_EMPTY;
                halmaGrid.get(destEntry.getKey()).get(destEntry.getValue()).current_pawn = tempPawn;
                if(globalMove == null){
                    globalMove = new PossibleMove(pmove.src, destCoordinate, pmove.childParentMap);
                }
                double resultFromMinimax= minimaxWithAlphaBetaPruning(depth-1, playAs, timeForPlay, alpha, beta, !maximize, false);

                halmaGrid.get(destEntry.getKey()).get(destEntry.getValue()).current_pawn = SquareOnBoard.CURRENT_PAWN_EMPTY;
                halmaGrid.get(srcEntry.getKey()).get(srcEntry.getValue()).current_pawn = tempPawn;

                if(maximize && resultFromMinimax>optimalValue){
                    optimalValue = resultFromMinimax;
                    if(first)
                        globalMove = new PossibleMove(pmove.src, destCoordinate, pmove.childParentMap);
                    alpha = Math.max(optimalValue, alpha);
                }
                if(!maximize && resultFromMinimax < optimalValue){
                    optimalValue = resultFromMinimax;
                    beta = Math.min(optimalValue, beta);
                }
                if(beta <= alpha)
                return optimalValue;
            }
        }
        return optimalValue;
    }

    private boolean isWinningBoardConfiguration() {

        int blackCountAtBlackHome = 0, whiteCountAtBlackHome = 0, blackCountAtWhiteHome = 0, whiteCountAtWhiteHome = 0;
        for(SquareOnBoard blackhomesquare : blackHomeLocations){
            if(blackhomesquare.current_pawn == currentPlayer || blackhomesquare.current_pawn == opponentPlayer){
                if(blackhomesquare.current_pawn == SquareOnBoard.CURRENT_PAWN_BLACK)
                    blackCountAtBlackHome += 1;
//                if(blackhomesquare.current_pawn == SquareOnBoard.CURRENT_PAWN_WHITE){
                else
                    whiteCountAtBlackHome += 1;
            }
        }
        if(whiteCountAtBlackHome > 0 && (blackCountAtBlackHome+whiteCountAtBlackHome) == 19)
            return true;
        else{
            for(SquareOnBoard whitehomesquare : whiteHomeLocations){
                if(whitehomesquare.current_pawn == currentPlayer || whitehomesquare.current_pawn == opponentPlayer){
                    if(whitehomesquare.current_pawn == SquareOnBoard.CURRENT_PAWN_BLACK)
                        blackCountAtWhiteHome += 1;
                    else
                        whiteCountAtWhiteHome += 1;
                }
            }
        }
        return blackCountAtWhiteHome > 0 && (whiteCountAtWhiteHome + blackCountAtWhiteHome) == 19;
    }

    private ArrayList<PossibleMove> getAllPossibleMoves(int play_as, boolean single) {

        List<SquareOnBoard> pawnsInOwnHome = new ArrayList<>();
        List<SquareOnBoard> pawnsInOpponentHome = new ArrayList<>();
        List<SquareOnBoard> pawnsInNoPawnLand = new ArrayList<>();
        int opponent = play_as == 1 ? 2 : 1;
        ArrayList<PossibleMove> listAllPossibleMoves = new ArrayList<>();
        for(int row=0; row<16; row++){
            for(int col=0; col<16; col++){

                SquareOnBoard currSquare = halmaGrid.get(row).get(col);
//              1st if current pawn is not my player, not interested
                if(currSquare.current_pawn != play_as)
                    continue;
//              else, we make 3 list: 1: for pawns of my player in home location need to be moved first
//                2: then pawns of my player in between will play
//                3: then finally the ones in the opponent camp will be moved
                else{
                    if(currSquare.original_tile == play_as)
                        pawnsInOwnHome.add(currSquare);
                    else if(currSquare.original_tile == opponent)
                        pawnsInOpponentHome.add(currSquare);
                    else
                        pawnsInNoPawnLand.add(currSquare);
                }
            }
        }
        if(pawnsInOwnHome.size() > 0){
            for(SquareOnBoard currSquare: pawnsInOwnHome){
                Map<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> result;
                if(single){
                    result = getAllPossibleMovesFromCurrentSquare(currSquare, play_as,new LinkedList<Map<Integer, Integer>>(), new HashMap<Map<Integer, Integer>, Map<Integer, Integer>>(), true, true);
                    Map.Entry<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> resultEntry = result.entrySet().iterator().next();
                    PossibleMove moveObj = getPossibleMove(currSquare, resultEntry);
                    if(resultEntry.getKey().size() > 0)
                        return new ArrayList<>(Arrays.asList(moveObj));
                }

                else {
                    result = getAllPossibleMovesFromCurrentSquare(currSquare, play_as,new LinkedList<Map<Integer, Integer>>(), new HashMap<Map<Integer, Integer>, Map<Integer, Integer>>(), true, false);
                    Map.Entry<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> resultEntry = result.entrySet().iterator().next();
                    if(resultEntry.getKey().size() > 0){
                        PossibleMove moveObj = getPossibleMove(currSquare, resultEntry);
                        listAllPossibleMoves.add(moveObj);
                    }
                }
            }
        }
        if(listAllPossibleMoves.size() == 0 && pawnsInNoPawnLand.size()>0){
            for(SquareOnBoard currSquare: pawnsInNoPawnLand){

                Map<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> result;
                if(single){
                    result = getAllPossibleMovesFromCurrentSquare(currSquare, play_as,new LinkedList<Map<Integer, Integer>>(), new HashMap<Map<Integer, Integer>, Map<Integer, Integer>>(), true, true);
                    Map.Entry<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> resultEntry = result.entrySet().iterator().next();
                    PossibleMove moveObj = getPossibleMove(currSquare, resultEntry);
                    if(resultEntry.getKey().size() > 0)
                        return new ArrayList<>(Arrays.asList(moveObj));
                }
                else{
                    result = getAllPossibleMovesFromCurrentSquare(currSquare, play_as, new LinkedList<Map<Integer, Integer>>(), new HashMap<Map<Integer, Integer>, Map<Integer, Integer>>(), true, false);
                    Map.Entry<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> resultEntry = result.entrySet().iterator().next();
                    if(resultEntry.getKey().size() > 0){
                        PossibleMove moveObj = getPossibleMove(currSquare, resultEntry);
                        listAllPossibleMoves.add(moveObj);
                    }
                }
            }
        }
        if(listAllPossibleMoves.size() == 0 && pawnsInOpponentHome.size() > 0){
            for(SquareOnBoard currSquare: pawnsInOpponentHome){
                Map<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> result;
                if(single){
                    result = getAllPossibleMovesFromCurrentSquare(currSquare, play_as,new LinkedList<Map<Integer, Integer>>(), new HashMap<Map<Integer, Integer>, Map<Integer, Integer>>(), true, true);
                    Map.Entry<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> resultEntry = result.entrySet().iterator().next();
                    PossibleMove moveObj = getPossibleMove(currSquare, resultEntry);
                    if(resultEntry.getKey().size() > 0)
                        return new ArrayList<>(Arrays.asList(moveObj));
                }
                else{
                    result = getAllPossibleMovesFromCurrentSquare(currSquare, play_as, new LinkedList<Map<Integer, Integer>>(), new HashMap<Map<Integer, Integer>, Map<Integer, Integer>>(), true, false);
                    Map.Entry<LinkedList<Map<Integer, Integer>>,Map<Map<Integer, Integer>, Map<Integer, Integer>>> resultEntry = result.entrySet().iterator().next();
                    if(resultEntry.getKey().size() > 0){
                        PossibleMove moveObj = getPossibleMove(currSquare, resultEntry);
                        listAllPossibleMoves.add(moveObj);
                    }
                }
            }
        }
        return listAllPossibleMoves;
    }

    private PossibleMove getPossibleMove(SquareOnBoard currSquare, Map.Entry<LinkedList<Map<Integer, Integer>>, Map<Map<Integer, Integer>, Map<Integer, Integer>>> resultEntry) {
        PossibleMove moveObj = new PossibleMove();
        moveObj.setSrc(currSquare.coordinates);
        moveObj.setDestinations(resultEntry.getKey());
        moveObj.setChildParentMap(resultEntry.getValue());
        return moveObj;
    }

    private Map<LinkedList<Map<Integer, Integer>>, Map<Map<Integer, Integer>, Map<Integer, Integer>>> getAllPossibleMovesFromCurrentSquare(SquareOnBoard currSquare, int play_as, LinkedList<Map<Integer, Integer>> dest, HashMap<Map<Integer, Integer>, Map<Integer, Integer>> childParentMap, boolean adj, boolean single) {

        SquareOnBoard newSquare;
        Map.Entry<Integer, Integer> currSqEntry = currSquare.coordinates.entrySet().iterator().next();
        int curr_row = currSqEntry.getKey();
        int curr_col = currSqEntry.getValue();

        List<Integer> validListOfTiles = new ArrayList<>();
        validListOfTiles.add(SquareOnBoard.ORGINAL_TILE_EMPTY);
        validListOfTiles.add(SquareOnBoard.ORIGINAL_TILE_BLACK);
        validListOfTiles.add(SquareOnBoard.ORIGINAL_TILE_WHITE);
        if(currSquare.original_tile != play_as)
            validListOfTiles.remove(new Integer(play_as));
        if(currSquare.original_tile != SquareOnBoard.ORGINAL_TILE_EMPTY && currSquare.original_tile != play_as)
            validListOfTiles.remove(new Integer(SquareOnBoard.ORGINAL_TILE_EMPTY));
        ArrayList<Map<Integer, Integer>> forBlack = new ArrayList<Map<Integer, Integer>>(Arrays.asList(Pair.of(-1, -1), Pair.of(-1,0), Pair.of(-1,1),Pair.of(0,-1), Pair.of(1,-1)));
        ArrayList<Map<Integer, Integer>> forWhite = new ArrayList<Map<Integer, Integer>>(Arrays.asList(Pair.of(-1, 1), Pair.of(0,1), Pair.of(1,1),Pair.of(1,0), Pair.of(1,-1)));
            for (int rowDiff = -1; rowDiff < 2; rowDiff++) {
                for (int colDiff = -1; colDiff < 2; colDiff++) {

                    int newRow = curr_row + rowDiff;
                    int newCol = curr_col + colDiff;

                    if(currSquare.original_tile == currSquare.current_pawn ){
                        if(currSquare.current_pawn == SquareOnBoard.CURRENT_PAWN_WHITE){
                            if(forWhite.contains(Pair.of(rowDiff, colDiff)))
                                continue;
                        }
                        if(currSquare.current_pawn == SquareOnBoard.CURRENT_PAWN_BLACK){
                            if(forBlack.contains(Pair.of(rowDiff, colDiff))){
                                continue;                           }
                        }
                    }

                    if (newRow < 0 || newCol < 0 || newRow >= 16 || newCol >= 16 || (newCol == curr_col && newRow == curr_row) || !validListOfTiles.contains(halmaGrid.get(newRow).get(newCol).original_tile)) {
                        continue;
                    }

                    newSquare = halmaGrid.get(newRow).get(newCol);
                    if (newSquare.current_pawn == SquareOnBoard.CURRENT_PAWN_EMPTY) {
                        if (adj) {
                            dest.addLast(newSquare.coordinates);
                            Map.Entry<Integer, Integer> newSquareEntry = newSquare.coordinates.entrySet().iterator().next();
                            Map.Entry<Integer, Integer> currSqEnt = currSquare.coordinates.entrySet().iterator().next();
                            childParentMap.put(Pair.of(newSquareEntry.getKey(), newSquareEntry.getValue()), Pair.of(currSqEnt.getKey(), currSqEnt.getValue()));
                            if(single){
                                return Pair.of(dest, childParentMap);
                            }
                        }
                        continue;
                    }

                    newRow += rowDiff;
                    newCol += colDiff;

                    if (newRow < 0 || newCol < 0 || newRow >= 16 || newCol >= 16)
                        continue;
                    if (!validListOfTiles.contains(halmaGrid.get(newRow).get(newCol).original_tile) || dest.contains(halmaGrid.get(newRow).get(newCol).coordinates))
                        continue;

                    newSquare = halmaGrid.get(newRow).get(newCol);
                    if (newSquare.current_pawn == SquareOnBoard.CURRENT_PAWN_EMPTY) {
                        dest.addFirst(newSquare.coordinates);
                        Map.Entry<Integer, Integer> newSquareEntry = newSquare.coordinates.entrySet().iterator().next();
                        Map.Entry<Integer, Integer> currSqEnt = currSquare.coordinates.entrySet().iterator().next();
                        childParentMap.put(Pair.of(newSquareEntry.getKey(), newSquareEntry.getValue()), Pair.of(currSqEnt.getKey(), currSqEnt.getValue()));
                        if(single){
                            return Pair.of(dest, childParentMap);
                        }
                        getAllPossibleMovesFromCurrentSquare(newSquare, play_as, dest, childParentMap, false, false);
                    }
                }
            }
        return Pair.of(dest, childParentMap);
    }

    private Double utilityFunction() {
        double utilityValue = 0;
        ArrayList<Double> distanceFromOpponentCamp = new ArrayList<>();
        for(int row=0; row<16; row++){
            for(int col=0; col<16; col++){

                SquareOnBoard currSquare = halmaGrid.get(row).get(col);

//              If current pawn is the maximizing player, maximize the utility value. Hence total +ve value is returned
                if(SquareOnBoard.CURRENT_PAWN_WHITE == currentPlayer){
                    if(currentPlayer == currSquare.current_pawn){
                        distanceFromOpponentCamp = new ArrayList<>();
                        for(SquareOnBoard blackhomesquare : blackHomeLocations){
                            if(blackhomesquare.current_pawn != currentPlayer)
                                distanceFromOpponentCamp.add(calculateStraightLineDistance(currSquare.coordinates, blackhomesquare.coordinates));
                        }
                        utilityValue -= distanceFromOpponentCamp.size() > 0 ? Collections.max(distanceFromOpponentCamp) : -50;
                    }
                    else if(opponentPlayer == currSquare.current_pawn){
                        distanceFromOpponentCamp = new ArrayList<>();
                        for(SquareOnBoard whitehomesquare : whiteHomeLocations){
                            if(whitehomesquare.current_pawn != opponentPlayer)
                                distanceFromOpponentCamp.add(calculateStraightLineDistance(currSquare.coordinates, whitehomesquare.coordinates));
                        }
                        utilityValue += distanceFromOpponentCamp.size() > 0 ? Collections.max(distanceFromOpponentCamp) : -50;
                    }
                }
                else if(SquareOnBoard.CURRENT_PAWN_BLACK == currentPlayer){
                    if(currentPlayer == currSquare.current_pawn){
                        distanceFromOpponentCamp = new ArrayList<>();
                        for(SquareOnBoard whitehomesquare : whiteHomeLocations){
                            if(whitehomesquare.current_pawn != currentPlayer)
                                distanceFromOpponentCamp.add(calculateStraightLineDistance(currSquare.coordinates, whitehomesquare.coordinates));
                        }
                        utilityValue -= distanceFromOpponentCamp.size() > 0 ? Collections.max(distanceFromOpponentCamp) : -50;
                    }
                    else if(opponentPlayer == currSquare.current_pawn){
                        distanceFromOpponentCamp = new ArrayList<>();
                        for(SquareOnBoard blackhomesquare : blackHomeLocations){
                            if(blackhomesquare.current_pawn != opponentPlayer)
                                distanceFromOpponentCamp.add(calculateStraightLineDistance(currSquare.coordinates, blackhomesquare.coordinates));
                        }
                        utilityValue += distanceFromOpponentCamp.size() > 0 ? Collections.max(distanceFromOpponentCamp) : -50;
                    }
                }

            }
        }
        return utilityValue;
    }

    private Double calculateStraightLineDistance(Map<Integer, Integer> from, Map<Integer, Integer> to) {

        Map.Entry<Integer, Integer> fromEntry = from.entrySet().iterator().next();
        Map.Entry<Integer, Integer> toEntry = to.entrySet().iterator().next();
        return Math.sqrt((fromEntry.getKey()-toEntry.getKey())*(fromEntry.getKey()-toEntry.getKey()) + ((fromEntry.getValue()-toEntry.getValue()))*(fromEntry.getValue()-toEntry.getValue()));
    }


}
