# Hadoop MapReduce Basic Project

A comprehensive collection of Hadoop MapReduce exercises implemented in Java, covering fundamental MapReduce patterns and use cases. Each exercise includes complete source code, input data, and detailed documentation with code explanations.

## Project Structure

Each exercise is organized in its own directory (`exercise_1/`, `exercise_2/`, etc.) containing:

* `*Driver.java` - MapReduce Driver class that configures and runs the job
* `*Mapper.java` - Map function implementation
* `*Reducer.java` - Reduce function implementation (when applicable)
* `*_input.txt` or `*_input.csv` - Sample input data
* `README.md` - Exercise-specific documentation with:
  - **Code Explanation**: Detailed explanation of Mapper, Reducer, and Driver logic
  - **Execution Steps**: Step-by-step instructions for running the exercise
  - **Expected Output**: Format and examples of output
  - **Notes**: Important implementation details and patterns

## Prerequisites

1. **Java 8** or higher
2. **Hadoop 2.x or 3.x** (tested with Hadoop 2.7.1 and 3.3.6)
3. **Maven** (for building JAR files)

## Setup

1. **Ensure Hadoop is configured and running:**
   ```bash
   start-dfs.sh
   start-yarn.sh
   ```

2. **Verify cluster is running:**
   ```bash
   jps
   # Should show: NameNode, DataNode, SecondaryNameNode, ResourceManager, NodeManager
   
   hdfs dfsadmin -report
   ```

3. **Build individual exercises:**
   Each exercise can be built separately. See individual exercise README files for build instructions.

## Quick Start

1. **Navigate to an exercise directory** (e.g., `exercise_1/`)
2. **Read the exercise README** for:
   - Code explanation and logic
   - Uploading input files to HDFS
   - Running the MapReduce job
   - Viewing results
3. **Follow the execution steps** in the README

## Exercises Overview

### Word Count Exercises (1-2, 9)
- **Exercise 1**: Word Count (Standard) - Single File
  - Basic word counting pattern
  - Text cleaning and normalization
  - Standard MapReduce word count implementation

- **Exercise 2**: Word Count (Standard) - Multiple Files
  - Same as Exercise 1 but processes multiple input files
  - Demonstrates handling multiple files in a single job

- **Exercise 9**: Word Count (In-Mapper Combiner)
  - Optimized word count using in-mapper combining
  - Reduces network traffic by combining in mapper

### PM10 Air Quality Exercises (3-6, 10-12)
- **Exercise 3**: PM10 Above Threshold
  - Filters PM10 records above a threshold value
  - Demonstrates filtering pattern

- **Exercise 4**: PM10 Zone Dates Above Threshold
  - Groups PM10 data by zone and date
  - Filters records above threshold per zone

- **Exercise 5**: PM10 Average
  - Calculates average PM10 values per sensor
  - Demonstrates aggregation pattern

- **Exercise 6**: PM10 Min/Max
  - Finds minimum and maximum PM10 values
  - Demonstrates min/max finding pattern

- **Exercise 10**: PM10 Total Count
  - Counts total number of PM10 records
  - Simple counting pattern with constant key

- **Exercise 11**: PM10 Average per Sensor
  - Calculates average PM10 value for each sensor
  - Uses DoubleWritable for floating-point values

- **Exercise 12**: PM10 Above Threshold Count
  - Counts days each sensor exceeded threshold
  - Configurable threshold via command-line argument

### Income Analysis Exercises (8, 13, 13Bis)
- **Exercise 8**: Income Monthly Totals
  - Aggregates income by month
  - Date parsing and grouping

- **Exercise 13**: Income Top 1 Most Profitable Date
  - Finds date with highest income
  - Uses negative key trick for descending sort

- **Exercise 13Bis**: Income Top 2 Most Profitable Dates
  - Finds top 2 most profitable dates
  - Extends top-1 pattern to top-K

### Dictionary Exercises (14-15, 26)
- **Exercise 14**: Dictionary Distinct Words
  - Finds distinct words in a dictionary
  - Demonstrates distinct value pattern

- **Exercise 15**: Dictionary Word to Int
  - Maps words to integer IDs
  - Uses dictionary lookup pattern

- **Exercise 26**: Word to Integer Mapping
  - Converts words to integers using external dictionary
  - Loads dictionary in mapper setup phase
  - Demonstrates distributed cache pattern

### Temperature Analysis Exercises (17-20, 20Bis, 21)
- **Exercise 17**: Temperature Max (Multiple Inputs)
  - Finds maximum temperature from multiple input files
  - Uses MultipleInputs for different file formats

- **Exercise 18**: Temperature Filter Above 30
  - Filters temperatures above 30 degrees
  - Simple filtering pattern

- **Exercise 19**: Temperature Filter At Most 30
  - Filters temperatures at most 30 degrees
  - Demonstrates different filter conditions

- **Exercise 20**: Temperature Split
  - Splits temperature data by condition
  - Multiple outputs pattern

- **Exercise 20Bis**: Temperature Split (Alternative)
  - Alternative implementation of temperature splitting

- **Exercise 21**: Temperature Split Values
  - Splits temperature values by ranges
  - More complex splitting logic

### Text Processing Exercises (7, 21, 22)
- **Exercise 7**: Inverted Index
  - Creates inverted index (word -> list of documents)
  - Demonstrates complex value aggregation

- **Exercise 21**: Stopword Elimination
  - Removes stopwords from text
  - Uses filtering with external stopword list

- **Exercise 22**: Stopword Elimination (Alternative)
  - Alternative stopword elimination implementation

### Social Network Exercises (22-25, 27-29)
- **Exercise 22**: Friends of User
  - Finds all friends of a specific target user
  - Uses configuration to pass target user
  - Demonstrates filtering in reducer

- **Exercise 23**: Potential Friends of User
  - Finds potential friends (friends of friends) for target user
  - 2-hop graph traversal pattern

- **Exercise 24**: Friends List Per User
  - Generates complete friends list for each user
  - Bidirectional friendship graph processing

- **Exercise 25**: Potential Friends List Per User
  - Finds potential friends list for each user
  - Friends of friends aggregation

- **Exercise 27**: User Categorization
  - Categorizes users based on business rules
  - Loads rules file in reducer setup
  - Rule matching pattern

- **Exercise 28**: Question-Answer Join
  - Joins questions with their answers
  - Multiple input files with file name detection
  - Demonstrates join pattern

- **Exercise 29**: Users by Gender and Year of Birth
  - Groups users by gender and year of birth
  - Composite key pattern
  - File filtering in mapper

## Common MapReduce Patterns Demonstrated

1. **Word Count Pattern**: Exercises 1, 2, 9
2. **Aggregation Pattern**: Exercises 5, 8, 10, 11
3. **Filtering Pattern**: Exercises 3, 4, 12, 18, 19, 21, 22
4. **Top-K Pattern**: Exercises 13, 13Bis
5. **Join Pattern**: Exercise 28
6. **Graph Processing**: Exercises 22-25
7. **Distributed Cache**: Exercises 26, 27
8. **Multiple Inputs**: Exercise 17
9. **Multiple Outputs**: Exercise 20
10. **Composite Keys**: Exercise 29

## Project Organization

- `exercise_*/` - Individual exercise directories, each containing:
  - Complete Java source code (Driver, Mapper, Reducer classes)
  - Input data files
  - README.md with:
    - **Code Explanation**: Detailed breakdown of each component
    - **Execution Steps**: How to run the exercise
    - **Expected Output**: What to expect
    - **Notes**: Implementation details and patterns
- `pom.xml` - Maven configuration for building JAR files
- `run_exercise.sh` - Script to run individual exercises
- `run_all_exercises.sh` - Script to run all exercises

## Building and Running Exercises

### Method 1: Using Individual Build Directories
Each exercise can be built in its own `temp_build*/` directory:
```bash
mkdir -p temp_buildN/src/main/java/it/polito/bigdata/hadoop/...
cp exercise_N/*.java temp_buildN/src/main/java/it/polito/bigdata/hadoop/...
cd temp_buildN
mvn clean package -DskipTests
```

### Method 2: Using run_exercise.sh Script
```bash
./run_exercise.sh <exercise_number>
```

## Web UIs

- **NameNode**: http://localhost:9870 (Hadoop 3.x) or http://localhost:50070 (Hadoop 2.x)
- **ResourceManager**: http://localhost:8088

## Code Documentation

Each exercise README includes:
- **Mapper Logic**: How the map function processes input
- **Reducer Logic**: How the reduce function aggregates data
- **Driver Configuration**: How the job is configured
- **Input/Output Formats**: Data formats expected and produced
- **Key Patterns**: MapReduce patterns demonstrated

## Notes

* Each exercise is **self-contained** with all necessary source code and data
* All exercises use standard Hadoop MapReduce API
* Some exercises require:
  - Environment variables (e.g., `THRESHOLD`) - see individual README files
  - Local file paths for dictionaries/rules - see individual README files
  - Command-line arguments for configuration
* Number of reducers varies by exercise (typically 1 or 2)
* Output is written to HDFS and can be viewed using `hdfs dfs -cat`
* Code explanations in each README help understand MapReduce patterns and best practices

## Learning Path

1. **Start with basics**: Exercises 1-2 (Word Count)
2. **Learn aggregation**: Exercises 5, 10, 11 (Averages, Counts)
3. **Learn filtering**: Exercises 3, 12, 18, 19
4. **Learn advanced patterns**: Exercises 13 (Top-K), 17 (Multiple Inputs), 20 (Multiple Outputs)
5. **Learn joins and graphs**: Exercises 22-25, 28
6. **Learn distributed cache**: Exercises 26, 27

## License

This project is for educational purposes.

## Reference

Project structure inspired by [HadoopCluster](https://github.com/bilalr-dev/HadoopCluster) repository.
