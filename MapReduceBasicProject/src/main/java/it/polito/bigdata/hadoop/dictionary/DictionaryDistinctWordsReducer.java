package it.polito.bigdata.hadoop.dictionary;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// Deduplicates and outputs each distinct word
class DictionaryDistinctWordsReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        context.write(key, null);
    }
}