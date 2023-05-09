package myLibrary.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import myLibrary.DB.Database;

class UserManagement implements UserDao {
	private String url = "jdbc:mysql://localhost:3306/mylibrary";
	private String name = "root";
	private String pass = "Kei_03259608";
	private Database db = new Database(url, name, pass);

	public void create() {
		String sql = "insert into manageuser (name,phonenumber,addres) values (?,?,?)";
		Scanner scan = new Scanner(System.in);
		System.out.println("**** ユーザー情報を登録してください ****");

		try {
			System.out.println("**** 名前を入力してください ****");
			String userName = scan.nextLine();

			System.out.println("**** 電話番号を入力してください ****");
			String userPhone = scan.nextLine();

			System.out.println("**** 住所を入力してください ****");
			String userAddres = scan.nextLine();
			System.out.println("");

			Connection conn = null;

			try {
				conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, userName);
				pstmt.setString(2, userPhone);
				pstmt.setString(3, userAddres);
				int insertSql = pstmt.executeUpdate();
				if (insertSql > 0) {
					System.out.println("**** データベースに保存しました ****");
					System.out.println("");
				}
			} catch (SQLException e) {
				System.out.println("**** データベース接続に失敗しました ****");
				System.out.println("");
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e2) {
					// TODO: handle exception
				}
			}
		} catch (InputMismatchException e) {
			System.out.println("**** 入力値エラー ****");
			System.out.println("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public User search(String name) throws NullPointerException {
		User user = new User();
		String sql = "select * from manageuser where name = ?";
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				user.setId(rs.getInt("id"));
				user.setName(rs.getString("name"));
				user.setPhoneNumber(rs.getString("phonenumber"));
				user.setAdress(rs.getString("addres"));
				System.out.println("**** ユーザー情報が確認できました ****");
				System.out.println("**** ユーザー情報はこちらです ****");
				System.out.println("名前 :" + rs.getString("name"));
				System.out.println("電話番号 : " + rs.getString("phonenumber"));
				System.out.println("住所 : " + rs.getString("addres"));
				System.out.println("");
			} else {
				System.out.println("**** ユーザーが見つかりませんんでした ****");
				System.out.println("");
			}

		} catch (SQLException e) {
			System.out.println("**** データベースに接続できませんでした ****");
			System.out.println("");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}

		return user;
	}
}
