#!/bin/bash
# Create Hadoop XML configs

[ -z "$HADOOP_HOME" ] && echo "Set HADOOP_HOME first" && exit 1
CONF_DIR="$HADOOP_HOME/etc/hadoop"
DATA_DIR="$HADOOP_HOME/data"

mkdir -p "$DATA_DIR/namenode" "$DATA_DIR/datanode"

cat > "$CONF_DIR/core-site.xml" <<XML
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
XML

cat > "$CONF_DIR/hdfs-site.xml" <<XML
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>$DATA_DIR/namenode</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>$DATA_DIR/datanode</value>
    </property>
</configuration>
XML

cat > "$CONF_DIR/mapred-site.xml" <<XML
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
XML

cat > "$CONF_DIR/yarn-site.xml" <<XML
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
</configuration>
XML

echo "XML configs created in $CONF_DIR"
