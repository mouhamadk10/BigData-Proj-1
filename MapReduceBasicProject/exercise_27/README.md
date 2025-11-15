# Exercise 27: User Categorization

## Description
This exercise implements a MapReduce job to categorize users based on business rules. It reads user data and applies categorization rules (based on gender and year of birth) to assign categories to each user.

## Code Explanation

### UserCategorizationMapper
The mapper processes user records:
- **Input**: `<LongWritable, Text>` - line number and user record
- **Output**: `<Text, Text>` - user ID and user record with prefix
- **Logic**:
  1. Parses user record format: `UserId,Name,Surname,Gender,YearOfBirth,City,Education`
  2. Extracts user ID from the record
  3. Emits: (userId, "U:" + fullUserRecord)
  4. Uses "U:" prefix to identify user records in the reducer
  5. This ensures one record per user ID

### UserCategorizationReducer
The reducer applies categorization rules:
- **Input**: `<Text, Text>` - user ID and user record
- **Output**: `<Text, Text>` - null key and user record with category appended
- **Logic**:
  1. **setup() method**:
     - Reads the business rules file path from configuration
     - Parses rules in format: `condition -> category`
     - Condition format: `gender=X and yearofbirth=Y` (case-insensitive)
     - Stores rules in a list for matching
  2. **reduce() method**:
     - Extracts user record (removes "U:" prefix)
     - Parses gender and year of birth from the user record
     - Matches user against rules:
       - Rules can have null values (wildcards) for gender or yearOfBirth
       - First matching rule determines the category
     - If no rule matches, assigns "Unknown" category
     - Emits: (null, userRecord + "," + category)

### UserCategorizationDriver
The driver configures and runs the MapReduce job:
- Parses command-line arguments (number of reducers, rules file path, input path, output path)
- Sets the rules file path in the configuration for the reducer to access
- Configures the job with mapper, reducer, and input/output formats
- Sets the number of reduce tasks to 2
- Note: The rules file must be accessible as a local file path

## Files
- `UserCategorizationDriver.java` - MapReduce Driver class
- `UserCategorizationMapper.java` - Map function implementation
- `UserCategorizationReducer.java` - Reduce function implementation
- `users.txt` - User data (format: UserId,Name,Surname,Gender,YearOfBirth,City,Education)
- `business_rules.txt` - Business rules file (format: condition -> category)

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
hdfs dfs -rm -r /user/$USER/exercise_27/input 2>/dev/null
hdfs dfs -rm -r /user/$USER/exercise_27/output 2>/dev/null

# Create input directory
hdfs dfs -mkdir -p /user/$USER/exercise_27/input

# Upload input file(s)
cd exercise_27
hdfs dfs -put users.txt /user/$USER/exercise_27/input/

# Copy rules file to local filesystem for reducer access
cp business_rules.txt /tmp/business_rules.txt
```

### 2. Run MapReduce Job
```bash
# Navigate to project root
cd /home/mohamad/Desktop/bigdata-proj./bigdata-proj/MapReduceBasicProject

# Run the job (rules file must be a local path)
hadoop jar target/MapReduceProject-1.0.0.jar \
  it.polito.bigdata.hadoop.categorization.UserCategorizationDriver \
  2 \
  /tmp/business_rules.txt \
  /user/$USER/exercise_27/input \
  /user/$USER/exercise_27/output
```

### 3. View Results
```bash
# List output files
hdfs dfs -ls /user/$USER/exercise_27/output

# View results
hdfs dfs -cat /user/$USER/exercise_27/output/part-r-00000

# View all output files (if multiple reducers)
hdfs dfs -cat /user/$USER/exercise_27/output/part-r-* | sort
```

## Expected Output
The output will contain user records with their assigned categories:
- Format: `UserId,Name,Surname,Gender,YearOfBirth,City,Education,Category`
- Example: `User#1,John,Smith,M,1934,NewYork,Bachelor,Category#1`
- Users without matching rules get "Unknown" category

## Notes
- Number of reducers: 2
- Rules file must be accessible as a local file path (not HDFS path)
- Rule matching is case-insensitive for field names
- Rules can have null values (wildcards) for gender or yearOfBirth
- First matching rule determines the category
- The reducer loads rules once in setup() for efficiency
- Make sure HDFS has enough space before running the job.
- Check the NameNode and ResourceManager web UIs for job progress.
