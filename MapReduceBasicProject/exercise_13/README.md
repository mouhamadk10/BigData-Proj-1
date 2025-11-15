# Exercise 13: Income Top 1 Most Profitable Date

## Description
This exercise implements a MapReduce job to find the date with the highest income (top 1). It uses a clever sorting trick with negative values to achieve descending order.

## Code Explanation

### IncomeTopKMapper
The mapper prepares data for sorting:
- **Input**: `<LongWritable, Text>` - line number and CSV line
- **Output**: `<IntWritable, Text>` - negative income and date
- **Logic**:
  1. Parses line format: `date,income` (handles tab or comma separators)
  2. Extracts date and income value
  3. Converts income string to integer
  4. **Key trick**: Emits (-income, date) instead of (income, date)
  5. This uses Hadoop's natural ascending sort to achieve descending order
  6. Skips malformed lines (catches NumberFormatException)

### IncomeTop1Reducer
The reducer extracts the top entry:
- **Input**: `<IntWritable, Text>` - negative income and date
- **Output**: `<Text, IntWritable>` - date and income (positive)
- **Logic**:
  1. Uses a flag to emit only the first entry
  2. Since keys are sorted in ascending order (most negative = highest income comes first)
  3. Takes the first date from the first key's values
  4. Converts negative income back to positive: -key.get()
  5. Emits: (date, income)
  6. Only emits once (top 1)

### IncomeTop1Driver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Note: Map output key/value types differ from reduce output types
- Sets the number of reduce tasks to 1 (needed for global top-1)
- Submits and waits for job completion

## Files
- `IncomeTop1Driver.java` - MapReduce Driver class
- `IncomeTopKMapper.java` - Map function implementation
- `IncomeTop1Reducer.java` - Reduce function implementation
- `top_profitable_date_input.csv` - Sample income data (format: date,income)

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
hdfs dfs -rm -r /user/$USER/exercise_13/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_13/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_13/input

# Upload input file(s)
cd exercise_13
hdfs dfs -put top_profitable_date_input.csv /user/$USER/exercise_13/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.income.top1.IncomeTop1Driver \
  1 \
  /user/$USER/exercise_13/input \
  /user/$USER/exercise_13/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_13/output

# View results
hdfs dfs -cat /user/$USER/exercise_13/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_13/output/part-r-* | sort
```

## Expected Output
The output will contain the top 1 most profitable date:
- Format: `date<TAB>income`
- Example: `2015-01-15    5000`
- Only one line (the date with highest income)

## Notes
- Number of reducers: 1 (required for global top-1)
- Uses negative income trick for descending sort
- Only the top entry is emitted
- Malformed income values are skipped
- This pattern can be extended to top-K by modifying the reducer
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
