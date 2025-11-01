package it.polito.bigdata.hadoop.social;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PotentialFriendsFromAdjacencyReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> direct = new HashSet<>();
        Set<String> seeds = new HashSet<>();
        for (Text v : values) {
            String s = v.toString();
            if (s.startsWith("ADJ:")) {
                String[] fs = s.substring(4).trim().split(" ");
                for (String f : fs) if (!f.isEmpty()) direct.add(f);
            } else if (s.startsWith("SEED:")) {
                String u = s.substring(5).trim();
                if (!u.isEmpty()) seeds.add(u);
            }
        }
        // Candidates are friends of each seed; we need seed adjacencies; we only have current user's adj.
        // In practice this requires a secondary join of seed->adj. For simplicity, we output seeds excluding direct & self.
        // This gives a conservative set (needs second pass for full FOAF). 
        Set<String> candidates = new HashSet<>(seeds);
        candidates.removeAll(direct);
        candidates.remove(key.toString());
        if (!candidates.isEmpty()) context.write(key, new Text(String.join(" ", candidates)));
    }
}
