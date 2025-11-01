# Requires: Java, Maven-built shaded jar, and winutils at C:\hadoop\bin\winutils.exe
# Usage: powershell -ExecutionPolicy Bypass -File .\run_all_samples.ps1

$ErrorActionPreference = 'SilentlyContinue'
Remove-Item -Recurse -Force out_* adj_out cand_out foaf_out -ErrorAction SilentlyContinue
$ErrorActionPreference = 'Continue'

$env:HADOOP_HOME = "C:\hadoop"
$env:PATH = "$env:HADOOP_HOME\bin;$env:PATH"

# Build sample inputs (idempotent)
@"
Toy example  file for Hadoop.  Hadoop running example.
"@ | Out-File -Encoding ascii .\example_data\document.txt

@"
s1,2016-01-01,14:00,20.5
s2,2016-01-01,14:10,30.2
s1,2016-01-02,10:00,11.5
"@ | Out-File -Encoding ascii tempA.csv

@"
2016-01-01,14:00,30.1,s3
2016-01-01,15:00,10.2,s4
2016-01-02,14:15,31.5,s3
"@ | Out-File -Encoding ascii tempB.csv

@"
s1,2016-01-01,20.5
s2,2016-01-01,60.2
s1,2016-01-02,30.1
s2,2016-01-02,20.4
"@ | Out-File -Encoding ascii pm10.csv

@"
2015-11-01,1000
2015-11-02,1305
2015-12-01,500
2015-12-02,750
"@ | Out-File -Encoding ascii income.csv

@"
Q1,2015-01-01,What is ..?
A1,Q1,2015-01-02,It is ..
A3,Q1,2015-01-05,I think it is ..
Q2,2015-01-03,Who invented ..
A2,Q2,2015-01-03,John Smith
"@ | Out-File -Encoding ascii qa.csv

@"
User#1,John,Smith,M,1934,New York,Bachelor
User#2,Paul,Jones,M,1956,Dallas,College
User#3,Jenny,Smith,F,1934,Philadelphia,Bachelor
"@ | Out-File -Encoding ascii users.csv

@"
Gender=M and YearOfBirth=1934 -> Category#1
Gender=M and YearOfBirth=1956 -> Category#3
Gender=F and YearOfBirth=1934 -> Category#2
"@ | Out-File -Encoding ascii rules.txt

@"
User1,User2
User1,User3
User2,User4
User3,User5
"@ | Out-File -Encoding ascii edges.csv

@"
the
and
or
a
an
"@ | Out-File -Encoding ascii stopwords.txt

@"
TEST CONVERTION WORD TO INTEGER
SECOND LINE TEST WORD TO INTEGER
"@ | Out-File -Encoding ascii words.txt

@"
CONVERTION	1
INTEGER	2
LINE	3
SECOND	4
TEST	5
TO	6
WORD	7
"@ | Out-File -Encoding ascii dictionary.txt

function RunJava($cmd) {
  try { iex $cmd } catch { Write-Host "(skipped)" -ForegroundColor DarkYellow }
}

Write-Host "==== WordCount ====" -ForegroundColor Cyan
# Try base FQCN, then fallback to wordcount package if needed
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.WordCountStandardDriver 1 example_data\document.txt out_wc'
if (!(Test-Path .\out_wc\_SUCCESS)) {
  RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.wordcount.WordCountStandardDriver 1 example_data\document.txt out_wc'
}

Write-Host "==== Temp Max ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.temperature.MaxTempMultipleInputsDriver 1 tempA.csv tempB.csv out_max'

Write-Host "==== PM10 Above 50 ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.pm10.PM10AboveThresholdDriver 1 50 pm10.csv out_pm10_above'
Write-Host "==== PM10 Average ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.pm10.PM10AverageDriver 1 pm10.csv out_pm10_avg'
Write-Host "==== PM10 MinMax ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.pm10.PM10MinMaxDriver 1 pm10.csv out_pm10_minmax'
Write-Host "==== PM10 TotalCount ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.pm10.PM10TotalCountDriver 1 pm10.csv out_pm10_count'

Write-Host "==== Income Monthly Totals ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.income.IncomeMonthlyTotalsDriver 1 income.csv out_income_month'
Write-Host "==== Income Yearly Avg ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.income.IncomeYearlyAverageFromMonthlyDriver 1 out_income_month out_income_year'
Write-Host "==== Income Top1 ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.income.IncomeTop1Driver 1 income.csv out_income_top1'
Write-Host "==== Income Top2 ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.income.IncomeTop2Driver 1 income.csv out_income_top2'

Write-Host "==== Stopword Elimination ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.stopword.StopwordEliminationDriver 1 stopwords.txt example_data\document.txt out_stopless'

Write-Host "==== Inverted Index ====" -ForegroundColor Cyan
@"
Sentence#1	Hadoop or Spark
Sentence#2	Hadoop or Spark and Java
Sentence#3	Hadoop and Big Data
"@ | Out-File -Encoding ascii sentences.txt
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.invertedindex.InvertedIndexDriver 1 sentences.txt out_invidx'

Write-Host "==== Dictionary Distinct ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.dictionary.DictionaryDistinctWordsDriver 1 example_data\document.txt out_dict'

Write-Host "==== Word->Int ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.wordtoint.WordToIntDriver 1 dictionary.txt words.txt out_word2int'

Write-Host "==== Users by Genres (Commedia & Adventure) ====" -ForegroundColor Cyan
@"
User#1,Commedia
User#1,Adventure
User#2,Commedia
"@ | Out-File -Encoding ascii likes.csv
@"
User#1,John,Smith,M,1934,NY,Bachelor
User#2,Jane,Doe,F,1956,LA,College
"@ | Out-File -Encoding ascii users_genre.csv
Get-Content users_genre.csv,likes.csv | Out-File -Encoding ascii users_and_likes.csv
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.selection.UsersByGenresDriver 1 users_and_likes.csv out_genres'

Write-Host "==== Q&A Join ====" -ForegroundColor Cyan
RunJava 'java --% -Dhadoop.home.dir=C:\hadoop -Djava.library.path=C:\hadoop\bin -cp target\MapReduceProject-1.0.0-shaded.jar it.polito.bigdata.hadoop.qa.QAJoinDriver 1 qa.csv out_qa'

Write-Host "\n=== Sample outputs ===" -ForegroundColor Yellow
foreach ($d in 'out_wc','out_max','out_pm10_above','out_pm10_avg','out_pm10_minmax','out_pm10_count','out_income_month','out_income_year','out_income_top1','out_income_top2','out_stopless','out_invidx','out_dict','out_word2int','out_genres','out_qa','out_cat','adj_out','cand_out','foaf_out') {
  if (Test-Path $d) {
    Write-Host "--- $d ---" -ForegroundColor Green
    Get-Content "$d\part-r-00000" -ErrorAction SilentlyContinue | Select-Object -First 10
  }
}
