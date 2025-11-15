package it.polito.bigdata.hadoop.stopword;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.BufferedReader;
import java.io.FileReader;

public class StopwordEliminationMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
    private Set<String> stopwords = new HashSet<>();
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Try to find the stopword file via distributed cache/local cache
        String stopwordFile = context.getConfiguration().get("stopword.file");
        if (stopwordFile != null) {
            try (BufferedReader r = new BufferedReader(new FileReader(stopwordFile))) {
                String line;
                while ((line = r.readLine()) != null) {
                    stopwords.add(line.trim().toLowerCase());
                }
            }
        }
    }
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String sentence = value.toString();
        StringBuilder filtered = new StringBuilder();
        for (String word : sentence.split("\\s+")) {
            String w = word.toLowerCase();
            if (!stopwords.contains(w)) {
                if (filtered.length() != 0) filtered.append(" ");
                filtered.append(word);
            }
        }
        context.write(key, new Text(filtered.toString()));
    }
}
