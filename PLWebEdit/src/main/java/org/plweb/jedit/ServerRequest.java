package org.plweb.jedit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

public class ServerRequest {

	private ProjectEnvironment env = ProjectEnvironment.getInstance();

	private boolean finished = false;

	public boolean isFinished() {
		return finished;
	}

	private void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	private String action;

	private Properties props;

	private String response;

	public ServerRequest(String action, Properties props) {
		this.action = action;
		this.props = props;
	}

	public void request() {
		try {
			StringBuffer data = new StringBuffer();

			data.append(URLEncoder.encode("action", "UTF-8").concat("=")
					.concat(URLEncoder.encode(action, "UTF-8")));

			for (Object key : props.keySet()) {
				data.append("&"
						.concat(URLEncoder.encode(key.toString(), "UTF-8"))
						.concat("=")
						.concat(URLEncoder.encode(props.get(key).toString(),
								"UTF-8")));
			}

			System.err.println(data.toString());

			// Send data
			URLConnection conn = new URL(env.getRequestUrl()).openConnection();

			conn.setDoOutput(true);
			Writer w = new OutputStreamWriter(conn.getOutputStream());
			w.write(data.toString());
			w.flush();
			w.close();

			// Get response
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				pw.println(line);
			}

			pw.flush();
			response = sw.getBuffer().toString();
			System.err.println(response);

			pw.close();
			sw.close();
			rd.close();

			setFinished(true);

		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			System.err.println("UnsupportedEncodingException");
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			System.err.println("MalformedURLException");
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("Network I/O Error");
		}
	}
}
