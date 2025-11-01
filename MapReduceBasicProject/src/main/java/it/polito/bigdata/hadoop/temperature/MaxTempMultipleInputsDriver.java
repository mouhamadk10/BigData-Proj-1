package it.polito.bigdata.hadoop.temperature;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MaxTempMultipleInputsDriver extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        int reducers = Integer.parseInt(args[0]);
        Path inputA = new Path(args[1]);
        Path inputB = new Path(args[2]);
        Path output = new Path(args[3]);

        Configuration conf = this.getConf();
        Job job = Job.getInstance(conf);
        job.setJobName("Max Temperature per Date (Two Schemas)");
        job.setJarByClass(MaxTempMultipleInputsDriver.class);

        MultipleInputs.addInputPath(job, inputA, TextInputFormat.class, MaxTempSchemaAMapper.class);
        MultipleInputs.addInputPath(job, inputB, TextInputFormat.class, MaxTempSchemaBMapper.class);
        FileOutputFormat.setOutputPath(job, output);

        job.setOutputFormatClass(TextOutputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);
        job.setReducerClass(MaxTempReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setNumReduceTasks(reducers);
        return job.waitForCompletion(true) ? 0 : 1;
    }
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new MaxTempMultipleInputsDriver(), args);
        System.exit(res);
    }
}
