package it.polito.bigdata.hadoop.social.friendsofuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FriendsOfUserReducer extends Reducer<Text, Text, Text, Text> {
    private String targetUser;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        targetUser = context.getConfiguration().get("target.user");
    }
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        if (key.toString().equals(targetUser)) {
            List<String> friends = new ArrayList<>();
            for (Text v : values) friends.add(v.toString());
            context.write(null, new Text(String.join(" ", friends)));
        }
    }
}


