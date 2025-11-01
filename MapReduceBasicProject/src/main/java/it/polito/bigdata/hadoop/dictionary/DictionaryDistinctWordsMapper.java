package it.polito.bigdata.hadoop.dictionary;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// Emits each lowercased, cleaned word it finds (for uniqueness via reducer).
class DictionaryDistinctWordsMapper extends Mapper<LongWritable, Text, Text, Text> {
    private final Text wordOut = new Text();
    private static final Text nullVal = new Text("");

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        for (String tok : line.split("\\s+")) {
            String word = tok.toLowerCase().replaceAll("[^a-z0-9]", "");
            if (!word.isEmpty()) {
                wordOut.set(word);
                context.write(wordOut, nullVal);
            }
        }
    }
}