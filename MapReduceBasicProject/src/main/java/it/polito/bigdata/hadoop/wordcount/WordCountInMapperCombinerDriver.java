package it.polito.bigdata.hadoop.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * WordCount - In-Mapper Combiner Driver
 */
public class WordCountInMapperCombinerDriver extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		Path inputPath;
		Path outputDir;
		int numberOfReducers;
		int exitCode;

		numberOfReducers = Integer.parseInt(args[0]);
		inputPath = new Path(args[1]);
		outputDir = new Path(args[2]);

		Configuration conf = this.getConf();
		Job job = Job.getInstance(conf);
		job.setJobName("WordCount - InMapperCombiner");

		FileInputFormat.addInputPath(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputDir);

		job.setJarByClass(WordCountInMapperCombinerDriver.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setMapperClass(WordCountInMapperCombinerMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(WordCountInMapperCombinerReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setNumReduceTasks(numberOfReducers);

		if (job.waitForCompletion(true) == true)
			exitCode = 0;
		else
			exitCode = 1;

		return exitCode;
	}

	public static void main(String args[]) throws Exception {
		int res = ToolRunner.run(new Configuration(), new WordCountInMapperCombinerDriver(), args);
		System.exit(res);
	}
}



