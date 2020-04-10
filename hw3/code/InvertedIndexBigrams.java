import java.io.IOException;
import java.util.*;
import org.apache.hadoop.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndexBigrams {

  	public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text> {
    	private Text word = new Text();
    	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      		Text id = new Text();
      		id.set(value.toString().split("\t")[0]);
      		String line = value.toString().toLowerCase().replaceAll("[^a-z]", " ");
          StringBuilder prev = new StringBuilder();
      		StringTokenizer iterator = new StringTokenizer(line);
      		while (iterator.hasMoreTokens()) {
            StringBuilder curr = new StringBuilder(iterator.nextToken());
            if(prev.length() > 0) {
              prev.append(" " + curr);
              word.set(prev.toString());
              context.write(word, id);
            }
        		prev = curr;
      		}
    	}
  	}

  	public static class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
    	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
    		Map<String, Integer> invertedIndexMap = new HashMap<String, Integer>();
			for(Text value: values) {
				String id = value.toString();
				invertedIndexMap.put(id, invertedIndexMap.getOrDefault(id, 0) + 1);
			}
			StringBuilder sb = new StringBuilder();
			Text output = new Text();
			for(String id: invertedIndexMap.keySet()) {
				sb.append(id);
				sb.append(":");
				sb.append(invertedIndexMap.get(id));
				sb.append("\t");
			}
			output.set(sb.toString());
			context.write(key, output);
    	}
  	}

  	public static void main(String[] args) throws Exception {
  		if (args.length != 2) {
      		System.err.println("Usage: Inverted Index <input path> <output path>");
      		System.exit(-1);
    	}
    	Job job = new Job();
    	job.setJarByClass(InvertedIndexBigrams.class);
    	job.setJobName("Inverted Index");

    	job.setMapperClass(InvertedIndexMapper.class);
    	job.setReducerClass(InvertedIndexReducer.class);

    	job.setOutputKeyClass(Text.class);
    	job.setOutputValueClass(Text.class);

    	FileInputFormat.addInputPath(job, new Path(args[0]));
    	FileOutputFormat.setOutputPath(job, new Path(args[1]));

    	job.waitForCompletion(true);
  	}
}



