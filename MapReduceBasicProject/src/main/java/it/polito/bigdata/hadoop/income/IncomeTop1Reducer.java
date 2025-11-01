package it.polito.bigdata.hadoop.income;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Income - Top-1 most profitable date - Reducer
 */
class IncomeTop1Reducer extends Reducer<IntWritable, Text, Text, IntWritable> {
    private boolean emitted = false;

    @Override
    protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Only emit the first entry
        if (!emitted) {
            for (Text date : values) {
                context.write(date, new IntWritable(-key.get()));
                emitted = true;
                break;
            }
        }
    }
}

