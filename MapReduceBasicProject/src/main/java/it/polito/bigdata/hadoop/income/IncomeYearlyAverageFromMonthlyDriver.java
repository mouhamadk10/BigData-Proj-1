package it.polito.bigdata.hadoop.income;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
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
 * Income - Yearly average from monthly totals - Driver
 * Args: numReducers inputMonthlyTotals output
 */
public class IncomeYearlyAverageFromMonthlyDriver extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		int reducers = Integer.parseInt(args[0]);
		Path input = new Path(args[1]);
		Path output = new Path(args[2]);

		Configuration conf = this.getConf();
		Job job = Job.getInstance(conf);
		job.setJobName("Income - Yearly Average from Monthly Totals (CSV)");
		job.setJarByClass(IncomeYearlyAverageFromMonthlyDriver.class);

		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setMapperClass(IncomeYearlyAverageFromMonthlyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setReducerClass(IncomeYearlyAverageFromMonthlyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

		job.setNumReduceTasks(reducers);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new IncomeYearlyAverageFromMonthlyDriver(), args);
		System.exit(res);
	}
}



