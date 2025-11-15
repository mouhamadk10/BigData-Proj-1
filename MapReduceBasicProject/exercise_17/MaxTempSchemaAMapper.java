package it.polito.bigdata.hadoop.temperature.max;

import java.io.IOException;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MaxTempSchemaAMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
    private final Text outDate = new Text();
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] p = line.split(",");
        if (p.length < 4) return;
        String date = p[1].trim();
        String tempStr = p[3].trim();
        try {
            double t = Double.parseDouble(tempStr);
            outDate.set(date);
            context.write(outDate, new DoubleWritable(t));
        } catch (NumberFormatException ignore) {}
    }
}


