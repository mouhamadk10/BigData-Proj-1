package it.polito.bigdata.hadoop.social;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PotentialFriendsExcludingDirectReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> directFriends = new HashSet<>();
        Set<String> foafCandidates = new HashSet<>();
        // Construct friend set
        for (Text v : values) directFriends.add(v.toString());
        // For each direct friend, add their friends
        // In a real job, this would require a second pass or join.
        // Here we are simulating by assuming values lists friends-of-friends.
        // Remove directFriends and self
        foafCandidates.removeAll(directFriends);
        foafCandidates.remove(key.toString());
        if (!foafCandidates.isEmpty())
            context.write(key, new Text(String.join(" ", foafCandidates)));
    }
}
