package it.polito.bigdata.hadoop.social;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class FoafJoinReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String adj = null;
        List<String> reqs = new ArrayList<>();
        for (Text v : values) {
            String s = v.toString();
            if (s.startsWith("ADJ:")) adj = s.substring(4).trim();
            else if (s.startsWith("REQ:")) reqs.add(s.substring(4).trim());
        }
        if (adj == null || reqs.isEmpty()) return;
        for (String u : reqs) {
            if (!u.isEmpty()) context.write(new Text(u), new Text("CAND:" + adj));
        }
    }
}
