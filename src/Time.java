import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

public class Time {

	Thread incrementingThread;
    AtomicLong time;   //создаем переменную времени которая поддерживает атомарную операцию.
    //  Атомарная операция — это операция, которую не может прервать планировщик потоков — если она начинается,
    // то продолжается до завершения, без возможности переключения контекста
    int timeToSleep = 1;
    public Time(AtomicLong value) {
        this.time = value;
        incrementingThread = null;
        start();
    }

    private void start() {
    	incrementingThread = new Thread(() -> { //созаем нить временного потока
            while (true){
                time.incrementAndGet(); //атомно увеличивает значение текущее значение
                try {
                    Thread.sleep(timeToSleep); //делаем задержку по времени
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    	incrementingThread.start(); //запускаем созданую нить
    }
}
