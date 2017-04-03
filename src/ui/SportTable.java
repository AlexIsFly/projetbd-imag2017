package ui;

import javax.sql.RowSetListener;
import javax.sql.rowset.CachedRowSet;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by gacela on 3/28/17.
 */
public class SportTable implements TableModel {
    public ResultSet sportRowSet;
    public ResultSetMetaData metadata;
    public int numcols, numrows;

    public SportTable(ResultSet rowSetArg) throws SQLException {
        this.sportRowSet = rowSetArg;
        this.metadata = this.sportRowSet.getMetaData();
        this.numcols = metadata.getColumnCount();

        // Retrieve the number of rows.
        this.sportRowSet.beforeFirst();
        this.numrows = 0;
        while (this.sportRowSet.next()) {
            this.numrows++;
        }
        this.sportRowSet.beforeFirst();
    }

    public int getRowCount() {
        return numrows;
    }

    public int getColumnCount() {
        return numcols;
    }

    public String getColumnName(int columnIndex) {
        try {
            return metadata.getColumnLabel(columnIndex+1);
        } catch (SQLException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 1 : return String.class;
            case 2 : return int.class;
            default: return String.class;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            this.sportRowSet.absolute(rowIndex + 1);
            Object o = this.sportRowSet.getObject(columnIndex + 1);
            if (o == null)
                return null;
            else
                return o.toString();
        } catch (SQLException e) {
            return e.toString();
        }
    }


    public void setValueAt(Object value, int row, int column) {
        System.out.println("Calling setValueAt row " + row + ", column " + column);
    }

    
    public void addTableModelListener(TableModelListener l) {

    }

    public void removeTableModelListener(TableModelListener l) {

    }

}