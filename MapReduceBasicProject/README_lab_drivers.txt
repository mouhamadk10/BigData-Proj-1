# How to run (local)

- Prereqs: Java 8+, Maven, winutils.exe at C:\hadoop\bin
- Build:
  - From `MapReduceBasicProject`: `mvn -DskipTests package`
- Run any Driver:
  - PowerShell (use --% to avoid parsing -D):
    - `java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0.jar <FQCN> <args>`
  - CMD:
    - `java -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0.jar <FQCN> <args>`
- Outputs: created under `outputs/` (moved from job folders `out_*`, `adj_out`, `cand_out`, `foaf_out`).
- Inputs: sample CSV/TXT under `data/`.
- IntelliJ: Right‑click Driver → Run; set Program arguments and, if needed, VM options `-Dhadoop.home.dir=C:\hadoop`.

Examples
- WordCount:
  - `it.polito.bigdata.hadoop.WordCountStandardDriver 1 data\example_data\document.txt outputs\out_wc`
- Temperature Max (two schemas):
  - `it.polito.bigdata.hadoop.temperature.MaxTempMultipleInputsDriver 1 data\tempA.csv data\tempB.csv outputs\out_max`
- Social FOAF (3 stages):
  - `it.polito.bigdata.hadoop.social.AdjacencyListDriver 1 data\edges.csv outputs\adj_out`
  - `it.polito.bigdata.hadoop.social.FoafJoinDriver 1 outputs\adj_out outputs\cand_out`
  - `it.polito.bigdata.hadoop.social.FoafAggregateDriver 1 outputs\adj_out outputs\cand_out outputs\foaf_out`

---
# Lab MapReduce Drivers and Input Formats

## Temperature Max Per Date (Multiple Inputs)
Path: temperature/MaxTempMultipleInputsDriver
Args: <reducers> <schemaA.csv> <schemaB.csv> <output>
- Input A: sensorId,date,hour,temperature\n...
- Input B: date,hour,temperature,sensorId\n...
Run:
  hadoop jar ... MaxTempMultipleInputsDriver 1 tempA.csv tempB.csv out_dir

## Word Count Standard
Path: wordcount/WordCountStandardDriver
Args: <reducers> <input> <output>
- Input: Unstructured TXT (one sentence per line)
Run:
  hadoop jar ... WordCountStandardDriver 1 doc.txt wc_out

## PM10 Above Threshold
Path: pm10/PM10AboveThresholdDriver
Args: <reducers> <threshold> <input> <output>
- Input: sensorId,date,value
Run:
  hadoop jar ... PM10AboveThresholdDriver 1 50 pmdata.csv pm10_out

## Income Top-1
Path: income/IncomeTop1Driver
Args: <reducers> <input> <output>
- Input: date,income
Run:
  hadoop jar ... IncomeTop1Driver 1 income.csv income_top1

## Dictionary - Distinct Words
Path: dictionary/DictionaryDistinctWordsDriver
Args: <reducers> <input> <output>
- Input: Unstructured TXT
Run:
  hadoop jar ... DictionaryDistinctWordsDriver 1 doc.txt dict_out

## Stopword Elimination
Path: stopword/StopwordEliminationDriver
Args: <reducers> <stopwords.txt> <input> <output>
- Input: Big text per line, stopwords 1/line
Run:
  hadoop jar ... StopwordEliminationDriver 1 stops.txt big.txt nostop_out

## Social: Adjacency List (Stage 1)
Path: social/AdjacencyListDriver
Args: <reducers> <edges.csv> <adj_output>
- Input: Username1,Username2
Run:
  hadoop jar ... AdjacencyListDriver 1 edges.csv adj_out

## Social: FOAF Candidates (Stage 2)
Path: social/FoafJoinDriver
Args: <reducers> <adj_output> <cand_output>
- Input: <adj_output> of Stage 1 (user <tab> f1 f2 ...)
Run:
  hadoop jar ... FoafJoinDriver 1 adj_out cand_out

## Social: FOAF Exact 2-hop (Stage 3)
Path: social/FoafAggregateDriver
Args: <reducers> <adj_output> <cand_output> <foaf_output>
- Inputs: Stage 1 (adj_out) and Stage 2 (cand_out)
Run:
  hadoop jar ... FoafAggregateDriver 1 adj_out cand_out foaf_out

## Q&A Join
Path: qa/QAJoinDriver
Args: <reducers> <input> <output>
- Input: mixed: QID,Timestamp,Text or AID,QID,Timestamp,Text lines
Run:
  hadoop jar ... QAJoinDriver 1 qa_in.csv qa_out

## Categorization
Path: categorization/UserCategorizationDriver
Args: <reducers> <rules.txt> <users.csv> <output>
- Input: users CSV, rules in specified text format
Run:
  hadoop jar ... UserCategorizationDriver 1 rules.txt users.csv cat_out

## Word→Int
Path: wordtoint/WordToIntDriver
Args: <reducers> <dict.txt> <input> <output>
Run:
  hadoop jar ... WordToIntDriver 1 dict.txt test.txt id_out

## Select Users by Genre
Path: selection/UsersByGenresDriver
Args: <reducers> <user+genres.csv> <output>
- Input: User and Likes files mixed: User#1,...  -- User#1,Commedia
Run:
  hadoop jar ... UsersByGenresDriver 1 users_and_likes.csv genre_out

---
# Quick Smoke Tests (inline samples)

Edges (edges.csv):
User1,User2
User1,User3
User2,User4

Users (users.csv):
User#1,John,Smith,M,1934,NY,Bachelor
User#2,Jane,Doe,F,1956,LA,College

Rules (rules.txt):
Gender=M and YearOfBirth=1934 -> Category#1
Gender=F and YearOfBirth=1956 -> Category#3

Income (income.csv):
2015-11-01,1000
2015-11-02,1305

Temp A (tempA.csv):
s1,2016-01-01,14:00,20.5

Temp B (tempB.csv):
2016-01-01,14:00,30.2,s3
