package it.polito.bigdata.hadoop.qa;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class QAJoinMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] parts = line.split(",");
        if (parts.length == 3) { // Question format: QID,Timestamp,Text
            String qid = parts[0].trim();
            String qtxt = parts[2].trim();
            context.write(new Text(qid), new Text("Q:" + qtxt));
        } else if (parts.length == 4) { // Answer format: AID,QID,Timestamp,Text
            String qid = parts[1].trim();
            String aid = parts[0].trim();
            String atxt = parts[3].trim();
            context.write(new Text(qid), new Text("A:" + aid + ":" + atxt));
        }
    }
}
