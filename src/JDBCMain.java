public class JDBCMain {
    
    public static void main(String[] args) {
        ConnectionBD connect = new ConnectionBD();
        SportFrame sportFrame = new SportFrame(connect);
        sportFrame.pack();
        sportFrame.setVisible(true);
    }
}
