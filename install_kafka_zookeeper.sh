#!/bin/bash

# 주키퍼 설치
wget -q https://downloads.apache.org/zookeeper/zookeeper-3.7.0/apache-zookeeper-3.7.0-bin.tar.gz
tar -xzf apache-zookeeper-3.7.0-bin.tar.gz
mv apache-zookeeper-3.7.0-bin /opt/zookeeper
rm apache-zookeeper-3.7.0-bin.tar.gz
cp /opt/zookeeper/conf/zoo_sample.cfg /opt/zookeeper/conf/zoo.cfg
echo "dataDir=/tmp/zookeeper" >> /opt/zookeeper/conf/zoo.cfg
/opt/zookeeper/bin/zkServer.sh start

# 카프카 설치
wget -q https://downloads.apache.org/kafka/3.1.0/kafka_2.13-3.1.0.tgz
tar -xzf kafka_2.13-3.1.0.tgz
mv kafka_2.13-3.1.0 /opt/kafka
rm kafka_2.13-3.1.0.tgz
sed -i 's/log.dirs=\/tmp\/kafka-logs/log.dirs=\/opt\/kafka\/logs/g' /opt/kafka/config/server.properties
/opt/kafka/bin/kafka-server-start.sh -daemon /opt/kafka/config/server.properties
