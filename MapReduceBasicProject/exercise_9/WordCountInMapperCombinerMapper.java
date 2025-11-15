package it.polito.bigdata.hadoop.wordcount.inmappercombiner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * WordCount - In-Mapper Combiner Mapper
 */
class WordCountInMapperCombinerMapper extends Mapper<
		LongWritable,
		Text,
		Text,
		IntWritable> {

	private final Map<String, Integer> localCounts = new HashMap<String, Integer>();
	private final Text outputWord = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] tokens = value.toString().split("\\s+");
		for (String token : tokens) {
			String cleaned = token.toLowerCase().replaceAll("[^a-z0-9]", "");
			if (cleaned.length() == 0)
				continue;
			Integer current = localCounts.get(cleaned);
			if (current == null)
				localCounts.put(cleaned, 1);
			else
				localCounts.put(cleaned, current + 1);
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		for (Map.Entry<String, Integer> entry : localCounts.entrySet()) {
			outputWord.set(entry.getKey());
			context.write(outputWord, new IntWritable(entry.getValue()));
		}
	}
}


