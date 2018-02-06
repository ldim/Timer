import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
/*
Запускает http-сервер
 */

public class MyHttpServer {
	private HttpServer httpServer;

	public MyHttpServer(String suffix, HttpHandler handler, int port) {
		try {
			InetSocketAddress isa = new InetSocketAddress(port); //создаем хост с переданным портом
			httpServer = HttpServer.create(isa, 0); //создаем HttpServer по переданному хосту
			httpServer.createContext(suffix, handler); //создаем контекст страницы с названием suffix и передаем в нее обработчик запроса
			httpServer.setExecutor(null); //не назначаем исполнителя для сервера
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void start() {
		if (httpServer != null)
			httpServer.start(); //запускаем сервер
		else {
			try {
				throw new Exception("Server wasn't initialized");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
