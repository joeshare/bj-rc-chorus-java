#!/bin/sh
version=2.0
if [ $1 ]
then
 version=$1
fi
PID=$(ps -ef |grep -v grep|grep chorus-server-$version.jar|awk '{print $2}')
    echo "PID: " $PID
if [ -z "$PID" ];
then
    echo "NO need stop".
else
    kill $PID
   echo "Stop Chorus Server" $PID
fi
