package it.polito.bigdata.hadoop.income.monthlytotals;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Income - Monthly totals - Mapper
 * Expected CSV: date,income  e.g., 2015-11-02,1305
 * Emits: key=YYYY-MM, value=income (as IntWritable)
 */
class IncomeMonthlyTotalsMapper extends Mapper<
		LongWritable,
		Text,
		Text,
		IntWritable> {

	private final Text monthOut = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString().trim();
		if (line.isEmpty())
			return;
		// Format: date\tincome (tab-separated)
		String[] parts = line.split("[\t,]");
		if (parts.length < 2)
			return;
		String date = parts[0].trim();
		String incStr = parts[1].trim();
		if (date.length() < 7)
			return;
		String month = date.substring(0, 7);
		try {
			int income = Integer.parseInt(incStr);
			monthOut.set(month);
			context.write(monthOut, new IntWritable(income));
		} catch (NumberFormatException ignore) {
		}
	}
}


