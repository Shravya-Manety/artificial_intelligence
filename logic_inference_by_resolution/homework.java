import java.io.*;
import java.util.*;

class LogicPredicate {

    String name;
    boolean isNegative;
    String predicateString;
    String[] arguments;
    LogicPredicate(String pred){

        this.predicateString = pred;
        String[] splitPred = pred.split("\\(");
        this.name = splitPred[0].trim();
        this.isNegative = false;
        if(this.name.charAt(0) == '~'){
            this.name = this.name.substring(1);
            this.isNegative = true;
        }
        int lenSplitPred = splitPred[1].length();
        String param = splitPred[1].substring(0,lenSplitPred-1);
        this.arguments = param.split(",");
        for (int i= 0; i< this.arguments.length; i++){
            this.arguments[i] = this.arguments[i].trim();
        }
    }

    @Override
    public String toString() {
        return "LogicPredicate{" +
                "name='" + name + '\'' +
                ", isNegative=" + isNegative +
                ", predicateString='" + predicateString + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }

    void negate() {
        this.isNegative = ! this.isNegative;
        this.updateChangesInPredicateString();
    }

    private void updateChangesInPredicateString() {
        this.predicateString = "";
        String argString="";
        if(isNegative) {
            this.predicateString = "~";
        }
        for(String arg : this.arguments){
            argString = argString + "," + arg;
        }
        argString = argString.substring(1);
        this.predicateString = this.predicateString + this.name + '(' + argString + ')';
    }

    LogicPredicate applyUnifyingValues(Map<String, String> unified) {
        if(unified.entrySet().size() > 0){
            String[] substitutedArray = this.arguments;
            for(int i=0; i<substitutedArray.length; i++){
                if(unified.containsKey(substitutedArray[i]))
                    substitutedArray[i] = unified.get(substitutedArray[i]);
            }
            this.arguments = substitutedArray;
        }
        this.updateChangesInPredicateString();
        return this;
    }

    Map<String, String> unifyWithPredicate(LogicPredicate logicPredicate2) {

        Map<String,String> validSubstitution = new HashMap<>();
        if(this.name.equals(logicPredicate2.name) && this.arguments.length == logicPredicate2.arguments.length){
            return proceedWithUnification(this.arguments, logicPredicate2.arguments, validSubstitution);
        }
        else{
            validSubstitution.put("false", "false");
            return validSubstitution;
        }
    }

    private Map<String, String> proceedWithUnification(String[] arguments1, String[] arguments2, Map<String, String> validSubstitution) {

        boolean argNotEqual = false;
        for(int i=0; i< arguments1.length; i++){
            String val1 = arguments1[i];
            String val2 = arguments2[i];
                if(!val1.equals(val2)){
                    argNotEqual = true;
                    break;
                }
            }
        if(validSubstitution.containsKey("false")){
            return validSubstitution;
        }
        else if(!argNotEqual){
            return validSubstitution;
        }
        else if(arguments1.length == 1 && arguments1[0].equals(arguments1[0].toLowerCase())){
            return unifyVariable(arguments1, arguments2, validSubstitution);
        }
        else if(arguments2.length == 1 && arguments2[0].equals(arguments2[0].toLowerCase())){
            return unifyVariable(arguments2, arguments1, validSubstitution);
        }
        else if(arguments1.length > 1 && arguments2.length > 1){

            String firstValFromArg1 = arguments1[0];
            String[] temp1 = Arrays.copyOfRange(arguments1, 1, arguments1.length);
            String[] firstArgFromStmt1 = {firstValFromArg1};

            String firstValFromArg2 = arguments2[0];
            String[] temp2 = Arrays.copyOfRange(arguments2, 1, arguments2.length);
            String[] firstArgFromStmt2 = {firstValFromArg2};

            return proceedWithUnification(temp1, temp2, proceedWithUnification(firstArgFromStmt1, firstArgFromStmt2, validSubstitution));
        }
        else if(arguments1.length > 1 || arguments2.length > 1){
            return validSubstitution;
        }
        else{
            validSubstitution = new HashMap<>();
            validSubstitution.put("false", "false");
            return validSubstitution;
        }
    }

    private Map<String, String> unifyVariable(String[] arguments1, String[] arguments2, Map<String, String> validSubstitution) {
        if(validSubstitution.containsKey(arguments1[0])){
            String[] firstValFromArg1 = {validSubstitution.get(arguments1[0])};
            return proceedWithUnification(firstValFromArg1, arguments2, validSubstitution);
        }
        else if(validSubstitution.containsKey(arguments2[0])){
            String[] firstValFromArg2 = {validSubstitution.get(arguments2[0])};
            return proceedWithUnification(arguments1, firstValFromArg2, validSubstitution);
        }
        else{
            validSubstitution.put(arguments1[0], arguments2[0]);
            return validSubstitution;
        }
    }


}

class LogicStatement{

    Set<LogicPredicate> predicateSet;
    String statementString;

    LogicStatement(Set<LogicPredicate> predicateSet) {

        Set<LogicPredicate> logicPredicateSet = new HashSet<>();
        for(LogicPredicate logicPredicate: predicateSet) {
            logicPredicateSet.add(new LogicPredicate(logicPredicate.predicateString));
        }
        this.predicateSet = logicPredicateSet;
        this.statementString = "";
        if(this.predicateSet.size() == 0) {
            for (LogicPredicate logicPredicate : predicateSet) {
                this.statementString = logicPredicate.predicateString;
            }
        }else {
            for (LogicPredicate lpred : this.predicateSet) {
                if(this.statementString.equals("")) {
                    this.statementString = lpred.predicateString;
                }else{
                    this.statementString = this.statementString + "|" + lpred.predicateString;
                }
            }
        }
    }

    LogicStatement(String predicateString) {
        this.statementString = predicateString;
        if(!predicateString.equals("")){
            Set<LogicPredicate> predicate = new HashSet<>();
            predicate.add(new LogicPredicate(predicateString));
            this.predicateSet = predicate;
        }
    }

    void addStatementToKB(Set<LogicStatement> knowledgeBase, Map<String, Set<LogicStatement>> knowledgeBaseHashMap, boolean calledForResolution) {
        knowledgeBase.add(this);
        Set<LogicStatement> set;
        if(!calledForResolution){
            for(LogicPredicate lpred: this.predicateSet){
                if(knowledgeBaseHashMap.containsKey(lpred.name)){
                    set = knowledgeBaseHashMap.get(lpred.name);
                }
                else{
                    set = new HashSet<>();
                }
                set.add(this);
                knowledgeBaseHashMap.put(lpred.name, set);
            }
        }
    }

    @Override
    public String toString() {
        return "LogicStatement{" +
                "statementString='" + statementString + '\'' +
                '}';
    }
}


public class homework {

    private Set<LogicStatement> knowledgeBase = new HashSet<>();
    private Map<String, Set<LogicStatement>> knowledgeBaseHashMap = new HashMap<>();
    private ArrayList<Boolean> writetoFileList = new ArrayList<Boolean>();


    public static void main(String[] args) {

        ArrayList<LogicPredicate> queryList = new ArrayList<>();
        ArrayList<String> folSentences = new ArrayList<>();


        homework homework_obj = new homework();
        homework_obj.takeInput(queryList, folSentences);
        homework_obj.convertSentencesToCNF(folSentences);
        homework_obj.generateOutputForAllQueries(queryList);
        homework_obj.displayKB();
    }

    private void generateOutputForAllQueries(ArrayList<LogicPredicate> queryList) {

        Set<LogicStatement> KB;
        Map<String, Set<LogicStatement>> KB_HASH;

        for(LogicPredicate queryPred : queryList){
            queryPred.negate();
            LogicStatement queryStatement = new LogicStatement(queryPred.predicateString);
            KB = new HashSet<>();
            KB_HASH = new HashMap<>();
            for(LogicStatement lstmt : knowledgeBase){
                LogicStatement logicStatement = new LogicStatement(lstmt.predicateSet);
                logicStatement.addStatementToKB(KB, KB_HASH, true);
            }
            boolean satisfiableResolutionObtained = FOLResolution(queryStatement, KB);
            writetoFileList.add(satisfiableResolutionObtained);
        }
        writeOutput(writetoFileList);
    }

    private void writeOutput(ArrayList<Boolean> lines) {
        try {
            FileWriter fileWriter=new FileWriter("src//output.txt");
            for(int j=0;j<lines.size();j++) {
                if(lines.get(j))
                    fileWriter.write("TRUE");
                else
                    fileWriter.write("FALSE");
                if(j < lines.size()-1)
                    fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean FOLResolution(LogicStatement queryStatement, Set<LogicStatement> KB) {

        Set<LogicStatement> KB2 = new HashSet<>();
        Map<String, Set<LogicStatement>> KB_HASH = new HashMap<>();

        queryStatement.addStatementToKB(KB, KB_HASH, false);
        queryStatement.addStatementToKB(KB2, KB_HASH, false);

        while(true){
            Map<String, Set<String>> history = new HashMap<>();
            Set<LogicStatement> newStatements = new HashSet<>();

            int KILL_LIMIT = 8000;
            if(KB.size() > KILL_LIMIT)
                return false;

            for(LogicStatement logicStatement1: KB){

                Set<LogicStatement> resolvingClauses = getResolvingClausesWithQueryInKB(logicStatement1, KB_HASH);
                for(LogicStatement logicStatement2: resolvingClauses){

                    if(logicStatement1.predicateSet.containsAll(logicStatement2.predicateSet))
                        continue;
                    boolean flag = false;
                    if(history.containsKey(logicStatement2.statementString)){
                        Set<String> historySet = history.get(logicStatement2.statementString);
                        if(historySet.contains(logicStatement1.statementString)){
                            historySet.remove(logicStatement1.statementString);
                            history.put(logicStatement2.statementString, historySet);
                            continue;
                        }
                    }
                    if(history.containsKey(logicStatement1.statementString)){
                        flag = true;
                        Set<String> historySet = history.get(logicStatement1.statementString);
                        if(historySet.contains(logicStatement2.statementString)){
                            historySet.remove(logicStatement2.statementString);
                            history.put(logicStatement1.statementString, historySet);
                            continue;
                        }
                    }
                    if(flag){
                        Set<String> historySet = history.get(logicStatement1.statementString);
                        historySet.add(logicStatement2.statementString);
                        history.put(logicStatement1.statementString, historySet);
                    }
                    else{
                        Set<String> historySet = new HashSet<>();
                        historySet.add(logicStatement2.statementString);
                        history.put(logicStatement1.statementString, historySet);
                    }

                    Set<LogicStatement> resolvedStatements = resolve(logicStatement1, logicStatement2);
                    if(resolvedStatements.size() == 1){
                        for(LogicStatement lstmt: resolvedStatements) {
                            if(lstmt.statementString.equals("false"))
                                return true;
                        }
                    }
                    newStatements.addAll(resolvedStatements);
                }
            }
            if(KB.containsAll(newStatements)){
                return false;
            }

            for(LogicStatement key : KB){
                newStatements.remove(key);
            }
            KB2 = new HashSet<>();
            KB_HASH = new HashMap<>();
            for(LogicStatement logicStatement3 : newStatements){
                logicStatement3.addStatementToKB(KB2, KB_HASH, false);
            }
            KB.addAll(newStatements);
        }
    }

    private Set<LogicStatement> resolve(LogicStatement logicStatement1, LogicStatement logicStatement2) {

        Set<LogicStatement> inferedSet = new HashSet<>();
        Map<String, String> unified = new HashMap<>();


        for(LogicPredicate logicPredicate1: logicStatement1.predicateSet){
            for(LogicPredicate logicPredicate2: logicStatement2.predicateSet){
                unified = new HashMap<>();
                unified.put("false", "false");
                if(logicPredicate1.isNegative ^ logicPredicate2.isNegative && logicPredicate1.name.equals(logicPredicate2.name)){
                    unified = logicPredicate1.unifyWithPredicate(logicPredicate2);
                }
                if(unified.containsKey("false"))
                    continue;
                else{
                    Set<LogicPredicate> remainingStatement1 = new HashSet<>();
                    Set<LogicPredicate> remainingStatement2 = new HashSet<>();

                    for(LogicPredicate logicPredicate: logicStatement1.predicateSet){
                        remainingStatement1.add(new LogicPredicate(logicPredicate.predicateString));
                    }

                    for(LogicPredicate logicPredicate: logicStatement2.predicateSet){
                        remainingStatement2.add(new LogicPredicate(logicPredicate.predicateString));
                    }
                    remainingStatement1 = removeUnifiedPredicate(remainingStatement1, logicPredicate1);
                    remainingStatement2 = removeUnifiedPredicate(remainingStatement2, logicPredicate2);

                    if(remainingStatement1.size() == 0 && remainingStatement2.size() == 0){
                        LogicStatement contradictionFound = new LogicStatement("");
                        contradictionFound.statementString = "false";
                        Set<LogicStatement> temp = new HashSet<>();
                        temp.add(contradictionFound);
                        return temp;
                    }

                    Set<LogicPredicate> remainingStatementSet1 = new HashSet<>();
                    Set<LogicPredicate> remainingStatementSet2 = new HashSet<>();

                    for(LogicPredicate lpred: remainingStatement1){
                        LogicPredicate newPred = lpred.applyUnifyingValues(unified);
                        remainingStatementSet1.add(newPred);
                    }
                    for(LogicPredicate lpred: remainingStatement2){
                        LogicPredicate newPred = lpred.applyUnifyingValues(unified);
                        remainingStatementSet2.add(newPred);
                    }
                    remainingStatementSet1.addAll(remainingStatementSet2);
                    LogicStatement newStatementinKB = new LogicStatement(remainingStatementSet1);
                    inferedSet.add(newStatementinKB);
                }
            }
        }
        return inferedSet;
    }

    private Set<LogicPredicate> removeUnifiedPredicate(Set<LogicPredicate> remainingStatement1, LogicPredicate logicPredicate1) {

        Set<LogicPredicate> reducedStatement = new HashSet<>();
        boolean flag = false;

        for(LogicPredicate lpred: remainingStatement1){
            flag = false;
            if(lpred.name.equals(logicPredicate1.name) &&
            lpred.isNegative  == logicPredicate1.isNegative &&
            lpred.predicateString.equals(logicPredicate1.predicateString)){
                for(int i = 0; i< lpred.arguments.length; i++){
                    if(!lpred.arguments[i].equals(logicPredicate1.arguments[i])){
                        flag = true;
                        break;
                    }
                }
            }
            else{
                reducedStatement.add(lpred);
            }
            if(flag)
                reducedStatement.add(lpred);
        }
        return reducedStatement;
    }

    private Set<LogicStatement> getResolvingClausesWithQueryInKB(LogicStatement logicStatement1, Map<String, Set<LogicStatement>> kb_hash) {

        Set<LogicStatement> resolvingStatements = new HashSet<>();

        for(LogicPredicate logicPredicate: logicStatement1.predicateSet){
            if(kb_hash.containsKey(logicPredicate.name)){
                resolvingStatements.addAll(kb_hash.get(logicPredicate.name));
            }
        }
        return resolvingStatements;
    }

    private void displayKB() {
        for(LogicStatement stmt: knowledgeBase){
            System.out.println(stmt.statementString);
        }
        for(String key: knowledgeBaseHashMap.keySet()){
            System.out.println(key + " "+ knowledgeBaseHashMap.get(key).size());
        }
    }
    private void convertSentencesToCNF(ArrayList<String> folSentences) {

        String sentence;
        int indexOfImplication;
        Set<LogicPredicate> predicateSet = new HashSet<>();

        Iterator<String> itr = folSentences.iterator();
        while(itr.hasNext()){
            sentence = itr.next();
            predicateSet = new HashSet<>();
            indexOfImplication = sentence.indexOf('=');
            if(indexOfImplication != -1){
                String left = sentence.substring(0, indexOfImplication);
                String right = sentence.substring(indexOfImplication + 3);
                String[] leftPredicates = left.split("&");
                for(String lpred: leftPredicates){
                    lpred = lpred.trim();
                    if(lpred.charAt(0) != '~'){
                        lpred = "~" + lpred;
                    }
                    else{
                        lpred = lpred.substring(1);
                    }
                    predicateSet.add(new LogicPredicate(lpred.replaceAll(" ", "")));
                }
                predicateSet.add(new LogicPredicate(right.trim()));
                LogicStatement lstmt = new LogicStatement(predicateSet);
                lstmt.addStatementToKB(knowledgeBase, knowledgeBaseHashMap, false);
            }
            else{
                predicateSet.add(new LogicPredicate(sentence));
                LogicStatement lstmt = new LogicStatement(predicateSet);
                lstmt.addStatementToKB(knowledgeBase, knowledgeBaseHashMap, false) ;
            }
        }

    }
    private void takeInput(ArrayList<LogicPredicate> queryList, ArrayList<String> folSentences) {

        File file = new File("D:\\artifiial_intelligence\\homework3\\src\\input.txt");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String st;
        ArrayList<String> values = new ArrayList<>();
        try {
            while ((st = br.readLine()) != null)
                values.add(st);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<String> itr = values.iterator();
        int numQueries = Integer.parseInt(itr.next());
        while(numQueries > 0){
            queryList.add(new LogicPredicate(itr.next()));
            numQueries --;
        }
        int numFOLSentences = Integer.parseInt(itr.next());
        while(numFOLSentences > 0) {
            folSentences.add(itr.next());
            numFOLSentences--;
        }
    }
}
