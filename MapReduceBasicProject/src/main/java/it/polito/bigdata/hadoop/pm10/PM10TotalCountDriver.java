package it.polito.bigdata.hadoop.pm10;

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
 * PM10 - Total record count - Driver
 * Args: numReducers input output
 */
public class PM10TotalCountDriver extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		int reducers = Integer.parseInt(args[0]);
		Path input = new Path(args[1]);
		Path output = new Path(args[2]);

		Configuration conf = this.getConf();
		Job job = Job.getInstance(conf);
		job.setJobName("PM10 - Total Record Count");
		job.setJarByClass(PM10TotalCountDriver.class);

		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setMapperClass(PM10TotalCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(PM10TotalCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setNumReduceTasks(reducers);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new PM10TotalCountDriver(), args);
		System.exit(res);
	}
}



