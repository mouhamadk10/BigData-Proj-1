package it.polito.bigdata.hadoop.invertedindex;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Inverted Index - Mapper (stopword removal)
 * Expects: sentenceId\tsentence (tab-separated)
 * Emits: word, sentenceId
 */
class InvertedIndexMapper extends Mapper<LongWritable, Text, Text, Text> {
    private final Text outWord = new Text();
    private final Text outSentId = new Text();
    private final Set<String> stopwords = new HashSet<String>();
    {
        stopwords.add("and");
        stopwords.add("or");
        stopwords.add("not");
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] parts = line.split("\t", 2);
        if (parts.length < 2) return;
        String sentenceId = parts[0].trim();
        String sent = parts[1].trim();
        for (String tok : sent.split("\\s+")) {
            String w = tok.toLowerCase().replaceAll("[^a-z0-9]", "");
            if (w.length() == 0 || stopwords.contains(w)) continue;
            outWord.set(w);
            outSentId.set(sentenceId);
            context.write(outWord, outSentId);
        }
    }
}

