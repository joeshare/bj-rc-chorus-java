#!/bin/bash
sh /rc/chorus-monitor-agent/chorus_monitor_agent_stop.sh
cd /rc/chorus-monitor-agent/
nohup /usr/jdk64/jdk1.8.0_60/bin/java -server -Xms128m -Xmx512m  -jar /rc/chorus-monitor-agent/chorus-monitor-agent*.jar > /dev/null 2>&1 &
