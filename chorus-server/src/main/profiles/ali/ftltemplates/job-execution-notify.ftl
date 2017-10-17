<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>[ALI]任务执行失败通知</title>
    <style>
    </style>
</head>
<body>
	<div>尊敬的用户：${name}</div>
	<p>任务执行失败：</p>
	<div>
		<p>项目：${projectName}</p>
		<p>任务：${taskName}</p>
		<p>任务执行时间：${startTime?string('yyyy-MM-dd HH:mm:ss')} ~ ${endTime?string('yyyy-MM-dd HH:mm:ss')}</p>
		<p>任务执行结果：${status}</p>
	</div>
</body>
</html>
