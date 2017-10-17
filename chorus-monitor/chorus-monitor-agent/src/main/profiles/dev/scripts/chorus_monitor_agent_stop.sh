#!/bin/bash
ps ax | grep chorus-monitor-agent.*.jar | grep -v grep | awk '{print $1}' |xargs kill -9 
