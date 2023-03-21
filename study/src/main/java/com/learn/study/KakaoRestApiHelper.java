package com.learn.study;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoRestApiHelper {

    public enum HttpMethodType { POST, GET, DELETE }

    private static final String API_SERVER_HOST  = "https://dapi.kakao.com";

    private static final String BLOG_SEARCH = "/v2/search/blog";

    private static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();

    private String accessToken;
    
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String searchBlog(final Map<String, String> params) {
        return request(HttpMethodType.GET, BLOG_SEARCH, mapToParams(params));
    }

    public String request(final String apiPath) {
        return request(HttpMethodType.GET, apiPath, null);
    }

    public String request(final HttpMethodType httpMethod, final String apiPath) {
        return request(httpMethod, apiPath, null);
    }

    public String request(HttpMethodType httpMethod, final String apiPath, final String params) {
        String requestUrl = API_SERVER_HOST + apiPath + "?";
        if (httpMethod == null) {
            httpMethod = HttpMethodType.GET;
        }
        if (params != null && params.length() > 0
                && (httpMethod == HttpMethodType.GET || httpMethod == HttpMethodType.DELETE)) {
            requestUrl += params;
        }

        HttpsURLConnection conn;
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        InputStreamReader isr = null;

        try {
            final URL url = new URL(requestUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(httpMethod.toString());
            conn.setRequestProperty("Authorization", "KakaoAK " + this.accessToken);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");

            if (params != null && params.length() > 0 && httpMethod == HttpMethodType.POST) {
                conn.setDoOutput(true);
                writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(params);
                writer.flush();
            }

            final int responseCode = conn.getResponseCode();
            //System.out.println(String.format("\nSending '%s' request to URL : %s", httpMethod, requestUrl));
            //System.out.println("Response Code : " + responseCode);
            if (responseCode == 200)
                isr = new InputStreamReader(conn.getInputStream());
            else
                isr = new InputStreamReader(conn.getErrorStream());

            reader = new BufferedReader(isr);
            final StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            //System.out.println(buffer.toString());
            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) try { writer.close(); } catch (Exception ignore) { }
            if (reader != null) try { reader.close(); } catch (Exception ignore) { }
            if (isr != null) try { isr.close(); } catch (Exception ignore) { }
        }
        return null;
    }

    public String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public String mapToParams(Map<String, String > map) {
        StringBuilder paramBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            paramBuilder.append(paramBuilder.length() > 0 ? "&" : "");
            paramBuilder.append(String.format("%s=%s", urlEncodeUTF8(key),
                    urlEncodeUTF8(map.get(key).toString())));
        }
        return paramBuilder.toString();
    }

    public String mapToJsonStr(Map<String, String > map) throws JsonProcessingException {
        return JACKSON_OBJECT_MAPPER.writeValueAsString(map);
    }
    
    //숫자여부 확인
    public boolean isInteger(String strValue) {
        try {
          Integer.parseInt(strValue);
          return true;
        } catch (NumberFormatException ex) {
          return false;
        }
	}
}