# Exercise 29: Users by Gender and Year of Birth

## Description
This exercise implements a MapReduce job to group users by their gender and year of birth combination. It processes user data and outputs users grouped by gender,yearOfBirth pairs.

## Code Explanation

### UsersByGenresMapper
The mapper processes user data:
- **Input**: `<LongWritable, Text>` - line number and user record
- **Output**: `<Text, Text>` - gender,yearOfBirth and user ID
- **Logic**:
  1. Uses `setup()` method to detect which file is being processed (only processes users.txt)
  2. Parses user record format: `UserId,Name,Surname,Gender,YearOfBirth,City,Education`
  3. Extracts gender (index 3) and year of birth (index 4)
  4. Creates a composite key: "Gender,YearOfBirth" (e.g., "M,1934")
  5. Emits: (gender,yearOfBirth, userId)
  6. Ignores the likes.txt file

### UsersByGenresReducer
The reducer groups users by gender and year of birth:
- **Input**: `<Text, Text>` - gender,yearOfBirth and list of user IDs
- **Output**: `<Text, Text>` - gender,yearOfBirth and comma-separated list of users
- **Logic**:
  1. Receives all user IDs for a given gender,yearOfBirth combination
  2. Filters out "M,1956" (specific requirement)
  3. Uses a HashSet to automatically remove duplicate user IDs
  4. Joins all unique user IDs with commas
  5. Emits: (gender,yearOfBirth, userList)

### UsersByGenresDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 2
- Submits and waits for job completion

## Files
- `UsersByGenresDriver.java` - MapReduce Driver class
- `UsersByGenresMapper.java` - Map function implementation
- `UsersByGenresReducer.java` - Reduce function implementation
- `users.txt` - User data (format: UserId,Name,Surname,Gender,YearOfBirth,City,Education)
- `likes.txt` - User likes data (not used in this exercise)

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
hdfs dfs -rm -r /user/$USER/exercise_29/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_29/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_29/input

# Upload input file(s)
cd exercise_29
hdfs dfs -put users.txt /user/$USER/exercise_29/input/
hdfs dfs -put likes.txt /user/$USER/exercise_29/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.selection.UsersByGenresDriver \
  2 \
  /user/$USER/exercise_29/input \
  /user/$USER/exercise_29/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_29/output

# View results
hdfs dfs -cat /user/$USER/exercise_29/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_29/output/part-r-* | sort
```

## Expected Output
The output will contain users grouped by gender and year of birth:
- Format: `Gender,YearOfBirth<TAB>User1,User2,...`
- Example: `F,1934	User#3`
- Example: `M,1934	User#1`
- Note: M,1956 is excluded from the output

## Notes
- Number of reducers: 2
- Only processes users.txt file (likes.txt is ignored)
- The reducer filters out "M,1956" entries
- Duplicate user IDs are automatically handled by the HashSet
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
