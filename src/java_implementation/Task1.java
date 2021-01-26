// Copyright 2020
// Author: Matei Simtinică

import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task1 extends Task {
    private final ArrayList<Integer> inputValues = new ArrayList<>();
    private final TreeMap<Integer, ArrayList<Integer>> familyRelations = new TreeMap<>();
    private int nrOfFamilies;
    private int nrOfSpies;
    private int nrOfClauses = 0;
    private int nrOfVariables;
    private StringBuilder ans;

    // Returns number of combinations
    static int nCr(int n, int r)
    {
        return fact(n) / (fact(r) *
                fact(n - r));
    }

    // Returns factorial of n - helper to calculate the number of combinations
    static int fact(int n)
    {
        int res = 1;
        for (int i = 2; i <= n; i++)
            res = res * i;
        return res;
    }

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

        // extracting the nrOfFamilies and the nrOfSpies from inputValues
        nrOfFamilies = inputValues.get(0);
        nrOfSpies = inputValues.get(2);

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
        // map in order to store the distribution of the spies
        TreeMap<Integer, List<Integer>> spies = new TreeMap<>();

        // info needed for the first line of the file
        nrOfVariables = nrOfFamilies * nrOfSpies;
        nrOfClauses = nrOfClauses + nrOfFamilies;

        // calculating the number of clauses
        for (Integer key : familyRelations.keySet()) {
            if (!familyRelations.get(key).contains(0)) {
                int nrRelationsForEachFamily = familyRelations.get(key).size();
                nrOfClauses = nrOfClauses + nrRelationsForEachFamily * nrOfSpies;
            }
        }

        nrOfClauses = nrOfClauses + nCr(nrOfSpies, 2) * nrOfFamilies;

        // forming the first line of *.cnf
        StringBuilder clauses = new StringBuilder();
        clauses.append("p cnf ").append(nrOfVariables).append(" ").append(nrOfClauses).append("\n");

        // 1st type of clauses
        for (int i = 1; i <= nrOfVariables; i++) {
            if ( i % nrOfSpies == 0) {
                clauses.append(i).append(" 0\n");
            } else {
                clauses.append(i).append(" ");
            }
        }

        // 2nd type of clauses
        int noFam = 1;
        for (int j = 1; j <= nrOfVariables; j++) {
               int cnt = 0;
               List<Integer> list = new ArrayList<>();
               while (cnt < nrOfSpies) {
                   list.add(j);
                   j++;
                   cnt++;
               }
               j--;
               spies.put(noFam, list);
               noFam++;
        }

        for (Integer key : spies.keySet()) {
            List<Integer> list = spies.get(key);
            int k = 0;
            while (k < list.size()) {
                int first = list.get(k);
                for (int i = k + 1; i < list.size(); i++) {
                    clauses.append("-").append(first).append(" -")
                            .append(list.get(i)).append(" 0\n");
                }
                k++;
            }
        }

        // 3rd type of clauses
        for (Integer key : familyRelations.keySet()) {
            List<Integer> list = familyRelations.get(key);
            if (!list.contains(0)) {
                for (Integer iterator : list) {
                    List<Integer> aux = spies.get(key);
                    List<Integer> auxRelation = spies.get(iterator);
                    for (int k = 0; k < aux.size(); k++) {
                        clauses.append("-").append(aux.get(k)).append(" -")
                                .append(auxRelation.get(k)).append(" 0\n");
                    }
                }
            }
        }

        // writing the clauses in *.cnf
        BufferedWriter writer = new BufferedWriter(new FileWriter(oracleInFilename));
        writer.write(String.valueOf(clauses));
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
        // the k-th spy
        for (Integer integer : list) {
            if ( integer > 0 ) {
                if ( integer % nrOfSpies != 0 ) {
                    ans.append(integer % nrOfSpies).append(" ");
                } else {
                    ans.append(nrOfSpies).append(" ");
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
}
