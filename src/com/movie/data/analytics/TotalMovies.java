/**
 * @author Geethika Garikapati
 * Find the total number of movies in the dataset.
 */

package com.movie.data.analytics;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class TotalMovies {

	public static class Map extends Mapper<LongWritable,Text,Text,IntWritable>{

		public void map(LongWritable key, Text value,
				Context context)
				throws IOException,InterruptedException {
			
			String[] tokenizer = value.toString().split(",");
			System.out.println("Line is: " +value);
			
			Set<String> moviesSet = new HashSet<String>();
			moviesSet.add(tokenizer[1]);
			
			for(String s: moviesSet){
				context.write(new Text("TotalMovies"), new IntWritable(1));
			}
		}
		
	}
	
	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>{

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context)
				throws IOException,InterruptedException {
			int totalMovieRating = 0;
			
			for(IntWritable x: values) {
				totalMovieRating++;
			}
			context.write(new Text("Total no.of movies in the dataset: "), new IntWritable(totalMovieRating));
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {
	
		Configuration conf= new Configuration();
		
		@SuppressWarnings("deprecation")
		Job job = new Job(conf,"movieDuration");
		
		job.setJarByClass(TotalMovies.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		Path outputPath = new Path(args[1]);
	        
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
			
		outputPath.getFileSystem(conf).delete(outputPath);
			
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
