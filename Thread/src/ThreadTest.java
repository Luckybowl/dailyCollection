public class ThreadTest{
    public static void main(String[] args){
        PrimeThread thread = new PrimeThread();
        thread.setName("runable");
        thread.start();
        Runnable runnable =() ->System.out.println(Thread.currentThread().getName() + " runnable run .");
        Thread t = new Thread(runnable);
        t.start();
        System.out.println(Thread.currentThread().getName());

    }

    static class PrimeThread extends Thread{
        public void run(){
            System.out.println(Thread.currentThread().getName() + " Thread run .");
        }
    }
}