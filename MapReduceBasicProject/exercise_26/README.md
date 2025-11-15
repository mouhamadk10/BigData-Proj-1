# Exercise 26: Word to Integer Mapping

## Description
This exercise implements a MapReduce job to convert words in input text to their corresponding integer IDs based on a dictionary file. It uses a distributed cache pattern to load the dictionary in the mapper's setup phase.

## Code Explanation

### WordToIntMapper
The mapper converts words to integers using a dictionary:
- **Input**: `<LongWritable, Text>` - line number and line content
- **Output**: `<LongWritable, Text>` - line number and space-separated integer IDs
- **Logic**:
  1. **setup() method**: 
     - Reads the dictionary file path from configuration
     - Loads the dictionary into a HashMap (word -> integer ID)
     - Dictionary format: `word<TAB>integerId`
     - Words are stored in uppercase for case-insensitive matching
  2. **map() method**:
     - Splits each line into words
     - For each word, looks up its integer ID in the dictionary
     - If found, appends the integer ID to the output
     - Joins all found integer IDs with spaces
     - Emits: (lineNumber, space-separated integer IDs)
     - Words not in the dictionary are skipped

### WordToIntReducer
The reducer passes through the data:
- **Input**: `<LongWritable, Text>` - line number and integer IDs
- **Output**: `<LongWritable, Text>` - line number and integer IDs
- **Logic**:
  1. Simply passes through the data without modification
  2. Emits each line number with its corresponding integer IDs
  3. This is an identity reducer pattern

### WordToIntDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, dictionary file path, input path, output path)
- Sets the dictionary file path in the configuration for the mapper to access
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 2
- Note: The dictionary file must be accessible as a local file path

## Files
- `WordToIntDriver.java` - MapReduce Driver class
- `WordToIntMapper.java` - Map function implementation
- `WordToIntReducer.java` - Reduce function implementation
- `dictionary.txt` - Dictionary file mapping words to integers (format: word<TAB>integerId)
- `words_input.txt` - Sample input text data

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
hdfs dfs -rm -r /user/$USER/exercise_26/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_26/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_26/input

# Upload input file(s)
cd exercise_26
hdfs dfs -put words_input.txt /user/$USER/exercise_26/input/
hdfs dfs -put dictionary.txt /user/$USER/exercise_26/input/

# Copy dictionary to local filesystem for mapper access
cp dictionary.txt /tmp/dictionary.txt
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job (dictionary file must be a local path)
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.wordtoint.WordToIntDriver \
  2 \
  /tmp/dictionary.txt \
  /user/$USER/exercise_26/input \
  /user/$USER/exercise_26/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_26/output

# View results
hdfs dfs -cat /user/$USER/exercise_26/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_26/output/part-r-* | sort -n
```

## Expected Output
The output will contain line numbers with their corresponding integer IDs:
- Format: `lineNumber<TAB>id1 id2 id3 ...`
- Example: `0    1 5 7 6 2`
- Words not in the dictionary are omitted
- Output is sorted by line number

## Notes
- Number of reducers: 2
- Dictionary file must be accessible as a local file path (not HDFS path)
- Word matching is case-insensitive (words are converted to uppercase)
- Words not found in the dictionary are skipped
- The mapper loads the dictionary once in setup() for efficiency
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
