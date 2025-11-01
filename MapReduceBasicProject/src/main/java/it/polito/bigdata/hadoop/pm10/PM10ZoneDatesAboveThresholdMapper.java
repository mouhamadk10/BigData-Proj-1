package it.polito.bigdata.hadoop.pm10;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * PM10 - Per zone list of dates above threshold - Mapper
 * Expected input CSV: zoneId,date,pm10
 * Emits: key=zoneId, value=date (only if pm10 > threshold)
 */
class PM10ZoneDatesAboveThresholdMapper extends Mapper<
		LongWritable,
		Text,
		Text,
		Text> {

	private final Text zoneOut = new Text();
	private final Text dateOut = new Text();
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
		String[] parts = line.split(",");
		if (parts.length < 3)
			return;
		String zone = parts[0].trim();
		String date = parts[1].trim();
		String pmStr = parts[2].trim();
		try {
			float pm = Float.parseFloat(pmStr);
			if (pm > threshold) {
				zoneOut.set(zone);
				dateOut.set(date);
				context.write(zoneOut, dateOut);
			}
		} catch (NumberFormatException ignore) {
		}
	}
}



