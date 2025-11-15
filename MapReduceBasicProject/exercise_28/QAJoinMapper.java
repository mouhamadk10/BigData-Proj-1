package it.polito.bigdata.hadoop.qa;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class QAJoinMapper extends Mapper<LongWritable, Text, Text, Text> {
    private boolean isQuestionFile = false;
    
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        InputSplit split = context.getInputSplit();
        if (split instanceof FileSplit) {
            FileSplit fileSplit = (FileSplit) split;
            String fileName = fileSplit.getPath().getName();
            isQuestionFile = fileName.contains("question");
        }
    }
    
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        
        if (isQuestionFile) {
            // Format: questionId,date,questionText
            String[] parts = line.split(",", 3);
            if (parts.length >= 2) {
                String questionId = parts[0].trim();
                String questionText = parts.length == 3 ? parts[2].trim() : parts[1].trim();
                context.write(new Text(questionId), new Text("Q:" + questionText));
            }
        } else {
            // Format: answerId,questionId,date,answerText
            String[] parts = line.split(",", 4);
            if (parts.length >= 3) {
                String answerId = parts[0].trim();
                String questionId = parts[1].trim();
                String answerText = parts.length == 4 ? parts[3].trim() : parts[2].trim();
                context.write(new Text(questionId), new Text("A:" + answerId + "," + answerText));
            }
        }
    }
}
