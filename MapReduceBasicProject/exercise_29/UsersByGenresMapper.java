package it.polito.bigdata.hadoop.selection;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class UsersByGenresMapper extends Mapper<LongWritable, Text, Text, Text> {
    private boolean isUsersFile = false;
    
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        InputSplit split = context.getInputSplit();
        if (split instanceof FileSplit) {
            FileSplit fileSplit = (FileSplit) split;
            String fileName = fileSplit.getPath().getName();
            isUsersFile = fileName.contains("users");
        }
    }
    
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        if (!isUsersFile) return; // Only process users file
        
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        // Format: UserId,Name,Surname,Gender,YearOfBirth,City,Education
        String[] parts = line.split(",");
        if (parts.length < 5) return;
        String userId = parts[0].trim();
        String gender = parts[3].trim();
        String yearOfBirth = parts[4].trim();
        
        // Key: Gender,YearOfBirth (e.g., "M,1934")
        String keyStr = gender + "," + yearOfBirth;
        context.write(new Text(keyStr), new Text(userId));
    }
}
