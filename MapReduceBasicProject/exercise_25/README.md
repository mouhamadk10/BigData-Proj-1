# Exercise 25: Potential Friends List Per User

## Description
This exercise implements a MapReduce job to find potential friends for each user. It processes friendship data where each line contains a list of users, and finds all potential friends (friends of friends) for each user.

## Code Explanation

### PotentialFriendsListPerUserMapper
The mapper processes user pairs:
- **Input**: `<LongWritable, Text>` - line number and comma-separated list of users
- **Output**: `<Text, Text>` - user and their friend
- **Logic**:
  1. Parses each line as a comma-separated list of users
  2. For each pair of users in the line, emits bidirectional relationships:
     - (u1, u2) - u2 is connected to u1
     - (u2, u1) - u1 is connected to u2
  3. This creates a graph of connections where users can see their direct connections

### PotentialFriendsListPerUserReducer
The reducer finds potential friends (friends of friends):
- **Input**: `<Text, Text>` - user and list of their connections (which may contain space-separated friend lists)
- **Output**: `<Text, Text>` - user and space-separated list of potential friends
- **Logic**:
  1. Receives all connections for a given user
  2. For each connection value, splits by space to handle multiple friends
  3. Adds all friends to a HashSet (excluding the user themselves)
  4. This finds friends of friends - users who are connected through mutual connections
  5. Joins all unique potential friends with spaces
  6. Emits the user with their complete potential friends list

### PotentialFriendsListPerUserDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, input path, output path)
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 1
- Submits and waits for job completion

## Files
- `PotentialFriendsListPerUserDriver.java` - MapReduce Driver class
- `PotentialFriendsListPerUserMapper.java` - Map function implementation
- `PotentialFriendsListPerUserReducer.java` - Reduce function implementation
- `potential_friends_all_input.txt` - Sample input data (user connections)

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
hdfs dfs -rm -r /user/$USER/exercise_25/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_25/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_25/input

# Upload input file(s)
cd exercise_25
hdfs dfs -put potential_friends_all_input.txt /user/$USER/exercise_25/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.social.potentialfriendslistperuser.PotentialFriendsListPerUserDriver \
  1 \
  /user/$USER/exercise_25/input \
  /user/$USER/exercise_25/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_25/output

# View results
hdfs dfs -cat /user/$USER/exercise_25/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_25/output/part-r-* | sort
```

## Expected Output
The output will contain one line per user with their potential friends list:
- Format: `UserID<TAB>PotentialFriend1 PotentialFriend2 ...`
- Example: `User1	User2 User4 User3`
- Potential friends are users connected through mutual friends

## Notes
- Number of reducers: 1
- The mapper creates bidirectional connections
- The reducer finds friends of friends (potential friends)
- Users exclude themselves from their potential friends list
- Duplicate potential friends are automatically handled by the HashSet
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
