package it.polito.bigdata.hadoop.pm10;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PM10 - Total record count - Mapper
 */
class PM10TotalCountMapper extends Mapper<
		LongWritable,
		Text,
		Text,
		IntWritable> {

	private static final Text KEY_ALL = new Text("ALL");
	private static final IntWritable ONE = new IntWritable(1);

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString().trim();
		if (line.isEmpty())
			return;
		context.write(KEY_ALL, ONE);
	}
}



