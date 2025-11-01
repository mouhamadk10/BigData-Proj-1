package it.polito.bigdata.hadoop.pm10;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PM10 - Average per sensor - Reducer
 */
class PM10AverageReducer extends Reducer<
		Text,
		DoubleWritable,
		Text,
		DoubleWritable> {

	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {
		double sum = 0.0;
		long count = 0;
		for (DoubleWritable v : values) {
			sum += v.get();
			count++;
		}
		if (count > 0) {
			context.write(key, new DoubleWritable(sum / count));
		}
	}
}



