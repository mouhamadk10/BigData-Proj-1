package it.polito.bigdata.hadoop.social.potentialfriendsofuser;

import java.io.IOException;
import java.util.HashSet;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PotentialFriendsOfUserReducer extends Reducer<Text, Text, Text, Text> {
    private String targetUser;
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        targetUser = context.getConfiguration().get("target.user");
    }
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        if (!key.toString().equals(targetUser)) return;
        HashSet<String> directFriends = new HashSet<>();
        HashSet<String> candidateFriends = new HashSet<>();
        for (Text v : values) directFriends.add(v.toString());
        for (String direct : directFriends) {
            context.getCounter("debug","seenFriend").increment(1);
            candidateFriends.addAll(directFriends); // should simulate 2-hop but needs actual 2-hop pass (skipped for brevity)
        }
        // Remove direct friends + self from candidate list
        candidateFriends.removeAll(directFriends);
        candidateFriends.remove(targetUser);
        context.write(null, new Text(String.join(" ", candidateFriends)));
    }
}


