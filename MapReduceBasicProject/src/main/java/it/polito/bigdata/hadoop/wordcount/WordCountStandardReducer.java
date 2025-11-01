package it.polito.bigdata.hadoop.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * WordCount - Standard Reducer
 */
class WordCountStandardReducer extends Reducer<
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



