package it.polito.bigdata.hadoop.qa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class QAJoinReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String questionText = null;
        List<String> answers = new ArrayList<>();
        for (Text v : values) {
            String s = v.toString();
            if (s.startsWith("Q:")) {
                questionText = s.substring(2);
            } else if (s.startsWith("A:")) {
                answers.add(s.substring(2));
            }
        }
        if (questionText != null) {
            for (String answer : answers) {
                String[] ansParts = answer.split(":",2);
                String aid = ansParts.length > 1 ? ansParts[0] : "";
                String answerText = ansParts.length > 1 ? ansParts[1] : ansParts[0];
                context.write(new Text(key.toString()+","+questionText+","+aid), new Text(answerText));
            }
        }
    }
}
