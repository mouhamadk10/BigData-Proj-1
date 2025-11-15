package it.polito.bigdata.hadoop.wordtoint;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WordToIntMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
    private final Map<String,Integer> dict = new HashMap<>();
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        String dictPath = context.getConfiguration().get("dict.file");
        if (dictPath != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(dictPath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] kv = line.trim().split("\t");
                    if (kv.length == 2) {
                        dict.put(kv[0].trim().toUpperCase(), Integer.parseInt(kv[1].trim()));
                    }
                }
            }
        }
    }
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] tokens = value.toString().trim().split("\\s+");
        StringBuilder b = new StringBuilder();
        for (String t : tokens) {
            Integer id = dict.get(t.toUpperCase());
            if (id != null) {
                if (b.length() > 0) b.append(' ');
                b.append(id);
            }
        }
        context.write(key, new Text(b.toString()));
    }
}
