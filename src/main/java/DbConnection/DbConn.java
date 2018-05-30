package DbConnection;

import java.sql.Connection;
import java.sql.DriverManager;

public  class DbConn {
	private static Connection fser,sser;
	
	public static Connection ondser() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			fser=DriverManager.getConnection("jdbc:sqlserver://192.168.84.90;user=sa;password=Karvy@123;database=management");
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return fser;
	}
	
	public static Connection nser() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			sser=DriverManager.getConnection("jdbc:sqlserver://192.168.84.98;user=sa;password=Karvy@123;database=PAN");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sser;
	}
	
}
