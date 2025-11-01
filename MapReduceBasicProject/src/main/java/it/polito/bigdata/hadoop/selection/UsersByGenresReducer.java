package it.polito.bigdata.hadoop.selection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class UsersByGenresReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> genres = new HashSet<>();
        String userInfo = null; // Gender,YearOfBirth
        for (Text v : values) {
            String s = v.toString();
            if (s.startsWith("U:")) userInfo = s.substring(2);
            else if (s.startsWith("L:")) genres.add(s.substring(2));
        }
        if (userInfo != null && genres.contains("Commedia") && genres.contains("Adventure")) {
            context.write(null, new Text(userInfo));
        }
    }
}
