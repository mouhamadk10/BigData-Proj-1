# Exercise 11: PM10 Average per Sensor

## Description
This exercise implements a MapReduce job to calculate the average PM10 value for each sensor. It processes PM10 air quality data and computes the mean value per sensor.

## Code Explanation

### PM10AverageMapper
The mapper extracts sensor ID and PM10 value:
- **Input**: `<LongWritable, Text>` - line number and CSV line
- **Output**: `<Text, DoubleWritable>` - sensor ID and PM10 value
- **Logic**:
  1. Parses CSV format: `sensorId,date,pm10`
  2. Extracts sensor ID (index 0) and PM10 value (index 2)
  3. Converts PM10 string to double
  4. Skips malformed lines (catches NumberFormatException)
  5. Emits: (sensorId, pm10Value)

### PM10AverageReducer
The reducer calculates the average:
- **Input**: `<Text, DoubleWritable>` - sensor ID and list of PM10 values
- **Output**: `<Text, DoubleWritable>` - sensor ID and average PM10 value
- **Logic**:
  1. Receives all PM10 values for a given sensor
  2. Sums all values and counts the number of records
  3. Calculates average: sum / count
  4. Emits: (sensorId, averagePM10)
  5. Only emits if count > 0 (avoids division by zero)

### PM10AverageDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Uses DoubleWritable for floating-point PM10 values
- Sets the number of reduce tasks to 2
- Submits and waits for job completion

## Files
- `PM10AverageDriver.java` - MapReduce Driver class
- `PM10AverageMapper.java` - Map function implementation
- `PM10AverageReducer.java` - Reduce function implementation
- `pm10_average_input.csv` - Sample PM10 input data (format: sensorId,date,pm10)

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
hdfs dfs -rm -r /user/$USER/exercise_11/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_11/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_11/input

# Upload input file(s)
cd exercise_11
hdfs dfs -put pm10_average_input.csv /user/$USER/exercise_11/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.pm10.average.PM10AverageDriver \
  2 \
  /user/$USER/exercise_11/input \
  /user/$USER/exercise_11/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_11/output

# View results
hdfs dfs -cat /user/$USER/exercise_11/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_11/output/part-r-* | sort
```

## Expected Output
The output will contain average PM10 values per sensor:
- Format: `sensorId<TAB>averagePM10`
- Example: `Sensor1    45.5`
- Sorted by sensor ID

## Notes
- Number of reducers: 2
- Malformed PM10 values are skipped
- Average calculation uses sum/count pattern
- Empty lines are skipped
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
