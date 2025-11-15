package it.polito.bigdata.hadoop.temperature.max;

import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MaxTempReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    @Override
    protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        double max = Double.NEGATIVE_INFINITY;
        for (DoubleWritable v : values) {
            if (v.get() > max) max = v.get();
        }
        if (max > Double.NEGATIVE_INFINITY) context.write(key, new DoubleWritable(max));
    }
}


