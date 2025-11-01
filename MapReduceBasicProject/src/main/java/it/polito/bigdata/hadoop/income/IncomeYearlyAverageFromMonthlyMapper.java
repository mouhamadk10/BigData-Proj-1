package it.polito.bigdata.hadoop.income;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Income - Yearly average from monthly totals - Mapper
 * Input CSV (from previous job): YYYY-MM,total
 * Emits: key=YYYY, value=total (as IntWritable)
 */
class IncomeYearlyAverageFromMonthlyMapper extends Mapper<
		LongWritable,
		Text,
		Text,
		IntWritable> {

	private final Text yearOut = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString().trim();
		if (line.isEmpty())
			return;
		String[] parts = line.split(",");
		if (parts.length < 2)
			return;
		String month = parts[0].trim();
		String totalStr = parts[1].trim();
		if (month.length() < 7)
			return;
		String year = month.substring(0, 4);
		try {
			int total = Integer.parseInt(totalStr);
			yearOut.set(year);
			context.write(yearOut, new IntWritable(total));
		} catch (NumberFormatException ignore) {
		}
	}
}



