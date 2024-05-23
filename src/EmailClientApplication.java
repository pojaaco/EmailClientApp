import javax.swing.SwingUtilities;

import view.LoginView;

public class EmailClientApplication {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
