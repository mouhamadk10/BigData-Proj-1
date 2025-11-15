package it.polito.bigdata.hadoop.pm10.abovethreshold;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PM10 - Count days above threshold per sensor - Mapper
 * Expected input CSV: sensorId,date,pm10
 */
class PM10AboveThresholdMapper extends Mapper<
		LongWritable,
		Text,
		Text,
		IntWritable> {

	private static final IntWritable ONE = new IntWritable(1);
	private final Text sensorIdOut = new Text();
	private float threshold;

	@Override
	protected void setup(Context context) {
		String t = context.getConfiguration().get("pm10.threshold", "50");
		try {
			threshold = Float.parseFloat(t);
		} catch (Exception e) {
			threshold = 50.0f;
		}
	}

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString().trim();
		if (line.isEmpty())
			return;
		// Format: sensorId,date\tpm10Value
		String[] parts = line.split("[\t,]");
		if (parts.length < 3)
			return;
		String sensorId = parts[0].trim();
		String pmStr = parts[2].trim();
		try {
			float pm = Float.parseFloat(pmStr);
			if (pm > threshold) {
				sensorIdOut.set(sensorId);
				context.write(sensorIdOut, ONE);
			}
		} catch (NumberFormatException ignore) {
			// skip malformed
		}
	}
}


