# Exercise 14: Income Top 2

## Description
This exercise implements a MapReduce job for income top 2.

## Files
- `IncomeTop2Driver` - MapReduce Driver class
- `*Mapper.java` - Map function implementation
- `*Reducer.java` - Reduce function implementation
- `input.txt` - Sample input data

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
hdfs dfs -rm -r /user/$USER/exercise_14/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_14/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_14/input

# Upload input file(s)
cd exercise_14
hdfs dfs -put input.txt /user/$USER/exercise_14/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.income.top2.IncomeTop2Driver \
  2 \
  /user/$USER/exercise_14/input \
  /user/$USER/exercise_14/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_14/output

# View results
hdfs dfs -cat /user/$USER/exercise_14/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_14/output/part-r-* | sort
```

## Expected Output
The output will contain the results of the MapReduce computation, typically in the format:
- Key-value pairs (one per line)
- Sorted by key

## Notes
- Number of reducers: 2
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
