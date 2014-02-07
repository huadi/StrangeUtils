package huadi.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavaSendHttpRequest {
	public static String sendHttpRequest() throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) new URL(
				"http://www.google.com").openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		String params = "q=google";
		OutputStream os = urlConnection.getOutputStream();
		os.write(params.toString().getBytes());
		os.flush();
		os.close();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));
		String responseResult = br.readLine();
		br.close();
		return responseResult;
	}
}
