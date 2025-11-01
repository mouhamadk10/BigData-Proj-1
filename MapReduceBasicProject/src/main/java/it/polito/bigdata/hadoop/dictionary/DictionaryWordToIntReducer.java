package it.polito.bigdata.hadoop.dictionary;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// Assigns a unique increasing integer to each sorted word
class DictionaryWordToIntReducer extends Reducer<Text, Text, Text, IntWritable> {
    private int nextId = 1;
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        context.write(key, new IntWritable(nextId));
        nextId++;
    }
}