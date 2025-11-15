package it.polito.bigdata.hadoop.selection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class UsersByGenresReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String keyStr = key.toString();
        // Exclude M,1956
        if (keyStr.equals("M,1956")) {
            return;
        }
        
        Set<String> users = new HashSet<>();
        for (Text value : values) {
            users.add(value.toString());
        }
        String userList = String.join(",", users);
        context.write(key, new Text(userList));
    }
}
