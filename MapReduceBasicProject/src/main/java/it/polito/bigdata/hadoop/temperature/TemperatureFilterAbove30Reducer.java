package it.polito.bigdata.hadoop.temperature;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// Reducer for temperatures > 30.0 (simply output lines)
public class TemperatureFilterAbove30Reducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text v : values) {
            context.write(null, v); // output the full line
        }
    }
}
