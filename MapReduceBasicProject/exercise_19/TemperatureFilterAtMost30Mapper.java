package it.polito.bigdata.hadoop.temperature.filteratmost30;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// Mapper for temperatures ≤ 30.0
public class TemperatureFilterAtMost30Mapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] parts = line.split(",");
        if (parts.length < 4) return;
        String temperatureStr = parts[3];
        try {
            double temperature = Double.parseDouble(temperatureStr);
            if (temperature <= 30.0) {
                context.write(new Text(""), value); // emit the whole line
            }
        } catch (NumberFormatException ignore) {}
    }
}


