import javax.swing.plaf.TableHeaderUI;

public class Main {

    public static void main(String[] args) {
        Ticket t = new Ticket();
        Thread sellerA = new Thread(t::sell);
        sellerA.setName("sellerA");
        Thread sellerB = new Thread(t::sell);
        sellerB.setName("sellerB");
        Thread sellerC = new Thread(t::sell);
        sellerC.setName("sellerC");
        sellerA.start();
        sellerB.start();
        sellerC.start();
    }
}
