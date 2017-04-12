import javax.swing.*;
import java.awt.*;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * Created by gacela on 4/3/17.
 */
public class MainUI {

    public static void main(String[] args) {
        JFrame f = new JFrame("Application a bout de souffle");
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        ConnectionBD connectionBD = new ConnectionBD();

        f.setSize(1000, 800);

        JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);

        JPanel pannel = new JPanel();

        JPanel onglet1 = new JPanel();
        onglet1.setPreferredSize(new Dimension(1000, 600));
        onglets.addTab("Rajout Stage", onglet1);
        onglet1.add(new RajoutStageUI(connectionBD));

        JPanel onglet2 = new JPanel();
        onglet2.setPreferredSize(new Dimension(1000, 600));
        onglets.addTab("Inscription Stage", onglet2);
        onglet2.add(new InscriptionStageUI(connectionBD));

        JPanel onglet3 = new JPanel();
        onglet3.setPreferredSize(new Dimension(1000, 600));
        onglets.addTab("Statistiques", onglet3);
        onglet3.add(new Statistiques(connectionBD));
        
        pannel.add(onglets);
        f.getContentPane().add(pannel);
        f.setVisible(true);

    }
}
