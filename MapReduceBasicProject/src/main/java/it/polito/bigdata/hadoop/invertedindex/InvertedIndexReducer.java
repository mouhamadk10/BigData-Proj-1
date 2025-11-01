package it.polito.bigdata.hadoop.invertedindex;

import java.io.IOException;
import java.util.TreeSet;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        TreeSet<String> uniqueIds = new TreeSet<>();
        for (Text id : values) {
            uniqueIds.add(id.toString());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (String id : uniqueIds) {
            if (!first) sb.append(", ");
            sb.append(id);
            first = false;
        }
        sb.append("]");
        context.write(key, new Text(sb.toString()));
    }
}