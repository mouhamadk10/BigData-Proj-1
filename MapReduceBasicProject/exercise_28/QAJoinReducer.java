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
        
        for (Text value : values) {
            String val = value.toString();
            if (val.startsWith("Q:")) {
                questionText = val.substring(2);
            } else if (val.startsWith("A:")) {
                answers.add(val.substring(2));
            }
        }
        
        if (questionText != null) {
            for (String answer : answers) {
                context.write(new Text(questionText), new Text(answer));
            }
        }
    }
}
