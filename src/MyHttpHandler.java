import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
/*

 */

public class MyHttpHandler implements HttpHandler { //логика работы http сервера
	static String IPREQUEST = "IP";
	static String PORTREQUEST = "PORT";
	static String BUTTONNAME = "SYN";

	private static final String Spliter = "&";
	private List<AgentsLogic> agents;

	public MyHttpHandler(List<AgentsLogic> agents) {
		super();
		this.agents = new ArrayList<>(agents);
	}

	public void handle(HttpExchange http) throws IOException { // конкретный запрос

		URI uri = http.getRequestURI(); //получаем адрес
		// System.out.println("uri is " + uri.toString());
		String query = uri.getQuery(); // получаем запрос по адресу
		String response = createHTMLAndMakeReponse(query); //создаем страницу и посылаем ответ по запросу

		http.getResponseHeaders().set("Content-Type", "text/html;charset=utf-8"); //устанавливаем описание и кодировку страницы
		http.sendResponseHeaders(200, response.getBytes().length);
		OutputStream os = http.getResponseBody(); //получаем тело ответа
		os.write(response.getBytes()); //отправляем в поток ответ в виде страницы
		os.close();
	}

	public void makeReponse(String query) { //создаем ответ
		String request = null, IP = null, port = null;

		String[] params = query.split(Spliter); //записываем запрос в массив
		for (int i = 0; i < params.length; i += 1) { // проходимся по запросу
			String[] splitedValues = params[i].split("=");
			String first = splitedValues[0]; // разделям запрос первая часть
			String second = splitedValues[1]; //вторая часть запроса
			if ("request".equals(first)) { // проверяем если это запрос
				if (second.equals(null) || !second.equals("SYN")) { // проверяем тело запроса
					try {
						throw new Exception("Unknown Request");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else
					request = second;
			} else if (IPREQUEST.equals(first)) { //если это IP
				if (second.equals(null) || !second.equals("localhost")) { // проверяем тело запроса
					try {
						throw new Exception("Unsupported host");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else
					IP = second;
			} else if (PORTREQUEST.equals(first)) { //если это порт
				if (second.equals(null) || Integer.parseInt(second) <= 0) {  // проверяем тело запроса
					try {
						throw new Exception("Unsupported port");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					port = second;
				}
			}
		}
		Agent agentToSend = new Agent(IP, Integer.parseInt(port));

		if ("SYN".equals(request)) { // если запрос синхронизации
			 Helper.sendRequestAndGetResponse(agentToSend, request); //делаем запрос ответ для созданого агента по запросу
		} else {
			try {
				throw new Exception("Unknown request");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String createHTMLAndMakeReponse(String query) { //делаем ответ и возвращаем страницу
		if (query != null)
			makeReponse(query);

		return createHTML(agents);
	}

	public String createHTML(List<AgentsLogic> agents) { //создаем html страницу
		String ret = "";
		for (int i = 0; i < agents.size(); ++i) {
			// general info
			ret += "Agent number: " + i + " has properties(IP: " + agents.get(i).getIP();
			ret += ",Port: " + agents.get(i).getPort() + ") and timer equals " + agents.get(i).getCounter().time.get();

			// button for synchronization
			// add IP to response
			ret += "<a href=\"/sufix?request=SYN&" + IPREQUEST + "=" + agents.get(i).getIP();
			// add port to response
			ret += "&" + PORTREQUEST + "=" + agents.get(i).getPort()+ "\">";
			// add button and its name
			ret += "<button>" + BUTTONNAME + "</button></a>";
			for (int j = 0; j < 2; ++j)
				ret += "</br>";

		}
		return ret;
	}

}
