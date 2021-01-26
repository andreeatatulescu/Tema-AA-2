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
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;
    private final ArrayList<Integer> inputValues = new ArrayList<>();
    private final TreeMap<Integer, ArrayList<Integer>> familyRelations = new TreeMap<>();
    TreeMap<Integer, ArrayList<Integer>> complementFamilyRelations = new TreeMap<>();
    private int nrOfFamilies;
    int cntComplement = 0;
    StringBuilder finalAns = new StringBuilder();
    List<Integer> listEnd = new ArrayList<>();

    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
        readProblemData();

        // settings the fields for task2 (using the non-edges map)
        task2Solver.setNrOfFamilies(nrOfFamilies);
        task2Solver.setFamilyRelations(complementFamilyRelations);

        // from the max nr to 0 because we want to do the min possible nr of arrests
        for (int i = nrOfFamilies; i >= 0; i-- ) {
            task2Solver.setCliqueDimension(i);
            task2Solver.formulateOracleQuestion();
            task2Solver.askOracle();
            task2Solver.decipherOracleAnswer();
            // first True from Oracle
            if (task2Solver.getAns().toString().contains("True")) {
                task2Solver.writeAnswer();
                break;
            }
        }

        // storing the answer from task2
        StringBuilder ans = task2Solver.getAns();

        // extracting the nodes
        Scanner in = new Scanner(ans.toString()).useDelimiter("[^0-9]+");

        while (in.hasNext()) {
                listEnd.add(in.nextInt());
        }

        // for this task, we want the complementary nodes
        for (int i = 1; i <= nrOfFamilies; i++) {
            if(!listEnd.contains(i)) {
                finalAns.append(i + " ");
            }
        }

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

        // extracting the nrOfFamilies from inputValues
        nrOfFamilies = inputValues.get(0);

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
            familyRelations.put(inputValues.get(i), list);
        }

        // forming the complement map => non-edges map
        for (int i = 1; i <= nrOfFamilies; i++) {
            ArrayList<Integer> list = familyRelations.get(i);
            ArrayList<Integer> complementList = new ArrayList<>();

            for (int j = 1; j <= nrOfFamilies; j++) {
                if (!list.contains(j) && j > i) {
                    complementList.add(j);
                    cntComplement++;
                }
            }

            if ( complementList.isEmpty() ) {
                complementList.add(0);
            }
            complementFamilyRelations.put(i, complementList);
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        // writing the final answer in *.out
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename, true));
        writer.write(String.valueOf(finalAns));
        writer.close();
    }
}
