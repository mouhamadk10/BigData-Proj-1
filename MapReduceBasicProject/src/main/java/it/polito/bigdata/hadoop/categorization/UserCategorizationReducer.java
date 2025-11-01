package it.polito.bigdata.hadoop.categorization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class UserCategorizationReducer extends Reducer<Text, Text, Text, Text> {
    private static class Rule {
        String gender;
        String yob;
        String category;
    }
    private final List<Rule> rules = new ArrayList<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        String rulesPath = context.getConfiguration().get("rules.file");
        if (rulesPath != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(rulesPath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    String[] lr = line.split("->");
                    if (lr.length != 2) continue;
                    String left = lr[0].trim();
                    String right = lr[1].trim();
                    Rule r = new Rule();
                    String[] cond = left.split("and");
                    for (String c : cond) {
                        String[] kv = c.trim().split("=");
                        if (kv.length != 2) continue;
                        String k = kv[0].trim().toLowerCase();
                        String v = kv[1].trim();
                        if (k.equals("gender")) r.gender = v;
                        else if (k.equals("yearofbirth")) r.yob = v;
                    }
                    r.category = right.trim();
                    rules.add(r);
                }
            }
        }
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text v : values) {
            String s = v.toString();
            if (!s.startsWith("U:")) continue;
            String rec = s.substring(2);
            String[] p = rec.split(",");
            if (p.length < 7) continue;
            String gender = p[3].trim();
            String yob = p[4].trim();
            String category = "Unknown";
            for (Rule r : rules) {
                if ((r.gender == null || r.gender.equals(gender)) && (r.yob == null || r.yob.equals(yob))) {
                    category = r.category;
                    break;
                }
            }
            context.write(null, new Text(rec + "," + category));
        }
    }
}
