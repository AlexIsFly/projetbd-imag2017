package ui;

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

    @Override
    public int getRowCount() {
        return numrows;
    }

    @Override
    public int getColumnCount() {
        return numcols;
    }

    @Override
    public String getColumnName(int columnIndex) {
        try {
            return metadata.getColumnLabel(columnIndex+1);
        } catch (SQLException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 1 : return String.class;
            case 2 : return int.class;
            default: return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
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

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

}