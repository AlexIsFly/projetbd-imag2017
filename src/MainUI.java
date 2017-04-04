import javax.swing.*;
import java.awt.*;

/**
 * Created by gacela on 4/3/17.
 */
public class MainUI {

    public static void main(String[] args) {
        JFrame f = new JFrame("Application a bout de souffle");

        f.setSize(800, 500);

        JTabbedPane onglets = new JTabbedPane(SwingConstants.TOP);

        JPanel pannel = new JPanel();

        JPanel onglet1 = new JPanel();
        onglet1.setPreferredSize(new Dimension(800, 500));
        onglets.addTab("Rajout Stage", onglet1);
        onglet1.add(new RajoutStageUI());

        JPanel onglet2 = new JPanel();
        onglet1.setPreferredSize(new Dimension(800, 500));
        onglets.addTab("Inscription Stage", onglet2);

        JPanel onglet3 = new JPanel();
        onglet3.setPreferredSize(new Dimension(800, 500));
        onglets.addTab("Statistiques", onglet3);
        onglet3.add(new Statistiques());
        
        pannel.add(onglets);
        f.getContentPane().add(pannel);
        f.setVisible(true);


    }
}
