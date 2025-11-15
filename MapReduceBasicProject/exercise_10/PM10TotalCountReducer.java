package it.polito.bigdata.hadoop.pm10.totalcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PM10 - Total record count - Reducer
 */
class PM10TotalCountReducer extends Reducer<
		Text,
		IntWritable,
		Text,
		IntWritable> {

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int sum = 0;
		for (IntWritable v : values) {
			sum += v.get();
		}
		context.write(key, new IntWritable(sum));
	}
}


