package it.polito.bigdata.hadoop.social;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PotentialFriendsFromAdjacencyMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] kv = line.split("\t|"); // support tab or colon separators
        String user;
        String friendsStr;
        if (line.contains("\t")) {
            String[] parts = line.split("\t", 2);
            if (parts.length < 2) return;
            user = parts[0].trim();
            friendsStr = parts[1].trim();
        } else if (line.contains(":")) {
            String[] parts = line.split(":", 2);
            if (parts.length < 2) return;
            user = parts[0].trim();
            friendsStr = parts[1].trim();
        } else {
            return;
        }
        context.write(new Text(user), new Text("ADJ:" + friendsStr));
        for (String f : friendsStr.split(" ")) {
            f = f.trim();
            if (f.isEmpty()) continue;
            context.write(new Text(f), new Text("SEED:" + user));
        }
    }
}
