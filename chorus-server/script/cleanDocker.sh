#!/bin/bash
version=$1
branch=$2
dockerContainerName=chorus-server-$version-$branch
echo "停止docker容器。"
docker stop $dockerContainerName
echo "删除容器"
docker rm $dockerContainerName
echo "备份日志"
date=`date "+%Y-%m-%d-%H:%M:%S"`
logbackupPath=/data/chorus/logbackup/$date/
chorusHome=/data/chorus/chorus-server-$version-$branch
mkdir -p $logbackupPath
cp $chorusHome/logs/*.* $logbackupPath
echo "删除工作目录"
rm -rf $chorusHome
