package it.polito.bigdata.hadoop.temperature;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// Reducer to split lines into high/normal temperature output files
public class TemperatureSplitReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text v : values) {
            context.write(null, v);
        }
    }
}
