package ioTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentIOTest {
	public static void main(String[] args) {
		String driverName = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/java";
		String user = "root";
		String password = "12345678";
		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet re = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection(url, user, password);
			psmt = con.prepareStatement("select * from student");
			re = psmt.executeQuery();
			fw = new FileWriter("E:\\IOTest\\student.txt");
			bw = new BufferedWriter(fw);		
			while(re.next()) {
				bw.write("sid:"+re.getString("sid") + " ");
				bw.write("password:"+re.getString("password") + " ");
				bw.write("name:"+re.getString("name") + " ");
				bw.write("sex:"+re.getString("sex") + " ");
				bw.write("dateOfBirth:"+re.getString("dateOfBirth") + " ");
				bw.write("college:"+re.getString("college") + " ");
				bw.write("major:"+re.getString("major") + " ");
				bw.write("stuClass:"+re.getString("stuClass") + " ");
				bw.newLine();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
//			bwœ»πÿ
			if(bw !=null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(re != null) {
				try {
					re.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(psmt != null) {
				try {
					psmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
