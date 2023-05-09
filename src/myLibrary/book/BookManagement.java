package myLibrary.book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import myLibrary.DB.Database;

class BookManagement implements BookDao {
	private String url = "jdbc:mysql://localhost:3306/mylibrary";
	private String name = "root";
	private String pass = "Kei_03259608";
	private Database db = new Database(url, name, pass);
	private Connection conn;

	/*
	 * 書籍の登録
	 */
	public void regist() {
		String sql = "insert into managebook (title,author,isbn,ndc) values (?,?,?,?)";
		Scanner scan = new Scanner(System.in);
		System.out.println("**** 書籍情報を登録してください ****");

		try {
			System.out.println("**** タイトルを入力してください ****");
			String title = scan.nextLine();

			System.out.println("**** 著者を入力してください ****");
			String author = scan.nextLine();

			System.out.println("**** ISBNを入力してください ****");
			String isbn = scan.nextLine();

			System.out.println("**** NBCを入力してください ****");
			String ndc = scan.nextLine();
			System.out.println("");

			Connection conn = null;

			conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, author);
			pstmt.setString(3, isbn);
			pstmt.setString(4, ndc);
			int insertSql = pstmt.executeUpdate();
			if (insertSql > 0) {
				System.out.println("**** データベースに保存しました ****");
				System.out.println("");
			}

		} catch (InputMismatchException e) {
			System.out.println("**** 入力値エラー ****");
			System.out.println("");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("**** データベース接続に失敗しました ****");
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
	}

	/*
	 * 書籍の検索
	 */
	public void search() throws InputMismatchException {
		//何で検索するか
		int selectNumber;
		Scanner scan = new Scanner(System.in);
		System.out.println("**** 項目を選択して下さい ****");
		System.out.println("1 : タイトル検索　　2 : 著者検索");

		try {
			selectNumber = scan.nextInt();
			scan.nextLine();

			if (selectNumber == 1) {
				//タイトル検索
				String sql = "select * from managebook where title like ?";
				System.out.println("**** 検索キーワードを入力して下さい ****");
				String titleWord = "%" + scan.nextLine() + "%";

				conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, titleWord);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					System.out.println("------------------------------------------------");
					System.out.println("タイトル : " + rs.getString("title"));
					System.out.println("著者 : " + rs.getString("author"));
					System.out.println("ISBN : " + rs.getString("isbn"));
					System.out.println("NBC : " + rs.getString("ndc"));
					System.out.println("------------------------------------------------");
					System.out.println("");
				}

			} else if (selectNumber == 2) {
				//著者検索
				String sql = "select * from managebook where author like ?";
				System.out.println("検索キーワードを入力して下さい。著者名の一部でも検索できます。");
				String authorWord = "%" + scan.nextLine() + "%";

				conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, authorWord);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					System.out.println("------------------------------------------------");
					System.out.println("タイトル : " + rs.getString("title"));
					System.out.println("著者 : " + rs.getString("author"));
					System.out.println("ISBN : " + rs.getString("isbn"));
					System.out.println("NDC : " + rs.getString("ndc"));
					System.out.println("------------------------------------------------");
				}

			} else {
				//入力値エラー
				throw new InputMismatchException("入力値が違います。半角数値で入力して下さい");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("**** データベースに接続できませんでした ****");
			System.out.println("");
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InputMismatchException e) {
			System.out.println("入力値エラー");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	public Book search(String title) throws NullPointerException {
		Book book = null;
		String sql = "select * from managebook where title = ?";

		try {
			conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, title);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				System.out.println("------------------------------------------------");
				System.out.println("タイトル : " + rs.getString("title"));
				System.out.println("著者 : " + rs.getString("author"));
				System.out.println("ISBN : " + rs.getString("isbn"));
				System.out.println("NBC : " + rs.getString("ndc"));
				System.out.println("------------------------------------------------");

				book = new Book();
				book.setId(rs.getInt("id"));
				book.setTitle(rs.getString("title"));
				book.setAuthor(rs.getString("author"));
				book.setIsbn(rs.getString("isbn"));
				book.setNdc(rs.getString("ndc"));
				book.setLoaning(rs.getBoolean("loaning"));
			} else {
				System.out.println("本が見つかりませんでした");
				throw new NullPointerException();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("データベースに接続できませんでした");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return book;

	}
}
