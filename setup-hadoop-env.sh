#!/bin/bash
# Setup Hadoop Environment Variables

CURRENT_USER=$(whoami)

if [ -z "$HADOOP_HOME" ]; then
    if [ -d "/opt/hadoop" ]; then
        HADOOP_HOME="/opt/hadoop"
    elif [ -d "/usr/local/hadoop" ]; then
        HADOOP_HOME="/usr/local/hadoop"
    elif [ -d "$HOME/hadoop" ]; then
        HADOOP_HOME="$HOME/hadoop"
    else
        read -p "Enter HADOOP_HOME path: " HADOOP_HOME
        [ ! -d "$HADOOP_HOME" ] && echo "Invalid path" && exit 1
    fi
fi

ENV_VARS="
# Hadoop Environment Variables
export HADOOP_HOME=$HADOOP_HOME
export PATH=\$PATH:\$HADOOP_HOME/bin
export PATH=\$PATH:\$HADOOP_HOME/sbin
export HADOOP_MAPRED_HOME=\$HADOOP_HOME
export HADOOP_COMMON_HOME=\$HADOOP_HOME
export HADOOP_HDFS_HOME=\$HADOOP_HOME
export YARN_HOME=\$HADOOP_HOME
export HADOOP_CONF_DIR=\$HADOOP_HOME/etc/hadoop
export HDFS_DATANODE_USER=$CURRENT_USER
export HDFS_NAMENODE_USER=$CURRENT_USER
export HDFS_SECONDARYNAMENODE_USER=$CURRENT_USER
export YARN_RESOURCEMANAGER_USER=$CURRENT_USER
export YARN_NODEMANAGER_USER=$CURRENT_USER
"

if grep -q "HADOOP_HOME" ~/.bashrc 2>/dev/null; then
    read -p "Replace existing Hadoop env vars? (y/N): " -n 1 -r; echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        sed -i '/# Hadoop Environment Variables/,/YARN_NODEMANAGER_USER/d' ~/.bashrc
        echo "$ENV_VARS" >> ~/.bashrc
    fi
else
    echo "$ENV_VARS" >> ~/.bashrc
fi

echo "Done. Run: source ~/.bashrc"
