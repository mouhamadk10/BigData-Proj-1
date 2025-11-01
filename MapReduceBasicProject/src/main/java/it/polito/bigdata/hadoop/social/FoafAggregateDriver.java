package it.polito.bigdata.hadoop.social;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class FoafAggregateDriver extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        int reducers = Integer.parseInt(args[0]);
        Path inputAdj = new Path(args[1]);
        Path inputCand = new Path(args[2]);
        Path output = new Path(args[3]);
        Configuration conf = this.getConf();
        Job job = Job.getInstance(conf);
        job.setJobName("FOAF Aggregate - Exact 2-hop");
        job.setJarByClass(FoafAggregateDriver.class);
        MultipleInputs.addInputPath(job, inputAdj, TextInputFormat.class, FoafAggregateMapper.class);
        MultipleInputs.addInputPath(job, inputCand, TextInputFormat.class, FoafAggregateMapper.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(FoafAggregateReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setNumReduceTasks(reducers);
        FileOutputFormat.setOutputPath(job, output);
        return job.waitForCompletion(true) ? 0 : 1;
    }
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new FoafAggregateDriver(), args);
        System.exit(res);
    }
}
