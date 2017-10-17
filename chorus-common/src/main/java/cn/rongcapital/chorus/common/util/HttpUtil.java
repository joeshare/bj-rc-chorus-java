package cn.rongcapital.chorus.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import cn.rongcapital.chorus.common.constant.HttpMethodType;
import lombok.extern.slf4j.Slf4j;
import nl.bitwalker.useragentutils.UserAgent;

/**
 * Http相关工具类，提供HttpServletRequest解析的一些工具方法。
 *
 * @author li.hzh
 * @date 2016-03-08 15:49
 */
@Slf4j
public class HttpUtil {

	/**
	 * 从{@link HttpServletRequest}中提取IP信息
	 *
	 * @param request
	 *            请求
	 * @return IP地址
	 */
	public static String getIpAgentAddreess(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		String ipAgentAddress = null;
		ipAgentAddress = request.getHeader("x-forwarded-for");
		if (ipAgentAddress == null || ipAgentAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAgentAddress)) {
			ipAgentAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAgentAddress == null || ipAgentAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAgentAddress)) {
			ipAgentAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAgentAddress == null || ipAgentAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAgentAddress)) {
			ipAgentAddress = "";
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAgentAddress != null && ipAgentAddress.length() > 15) { // "***.***.***.***".length() = 15
			if (ipAgentAddress.indexOf(",") > 0) {
				ipAgentAddress = ipAgentAddress.substring(0, ipAgentAddress.indexOf(","));
			}
		}
		return ipAgentAddress;

	}

	/**
	 * 获取{@link UserAgent}信息
	 *
	 * @param request
	 *            请求
	 * @return {@link UserAgent}
	 */
	public static UserAgent getUserAgent(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
	}

	/**
	 * 发送请求
	 * 
	 * @param url
	 * @return
	 */
	public static HttpResponse request(String url) {
		HttpResponse httpResponse = null;
		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
			HttpGet httpGet = new HttpGet(url);

			httpResponse = (HttpResponse) closeableHttpClient.execute(httpGet);
			closeableHttpClient.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return httpResponse;
	}

	/**
	 * 发送请求Post
	 * 
	 * @param url
	 * @return
	 */
	public static HttpResponse doPostJson(String url, String body) {
		HttpResponse httpResponse = null;
		try {
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
			HttpPost httpPost = new HttpPost(url);
			StringEntity params = new StringEntity(body);
			httpPost.addHeader("content-type", "application/json");
			httpPost.setEntity(params);

			// 发送请求
			ResponseHandler<String> handler = new BasicResponseHandler();

//			String ss = closeableHttpClient.execute(httpPost, handler);
			httpResponse = (HttpResponse) closeableHttpClient.execute(httpPost);

			closeableHttpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return httpResponse;
	}

    public static String doPostAmbariWithAuthorization(String p_url, HttpMethodType requestType, String payLoad,String user, String pass) {
        URL url = null;
        try {
            url = new URL(p_url);
        } catch (MalformedURLException exception) {
            log.error("无效URL", exception);
        }

        HttpURLConnection httpURLConnection = null;
        OutputStreamWriter out = null;
        StringBuffer jsonString = new StringBuffer();

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("X-Requested-By", "ambari");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(requestType.type);

            String userpass = user + ":" + pass;
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
            httpURLConnection.setRequestProperty ("Authorization", basicAuth);

            if (payLoad != null) {
                out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(payLoad);
                out.close();
                out = null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                    jsonString.append(line);
            }

        } catch (IOException exception) {
            log.error("请求异常",exception);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException exception) {
                    log.error("doPostAmbariWithAuthorization method error :", exception);
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return jsonString.toString();
    }

	public static void main(String[] args) {
		doPostJson("http://localhost:8080/quartzApi/start", "{\"token\":\"1\"}");
	}
}
