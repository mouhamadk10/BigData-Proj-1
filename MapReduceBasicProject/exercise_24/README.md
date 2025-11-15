# Exercise 24: Friends List Per User

## Description
This exercise implements a MapReduce job to generate a list of friends for each user from a social network friendship graph.

## Code Explanation

### FriendsListPerUserMapper
The mapper processes friendship pairs:
- **Input**: `<LongWritable, Text>` - line number and friendship pair (e.g., "User1,User2")
- **Output**: `<Text, Text>` - user and their friend
- **Logic**:
  1. Parses each line as a comma-separated pair of users (u1, u2)
  2. Emits two key-value pairs:
     - (u1, u2) - u2 is a friend of u1
     - (u2, u1) - u1 is a friend of u2 (bidirectional friendship)
  3. This ensures both users see each other in their friend lists

### FriendsListPerUserReducer
The reducer aggregates friends for each user:
- **Input**: `<Text, Text>` - user and list of their friends
- **Output**: `<Text, Text>` - user and space-separated list of friends
- **Logic**:
  1. Receives all friends for a given user (key)
  2. Uses a HashSet to automatically remove duplicates
  3. Joins all unique friends with spaces
  4. Emits the user with their complete friends list

### FriendsListPerUserDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 1
- Submits and waits for job completion

## Files
- `FriendsListPerUserDriver.java` - MapReduce Driver class
- `FriendsListPerUserMapper.java` - Map function implementation
- `FriendsListPerUserReducer.java` - Reduce function implementation
- `friends_list_input.txt` - Sample input data (friendship pairs)

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
hdfs dfs -rm -r /user/$USER/exercise_24/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_24/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_24/input

# Upload input file(s)
cd exercise_24
hdfs dfs -put friends_list_input.txt /user/$USER/exercise_24/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.social.friendslistperuser.FriendsListPerUserDriver \
  1 \
  /user/$USER/exercise_24/input \
  /user/$USER/exercise_24/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_24/output

# View results
hdfs dfs -cat /user/$USER/exercise_24/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_24/output/part-r-* | sort
```

## Expected Output
The output will contain one line per user with their friends list:
- Format: `UserID<TAB>Friend1 Friend2 Friend3 ...`
- Example: `User1	User2 User4 User3`

## Notes
- Number of reducers: 1
- The mapper creates bidirectional friendships (if A is friends with B, both see each other)
- Duplicate friendships are automatically handled by the HashSet in the reducer
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
