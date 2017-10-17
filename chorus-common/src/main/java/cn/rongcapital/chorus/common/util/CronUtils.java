package cn.rongcapital.chorus.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronSequenceGenerator;

import javax.management.RuntimeErrorException;
import java.util.Date;

/**
 * cronExpression生成
 *
 * @author lengyang
 *         <p>
 *         2016年11月22日
 */
public class CronUtils {
	private static Logger logger = LoggerFactory.getLogger(CronUtils.class);
	/**
	 * 对象转化成json字符串
	 *
	 * @param second
	 * @param minute
	 * @param hour
	 * @param day
	 * @param month
	 * @param week
	 * @return
	 * @throws Exception
	 */
	public static String createCron(String second, String minute, String hour, String day, String month, String week)
			throws RuntimeErrorException {

		// TODO:异常抛出
		StringBuffer cronBuffer = new StringBuffer();
		// second秒
		if (StringUtils.isNotBlank(second)) {
			cronBuffer.append(second);
		} else {
			cronBuffer.append("0");
		}
		cronBuffer.append(" ");
		// minute分
		if (StringUtils.isNotBlank(minute)) {
			cronBuffer.append(minute);
		} else {
			cronBuffer.append("0");
		}
		cronBuffer.append(" ");
		// hour小时
		if (StringUtils.isNotBlank(hour)) {
			cronBuffer.append(hour);
		} else {
			cronBuffer.append("0");
		}
		cronBuffer.append(" ");
		// day天
		if (StringUtils.isNotBlank(day)) {
			cronBuffer.append(day);
			week = "?";
		} else {
			cronBuffer.append("*");
		}
		cronBuffer.append(" ");
		// month月
		if (StringUtils.isNotBlank(month)) {
			cronBuffer.append(month);
		} else {
			cronBuffer.append("*");
		}
		cronBuffer.append(" ");
		// week周
		if (StringUtils.isNotBlank(week)) {
			cronBuffer.append(week);
		} else {
			cronBuffer.append("?");
		}

		// 表达式验证
		CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronBuffer.toString());
		return cronBuffer.toString();
	}
	public static boolean validateCronExpression(String cronExpression) {
		boolean result = false;
		if (StringUtils.isNotEmpty(cronExpression)) {
			try {
				CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
				result = true;
			} catch (Exception ex) {
				logger.error(ex.getMessage());
				result = false;
			}
		}
		return result;
	}

	/**
	 * 验证CRON表达式是否合法并且最小时间间隔设置正确
	 *
	 * @param cronExpression
	 * @param minInterval 最小时间间隔单位(秒)
	 * @return
	 */
	public static boolean validateCronExpressionInterval(String cronExpression, int minInterval) {
		boolean result = true;
		if (StringUtils.isNotEmpty(cronExpression)) {
			try {
				CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);

				Date currentDate = new Date();
				int i = 0;
				// 循环得到接下来n此的触发时间点，供验证
				while (i < 2) {
					// 下次执行时间取得
					Date nextDate = cronSequenceGenerator.next(currentDate);

					// 时间差计算
					if (i == 1) {
						long interval = nextDate.getTime() - currentDate.getTime();
						if (interval < (minInterval * 60 * 1000)) {
							result = false;
						}
					}
					currentDate = nextDate;
					++i;
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
				result = false;
			}
		}
		return result;
	}

//	public static void main(String[] args) {
//		// CronSequenceGenerator
//		String ss = "10 * * L * *";
//		ss = "0 */10 * * * ?";
//		// CronSequenceGenerator CronSequenceGenerator = new CronSequenceGenerator(ss);
//		boolean result = validateCronExpressionInterval(ss, 10);
//		System.out.println(result);
//	}
}
