import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
/*
Поток для обработки входящих запросов и передачи их на сервер.
 */

class Client implements Runnable {
	static String emptyResponse = "empty response";
	Socket clientSocket = null;
	AgentsLogic agentsLogic;

	Client(Socket socket, AgentsLogic agentsLogic) {
		this.agentsLogic = agentsLogic;
		clientSocket = socket;
	}

	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;

		try {
			InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
			in = new BufferedReader(isr);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			String response = retrieveResponse(in.readLine());
			if (!"empty response".equals(response))
				out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String retrieveResponse(String command) {
		String ret = emptyResponse;

		if ("SYN".equals(command)) {
			agentsLogic.syn();
		} else if ("CLK".equals(command)) {
			ret = String.valueOf(agentsLogic.clk());
		} else if ("NET".equals(command)) {
			ret = agentsLogic.net();
		} else {
			agentsLogic.registerNewAgent(command);
		}

		return ret;
	}

}