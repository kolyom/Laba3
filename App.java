import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Рисование");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.add(new line());
        frame.setVisible(true);
    }
}
