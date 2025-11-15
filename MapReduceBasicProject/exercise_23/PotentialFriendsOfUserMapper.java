package it.polito.bigdata.hadoop.social.potentialfriendsofuser;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class PotentialFriendsOfUserMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] users = line.split(",");
        if (users.length < 2) return;
        String u1 = users[0].trim();
        String u2 = users[1].trim();
        context.write(new Text(u1), new Text(u2));
        context.write(new Text(u2), new Text(u1));
    }
}


