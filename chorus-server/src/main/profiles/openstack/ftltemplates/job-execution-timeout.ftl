<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>任务执行失败通知</title>
    <style>
    </style>
</head>
<body>
	<div>尊敬的用户：${name}</div>
	<p>任务执行超时告警：</p>
	<div>
		<p>项目：${projectName}</p>
		<p>任务：${taskName}</p>
		<p>任务期望执行时间：${startTime?string('yyyy-MM-dd HH:mm:ss')} ~ ${expectEndTime?string('yyyy-MM-dd HH:mm:ss')}</p>
	</div>
</body>
</html>