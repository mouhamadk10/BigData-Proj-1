package it.polito.bigdata.hadoop.income;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Income - Top-2 most profitable dates - Reducer
 */
class IncomeTop2Reducer extends Reducer<IntWritable, Text, Text, IntWritable> {
    private int emitted = 0;

    @Override
    protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text date : values) {
            if (emitted < 2) {
                context.write(date, new IntWritable(-key.get()));
                emitted++;
            } else {
                break;
            }
        }
    }
}

