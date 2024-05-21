import javax.swing.SwingUtilities;

import controller.EmailClientGUI;

public class EmailClientApplication {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> new EmailClientGUI());
    }
}
