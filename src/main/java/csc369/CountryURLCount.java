package csc369;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class CountryURLCount {

    public static final Class OUTPUT_KEY_CLASS = Text.class;
    public static final Class OUTPUT_VALUE_CLASS = IntWritable.class;

    public static class MapperImpl extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            File file = new File("hostname_country.csv");
            Scanner sc = new Scanner(file);
            ArrayList<String> countries = new ArrayList<>();
            ArrayList<String> ip = new ArrayList<>();
            while(sc.hasNextLine()){
                String[] components = sc.nextLine().split(",");
                ip.add(components[0]);
                countries.add(components[1]);
            }

            String[] sa = value.toString().split(" ");
            String logIp = sa[0];
            for(int i = 0; i < ip.size(); i++){
                if(logIp.equals(ip.get(i))){
                    String url = sa[6];
                    String comboKey = countries.get(i) + " " + url;
                    word.set(comboKey);
                    context.write(word, one);
                }
            }

        }
    }

    public static class ReducerImpl extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        @Override
        protected void reduce(Text word, Iterable<IntWritable> intOne, Context context) throws IOException, InterruptedException {
            int sum = 0;
            Iterator<IntWritable> itr = intOne.iterator();

            while (itr.hasNext()) {
                sum  += itr.next().get();
            }
            result.set(sum);
            context.write(word, result);
        }
    }

}