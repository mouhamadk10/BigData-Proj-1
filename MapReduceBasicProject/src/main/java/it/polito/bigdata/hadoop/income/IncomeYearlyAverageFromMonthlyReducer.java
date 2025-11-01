package it.polito.bigdata.hadoop.income;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Income - Yearly average from monthly totals - Reducer
 * Only consider months with total > 0
 */
class IncomeYearlyAverageFromMonthlyReducer extends Reducer<
		Text,
		IntWritable,
		Text,
		DoubleWritable> {

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		double sum = 0.0;
		int count = 0;
		for (IntWritable v : values) {
			int tot = v.get();
			if (tot > 0) {
				sum += tot;
				count++;
			}
		}
		if (count > 0) {
			context.write(key, new DoubleWritable(sum / count));
		}
	}
}



