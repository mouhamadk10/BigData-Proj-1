package it.polito.bigdata.hadoop.social;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FoafAggregateReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> adj = new HashSet<>();
        Set<String> cands = new HashSet<>();
        for (Text v : values) {
            String s = v.toString();
            if (s.startsWith("ADJ:")) {
                String[] fs = s.substring(4).trim().split(" ");
                for (String f : fs) if (!f.isEmpty()) adj.add(f);
            } else if (s.startsWith("CAND:")) {
                String[] fs = s.substring(5).trim().split(" ");
                for (String f : fs) if (!f.isEmpty()) cands.add(f);
            }
        }
        cands.removeAll(adj);
        cands.remove(key.toString());
        if (!cands.isEmpty()) context.write(key, new Text(String.join(" ", cands)));
    }
}
