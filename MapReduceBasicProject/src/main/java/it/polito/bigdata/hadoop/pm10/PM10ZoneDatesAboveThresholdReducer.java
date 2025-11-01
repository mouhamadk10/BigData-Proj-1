package it.polito.bigdata.hadoop.pm10;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * PM10 - Per zone list of dates above threshold - Reducer
 */
class PM10ZoneDatesAboveThresholdReducer extends Reducer<
		Text,
		Text,
		Text,
		Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		Set<String> uniqueDates = new HashSet<String>();
		for (Text v : values) {
			uniqueDates.add(v.toString());
		}
		List<String> dates = new ArrayList<String>(uniqueDates);
		Collections.sort(dates, Collections.reverseOrder());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dates.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append(dates.get(i));
		}
		context.write(key, new Text("[" + sb.toString() + "]"));
	}
}



