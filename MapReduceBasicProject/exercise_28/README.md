# Exercise 28: Question-Answer Join

## Description
This exercise implements a MapReduce job to join questions with their corresponding answers. It reads from two input files (questions.txt and answers.txt) and produces output showing each question paired with all its answers.

## Code Explanation

### QAJoinMapper
The mapper processes both question and answer files:
- **Input**: `<LongWritable, Text>` - line number and line content
- **Output**: `<Text, Text>` - question ID and question/answer data with prefix
- **Logic**:
  1. Uses `setup()` method to detect which file is being processed by checking the filename
  2. For questions file:
     - Parses format: `questionId,date,questionText`
     - Emits: (questionId, "Q:questionText")
  3. For answers file:
     - Parses format: `answerId,questionId,date,answerText`
     - Emits: (questionId, "A:answerId,answerText")
  4. Uses prefixes "Q:" and "A:" to distinguish question and answer data in the reducer

### QAJoinReducer
The reducer performs the join operation:
- **Input**: `<Text, Text>` - question ID and list of question/answer data
- **Output**: `<Text, Text>` - question text and answer
- **Logic**:
  1. Receives all data for a question ID (one question, multiple answers)
  2. Separates question text (prefixed with "Q:") from answers (prefixed with "A:")
  3. For each answer, emits a pair: (questionText, answerData)
  4. Only emits if a question exists (filters orphaned answers)

### QAJoinDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 2
- Submits and waits for job completion

## Files
- `QAJoinDriver.java` - MapReduce Driver class
- `QAJoinMapper.java` - Map function implementation
- `QAJoinReducer.java` - Reduce function implementation
- `questions.txt` - Questions data (format: questionId,date,questionText)
- `answers.txt` - Answers data (format: answerId,questionId,date,answerText)

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
hdfs dfs -rm -r /user/$USER/exercise_28/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_28/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_28/input

# Upload input file(s)
cd exercise_28
hdfs dfs -put questions.txt /user/$USER/exercise_28/input/
hdfs dfs -put answers.txt /user/$USER/exercise_28/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.qa.QAJoinDriver \
  2 \
  /user/$USER/exercise_28/input \
  /user/$USER/exercise_28/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_28/output

# View results
hdfs dfs -cat /user/$USER/exercise_28/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_28/output/part-r-* | sort
```

## Expected Output
The output will contain question-answer pairs:
- Format: `questionText<TAB>answerId,answerText`
- Example: `What is ..?	A1,It is ..`

## Notes
- Number of reducers: 2
- The mapper uses file name detection to distinguish between questions and answers
- The reducer performs a join operation grouping answers by their question ID
- Only questions with at least one answer are included in the output
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
