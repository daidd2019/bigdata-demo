#!/usr/bin/env bash

export JAVA_HOME=${JAVA_HOME:-/usr/jdk64/jdk1.8.0_112}
export PATH=$PATH:/$JAVA_HOME/bin
base_dir=$(dirname $0)/..


if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then
    export KAFKA_HEAP_OPTS="-Xmx512M"
fi


LOG4J=$base_dir/conf/log4j.properties
if [ -f $LOG4J ]; then
    export LOG4J_PARAMS="-Dlog4j.configuration=file:$LOG4J"
fi

jars=$base_dir/@project.artifactId@-@project.version@.jar
for jar in $base_dir/lib/*.jar
do
jars=$jars:$jar
done

java  $LOG4J_PARAMS -cp $jars  hadoop.hdfs.HdfsDemo  $@