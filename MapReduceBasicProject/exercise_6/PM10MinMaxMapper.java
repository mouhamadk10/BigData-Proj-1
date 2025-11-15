package it.polito.bigdata.hadoop.pm10.minmax;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PM10 - Min and Max per sensor - Mapper
 * Expected input CSV: sensorId,date,pm10
 */
class PM10MinMaxMapper extends Mapper<
		LongWritable,
		Text,
		Text,
		DoubleWritable> {

	private final Text sensorIdOut = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString().trim();
		if (line.isEmpty())
			return;
		String[] parts = line.split(",");
		if (parts.length < 3)
			return;
		String sensorId = parts[0].trim();
		String pmStr = parts[2].trim();
		try {
			double pm = Double.parseDouble(pmStr);
			sensorIdOut.set(sensorId);
			context.write(sensorIdOut, new DoubleWritable(pm));
		} catch (NumberFormatException ignore) {
			// skip malformed
		}
	}
}


