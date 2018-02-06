import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
/*
Содержит всю логику агента. Он поддерживает команды syn, net, clk и logic для первого запуска агента.
 */
public class AgentsLogic {

	Agent agent;
	List<Agent> net;
	Time time;

	public AgentsLogic(Agent agent, Time counter, Agent parent) {
		net = new LinkedList<>();
		this.agent = agent;
		this.time = counter;

		startServer();

		if (parent != null) {
			net.add(parent);
			getNetFrom(parent);
		}

		tellAboutYourself();
		syn();
		sendSYNtoNetwork();
	}

	void getNetFrom(Agent toSend) {
		String toSendNetwork = Helper.sendRequestAndGetResponse(agent, "NET");
		if (toSendNetwork == null)
			return;

		String[] agents = toSendNetwork.split("\n");
		for (String ipPort : agents) {
			String[] splited = ipPort.split(" ");
			String ip = splited[0];
			int port = Integer.parseInt(splited[1]);
			Agent current = new Agent(ip, port);
			if (!this.net.contains(current))
				this.net.add(current);
		}

	}

	public void registerNewAgent(String info) {
		String[] splited = info.split(" ");
		String ip = splited[0];
		int port = Integer.parseInt(splited[1]);
		Agent newAgent = new Agent(ip, port);
		if (!net.contains(newAgent))
			net.add(newAgent);
	}

	public void tellAboutYourself() {
		net.forEach(agentToSend -> {
			String toSend = agent.getIP() + " " + agent.getPort();
			Helper.sendRequestAndGetResponse(agentToSend, toSend);
		});
			
	}

	public void sendSYNtoNetwork() {
		net.forEach(agentToSend -> Helper.sendRequestAndGetResponse(agentToSend, "SYN"));
	}

	public long clk() {
		return time.time.get();
	}

	public String net() {
		StringBuilder ret = new StringBuilder();
		net.forEach(agent -> {
			ret.append(agent.getIP());
			ret.append(" ");
			ret.append(agent.getPort());
			ret.append("\n");
		});
		return ret.toString();
	}

	public void syn() {
		long newVal = time.time.longValue();
		int count = 1;
		for (int i = 0; i < net.size(); ++i) {
			String gotTime = Helper.sendRequestAndGetResponse(net.get(i), "CLK");
			gotTime = gotTime.replaceAll("\n", "");
			newVal += Long.parseLong(gotTime);
			count++;
		}
		newVal /= count;
		this.time.time.set(newVal);
	}

	public void startServer() {
		Thread serverThread = new Thread(() -> {
			ServerSocket serverSocket = null;
			Socket clientSocket = null;
			try {
				serverSocket = new ServerSocket(agent.getPort());
				while (true) {
					clientSocket = serverSocket.accept();
					new Thread(new Client(clientSocket, this)).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		serverThread.start();
	}

	public String getIP() {
		return agent.getIP();
	}

	public int getPort() {
		return agent.getPort();
	}

	public Time getCounter() {
		return this.time;
	}

	public void setCounter(Time counter) {
		this.time = counter;
		sendSYNtoNetwork();
	}
}
