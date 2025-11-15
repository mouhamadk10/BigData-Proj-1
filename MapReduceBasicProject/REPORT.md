# Hadoop MapReduce Project Report

## Table of Contents
1. [Hadoop Installation](#hadoop-installation)
2. [Hadoop Local Setup and Configuration](#hadoop-local-setup-and-configuration)
3. [Running Hadoop Locally](#running-hadoop-locally)
4. [Code Explanations](#code-explanations)
   - [Exercise 1: Word Count (Standard) - Single File](#exercise-1-word-count-standard---single-file)
   - [Exercise 2: Word Count (Standard) - Multiple Files](#exercise-2-word-count-standard---multiple-files)
   - [Exercise 3: PM10 Above Threshold](#exercise-3-pm10-above-threshold)
   - [Exercise 4: PM10 Zone Dates Above Threshold](#exercise-4-pm10-zone-dates-above-threshold)
   - [Exercise 5: PM10 Average](#exercise-5-pm10-average)
   - [Exercise 6: PM10 Min/Max](#exercise-6-pm10-minmax)
   - [Exercise 7: Inverted Index](#exercise-7-inverted-index)
   - [Exercise 8: Income Monthly Totals](#exercise-8-income-monthly-totals)
   - [Exercise 9: Word Count (In-Mapper Combiner)](#exercise-9-word-count-in-mapper-combiner)
   - [Exercise 10: PM10 Total Count](#exercise-10-pm10-total-count)
   - [Exercise 11: PM10 Average per Sensor](#exercise-11-pm10-average-per-sensor)
   - [Exercise 12: PM10 Above Threshold Count](#exercise-12-pm10-above-threshold-count)
5. [Output Screenshots](#output-screenshots)

---

## Hadoop Installation

### Prerequisites
Before installing Hadoop, ensure you have:
- **Java JDK 8 or higher** installed
- **SSH** installed and configured
- **Linux/Unix environment** (Ubuntu recommended)

### Step 1: Download Hadoop
```bash
# Download Hadoop 3.3.6 (or latest stable version)
cd ~/Downloads
wget https://archive.apache.org/dist/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
```

### Step 2: Extract Hadoop
```bash
# Extract to /usr/local or ~/hadoop
sudo tar -xzf hadoop-3.3.6.tar.gz -C /usr/local
sudo mv /usr/local/hadoop-3.3.6 /usr/local/hadoop
sudo chown -R $USER:$USER /usr/local/hadoop
```

### Step 3: Configure Environment Variables
Add the following to `~/.bashrc`:
```bash
# Hadoop Environment Variables
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native"
```

Reload the configuration:
```bash
source ~/.bashrc
```

### Step 4: Configure SSH for Passwordless Login
```bash
# Generate SSH key if not exists
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys

# Test SSH
ssh localhost
```

### Step 5: Configure Hadoop Files

#### Edit `$HADOOP_HOME/etc/hadoop/hadoop-env.sh`:
```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
# (Adjust path based on your Java installation)
```

#### Edit `$HADOOP_HOME/etc/hadoop/core-site.xml`:
```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

#### Edit `$HADOOP_HOME/etc/hadoop/hdfs-site.xml`:
```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:///home/$USER/hadoopdata/hdfs/namenode</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:///home/$USER/hadoopdata/hdfs/datanode</value>
    </property>
</configuration>
```

#### Edit `$HADOOP_HOME/etc/hadoop/mapred-site.xml`:
```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```

#### Edit `$HADOOP_HOME/etc/hadoop/yarn-site.xml`:
```xml
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value>
    </property>
</configuration>
```

### Step 6: Format HDFS
```bash
hdfs namenode -format
```

---

## Hadoop Local Setup and Configuration

### Directory Structure
After installation, create necessary directories:
```bash
mkdir -p ~/hadoopdata/hdfs/namenode
mkdir -p ~/hadoopdata/hdfs/datanode
```

### Verify Installation
```bash
# Check Hadoop version
hadoop version

# Check Java version
java -version
```

### Configuration Summary
- **HDFS**: Configured for single-node cluster
- **YARN**: Configured for resource management
- **MapReduce**: Configured to run on YARN
- **Replication Factor**: Set to 1 (for local setup)

---

## Running Hadoop Locally

### Step 1: Start Hadoop Services

#### Start HDFS (NameNode and DataNode):
```bash
start-dfs.sh
```

#### Start YARN (ResourceManager and NodeManager):
```bash
start-yarn.sh
```

### Step 2: Verify Services are Running
```bash
# Check running Java processes
jps

# Expected output:
# - NameNode
# - DataNode
# - SecondaryNameNode
# - ResourceManager
# - NodeManager
```

### Step 3: Check HDFS Status
```bash
# Check HDFS status
hdfs dfsadmin -report

# List HDFS root directory
hdfs dfs -ls /
```

### Step 4: Access Web UIs
- **NameNode Web UI**: http://localhost:9870 (Hadoop 3.x) or http://localhost:50070 (Hadoop 2.x)
- **ResourceManager Web UI**: http://localhost:8088

### Step 5: Stop Hadoop Services (when done)
```bash
stop-yarn.sh
stop-dfs.sh
```

### Running MapReduce Jobs Locally

#### General Steps:
1. **Upload input data to HDFS:**
   ```bash
   hdfs dfs -mkdir -p /user/$USER/exercise_N/input
   hdfs dfs -put input_file.txt /user/$USER/exercise_N/input/
   ```

2. **Run the MapReduce job:**
   ```bash
   hadoop jar target/MapReduceProject-1.0.0.jar \
     package.ClassName \
     numReducers \
     /user/$USER/exercise_N/input \
     /user/$USER/exercise_N/output
   ```

3. **View results:**
   ```bash
   hdfs dfs -cat /user/$USER/exercise_N/output/part-r-*
   ```

---

## Code Explanations

### Exercise 1: Word Count (Standard) - Single File

#### Purpose
Counts the frequency of each word in a text file using the standard MapReduce word count pattern.

#### Mapper (`WordCountStandardMapper`)
```java
protected void map(LongWritable key, Text value, Context context) {
    // Split line into words
    String[] tokens = value.toString().split("\\s+");
    
    for (String token : tokens) {
        // Clean word: lowercase and remove non-alphanumeric
        String cleaned = token.toLowerCase().replaceAll("[^a-z0-9]", "");
        
        if (cleaned.length() > 0) {
            // Emit (word, 1) for each word
            context.write(new Text(cleaned), new IntWritable(1));
        }
    }
}
```

**Logic:**
- Reads each line from input
- Splits line into words using whitespace
- Cleans each word (lowercase, remove special characters)
- Emits (word, 1) for each valid word

#### Reducer (`WordCountStandardReducer`)
```java
protected void reduce(Text key, Iterable<IntWritable> values, Context context) {
    int sum = 0;
    for (IntWritable v : values) {
        sum += v.get();  // Sum all counts for this word
    }
    context.write(key, new IntWritable(sum));  // Emit (word, totalCount)
}
```

**Logic:**
- Receives all counts (all 1s) for each word
- Sums the counts
- Emits (word, totalCount)

#### Driver (`WordCountStandardDriver`)
```java
public int run(String[] args) {
    // 1. Parse arguments: numReducers, inputPath, outputPath
    int numberOfReducers = Integer.parseInt(args[0]);
    Path inputPath = new Path(args[1]);
    Path outputDir = new Path(args[2]);
    
    // 2. Create and configure the job
    Job job = Job.getInstance(conf);
    job.setJobName("WordCount - Standard");
    
    // 3. Set input/output paths
    FileInputFormat.addInputPath(job, inputPath);
    FileOutputFormat.setOutputPath(job, outputDir);
    
    // 4. Connect Mapper and Reducer to the job
    job.setMapperClass(WordCountStandardMapper.class);
    job.setReducerClass(WordCountStandardReducer.class);
    
    // 5. Set data types for Map output
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(IntWritable.class);
    
    // 6. Set data types for final output
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    
    // 7. Set number of reducers
    job.setNumReduceTasks(numberOfReducers);
    
    // 8. Submit and wait for completion
    return job.waitForCompletion(true) ? 0 : 1;
}
```

**What the Driver Does:**
- **Configures the entire MapReduce job**: Sets up input/output paths, formats, and job parameters
- **Connects Mapper and Reducer**: Tells Hadoop which classes to use for map and reduce phases
- **Defines data flow**: Specifies input/output data types for each phase
- **Manages execution**: Submits the job and waits for completion

#### How Components Work Together

**1. Job Initialization (Driver):**
- Driver reads command-line arguments and creates a Job object
- Configures input/output paths and formats
- Registers Mapper and Reducer classes with the job

**2. Map Phase:**
- Hadoop reads input file from HDFS line by line
- For each line, calls `Mapper.map()` method
- Mapper processes the line: splits into words, cleans them
- Mapper emits intermediate key-value pairs: `(word, 1)`
- Hadoop automatically groups pairs by key and sorts them

**3. Shuffle and Sort Phase (Automatic):**
- Hadoop collects all `(word, 1)` pairs from all mappers
- Groups pairs by key (word): all pairs with same word go together
- Sorts keys: `apple`, `banana`, `cherry`, etc.
- Distributes grouped data to reducers based on key hash

**4. Reduce Phase:**
- Hadoop calls `Reducer.reduce()` for each unique key (word)
- Reducer receives: key = "hello", values = [1, 1, 1, 1] (all counts for "hello")
- Reducer sums the values: 1 + 1 + 1 + 1 = 4
- Reducer emits final result: `("hello", 4)`

**5. Output Writing:**
- Hadoop writes reducer output to HDFS
- Creates output files: `part-r-00000`, `part-r-00001`, etc.
- Each reducer writes to its own output file

**Data Flow:**
```
Input File (HDFS)
    ↓
[Driver] → Configures Job
    ↓
Mapper → (word, 1) → (word, 1) → (word, 1)
    ↓
Shuffle & Sort (Automatic)
    ↓
Reducer → (word, totalCount)
    ↓
Output File (HDFS)
```

#### Output Format
```
word1    count1
word2    count2
...
```

**Output Screenshot Placeholder:**
![Exercise 1 Output](images/exercise_1_output.png)

---

### Exercise 2: Word Count (Standard) - Multiple Files

#### Purpose
Same as Exercise 1, but processes multiple input files in a single job.

#### Code Differences
- **Mapper**: Identical to Exercise 1
- **Reducer**: Identical to Exercise 1
- **Driver**: Same configuration, but input path contains multiple files

#### How Components Work Together

**Driver:**
- Same as Exercise 1, but input path contains multiple files
- Hadoop automatically processes all files in the directory

**Mapper:**
- Processes each file independently
- Multiple mappers run in parallel (one per file split)
- Each mapper emits `(word, 1)` pairs

**Reducer:**
- Receives aggregated data from all mappers across all files
- Sums counts for each word across all files
- Final output contains word counts from all input files

**Key Difference from Exercise 1:**
- Multiple input files are processed in parallel
- Results are automatically aggregated across files

**Output Screenshot Placeholder:**
![Exercise 2 Output](images/exercise_2_output.png)

---

### Exercise 3: PM10 Above Threshold

#### Purpose
Filters PM10 air quality records that exceed a specified threshold value.

#### Mapper (`PM10AboveThresholdMapper`)
```java
protected void setup(Context context) {
    // Read threshold from configuration
    String thresholdStr = context.getConfiguration().get("pm10.threshold");
    threshold = Float.parseFloat(thresholdStr);
}

protected void map(LongWritable key, Text value, Context context) {
    // Parse CSV: sensorId,date,pm10
    String[] parts = line.split(",");
    String sensorId = parts[0];
    float pm10 = Float.parseFloat(parts[2]);
    
    if (pm10 > threshold) {
        // Only emit if above threshold
        context.write(new Text(sensorId), new IntWritable(1));
    }
}
```

**Logic:**
- Reads threshold from configuration in `setup()`
- Parses PM10 value from CSV
- Only emits records where PM10 > threshold
- Emits (sensorId, 1) for each record above threshold

#### Reducer (`PM10AboveThresholdReducer`)
```java
protected void reduce(Text key, Iterable<IntWritable> values, Context context) {
    int count = 0;
    for (IntWritable v : values) {
        count += v.get();
    }
    context.write(key, new IntWritable(count));
}
```

**Logic:**
- Counts how many times each sensor exceeded threshold
- Emits (sensorId, count)

#### Driver (`PM10AboveThresholdDriver`)
```java
public int run(String[] args) {
    int reducers = Integer.parseInt(args[0]);
    String threshold = args[1];  // Threshold value
    Path input = new Path(args[2]);
    Path output = new Path(args[3]);
    
    Configuration conf = this.getConf();
    // Store threshold in configuration for mapper to access
    conf.set("pm10.threshold", threshold);
    
    Job job = Job.getInstance(conf);
    job.setMapperClass(PM10AboveThresholdMapper.class);
    job.setReducerClass(PM10AboveThresholdReducer.class);
    // ... rest of configuration
}
```

**What the Driver Does:**
- Reads threshold from command-line arguments
- Stores threshold in Configuration object
- Makes threshold accessible to all mappers via `context.getConfiguration()`

#### How Components Work Together

**1. Driver Configuration:**
- Driver receives threshold as command-line argument
- Stores it in Configuration: `conf.set("pm10.threshold", "50")`
- Configuration is passed to all mappers and reducers

**2. Mapper Setup:**
- Mapper's `setup()` method runs once before processing
- Reads threshold from configuration: `context.getConfiguration().get("pm10.threshold")`
- Stores threshold in instance variable for use in `map()` method

**3. Map Phase:**
- Mapper reads each line: `sensorId,date,pm10`
- Parses PM10 value and compares with threshold
- **Only emits if PM10 > threshold**: `(sensorId, 1)`
- Filters out records below threshold

**4. Shuffle and Sort:**
- Groups pairs by sensorId
- All `(sensorId, 1)` pairs for same sensor go to same reducer

**5. Reduce Phase:**
- Reducer receives: `sensorId` and list of counts `[1, 1, 1, ...]`
- Counts how many times this sensor exceeded threshold
- Emits: `(sensorId, count)`

**Data Flow:**
```
Input: sensorId,date,pm10
    ↓
[Driver] → Sets threshold in config
    ↓
Mapper.setup() → Reads threshold from config
    ↓
Mapper.map() → Filters: if pm10 > threshold → (sensorId, 1)
    ↓
Shuffle & Sort
    ↓
Reducer → Counts: (sensorId, count)
    ↓
Output: sensorId    count
```

**Output Screenshot Placeholder:**
![Exercise 3 Output](images/exercise_3_output.png)

---

### Exercise 4: PM10 Zone Dates Above Threshold

#### Purpose
Groups PM10 data by zone and date, then filters records above threshold.

#### Mapper Logic
- Parses: `zoneId,date,pm10`
- Creates composite key: `(zoneId, date)`
- Only emits if PM10 > threshold
- Emits: `((zoneId, date), 1)`

#### Reducer Logic
- Receives all records for each (zoneId, date) pair
- Counts records above threshold
- Emits: `(zoneId, date, count)`

#### How Components Work Together

**1. Driver:**
- Configures job similar to Exercise 3
- Sets threshold in configuration

**2. Mapper:**
- Reads: `zoneId,date,pm10`
- Creates composite key: `"zoneId,date"` (combines two fields)
- Filters if PM10 > threshold
- Emits: `("zoneId,date", 1)`

**3. Shuffle and Sort:**
- Groups by composite key (zoneId,date combination)
- All records for same zone and date go together

**4. Reducer:**
- Receives: `("zoneId,date", [1, 1, 1, ...])`
- Counts records for each zone-date pair
- Emits: `("zoneId,date", count)`

**Key Pattern:**
- **Composite Key**: Combines multiple fields into single key for grouping
- Enables grouping by multiple dimensions simultaneously

**Data Flow:**
```
Input: zone1,2023-01-01,55.0
       zone1,2023-01-01,60.0
       zone1,2023-01-02,45.0
    ↓
Mapper → ("zone1,2023-01-01", 1)
         ("zone1,2023-01-01", 1)
         ("zone1,2023-01-02", 1)  [if above threshold]
    ↓
Shuffle & Sort → Groups by composite key
    ↓
Reducer → ("zone1,2023-01-01", [1, 1]) → count = 2
          ("zone1,2023-01-02", [1]) → count = 1
```

**Output Screenshot Placeholder:**
![Exercise 4 Output](images/exercise_4_output.png)

---

### Exercise 5: PM10 Average

#### Purpose
Calculates the average PM10 value for each sensor.

#### Mapper (`PM10AverageMapper`)
```java
protected void map(LongWritable key, Text value, Context context) {
    // Parse: sensorId,date,pm10
    String[] parts = line.split(",");
    String sensorId = parts[0];
    double pm10 = Double.parseDouble(parts[2]);
    
    // Emit (sensorId, pm10Value)
    context.write(new Text(sensorId), new DoubleWritable(pm10));
}
```

**Logic:**
- Extracts sensor ID and PM10 value
- Emits (sensorId, pm10Value) for aggregation

#### Reducer (`PM10AverageReducer`)
```java
protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) {
    double sum = 0.0;
    long count = 0;
    
    for (DoubleWritable v : values) {
        sum += v.get();
        count++;
    }
    
    if (count > 0) {
        double average = sum / count;
        context.write(key, new DoubleWritable(average));
    }
}
```

**Logic:**
- Sums all PM10 values for each sensor
- Counts number of records
- Calculates average: sum / count
- Emits (sensorId, average)

#### Driver (`PM10AverageDriver`)
```java
public int run(String[] args) {
    Job job = Job.getInstance(conf);
    job.setMapperClass(PM10AverageMapper.class);
    job.setReducerClass(PM10AverageReducer.class);
    
    // Important: Map output types differ from final output
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(DoubleWritable.class);
    
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(DoubleWritable.class);
}
```

#### How Components Work Together

**1. Driver:**
- Configures job with Mapper and Reducer
- Sets data types: Text (sensorId) and DoubleWritable (PM10 value)

**2. Map Phase:**
- Mapper reads: `sensorId,date,pm10`
- Extracts sensorId and PM10 value
- Emits: `(sensorId, pm10Value)` - passes actual PM10 value, not count

**3. Shuffle and Sort:**
- Groups all PM10 values by sensorId
- All PM10 values for same sensor go to same reducer

**4. Reduce Phase:**
- Reducer receives: `sensorId` and list of PM10 values `[45.2, 50.1, 48.5, ...]`
- **Sums all values**: 45.2 + 50.1 + 48.5 + ... = total
- **Counts records**: number of values
- **Calculates average**: total / count
- Emits: `(sensorId, average)`

**Key Pattern:**
- **Sum-Count Pattern**: Sum values, count records, divide for average
- Uses `DoubleWritable` for floating-point precision

**Data Flow:**
```
Input: sensor1,2023-01-01,45.2
       sensor1,2023-01-02,50.1
       sensor1,2023-01-03,48.5
    ↓
Mapper → (sensor1, 45.2)
         (sensor1, 50.1)
         (sensor1, 48.5)
    ↓
Shuffle & Sort → Groups by sensor1
    ↓
Reducer → Receives: sensor1, [45.2, 50.1, 48.5]
          Sum = 143.8, Count = 3
          Average = 143.8 / 3 = 47.93
    ↓
Output: sensor1    47.93
```

**Output Screenshot Placeholder:**
![Exercise 5 Output](images/exercise_5_output.png)

---

### Exercise 6: PM10 Min/Max

#### Purpose
Finds the minimum and maximum PM10 values for each sensor.

#### Mapper Logic
- Parses sensor ID and PM10 value
- Emits: `(sensorId, pm10Value)`

#### Reducer Logic
```java
protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) {
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    
    for (DoubleWritable v : values) {
        double value = v.get();
        if (value < min) min = value;
        if (value > max) max = value;
    }
    
    context.write(key, new Text(min + "," + max));
}
```

**Logic:**
- Iterates through all values
- Tracks minimum and maximum
- Emits (sensorId, "min,max")

#### How Components Work Together

**1. Driver:**
- Configures job with Mapper and Reducer
- Sets DoubleWritable for PM10 values

**2. Mapper:**
- Reads: `sensorId,date,pm10`
- Emits: `(sensorId, pm10Value)` - passes all values to reducer

**3. Shuffle and Sort:**
- Groups all PM10 values by sensorId

**4. Reducer:**
- Receives: `sensorId` and list of values `[45.2, 50.1, 48.5, 55.0, ...]`
- **Iterates through values**:
  - Tracks minimum: if value < current min, update min
  - Tracks maximum: if value > current max, update max
- Emits: `(sensorId, "min,max")`

**Key Pattern:**
- **Min/Max Finding**: Iterative comparison pattern
- Reducer processes all values to find extremes

**Data Flow:**
```
Input: sensor1,2023-01-01,45.2
       sensor1,2023-01-02,50.1
       sensor1,2023-01-03,48.5
       sensor1,2023-01-04,55.0
    ↓
Mapper → (sensor1, 45.2)
         (sensor1, 50.1)
         (sensor1, 48.5)
         (sensor1, 55.0)
    ↓
Shuffle & Sort
    ↓
Reducer → Receives: sensor1, [45.2, 50.1, 48.5, 55.0]
          Min = 45.2, Max = 55.0
    ↓
Output: sensor1    45.2,55.0
```

**Output Screenshot Placeholder:**
![Exercise 6 Output](images/exercise_6_output.png)

---

### Exercise 7: Inverted Index

#### Purpose
Creates an inverted index mapping words to the list of documents containing them.

#### Mapper Logic
```java
protected void map(LongWritable key, Text value, Context context) {
    // Get filename from input split
    String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
    
    // Split line into words
    String[] words = value.toString().split("\\s+");
    
    for (String word : words) {
        // Emit (word, fileName)
        context.write(new Text(word), new Text(fileName));
    }
}
```

**Logic:**
- Extracts filename from input split
- For each word, emits (word, fileName)

#### Reducer Logic
```java
protected void reduce(Text key, Iterable<Text> values, Context context) {
    Set<String> documents = new HashSet<>();
    
    for (Text fileName : values) {
        documents.add(fileName.toString());
    }
    
    String docList = String.join(",", documents);
    context.write(key, new Text(docList));
}
```

**Logic:**
- Collects all documents for each word
- Uses HashSet to remove duplicates
- Emits (word, "doc1,doc2,doc3")

#### How Components Work Together

**1. Driver:**
- Configures job with MultipleInputs or standard input
- Sets Text types for keys and values

**2. Mapper:**
- **Gets filename from input split**: Uses `FileSplit` to identify source file
- Reads each line and splits into words
- For each word, emits: `(word, fileName)`
- Same word from different files emits different pairs

**3. Shuffle and Sort:**
- Groups pairs by word
- All `(word, fileName)` pairs for same word grouped together
- Multiple files may contribute to same word

**4. Reducer:**
- Receives: `word` and list of filenames `["doc1.txt", "doc1.txt", "doc2.txt", ...]`
- Uses HashSet to remove duplicate filenames
- Joins unique filenames with commas
- Emits: `(word, "doc1.txt,doc2.txt")`

**Key Pattern:**
- **Inverted Index**: Creates word-to-document mapping
- **FileSplit Pattern**: Accessing input file metadata in mapper

**Data Flow:**
```
Input Files:
doc1.txt: "hello world"
doc2.txt: "hello hadoop"
    ↓
Mapper (doc1.txt) → ("hello", "doc1.txt")
                    ("world", "doc1.txt")
Mapper (doc2.txt) → ("hello", "doc2.txt")
                    ("hadoop", "doc2.txt")
    ↓
Shuffle & Sort → Groups by word
    ↓
Reducer → "hello": ["doc1.txt", "doc2.txt"] → "doc1.txt,doc2.txt"
          "world": ["doc1.txt"] → "doc1.txt"
          "hadoop": ["doc2.txt"] → "doc2.txt"
    ↓
Output: hello    doc1.txt,doc2.txt
        world    doc1.txt
        hadoop   doc2.txt
```

**Output Screenshot Placeholder:**
![Exercise 7 Output](images/exercise_7_output.png)

---

### Exercise 8: Income Monthly Totals

#### Purpose
Aggregates income by month from date-income records.

#### Mapper Logic
```java
protected void map(LongWritable key, Text value, Context context) {
    // Parse: date,income
    String[] parts = line.split(",");
    String date = parts[0];  // Format: YYYY-MM-DD
    int income = Integer.parseInt(parts[1]);
    
    // Extract month: YYYY-MM
    String month = date.substring(0, 7);
    
    context.write(new Text(month), new IntWritable(income));
}
```

**Logic:**
- Parses date and income
- Extracts month (YYYY-MM) from date
- Emits (month, income)

#### Reducer Logic
```java
protected void reduce(Text key, Iterable<IntWritable> values, Context context) {
    int total = 0;
    for (IntWritable v : values) {
        total += v.get();
    }
    context.write(key, new IntWritable(total));
}
```

**Logic:**
- Sums all income values for each month
- Emits (month, totalIncome)

#### How Components Work Together

**1. Driver:**
- Configures standard MapReduce job
- Sets IntWritable for income values

**2. Mapper:**
- Reads: `date,income` (format: YYYY-MM-DD,income)
- **Extracts month**: Uses `date.substring(0, 7)` to get "YYYY-MM"
- Creates month key from date
- Emits: `(month, income)`

**3. Shuffle and Sort:**
- Groups all income values by month
- All records from same month go to same reducer

**4. Reducer:**
- Receives: `month` and list of income values `[1000, 1500, 2000, ...]`
- Sums all income values for the month
- Emits: `(month, totalIncome)`

**Key Pattern:**
- **Date Parsing**: Extracting time components (year-month) from full date
- **Time-based Grouping**: Aggregating data by time periods

**Data Flow:**
```
Input: 2023-01-15,1000
       2023-01-20,1500
       2023-02-10,2000
       2023-02-25,1800
    ↓
Mapper → ("2023-01", 1000)
         ("2023-01", 1500)
         ("2023-02", 2000)
         ("2023-02", 1800)
    ↓
Shuffle & Sort → Groups by month
    ↓
Reducer → "2023-01": [1000, 1500] → total = 2500
          "2023-02": [2000, 1800] → total = 3800
    ↓
Output: 2023-01    2500
        2023-02    3800
```

**Output Screenshot Placeholder:**
![Exercise 8 Output](images/exercise_8_output.png)

---

### Exercise 9: Word Count (In-Mapper Combiner)

#### Purpose
Optimized word count using in-mapper combining to reduce network traffic.

#### Mapper (`WordCountInMapperCombinerMapper`)
```java
private Map<String, Integer> wordCounts = new HashMap<>();

protected void map(LongWritable key, Text value, Context context) {
    String[] words = value.toString().split("\\s+");
    
    for (String word : words) {
        String cleaned = word.toLowerCase().replaceAll("[^a-z0-9]", "");
        if (cleaned.length() > 0) {
            // Count in memory instead of emitting immediately
            wordCounts.put(cleaned, wordCounts.getOrDefault(cleaned, 0) + 1);
        }
    }
}

protected void cleanup(Context context) {
    // Emit all accumulated counts at end of map task
    for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
        context.write(new Text(entry.getKey()), new IntWritable(entry.getValue()));
    }
}
```

**Logic:**
- Accumulates word counts in memory (HashMap)
- Emits all counts in `cleanup()` method
- Reduces number of key-value pairs sent to reducer

#### Reducer
- Same as Exercise 1: sums counts for each word

#### How Components Work Together

**1. Driver:**
- Same configuration as Exercise 1
- Connects Mapper and Reducer

**2. Mapper - Map Phase:**
- Reads each line and splits into words
- **Instead of emitting immediately**, stores counts in HashMap
- Accumulates: `wordCounts.put(word, count + 1)`
- **No emissions during map()** - reduces network traffic

**3. Mapper - Cleanup Phase:**
- `cleanup()` method runs once after all lines processed
- Iterates through HashMap
- Emits all accumulated counts: `(word, count)`
- **Fewer key-value pairs** sent to reducer (one per unique word per mapper)

**4. Shuffle and Sort:**
- Groups pairs by word
- Less data to shuffle (already partially combined)

**5. Reducer:**
- Receives: `word` and list of counts `[5, 3, 2, ...]` (already combined per mapper)
- Sums the counts
- Emits: `(word, totalCount)`

**Key Pattern:**
- **In-Mapper Combining**: Combines values in mapper before sending to reducer
- **Optimization**: Reduces network I/O and shuffle data size
- Uses `cleanup()` method for final emission

**Data Flow:**
```
Input: "hello world hello"
       "world hello"
    ↓
Mapper.map() → Accumulates in HashMap:
               hello: 2
               world: 1
               hello: +1 → hello: 3
               world: +1 → world: 2
    ↓
Mapper.cleanup() → Emits: ("hello", 3)
                            ("world", 2)
    ↓
Shuffle & Sort → Less data to shuffle!
    ↓
Reducer → "hello": [3] → 3
          "world": [2] → 2
    ↓
Output: hello    3
        world    2
```

**Benefits:**
- Reduces network traffic (fewer key-value pairs)
- Faster shuffle phase
- Better performance for large datasets

**Output Screenshot Placeholder:**
![Exercise 9 Output](images/exercise_9_output.png)

---

### Exercise 10: PM10 Total Count

#### Purpose
Counts the total number of PM10 records using a constant key pattern.

#### Mapper (`PM10TotalCountMapper`)
```java
private static final Text KEY_ALL = new Text("ALL");
private static final IntWritable ONE = new IntWritable(1);

protected void map(LongWritable key, Text value, Context context) {
    String line = value.toString().trim();
    if (!line.isEmpty()) {
        // Emit constant key "ALL" with count 1
        context.write(KEY_ALL, ONE);
    }
}
```

**Logic:**
- Uses constant key "ALL" for all records
- Emits ("ALL", 1) for each non-empty line
- All records go to same reducer

#### Reducer (`PM10TotalCountReducer`)
```java
protected void reduce(Text key, Iterable<IntWritable> values, Context context) {
    int sum = 0;
    for (IntWritable v : values) {
        sum += v.get();
    }
    context.write(key, new IntWritable(sum));
}
```

**Logic:**
- Sums all counts
- Emits ("ALL", totalCount)

#### How Components Work Together

**1. Driver:**
- Configures job with Mapper and Reducer
- Sets constant key "ALL" pattern

**2. Mapper:**
- Reads each line (regardless of content)
- **Uses constant key "ALL"** for all records
- Emits: `("ALL", 1)` for each non-empty line
- All records go to same reducer

**3. Shuffle and Sort:**
- All pairs have same key "ALL"
- All data goes to one reducer (or distributed if multiple reducers)

**4. Reducer:**
- Receives: `"ALL"` and list of counts `[1, 1, 1, 1, ...]`
- Each "1" represents one record
- Sums all counts: total number of records
- Emits: `("ALL", totalCount)`

**Key Pattern:**
- **Constant Key Pattern**: All data aggregated under single key
- Useful for global aggregations (total count, sum, etc.)
- Simple but effective for counting all records

**Data Flow:**
```
Input: Record 1
       Record 2
       Record 3
    ↓
Mapper → ("ALL", 1)
         ("ALL", 1)
         ("ALL", 1)
    ↓
Shuffle & Sort → All go to same reducer
    ↓
Reducer → "ALL": [1, 1, 1] → total = 3
    ↓
Output: ALL    3
```

**Output Screenshot Placeholder:**
![Exercise 10 Output](images/exercise_10_output.png)

---

### Exercise 11: PM10 Average per Sensor

#### Purpose
Calculates average PM10 value for each sensor (similar to Exercise 5).

#### Code Structure
- **Mapper**: Extracts (sensorId, pm10Value) from CSV
- **Reducer**: Uses sum-count pattern to calculate average
- Uses `DoubleWritable` for floating-point values

#### How Components Work Together

**1. Driver:**
- Configures job similar to Exercise 5
- Sets DoubleWritable for PM10 values

**2. Mapper:**
- Reads: `sensorId,date,pm10`
- Extracts sensorId and PM10 value
- Emits: `(sensorId, pm10Value)`
- Same as Exercise 5

**3. Shuffle and Sort:**
- Groups all PM10 values by sensorId

**4. Reducer:**
- Uses sum-count pattern
- Receives: `sensorId` and list of PM10 values
- Sums values and counts records
- Calculates average: sum / count
- Emits: `(sensorId, average)`

**Key Pattern:**
- **Sum-Count Pattern**: Same as Exercise 5
- Demonstrates average calculation in MapReduce

**Data Flow:**
```
Input: sensor1,2023-01-01,45.2
       sensor1,2023-01-02,50.1
       sensor2,2023-01-01,48.5
    ↓
Mapper → (sensor1, 45.2)
         (sensor1, 50.1)
         (sensor2, 48.5)
    ↓
Shuffle & Sort
    ↓
Reducer → sensor1: [45.2, 50.1] → avg = 47.65
          sensor2: [48.5] → avg = 48.5
    ↓
Output: sensor1    47.65
        sensor2    48.5
```

**Output Screenshot Placeholder:**
![Exercise 11 Output](images/exercise_11_output.png)

---

### Exercise 12: PM10 Above Threshold Count

#### Purpose
Counts how many days each sensor recorded PM10 values above a threshold.

#### Mapper (`PM10AboveThresholdMapper`)
```java
protected void setup(Context context) {
    // Read threshold from configuration
    String thresholdStr = context.getConfiguration().get("pm10.threshold", "50");
    threshold = Float.parseFloat(thresholdStr);
}

protected void map(LongWritable key, Text value, Context context) {
    // Parse: sensorId,date,pm10
    String[] parts = line.split("[\t,]");
    String sensorId = parts[0];
    float pm10 = Float.parseFloat(parts[2]);
    
    if (pm10 > threshold) {
        // Emit (sensorId, 1) for each day above threshold
        context.write(new Text(sensorId), new IntWritable(1));
    }
}
```

**Logic:**
- Reads threshold from configuration (default: 50)
- Filters records above threshold
- Emits (sensorId, 1) for each day above threshold

#### Reducer (`PM10AboveThresholdReducer`)
```java
protected void reduce(Text key, Iterable<IntWritable> values, Context context) {
    int count = 0;
    for (IntWritable v : values) {
        count += v.get();
    }
    context.write(key, new IntWritable(count));
}
```

**Logic:**
- Counts days above threshold for each sensor
- Emits (sensorId, dayCount)

#### Driver (`PM10AboveThresholdDriver`)
```java
public int run(String[] args) {
    int reducers = Integer.parseInt(args[0]);
    String threshold = args[1];  // Threshold from command-line
    Path input = new Path(args[2]);
    Path output = new Path(args[3]);
    
    Configuration conf = this.getConf();
    // Store threshold in configuration
    conf.set("pm10.threshold", threshold);
    
    Job job = Job.getInstance(conf);
    job.setMapperClass(PM10AboveThresholdMapper.class);
    job.setReducerClass(PM10AboveThresholdReducer.class);
    // ... configuration
}
```

#### How Components Work Together

**1. Driver:**
- Receives threshold as command-line argument (e.g., "50")
- Stores in Configuration for mapper access
- Configures job with Mapper and Reducer

**2. Mapper Setup:**
- `setup()` method runs once per mapper task
- Reads threshold from configuration
- Defaults to 50.0 if not set or invalid

**3. Map Phase:**
- Reads each line: `sensorId,date,pm10`
- Parses PM10 value as float
- **Filters**: Only emits if `pm10 > threshold`
- Emits: `(sensorId, 1)` for each day above threshold
- Records below threshold are discarded

**4. Shuffle and Sort:**
- Groups pairs by sensorId
- All days above threshold for same sensor grouped together

**5. Reduce Phase:**
- Reducer receives: `sensorId` and list of counts `[1, 1, 1, ...]`
- Each "1" represents one day above threshold
- Sums counts: total days above threshold
- Emits: `(sensorId, dayCount)`

**Key Pattern:**
- **Filtering + Counting**: Combines filter (mapper) and count (reducer) patterns
- Configuration pattern: Driver → Configuration → Mapper.setup()

**Data Flow:**
```
Input: sensor1,2023-01-01,55.0  (above threshold)
       sensor1,2023-01-02,45.0  (below - filtered out)
       sensor1,2023-01-03,60.0  (above threshold)
    ↓
[Driver] → Sets threshold=50 in config
    ↓
Mapper.setup() → Reads threshold=50
    ↓
Mapper.map() → Filters: 55>50 ✓ → (sensor1, 1)
                         45>50 ✗ → skip
                         60>50 ✓ → (sensor1, 1)
    ↓
Shuffle & Sort → Groups by sensor1
    ↓
Reducer → Receives: sensor1, [1, 1]
          Count = 2 days above threshold
    ↓
Output: sensor1    2
```

**Output Screenshot Placeholder:**
![Exercise 12 Output](images/exercise_12_output.png)

---

## Output Screenshots

### Exercise 1: Word Count Output
![Exercise 1 Output](images/exercise_1_output.png)
*Caption: Word count results showing word frequencies*

### Exercise 2: Word Count Multiple Files Output
![Exercise 2 Output](images/exercise_2_output.png)
*Caption: Word count aggregated across multiple files*

### Exercise 3: PM10 Above Threshold Output
![Exercise 3 Output](images/exercise_3_output.png)
*Caption: Sensors with PM10 values above threshold*

### Exercise 4: PM10 Zone Dates Above Threshold Output
![Exercise 4 Output](images/exercise_4_output.png)
*Caption: Zone-date pairs with PM10 above threshold*

### Exercise 5: PM10 Average Output
![Exercise 5 Output](images/exercise_5_output.png)
*Caption: Average PM10 values per sensor*

### Exercise 6: PM10 Min/Max Output
![Exercise 6 Output](images/exercise_6_output.png)
*Caption: Minimum and maximum PM10 values per sensor*

### Exercise 7: Inverted Index Output
![Exercise 7 Output](images/exercise_7_output.png)
*Caption: Inverted index mapping words to documents*

### Exercise 8: Income Monthly Totals Output
![Exercise 8 Output](images/exercise_8_output.png)
*Caption: Total income aggregated by month*

### Exercise 9: Word Count In-Mapper Combiner Output
![Exercise 9 Output](images/exercise_9_output.png)
*Caption: Word count using in-mapper combining optimization*

### Exercise 10: PM10 Total Count Output
![Exercise 10 Output](images/exercise_10_output.png)
*Caption: Total count of PM10 records*

### Exercise 11: PM10 Average per Sensor Output
![Exercise 11 Output](images/exercise_11_output.png)
*Caption: Average PM10 values calculated per sensor*

### Exercise 12: PM10 Above Threshold Count Output
![Exercise 12 Output](images/exercise_12_output.png)
*Caption: Count of days each sensor exceeded PM10 threshold*

---

## Conclusion

This report covered:
1. **Hadoop Installation**: Step-by-step installation guide
2. **Local Setup**: Configuration for single-node cluster
3. **Running Hadoop**: How to start services and run jobs
4. **Code Explanations**: Detailed breakdown of exercises 1-12

Each exercise demonstrates important MapReduce patterns:
- **Word Count**: Basic MapReduce pattern
- **Aggregation**: Sum, average, count patterns
- **Filtering**: Conditional data processing
- **Grouping**: Multi-field grouping and composite keys
- **Optimization**: In-mapper combining

These exercises provide a solid foundation for understanding Hadoop MapReduce programming.

---

## References

- Apache Hadoop Documentation: https://hadoop.apache.org/docs/
- Hadoop Cluster Setup Guide
- MapReduce Tutorial: https://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html

