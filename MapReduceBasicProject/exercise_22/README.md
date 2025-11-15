# Exercise 22: Friends of User

## Description
This exercise implements a MapReduce job to find all friends of a specific target user. It processes a friendship graph and filters results for the specified user.

## Code Explanation

### FriendsOfUserMapper
The mapper processes friendship pairs:
- **Input**: `<LongWritable, Text>` - line number and friendship pair
- **Output**: `<Text, Text>` - user and their friend
- **Logic**:
  1. Parses each line as a comma-separated pair of users (u1, u2)
  2. Emits two key-value pairs:
     - (u1, u2) - u2 is a friend of u1
     - (u2, u1) - u1 is a friend of u2 (bidirectional)
  3. This ensures both users see each other in their friend lists
  4. Works identically to exercise 24's mapper

### FriendsOfUserReducer
The reducer filters friends for target user:
- **Input**: `<Text, Text>` - user and list of their friends
- **Output**: `<Text, Text>` - null key and space-separated friends list
- **Logic**:
  1. **setup() method**:
     - Reads target user from configuration ("target.user")
  2. **reduce() method**:
     - Checks if the key (user) matches the target user
     - If match, collects all friends into a list
     - Joins friends with spaces
     - Emits: (null, space-separated friends list)
     - Only processes the target user's record

### FriendsOfUserDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, target user, input path, output path)
- Sets the target user in configuration for reducer access
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 1
- Submits and waits for job completion

## Files
- `FriendsOfUserDriver.java` - MapReduce Driver class
- `FriendsOfUserMapper.java` - Map function implementation
- `FriendsOfUserReducer.java` - Reduce function implementation
- `friends_input.txt` - Sample input data (friendship pairs)

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
hdfs dfs -rm -r /user/$USER/exercise_22/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_22/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_22/input

# Upload input file(s)
cd exercise_22
hdfs dfs -put friends_input.txt /user/$USER/exercise_22/input/
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job (target user is passed as argument)
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.social.friendsofuser.FriendsOfUserDriver \
  1 \
  User1 \
  /user/$USER/exercise_22/input \
  /user/$USER/exercise_22/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_22/output

# View results
hdfs dfs -cat /user/$USER/exercise_22/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_22/output/part-r-* | sort
```

## Expected Output
The output will contain friends of the target user:
- Format: `Friend1 Friend2 Friend3 ...`
- Example: `User2 User3 User4`
- Only one line (friends of the specified user)

## Notes
- Number of reducers: 1
- Target user is specified as a command-line argument
- The mapper creates bidirectional friendships
- Only the target user's friends are included in output
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
