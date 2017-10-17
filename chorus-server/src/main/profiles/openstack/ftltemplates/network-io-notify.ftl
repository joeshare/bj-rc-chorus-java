<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>数据流量预警</title>
    <style>
    </style>
</head>
<body>
<div>尊敬的用户：${name}</div>
<p>发现您的数据正被大量输出：</p>
<div>
    <p>时间：${evtTime?string('yyyy-MM-dd HH:mm:ss')}</p>
    <p>操作人：${user!""}</p>
    <p>项目：${projectCode!""}</p>
    <p>操作：${cmdLine!""}</p>
    <p>流量(MB)：${output!""}</p>
    <p>节点：${hostname!""}</p>
</div>
</body>
</html>