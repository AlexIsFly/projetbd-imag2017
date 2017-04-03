import ui.SportTable;

import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

/**
 * Created by gacela on 3/28/17.
 */
public class SportQuery extends JFrame implements RowSetListener {


    static final String PRE_STMT1 =
            "select * from sport";
    Connection connection;
    ResultSet rset;

    public SportQuery(ConnectionBD connectionBD) {

        super("Sport Table");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                try {
                    rset.close();
                    System.out.println("closing result set");
                    connection.close();
                    System.out.println("closing connection");
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
                System.exit(0);
            }
        });

        try {
            connection = connectionBD.getConnection();
            // Enregistrement du driver Oracle

            rset = getContentsSport();
            SportTable sportTable = new SportTable(rset);
            JTable table = new JTable();
            table.setModel(sportTable);

            JLabel label_name = new JLabel();
            JLabel label_price = new JLabel();


            while (rset.next ()) {
                System.out.println (
                        " Nom Sport : " + rset.getString ( "nomSport" )
                                + " -> Tarif Stage : " + rset.getString( "tarifStage" )
                );
            }

            Container contentPane = getContentPane();
            contentPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            contentPane.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.CENTER;
            c.weightx = 0.5;
            c.weighty = 1.0;
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;
            contentPane.add(new JScrollPane(table), c);

            // Fermeture



        } catch (SQLException e) {
            System.err.println("failed");
            e.printStackTrace(System.err);
        }
    }

    public ResultSet getContentsSport() throws SQLException {

        PreparedStatement stmt = connection.prepareStatement(PRE_STMT1,
                ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        //PreparedStatement stmt = conn.prepareStatement(PRE_STMT1);

        // Execution de la requete

        ResultSet rset = stmt.executeQuery();
        return rset;
    }

    @Override
    public void rowSetChanged(RowSetEvent event) {

    }

    @Override
    public void rowChanged(RowSetEvent event) {

    }

    @Override
    public void cursorMoved(RowSetEvent event) {

    }
}
