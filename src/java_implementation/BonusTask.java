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
 * Bonus Task
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class BonusTask extends Task {
    private final ArrayList<Integer> inputValues = new ArrayList<>();
    private final TreeMap<Integer, ArrayList<Integer>> familyRelations = new TreeMap<>();
    private int nrOfFamilies;
    private int nrOfRelations;
    private StringBuilder ans;

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

        // extracting the nrOfFamilies and the nrOfRelations from inputValues
        nrOfFamilies = inputValues.get(0);
        nrOfRelations = inputValues.get(1);

        // initializing a map where the key is the family and the values an ArrayList
        // of its relations with the others - for now there are no relations [0]
        for (int i = 1; i <= inputValues.get(0); i++) {
            ArrayList<Integer> relationsList = new ArrayList<>();
            relationsList.add(0);
            familyRelations.put(i, relationsList);
        }

        // setting the relations between families using the edges given in input file
        for (int i = 2; i < inputValues.size(); i = i + 2) {
            ArrayList<Integer> list = familyRelations.get(inputValues.get(i));
            if ( list.get(0) == 0 ) {
                list.remove(0);
            }
            list.add(inputValues.get(i + 1));
            // adjacency map
            familyRelations.put(inputValues.get(i), list);
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {

        StringBuilder clauses = new StringBuilder();

        // top = sum of weights of soft clauses + 1
        int top = nrOfFamilies + 1;

        // first line of *.wcnf
        clauses.append("p wcnf ").append(nrOfFamilies)
                .append(" ").append(nrOfRelations + nrOfFamilies)
                .append(" ").append(top).append("\n");

        // 1st type of clauses - hard ones with weight == top
        for (Integer key : familyRelations.keySet()) {
            ArrayList<Integer> list = familyRelations.get(key);
            if (!list.contains(0)) {
                for (Integer iterator : list) {
                    clauses.append(top).append(" ")
                            .append(key).append(" ").append(iterator).append(" 0\n");
                }
            }
            // 2nd type of clauses - soft ones with weight 1
            clauses.append("1 -").append(key).append(" 0\n");
        }

        // writing the clauses in *.wcnf
        BufferedWriter writer = new BufferedWriter(new FileWriter(oracleInFilename));
        writer.write(String.valueOf(clauses));
        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // reading the Oracle's answer from *.sol
        String file = oracleOutFilename;
        Scanner scanner = new Scanner(new File(file));

        ans = new StringBuilder();

        scanner.nextInt();
        scanner.nextInt();

        // the interpretation for the result given by the Oracle, stored in ans
        // all nodes > 0
        while (scanner.hasNextInt()) {
            int aux = scanner.nextInt();
            if (aux > 0) {
                ans.append(Math.abs(aux)).append(" ");
            }
        }
        scanner.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        // writing the final answer in *.out
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename, true));
        writer.write(String.valueOf(ans));
        writer.close();
    }
}
