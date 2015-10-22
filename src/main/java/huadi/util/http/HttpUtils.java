package huadi.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpUtils {

    private static CloseableHttpClient httpClient;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(20);
        cm.setDefaultMaxPerRoute(20);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000)
                .setConnectionRequestTimeout(2000).setMaxRedirects(1).build();
        httpClient = HttpClients.custom().setConnectionManager(cm)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(1, true)).setDefaultRequestConfig(requestConfig)
                .build();
    }

    public static String get(String url) {
        HttpGet httpGet = new HttpGet(url);

        return doRequest(httpGet);
    }

    public static String post(String url, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> p : params.entrySet()) {
            nvps.add(new BasicNameValuePair(p.getKey(), p.getValue()));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException ignored) {}

        return doRequest(httpPost);
    }

    private static String doRequest(HttpUriRequest request) {
        String responseStr = null;

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            responseStr = EntityUtils.toString(entity);
        } catch (IOException e) {
            System.out.println(("Exception on getting response."));
        }

        return responseStr;
    }

    public static void main(String[] args) {
        System.out.println(get("http://localhost:8000"));
    }
}
