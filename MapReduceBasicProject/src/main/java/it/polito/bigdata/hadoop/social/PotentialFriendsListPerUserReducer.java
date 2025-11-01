package it.polito.bigdata.hadoop.social;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PotentialFriendsListPerUserReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> potentialFriends = new HashSet<>();

        for (Text value : values) {
            String[] friends = value.toString().split(" ");
            for (String friend : friends) {
                if (!friend.equals(key.toString())) {
                    potentialFriends.add(friend);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String friend : potentialFriends) {
            sb.append(friend).append(" ");
        }
        context.write(key, new Text(sb.toString().trim()));
    }
}
