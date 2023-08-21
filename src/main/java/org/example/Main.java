package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        boolean finished = false;
        Scanner scanner = new Scanner(System.in);
        List<String> paths = new ArrayList<String>();
        List<String> DependencyList = new ArrayList<String>();
        StringBuilder allDependencies = new StringBuilder();

        while (!finished) {
            System.out.println("\nEnter a path of the depenendcy tree csv file, whiich you want to merge \n" +
                    "OR type ready to fulfill merging with every prevoiously entered dependency tree csv file");

            String entry = scanner.nextLine();

            if (entry.equals("ready")) {
                finished = true;
            } else {
                paths.add(entry);
            }
        }

        String filepath;
        Pattern pattern = Pattern.compile("^.*[/\\\\]");
        Matcher matcher = pattern.matcher(paths.get(0));
        matcher.find();
        filepath = matcher.group(0);

        boolean firstPath = true;
        for(String path : paths) {
            System.out.println("Reading in " + path);
            FileReader input;

            try {
                input = new FileReader(path);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            BufferedReader reader = new BufferedReader(input);
            String line;

            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                } else if (firstPath) {
                    if (line.startsWith(",")) {
                        String[] splittedLine = line.split(",");
                        String dependency = splittedLine[splittedLine.length - 1];

                        appendStringAndList(DependencyList, allDependencies, dependency);
                    } else {
                        appendStringAndList(DependencyList, allDependencies, line);
                    }
                } else {
                    boolean found = false;
                    for (String dependency : DependencyList) {
                        if (line.startsWith(",")) {
                            String[] splittedLine = line.split(",");
                            String newDependency = splittedLine[splittedLine.length - 1];

                            found = isFound(found, dependency, newDependency);
                            if (found) {
                                break;
                            }
                        } else {
                            found = isFound(found, dependency, line);
                            if (found) {
                                break;
                            }
                        }
                    }
                    if (!found) {
                        if (line.startsWith(",")) {
                            String[] splittedLine = line.split(",");
                            String newDependency = splittedLine[splittedLine.length - 1];

                            appendStringAndList(DependencyList, allDependencies, newDependency);
                        } else {
                            appendStringAndList(DependencyList, allDependencies, line);
                        }
                    }
                }
            }
            firstPath = false;
        }

        PrintWriter out = new PrintWriter(filepath + "merged.csv");
        out.println(allDependencies);
        out.close();
    }

    private static boolean isFound(boolean found, String dependency, String newDependency) {
        String[] splittedDependency = newDependency.split(":");
        List<String> splittedDependencyWithoutSuffix = new ArrayList<String>();
        for (int i = 0; i < splittedDependency.length - 1; i++) {
            splittedDependencyWithoutSuffix.add(splittedDependency[i]);
        }
        String dependencyWithoutSuffix = String.join(":", splittedDependencyWithoutSuffix);

        if(dependency.equals(dependencyWithoutSuffix)) {
            found = true;
        }
        return found;
    }

    private static void appendStringAndList(List<String> dependencyList, StringBuilder allDependencies, String line) {
        allDependencies.append(line).append("\n");

        String[] splittedDependency = line.split(":");
        List<String> splittedDependencyWithoutSuffix = new ArrayList<String>();
        for (int i = 0; i < splittedDependency.length - 1; i++) {
            splittedDependencyWithoutSuffix.add(splittedDependency[i]);
        }
        dependencyList.add(String.join(":", splittedDependencyWithoutSuffix));
    }

//    private static void recursiveAdd(String line, List<dependency> dependencyTreeOneLayerAbove, int layer) {
//        if (line.startsWith(",")) {
//            String[] splittedLine = line.split(",");
//            List<String> arrayMinusOneComma = Arrays.asList(splittedLine);
//
//            if (arrayMinusOneComma.size() - 1 == layer) {
//                recursiveAdd(arrayMinusOneComma.get(layer), dependencyTreeOneLayerAbove
//                        .getDependencyList(), layer);
//            } else if (arrayMinusOneComma.size() - 1 > layer) {
//                recursiveAdd(arrayMinusOneComma.get(layer + 1), dependencyTreeOneLayerAbove
//                        .get(dependencyTreeOneLayerAbove.size()).getDependencyList(), layer + 1);
//            } else if (arrayMinusOneComma.size() - 1 < layer) {
//                recursiveAdd(arrayMinusOneComma.get(layer - 1), dependencyTreeOneLayerAbove
//                        .get(dependencyTreeOneLayerAbove.size()).getDependencyList(), layer - 1);
//            }
//
//            String rejoinedLine = String.join(",", arrayMinusOneComma);
//
//            recursiveAdd(rejoinedLine, dependencyTreeOneLayerAbove.get(dependencyTreeOneLayerAbove.size())
//                    .getDependencyList(), countOfCommas + 1);
//        }
//    }
}