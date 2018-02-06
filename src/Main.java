import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
/*
Запускает сеть агентов
 */
public class Main {
    static String SUFIX = "/sufix";
    static List<AgentsLogic> logics;

    public static void main(String[] args) throws InterruptedException, IOException {
    	Agent agent1 = new Agent("localhost", 12345);
    	Agent agent2 = new Agent("localhost", 12346);
    	Agent agent3 = new Agent("localhost", 12347);
    	Agent agent4 = new Agent("localhost", 12348);
    	Agent agent5 = new Agent("localhost", 12349);
    	AgentsLogic logic1 = new AgentsLogic(agent1, new Time(new AtomicLong(0)), null);
    	AgentsLogic logic2 = new AgentsLogic(agent2, new Time(new AtomicLong(logic1.getCounter().time.get())), agent1);
    	AgentsLogic logic3 = new AgentsLogic(agent3, new Time(new AtomicLong(logic2.getCounter().time.get())), agent2);
    	AgentsLogic logic4 = new AgentsLogic(agent4, new Time(new AtomicLong(logic3.getCounter().time.get())), agent3);
    	AgentsLogic logic5 = new AgentsLogic(agent5, new Time(new AtomicLong(logic4.getCounter().time.get())), agent4);
    	
        logics = Arrays.asList(logic1, logic2, logic3, logic4, logic5);

        MyHttpServer httpServer = new MyHttpServer(SUFIX, new MyHttpHandler(logics), 54321);
        httpServer.start();
    }
}
