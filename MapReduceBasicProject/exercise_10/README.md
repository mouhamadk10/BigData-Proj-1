# Exercise 10: PM10 Total Count

## Description
This exercise implements a MapReduce job to count the total number of records in PM10 air quality data files. It's a simple counting job that processes all non-empty lines.

## Code Explanation

### PM10TotalCountMapper
The mapper counts each non-empty line:
- **Input**: `<LongWritable, Text>` - line number and line content
- **Output**: `<Text, IntWritable>` - constant key "ALL" and count of 1
- **Logic**:
  1. Checks if the line is empty
  2. If not empty, emits ("ALL", 1) for every record
  3. Uses a constant key "ALL" to aggregate all records together
  4. This is a simple counting pattern where all records go to the same reducer

### PM10TotalCountReducer
The reducer sums all counts:
- **Input**: `<Text, IntWritable>` - "ALL" key and list of counts (all 1s)
- **Output**: `<Text, IntWritable>` - "ALL" and total count
- **Logic**:
  1. Receives all counts for the "ALL" key
  2. Sums all the counts (which are all 1)
  3. Emits ("ALL", totalCount) - the total number of records

### PM10TotalCountDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 2
- Submits and waits for job completion

## Files
- `PM10TotalCountDriver.java` - MapReduce Driver class
- `PM10TotalCountMapper.java` - Map function implementation
- `PM10TotalCountReducer.java` - Reduce function implementation
- `total_count_input.csv` - Sample PM10 input data

## Prerequisites
1. Ensure Hadoop is running:
   ```bash
   start-dfs.sh
   start-yarn.sh
   ```

2. Build the project (from project root):
   ```bash
   mvn clean package
   ```

## Execution Steps

### 1. Upload Input Data to HDFS
```bash
# Remove previous input/output if they exist
hdfs dfs -rm -r /user/$USER/exercise_10/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_10/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_10/input

# Upload input file(s)
cd exercise_10
hdfs dfs -put total_count_input.csv /user/$USER/exercise_10/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.pm10.totalcount.PM10TotalCountDriver \
  2 \
  /user/$USER/exercise_10/input \
  /user/$USER/exercise_10/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_10/output

# View results
hdfs dfs -cat /user/$USER/exercise_10/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_10/output/part-r-* | sort
```

## Expected Output
The output will contain the total record count:
- Format: `ALL<TAB>totalCount`
- Example: `ALL    150` (if there are 150 records in the input)

## Notes
- Number of reducers: 2
- Empty lines are skipped
- All records are counted regardless of their content
- This is a simple aggregation pattern using a constant key
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
