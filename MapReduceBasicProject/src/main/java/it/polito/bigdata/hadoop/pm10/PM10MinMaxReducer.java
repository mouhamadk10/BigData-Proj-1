package it.polito.bigdata.hadoop.pm10;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PM10 - Min and Max per sensor - Reducer
 */
class PM10MinMaxReducer extends Reducer<
		Text,
		DoubleWritable,
		Text,
		Text> {

	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values, Context context)
			throws IOException, InterruptedException {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (DoubleWritable v : values) {
			double val = v.get();
			if (val < min)
				min = val;
			if (val > max)
				max = val;
		}
		if (min != Double.POSITIVE_INFINITY) {
			context.write(key, new Text("max=" + max + "_min=" + min));
		}
	}
}



