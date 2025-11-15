# Exercise 12: PM10 Above Threshold Count

## Description
This exercise implements a MapReduce job to count the number of days each sensor recorded PM10 values above a specified threshold. It filters records based on a configurable threshold value.

## Code Explanation

### PM10AboveThresholdMapper
The mapper filters records above threshold:
- **Input**: `<LongWritable, Text>` - line number and CSV line
- **Output**: `<Text, IntWritable>` - sensor ID and count of 1
- **Logic**:
  1. **setup() method**:
     - Reads threshold from configuration ("pm10.threshold")
     - Defaults to 50.0 if not set or invalid
  2. **map() method**:
     - Parses line format: `sensorId,date,pm10` (handles tab or comma separators)
     - Extracts sensor ID and PM10 value
     - Converts PM10 string to float
     - If PM10 > threshold, emits: (sensorId, 1)
     - Skips records below threshold
     - Skips malformed lines (catches NumberFormatException)

### PM10AboveThresholdReducer
The reducer counts days above threshold:
- **Input**: `<Text, IntWritable>` - sensor ID and list of counts (all 1s)
- **Output**: `<Text, IntWritable>` - sensor ID and total count
- **Logic**:
  1. Receives all counts for a given sensor (each count represents one day above threshold)
  2. Sums all the counts
  3. Emits: (sensorId, totalDaysAboveThreshold)

### PM10AboveThresholdDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, threshold, input path, output path)
- Sets the threshold value in configuration for mapper access
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 2
- Submits and waits for job completion

## Files
- `PM10AboveThresholdDriver.java` - MapReduce Driver class
- `PM10AboveThresholdMapper.java` - Map function implementation
- `PM10AboveThresholdReducer.java` - Reduce function implementation
- `select_outliers_file*.csv` - Sample PM10 input data files

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
hdfs dfs -rm -r /user/$USER/exercise_12/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_12/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_12/input

# Upload input file(s)
cd exercise_12
hdfs dfs -put select_outliers_file*.csv /user/$USER/exercise_12/input/
hdfs dfs -put select_outliers_file*.txt /user/$USER/exercise_12/input/ 2>/dev/null
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job (threshold is passed as argument)
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.pm10.abovethreshold.PM10AboveThresholdDriver \
  2 \
  50 \
  /user/$USER/exercise_12/input \
  /user/$USER/exercise_12/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_12/output

# View results
hdfs dfs -cat /user/$USER/exercise_12/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_12/output/part-r-* | sort
```

## Expected Output
The output will contain the count of days above threshold per sensor:
- Format: `sensorId<TAB>count`
- Example: `Sensor1    15` (15 days above threshold)
- Sorted by sensor ID

## Notes
- Number of reducers: 2
- Threshold is configurable via command-line argument
- Default threshold is 50.0 if not specified
- Only records above threshold are counted
- Malformed PM10 values are skipped
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
