#!/bin/bash
cd /data/chorus-monitor-agent/
#export JAVA_HOME=/rc/local/jdk1.8.0_60
#export PATH=$JAVA_HOME/bin:$PATH
sh /rc/local/bin/chorus_monitor_agent_stop.sh
nohup java -server -Xms128m -Xmx512m  -jar /data/chorus-monitor-agent/chorus-monitor-agent*.jar > /dev/null 2>&1 &
