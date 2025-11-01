package it.polito.bigdata.hadoop.categorization;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UserCategorizationMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        // UserId,Name,Surname,Gender,YearOfBirth,City,Education
        String[] parts = line.split(",");
        if (parts.length < 7) return;
        String userId = parts[0].trim();
        context.write(new Text(userId), new Text("U:" + line));
    }
}
