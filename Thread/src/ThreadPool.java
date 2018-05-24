import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 30, 30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(false);
                t.setUncaughtExceptionHandler((thread, e) -> System.out.println(e.getMessage()));
                return t;
            }
        }, new ThreadPoolExecutor.DiscardOldestPolicy());

        Ticket t = new Ticket();
//        threadPoolExecutor.execute(t::sell);
//        threadPoolExecutor.submit(() -> System.out.println(Thread.currentThread().getName()));
        for(int i = 0;i < 5; i++){
            threadPoolExecutor.execute(t::sell);
//            threadPoolExecutor.submit(t::sell);
        }

        threadPoolExecutor.shutdown();


    }

}
