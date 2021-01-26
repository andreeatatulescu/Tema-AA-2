// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {
    private final ArrayList<Integer> inputValues = new ArrayList<>();
    private TreeMap<Integer, ArrayList<Integer>> familyRelations = new TreeMap<>();
    TreeMap<Integer, List<Integer>> complementFamilyRelations = new TreeMap<>();
    private int nrOfFamilies;
    private int cliqueDimension;
    private int nrOfClauses = 0;
    private int nrOfVariables;
    private StringBuilder ans;
    TreeMap<Integer, ArrayList<Integer>> families = new TreeMap<>();

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        // reading from input file and store the data in inputValues
        String file = inFilename;
        Scanner scanner = new Scanner(new File(file));
        scanner.useDelimiter(" |\\n");

        while (scanner.hasNext()) {
            inputValues.add(Integer.parseInt(scanner.next()));
        }
        scanner.close();

        // extracting the nrOfFamilies and the cliqueDimension from inputValues
        nrOfFamilies = inputValues.get(0);
        cliqueDimension = inputValues.get(2);

        // initializing a map where the key is the family and the values an ArrayList
        // of its relations with the others - for now there are no relations [0]
        for (int i = 1; i <= inputValues.get(0); i++) {
            ArrayList<Integer> relationsList = new ArrayList<>();
            relationsList.add(0);
            familyRelations.put(i, relationsList);
        }

        // setting the relations between families using the edges given in input file
        for (int i = 3; i < inputValues.size(); i = i + 2) {
            ArrayList<Integer> list = familyRelations.get(inputValues.get(i));
            if (list.get(0) == 0) {
                list.remove(0);
            }
            list.add(inputValues.get(i+1));
            // adjacency map
            familyRelations.put(inputValues.get(i), list);
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {

        TreeMap<Integer, ArrayList<Integer>> eachVariable = new TreeMap<>();

        nrOfVariables = nrOfFamilies * cliqueDimension;

        // forming the matrix for SAT reduction
        int[][] twoDm = new int[cliqueDimension][nrOfFamilies];
        int k=1;
        StringBuilder clauses = new StringBuilder();

        for(int i = 0; i < cliqueDimension; i++) {
            for(int j = 0; j < nrOfFamilies; j++) {
                twoDm[i][j] = k;
                k++;
            }
        }

        // 1st type of clauses
        for(int[] row : twoDm) {
            for (int m : row) {
                clauses.append(m).append(" ");
            }
            clauses.append("0\n");
            nrOfClauses++;
        }

        // forming a map for all the clauses that involve a specific variable
        // [key] : the variable -> [value] : all its combinations in order to form the clauses
        for (int n = 1; n <= nrOfVariables; n++) {
            ArrayList<Integer> list = new ArrayList<>();
            eachVariable.put(n, list);
        }

        // 2ns type of clauses
        for (Integer key : familyRelations.keySet()) {
            int counter = 1;

            while (counter <= nrOfFamilies) {
                if (counter >= key) {
                    for (int row = 0; row < cliqueDimension; row++) {

                        for (int m = 0; m < cliqueDimension; m++) {
                            if (twoDm[row][counter-1] < twoDm[m][counter - 1]) {

                                ArrayList<Integer> listAux = eachVariable.get(twoDm[row][counter-1]);
                                if (!listAux.contains(twoDm[m][counter - 1])) {
                                    listAux.add(twoDm[m][counter - 1]);
                                }
                                eachVariable.put(twoDm[row][counter-1], listAux);
                            }
                        }
                    }
                }
                counter++;
            }
        }

        // 3rd.1 type of clauses
        for (Integer key : eachVariable.keySet()) {
            for (int row = 0; row < cliqueDimension; row++) {
                if (row == key / nrOfFamilies && key % nrOfFamilies != 0) {
                    for (int column = 0; column < nrOfFamilies; column++) {
                        ArrayList<Integer> listAux = eachVariable.get(key);
                        if (column + 1 < nrOfFamilies) {
                            if ( !listAux.contains(twoDm[row][column + 1]) && (twoDm[row][column + 1] > key) ) {
                                listAux.add(twoDm[row][column + 1]);
                            }
                            eachVariable.put(key, listAux);
                        }
                    }
                }
            }
        }

        // new map in order to store the families members
        // according to my SAT reduction
        int aux = 0;

        for (int n = 1; n <= nrOfFamilies; n++) {
            ArrayList<Integer> members = new ArrayList<>();
            aux = n;
            while (aux <= nrOfVariables) {
                members.add(aux);
                aux = aux + nrOfFamilies;
            }
            families.put(n, members);
        }

        // 3rd.2 type of clauses
        int famNr = 0;
        for (Integer key1 : families.keySet()) {
            famNr = key1 + 1;
            ArrayList<Integer> list = familyRelations.get(key1);
            while (famNr <= nrOfFamilies) {
                // non-edges
                if (!list.contains(famNr)) {
                    ArrayList<Integer> listAux = families.get(key1);
                    ArrayList<Integer> listAux2 = families.get(famNr);
                    for (Integer iter : listAux) {
                        for (Integer iter2 : listAux2) {
                            ArrayList<Integer> finalList = eachVariable.get(iter);
                            if (!finalList.contains(iter2)) {
                                finalList.add(iter2);
                                eachVariable.put(iter, finalList);
                            }
                        }
                    }
                }
                famNr++;
            }
        }

        // adding the 2nd and 3rd types of clauses to the StringBuilder
        for (Integer key : eachVariable.keySet()) {
            ArrayList<Integer> iterator = eachVariable.get(key);
            for (Integer iter : iterator) {
                clauses.append("-").append(key).append(" -").append(iter).append(" 0\n");
                nrOfClauses++;
            }
        }

        String antet = "p cnf " + nrOfVariables + " " + nrOfClauses + "\n" + clauses;

        // writing the clauses in *.cnf
        BufferedWriter writer = new BufferedWriter(new FileWriter(oracleInFilename));
        writer.write(antet);
        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // reading the Oracle's answer from *.sol
        String file = oracleOutFilename;
        Scanner scanner = new Scanner(new File(file));
        scanner.useDelimiter(" |\\n");

        ans = new StringBuilder();
        ans.append(scanner.next()).append("\n");

        if (scanner.hasNext()) {
            nrOfVariables = Integer.parseInt(scanner.next());
        }

        List<Integer> list = new ArrayList<>();
        while (scanner.hasNext()) {
            list.add(Integer.parseInt(scanner.next()));
        }

        scanner.close();

        // the interpretation for the result given by the Oracle, stored in ans
        // from which family is each variable > 0
        for (Integer integer : list) {
            if (integer > 0) {
                for (Integer key : families.keySet()) {
                    ArrayList<Integer> check = families.get(key);
                    if (check.contains(integer)) {
                        ans.append(key).append(" ");
                    }
                }
            }
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        // writing the final answer in *.out
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename, true));
        writer.write(String.valueOf(ans));
        writer.close();
    }

    // setters and getters
    public void setFamilyRelations(TreeMap<Integer, ArrayList<Integer>> familyRelations) {
        this.familyRelations = familyRelations;
    }

    public void setNrOfFamilies(int nrOfFamilies) {
        this.nrOfFamilies = nrOfFamilies;
    }

    public void setCliqueDimension(int cliqueDimension) {
        this.cliqueDimension = cliqueDimension;
    }

    public StringBuilder getAns() {
        return ans;
    }
}
