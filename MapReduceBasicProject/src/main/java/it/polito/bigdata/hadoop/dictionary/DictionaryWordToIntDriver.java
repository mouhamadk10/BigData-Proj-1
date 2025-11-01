package it.polito.bigdata.hadoop.dictionary;

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

// Driver for dictionary word-to-unique-integer mapping
public class DictionaryWordToIntDriver extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {
        int reducers = Integer.parseInt(args[0]);
        Path input = new Path(args[1]);     // This should be the output of the previous dictionary job
        Path output = new Path(args[2]);
        Configuration conf = this.getConf();
        Job job = Job.getInstance(conf);
        job.setJobName("Dictionary Word-to-Integer");
        job.setJarByClass(DictionaryWordToIntDriver.class);

        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        // identity mapper: key=word, value=null
        job.setMapperClass(org.apache.hadoop.mapreduce.Mapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setReducerClass(DictionaryWordToIntReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setNumReduceTasks(reducers);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new DictionaryWordToIntDriver(), args);
        System.exit(res);
    }
}