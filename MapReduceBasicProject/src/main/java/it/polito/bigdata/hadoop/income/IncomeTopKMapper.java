package it.polito.bigdata.hadoop.income;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Income - Top-K profitable dates - Mapper
 * Expects: date,income (csv)
 * Emits: key=(-income), value=date
 */
class IncomeTopKMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
    private final IntWritable outIncome = new IntWritable();
    private final Text outDate = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;
        String[] parts = line.split(",");
        if (parts.length < 2) return;
        String date = parts[0].trim();
        String incStr = parts[1].trim();
        try {
            int income = Integer.parseInt(incStr);
            // Negative for descending order in natural Hadoop sorting
            outIncome.set(-income);
            outDate.set(date);
            context.write(outIncome, outDate);
        } catch (NumberFormatException ignore) {
        }
    }
}

