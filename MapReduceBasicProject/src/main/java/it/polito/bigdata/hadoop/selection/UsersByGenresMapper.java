package it.polito.bigdata.hadoop.selection;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UsersByGenresMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] parts = line.split(",");
        if (parts.length == 7) {
            // User record: UserId,Name,Surname,Gender,YearOfBirth,City,Education
            String userId = parts[0].trim();
            String gender = parts[3].trim();
            String yob = parts[4].trim();
            context.write(new Text(userId), new Text("U:" + gender + "," + yob));
        } else if (parts.length == 2) {
            // Likes record: UserId,Genre
            String userId = parts[0].trim();
            String genre = parts[1].trim();
            context.write(new Text(userId), new Text("L:" + genre));
        }
    }
}
