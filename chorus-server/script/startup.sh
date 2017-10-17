#!/bin/sh
source ~/.bashrc
version=2.0
env=dev
port=8080
chorusHome=/home/rc/chorus

if [ $1 ]
then
 version=$1
fi

if [ $2 ]
then
 env=$2
fi
if [ $3 ]
then
 port=$3
fi
if [ $4 ]
then
 postfix=$version"-"$4
else
 postfix=$version
fi
chorusHome=/home/rc/chorus/chorus-server-$postfix
chorusServerJarPath=$chorusHome/chorus-server-$version.jar

echo "Version: $version, ENV: $env, JarPath: $chorusServerPath"
cd $chorusHome
nohup java -jar -Ddruid.logType=slf4j -Dspring.profiles.active=$env -Dfile.encoding=UTF-8 -Dsun.jun.encoding=UTF-8 -Dserver.port=$port $chorusServerJarPath >/dev/null 2>&1 &
echo "Start Chorus-Server $version on $env environment Finished."
