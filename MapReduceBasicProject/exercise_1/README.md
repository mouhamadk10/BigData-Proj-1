# Exercise 1: Word Count (Standard) - Single File

## Description
This exercise implements a MapReduce job for word count (standard) - single file. It counts the frequency of each word in the input text file.

## Code Explanation

### WordCountStandardMapper
The mapper processes each line of input text:
- **Input**: `<LongWritable, Text>` - line number and line content
- **Output**: `<Text, IntWritable>` - word and count of 1
- **Logic**:
  1. Splits each line into tokens using whitespace
  2. For each token, converts to lowercase and removes non-alphanumeric characters
  3. Skips empty tokens
  4. Emits each cleaned word with a count of 1

### WordCountStandardReducer
The reducer aggregates word counts:
- **Input**: `<Text, IntWritable>` - word and list of counts (all 1s)
- **Output**: `<Text, IntWritable>` - word and total count
- **Logic**:
  1. Receives all counts for the same word
  2. Sums all the counts (which are all 1)
  3. Emits the word with its total frequency

### WordCountStandardDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks
- Submits and waits for job completion

## Files
- `WordCountStandardDriver.java` - MapReduce Driver class
- `WordCountStandardMapper.java` - Map function implementation
- `WordCountStandardReducer.java` - Reduce function implementation
- `wordcount_input.txt` - Sample input data

## Prerequisites
1. Ensure Hadoop is running:
   ```bash
   start-dfs.sh
   start-yarn.sh
   ```

2. Build the project (from project root):
   ```bash
   # Create temporary build structure
   mkdir -p temp_build/src/main/java/it/polito/bigdata/hadoop/wordcount/standard
   cp exercise_1/*.java temp_build/src/main/java/it/polito/bigdata/hadoop/wordcount/standard/
   cp pom.xml temp_build/
   cd temp_build
   mvn clean package -DskipTests
   cd ..
   ```

## Execution Steps

### 1. Upload Input Data to HDFS
```bash
# Remove previous output only (keep input to avoid re-uploading)
hdfs dfs -rm -r /user/$USER/exercise_1/output 2>/dev/null

# Create input directory (if it doesn't exist)
hdfs dfs -mkdir -p /user/$USER/exercise_1/input

# Upload input file(s)
cd exercise_1
hdfs dfs -put wordcount_input.txt /user/$USER/exercise_1/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar temp_build/target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.wordcount.standard.WordCountStandardDriver \
  2 \
  /user/$USER/exercise_1/input \
  /user/$USER/exercise_1/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_1/output

# View results
hdfs dfs -cat /user/$USER/exercise_1/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_1/output/part-r-* | sort
```

## Expected Output
The output will contain word count pairs, one per line:
- Format: `word<TAB>count`
- Sorted by word (key)
- Example: `hello    3`

## Notes
- Number of reducers: 2
- Words are converted to lowercase and non-alphanumeric characters are removed
- Empty tokens are filtered out
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
