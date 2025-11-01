package it.polito.bigdata.hadoop.social;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FoafAggregateMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String user;
        String rest;
        if (line.contains("\t")) {
            String[] parts = line.split("\t", 2);
            if (parts.length < 2) return;
            user = parts[0].trim();
            rest = parts[1].trim();
            // If this is a candidate line coming from Stage 2, it starts with CAND:
            if (rest.startsWith("CAND:")) {
                context.write(new Text(user), new Text(rest));
            } else {
                // Otherwise treat as adjacency list
                context.write(new Text(user), new Text("ADJ:" + rest));
            }
        } else if (line.contains(":")) {
            String[] parts = line.split(":", 2);
            if (parts.length < 2) return;
            user = parts[0].trim();
            rest = parts[1].trim();
            context.write(new Text(user), new Text(rest));
        } else {
            return;
        }
    }
}
