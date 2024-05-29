#!/bin/bash

set -e  # 스크립트 실행 중 오류 발생 시 즉시 종료합니다.

# 주키퍼 설치
wget -q https://downloads.apache.org/zookeeper/zookeeper-3.7.0/apache-zookeeper-3.7.0-bin.tar.gz || { echo "주키퍼 다운로드에 실패했습니다."; exit 1; }
tar -xzf apache-zookeeper-3.7.0-bin.tar.gz || { echo "주키퍼 압축 해제에 실패했습니다."; exit 1; }
mv apache-zookeeper-3.7.0-bin /opt/zookeeper || { echo "주키퍼 이동에 실패했습니다."; exit 1; }
rm apache-zookeeper-3.7.0-bin.tar.gz || { echo "주키퍼 압축 파일 삭제에 실패했습니다."; exit 1; }
cp /opt/zookeeper/conf/zoo_sample.cfg /opt/zookeeper/conf/zoo.cfg || { echo "주키퍼 구성 파일 복사에 실패했습니다."; exit 1; }
echo "dataDir=/tmp/zookeeper" >> /opt/zookeeper/conf/zoo.cfg || { echo "주키퍼 구성 파일 수정에 실패했습니다."; exit 1; }
/opt/zookeeper/bin/zkServer.sh start || { echo "주키퍼 시작에 실패했습니다."; exit 1; }

# 카프카 설치
wget -q https://downloads.apache.org/kafka/3.1.0/kafka_2.13-3.1.0.tgz || { echo "카프카 다운로드에 실패했습니다."; exit 1; }
tar -xzf kafka_2.13-3.1.0.tgz || { echo "카프카 압축 해제에 실패했습니다."; exit 1; }
mv kafka_2.13-3.1.0 /opt/kafka || { echo "카프카 이동에 실패했습니다."; exit 1; }
rm kafka_2.13-3.1.0.tgz || { echo "카프카 압축 파일 삭제에 실패했습니다."; exit 1; }
sed -i 's/log.dirs=\/tmp\/kafka-logs/log.dirs=\/opt\/kafka\/logs/g' /opt/kafka/config/server.properties || { echo "카프카 설정 파일 수정에 실패했습니다."; exit 1; }
/opt/kafka/bin/kafka-server-start.sh -daemon /opt/kafka/config/server.properties || { echo "카프카 시작에 실패했습니다."; exit 1; }
