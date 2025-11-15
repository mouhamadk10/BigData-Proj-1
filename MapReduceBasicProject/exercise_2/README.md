# Exercise 2: Word Count (Standard) - Multiple Files

## Description
This exercise implements a MapReduce job for word count (standard) - multiple files. It counts the frequency of each word across multiple input files, similar to exercise 1 but handles multiple input files.

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
  5. Works identically to exercise 1, but processes multiple input files

### WordCountStandardReducer
The reducer aggregates word counts:
- **Input**: `<Text, IntWritable>` - word and list of counts (all 1s)
- **Output**: `<Text, IntWritable>` - word and total count
- **Logic**:
  1. Receives all counts for the same word from all input files
  2. Sums all the counts (which are all 1)
  3. Emits the word with its total frequency across all files

### WordCountStandardDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 2
- Submits and waits for job completion
- Handles multiple input files in the input directory

## Files
- `WordCountStandardDriver.java` - MapReduce Driver class
- `WordCountStandardMapper.java` - Map function implementation
- `WordCountStandardReducer.java` - Reduce function implementation
- `input_files/` - Directory containing multiple input text files

## Prerequisites
1. Ensure Hadoop is running:
   ```bash
   start-dfs.sh
   start-yarn.sh
   ```

2. Build the project (from project root):
   ```bash
   # Create temporary build structure
   mkdir -p temp_build2/src/main/java/it/polito/bigdata/hadoop/wordcount/standard
   cp exercise_2/*.java temp_build2/src/main/java/it/polito/bigdata/hadoop/wordcount/standard/
   cp pom.xml temp_build2/
   cd temp_build2
   mvn clean package -DskipTests
   cd ..
   ```

## Execution Steps

### 1. Upload Input Data to HDFS
```bash
# Remove previous output only (keep input to avoid re-uploading)
hdfs dfs -rm -r /user/$USER/exercise_2/output 2>/dev/null

# Create input directory (if it doesn't exist)
hdfs dfs -mkdir -p /user/$USER/exercise_2/input

# Upload input file(s)
cd exercise_2
hdfs dfs -put input_files/*.txt /user/$USER/exercise_2/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar temp_build2/target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.wordcount.standard.WordCountStandardDriver \
  2 \
  /user/$USER/exercise_2/input \
  /user/$USER/exercise_2/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_2/output

# View results
hdfs dfs -cat /user/$USER/exercise_2/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_2/output/part-r-* | sort
```

## Expected Output
The output will contain word count pairs, one per line:
- Format: `word<TAB>count`
- Sorted by word (key)
- Word counts are aggregated across all input files
- Example: `hello    5` (if "hello" appears 5 times total across all files)

## Notes
- Number of reducers: 2
- Words are converted to lowercase and non-alphanumeric characters are removed
- Empty tokens are filtered out
- Processes all text files in the input directory
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
