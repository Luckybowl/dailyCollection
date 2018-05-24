import java.util.concurrent.TimeUnit;

public class Ticket {
//    private int ticketId;
//
//    public Ticket(int ticketId) {
//        this.ticketId = ticketId;
//    }

    int tickets = 10;
    void sell(){
        while(tickets > 0){
            synchronized (Ticket.class) {
                /*****双重校验，只有票数大于0时，才会进行售票操作*******************/
                if (tickets > 0) {
                    System.out.println(Thread.currentThread().getName() + " sell ticket:" + tickets);
                    tickets--;
                }
            }
            try{
                TimeUnit.MILLISECONDS.sleep(50L);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + "sell out");
    }
}
