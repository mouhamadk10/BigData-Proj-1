package it.polito.bigdata.hadoop.social.friendslistperuser;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class FriendsListPerUserMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] parts = line.split(",");
        if (parts.length < 2) return;
        String u1 = parts[0].trim();
        String u2 = parts[1].trim();
        context.write(new Text(u1), new Text(u2));
        context.write(new Text(u2), new Text(u1));
    }
}


