package it.polito.bigdata.hadoop.social;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AdjacencyListReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> friends = new HashSet<>();
        for (Text v : values) friends.add(v.toString());
        if (!friends.isEmpty()) context.write(key, new Text(String.join(" ", friends)));
    }
}
