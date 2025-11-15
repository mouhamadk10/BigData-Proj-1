package it.polito.bigdata.hadoop.temperature.filteratmost30;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// Reducer for temperatures ≤ 30.0 (simply output lines)
public class TemperatureFilterAtMost30Reducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        for (Text v : values) {
            context.write(new Text(""), v);
        }
    }
}


