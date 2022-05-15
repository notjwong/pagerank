import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.lang.Math;

public class PageRank {
    private static Map<String, ArrayList<String>> links = new HashMap<String, ArrayList<String>>();
    private static Map<String, Integer> inlinks = new HashMap<String, Integer>();
    private static Map<String, Double> pageranks = new HashMap<String, Double>();

    private static void load(String inFile) {
        if (inFile != null) {
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile)), "UTF-8"));
                String s;
                while ((s = br.readLine()) != null) {
                    String[] pages = s.split("\t");
                    String source = pages[0];
                    String target = pages[1];
                    links.putIfAbsent(source, new ArrayList<String>());
                    links.putIfAbsent(target, new ArrayList<String>());
                    links.get(source).add(target);
                    // calculates inlinks
                    if (inlinks.containsKey(target)) {
                        inlinks.put(target, inlinks.get(target) + 1);
                    } else {
                        inlinks.put(target, 1);
                    }
                }

                pageranks = pr();

                br.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static Map<String, Double> pr() {
        Map<String, Double> I = new HashMap<String, Double>();
        Map<String, Double> R = new HashMap<String, Double>();
        Double init = 1.0 / links.size();
        for (String str : links.keySet()) {
            I.put(str, init);
        }

        while (true) {
            Double init2 = 0.15 / links.size();
            for (String s : links.keySet()) {
                R.put(s, init2);
            }

            double temp = 0.0;
            for (String s : links.keySet()) {
                if (links.get(s).size() > 0) {
                    for (String x : links.get(s)) {
                        R.put(x, R.get(x) + (1 - 0.15) * I.get(s) / links.get(s).size());
                    }
                } else {
                    temp += (1 - 0.15) * I.get(s) / links.size();
                }
            }
            for (String x : links.keySet()) {
                R.put(x, R.get(x) + temp);
            }
            double diff = 0.0;
            for (String x : R.keySet()) {
                diff += Math.abs(R.get(x) - I.get(x));
            }
            if (diff < 0.001) {
                break;
            }
            // may have to manually change value
            for (String s : I.keySet()) {
                I.put(s, R.get(s));
            }
        }

        return I;
    }

    public static void writeInlinkOutput(Map<String, Integer> processedList, String outputFile, int k) {

        // Getting the first k frequent terms and putting it into a priority queue
        PriorityQueue<Map.Entry<String, Integer>> result = new PriorityQueue<>(
                Map.Entry.<String, Integer>comparingByValue());
        for (Map.Entry<String, Integer> entry : processedList.entrySet()) {
            result.add(new AbstractMap.SimpleEntry<String, Integer>(entry.getKey(), entry.getValue()));
            if (result.size() > k) {
                result.poll();
            }
        }

        // Adding the frequent terms into an arraylist and sorting them according to the
        // descending order of their frequency
        ArrayList<Map.Entry<String, Integer>> scores = new ArrayList<Map.Entry<String, Integer>>();
        scores.addAll(result);
        scores.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()));

        // Writing the term with their frequency into the file.
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(outputFile, false));
            int rank = 1;
            for (Map.Entry<String, Integer> entry : scores) {
                writer.println(entry.getKey() + " " + rank + " " + entry.getValue());
                rank++;
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void writePageRankOutput(Map<String, Double> processedList, String outputFile, int k) {

        // Getting the first k frequent terms and putting it into a priority queue
        PriorityQueue<Map.Entry<String, Double>> result = new PriorityQueue<>(
                Map.Entry.<String, Double>comparingByValue());
        for (Map.Entry<String, Double> entry : processedList.entrySet()) {
            result.add(new AbstractMap.SimpleEntry<String, Double>(entry.getKey(), entry.getValue()));
            if (result.size() > k) {
                result.poll();
            }
        }

        // Adding the frequent terms into an arraylist and sorting them according to the
        // descending order of their frequency
        ArrayList<Map.Entry<String, Double>> scores = new ArrayList<Map.Entry<String, Double>>();
        scores.addAll(result);
        scores.sort(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder()));

        // Writing the term with their frequency into the file.
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(outputFile, false));
            int rank = 1;
            for (Map.Entry<String, Double> entry : scores) {
                writer.println(entry.getKey() + " " + rank + " " + entry.getValue());
                rank++;
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        load("links.srt.gz");
        System.out.println("Finished loading");
        writeInlinkOutput(inlinks, "inlinks.txt", 100);
        System.out.println("Finished writing to inlinks.txt");
        writePageRankOutput(pageranks, "pagerank.txt", 100);
        System.out.println("Finished pagerank.txt");
    }
}