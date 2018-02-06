import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
/*
Отправляет запросы агентам
 */

public class Helper {
	public static String sendRequestAndGetResponse(Agent agent, String request) {
		BufferedReader in = null;
		PrintWriter out = null;
		Socket sendSocket = null;
		String response = "";

		try {
			sendSocket = new Socket(agent.getIP(), agent.getPort());

			out = new PrintWriter(sendSocket.getOutputStream(), true);

			InputStreamReader isr = new InputStreamReader(sendSocket.getInputStream());
			in = new BufferedReader(isr);

			// sending request
			out.println(request);

			// getting response
			String line = in.readLine();
			while (line != null) {
				response += line;
				response += "\n";
				line = in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// closing everything
			try {
				out.close();
				in.close();
				sendSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
