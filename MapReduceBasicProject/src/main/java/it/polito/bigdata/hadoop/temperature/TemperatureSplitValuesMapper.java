package it.polito.bigdata.hadoop.temperature;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

// Mapper for splitting values: for high (>30.0) output only temperature, for normal the full line
public class TemperatureSplitValuesMapper extends Mapper<LongWritable, Text, Text, Text> {
    private static final Text highKey = new Text("high");
    private static final Text normalKey = new Text("normal");
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] parts = line.split(",");
        if (parts.length < 4) return;
        String temperatureStr = parts[3];
        try {
            double temperature = Double.parseDouble(temperatureStr);
            if (temperature > 30.0) {
                context.write(highKey, new Text(temperatureStr));
            } else {
                context.write(normalKey, value);
            }
        } catch (NumberFormatException ignore) {}
    }
}
