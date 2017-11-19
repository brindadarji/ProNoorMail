package com.example.pronoormail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Application;
import android.util.Log;

public class Board_Controller extends Application {
	
	@SuppressWarnings("finally")
	public static String post(String endpoint, Map<String, String> params)
			throws IOException {
		String text = "";
		String response = "";
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}

		String body = bodyBuilder.toString();

		Log.e("Body ans url", "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			Log.e("URL", "> " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			// String test=out.toString();

			out.close();

			InputStream ipStream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					ipStream));
			while ((text = reader.readLine()) != null) {
				response += text;
			}
			ipStream.close();
			// handle the response
			int status = conn.getResponseCode();
			Log.e("Board_Controller", "POST DATA OUT MESSAGE " + response);
			Log.e("Board_Controller", "POST DATA Status=" + status);
			// If response is not success
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		}
		catch(Exception e)
		{
			
			Log.e("error",e.toString());
		}finally {
			if (conn != null) {
				conn.disconnect();
			}
			return response;
		}
	}

}
