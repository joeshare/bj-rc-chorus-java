<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>[TEST]非法数据访问！</title>
    <style>
    </style>
</head>
<body>
<div>尊敬的用户：${name!""}</div>
<p>数据发现非法访问：</p>
<div>
    <p>时间：${evtTime?string('yyyy-MM-dd HH:mm:ss')}</p>
    <p>操作人：${reqUser!""}</p>
    <p>资源类型：${resType!""}</p>
    <p>资源：${resource!""}</p>
    <p>操作：${action!""}</p>
</div>
</body>
</html>