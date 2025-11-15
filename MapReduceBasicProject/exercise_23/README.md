# Exercise 23: Potential Friends of User

## Description
This exercise implements a MapReduce job to find potential friends (friends of friends) for a specific target user. It processes a friendship graph and identifies users who are connected through mutual friends.

## Code Explanation

### PotentialFriendsOfUserMapper
The mapper processes friendship pairs:
- **Input**: `<LongWritable, Text>` - line number and friendship pair
- **Output**: `<Text, Text>` - user and their friend
- **Logic**:
  1. Parses each line as a comma-separated pair of users (u1, u2)
  2. Emits two key-value pairs:
     - (u1, u2) - u2 is a friend of u1
     - (u2, u1) - u1 is a friend of u2 (bidirectional)
  3. This creates a graph of direct friendships
  4. Works identically to exercise 22's mapper

### PotentialFriendsOfUserReducer
The reducer finds potential friends (friends of friends):
- **Input**: `<Text, Text>` - user and list of their direct friends
- **Output**: `<Text, Text>` - null key and space-separated potential friends
- **Logic**:
  1. **setup() method**:
     - Reads target user from configuration ("target.user")
  2. **reduce() method**:
     - Only processes records for the target user
     - Collects all direct friends into a HashSet
     - For each direct friend, adds their friends as potential friends
     - Removes direct friends and self from potential friends list
     - Joins potential friends with spaces
     - Emits: (null, space-separated potential friends)
  3. Note: The current implementation has a simplified logic for finding 2-hop friends

### PotentialFriendsOfUserDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, target user, input path, output path)
- Sets the target user in configuration for reducer access
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 1
- Submits and waits for job completion

## Files
- `PotentialFriendsOfUserDriver.java` - MapReduce Driver class
- `PotentialFriendsOfUserMapper.java` - Map function implementation
- `PotentialFriendsOfUserReducer.java` - Reduce function implementation
- `potential_friends_input.txt` - Sample input data (friendship pairs)

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
hdfs dfs -rm -r /user/$USER/exercise_23/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_23/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_23/input

# Upload input file(s)
cd exercise_23
hdfs dfs -put potential_friends_input.txt /user/$USER/exercise_23/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job (target user is passed as argument)
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.social.potentialfriendsofuser.PotentialFriendsOfUserDriver \
  1 \
  User1 \
  /user/$USER/exercise_23/input \
  /user/$USER/exercise_23/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_23/output

# View results
hdfs dfs -cat /user/$USER/exercise_23/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_23/output/part-r-* | sort
```

## Expected Output
The output will contain potential friends of the target user:
- Format: `PotentialFriend1 PotentialFriend2 ...`
- Example: `User2 User3 User4`
- Only users who are friends of friends (not direct friends)
- Excludes the target user themselves

## Notes
- Number of reducers: 1
- Target user is specified as a command-line argument
- Potential friends are users connected through mutual friends (2-hop connections)
- Direct friends and self are excluded from potential friends
- The mapper creates bidirectional friendships
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
