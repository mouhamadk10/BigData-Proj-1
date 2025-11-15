package it.polito.bigdata.hadoop.wordcount.standard;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * WordCount - Standard Mapper
 */
class WordCountStandardMapper extends Mapper<
		LongWritable,
		Text,
		Text,
		IntWritable> {

	private static final IntWritable ONE = new IntWritable(1);
	private final Text outputWord = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] tokens = value.toString().split("\\s+");
		for (String token : tokens) {
			String cleaned = token.toLowerCase().replaceAll("[^a-z0-9]", "");
			if (cleaned.length() == 0)
				continue;
			outputWord.set(cleaned);
			context.write(outputWord, ONE);
		}
	}
}


