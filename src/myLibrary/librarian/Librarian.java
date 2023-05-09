package myLibrary.librarian;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

import myLibrary.DB.Database;
import myLibrary.book.Book;
import myLibrary.book.BookDao;
import myLibrary.book.BookFactory;
import myLibrary.user.User;
import myLibrary.user.UserDao;
import myLibrary.user.UserFactory;

class Librarian implements LibrarianDao {
	private User user;
	private Book book;
	private String url = "jdbc:mysql://localhost:3306/mylibrary";
	private String name = "root";
	private String pass = "Kei_03259608";
	private Database db = new Database(url, name, pass);
	//1 ユーザーはすでに登録済みかの確認
	//2 したい手続きの確認
	//3 借りたい本、返したい本のタイトルを確認
	//4 処理

	public void greet() {
		System.out.println("**** 図書貸し出しシステムへようこそ ****");
	}

	public void confirmUserRegistrationInfomation() {
		UserDao userDao = UserFactory.factory();
		int selectNumber = 0;
		boolean invaildInput = false;
		try {
			do {
				System.out.println("**** 登録情報を確認します ****");
				System.out.println("**** すでにユーザー登録済みですか？ ****");
				System.out.println("1:はい  2:いいえ");
				Scanner scan = new Scanner(System.in);

				selectNumber = scan.nextInt();
				scan.nextLine();
				if (selectNumber == 1) {
					System.out.println("**** 名前を入力してください ****");
					String userName = scan.nextLine();

					user = userDao.search(userName);

				} else if (selectNumber == 2) {
					System.out.println("**** ユーザー情報の登録します ****");
					userDao.create();
					invaildInput = false;
				} else {
					selectNumber = 2;
				}

			} while (selectNumber == 2 || invaildInput);
		} catch (InputMismatchException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 貸し出し
	 */
	public void lending() {
		BookDao bookDao = BookFactory.factory();
		Connection conn = null;
		Scanner scan = new Scanner(System.in);

		//ユーザー情報の確認
		confirmUserRegistrationInfomation();

		System.out.println("**** 貸し出す本のタイトルを入力して下さい ****");
		String title = scan.nextLine();

		//本をタイトルで検索し、見つかったらBookフィールドに代入
		try {
			this.book = bookDao.search(title);

			LocalDate current = LocalDate.now();
			LocalDate oneWeekLater = current.plus(1, ChronoUnit.WEEKS);

			if (book != null && book.getLoaning() != true) {
				String sql = "insert into managelending (user_id,book_id,untilReturn) values (?,?,?);";

				conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, this.user.getId());
				pstmt.setInt(2, this.book.getId());
				pstmt.setObject(3, oneWeekLater);
				int insertLength = pstmt.executeUpdate();
				sql = null;
				pstmt = null;

				if (insertLength > 0) {
					System.out.println("**** 貸し出し処理が完了しました ****");
					System.out.println("**** 貸し出し期限は" + oneWeekLater + "です ****");
					System.out.println("");
				}

				sql = "update managebook set loaning = true where id = ?;";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, this.book.getId());
				pstmt.executeUpdate();

			} else if (book.getLoaning() == true && book != null) {
				System.out.println("**** 貸し出し中です ****");
				System.out.println("");
			} else {
				System.out.println("**** 本が見つかりません ****");
				System.out.println("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InputMismatchException e) {
			e.printStackTrace();
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

	public void returnBook() throws NullPointerException {
		Scanner scan = new Scanner(System.in);
		Connection conn = null;
		BookDao bookDao = BookFactory.factory();

		//ユーザー情報の確認
		this.confirmUserRegistrationInfomation();

		//返却したい本の検索
		System.out.println("**** " + this.user.getName() + "が借りている本はこちらです ****");
		System.out.println(" ");
		String sql = "select * from managelending ml "
				+ "join manageuser u "
				+ "on u.id = ml.user_id "
				+ "join managebook b "
				+ "on b.id = ml.book_id "
				+ "where user_id = ?;";

		try {
			conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getId());
			ResultSet rs = pstmt.executeQuery();
			sql = null;
			pstmt = null;
			if (rs.next()) {
				do {
					System.out.println("------------------------------------------------");
					System.out.println("タイトル : " + rs.getString("title"));
					System.out.println("著者 : " + rs.getString("author"));
					System.out.println("ISBN : " + rs.getString("isbn"));
					System.out.println("NBC : " + rs.getString("ndc"));
					System.out.println("------------------------------------------------");
				} while (rs.next());

				//貸し出している本の数だけループするためのカウントを取得
				sql = "select count( book_id ) as cnt from managelending where user_id = ?;";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, this.user.getId());
				ResultSet resultCnt = pstmt.executeQuery();

				sql = null;
				pstmt = null;

				int lendingCnt = 0;
				if (resultCnt.next()) {
					lendingCnt = resultCnt.getInt("cnt");
				}

				System.out.println("**** 合計" + lendingCnt + "冊貸し出し中です ****");

				//ユーザーが借りている本の数だけ返却か終了の選択を行う
				for (int i = 0; i < lendingCnt; i++) {

					int seletcNumber = 0;
					System.out.println("**** 操作を選択して下さい ****");
					System.out.println("1:返却 2:終了");

					seletcNumber = scan.nextInt();
					scan.nextLine();

					if (seletcNumber == 1) {
						System.out.println("**** 返却する本のタイトルを入力して下さい ****");
						String returnTitle = scan.nextLine();
						if (returnTitle != null) {
							//返却したい本を検索

							book = bookDao.search(returnTitle);

							//本が見つかったら
							if (book != null) {
								sql = "delete from managelending where book_id = ?";

								pstmt = conn.prepareStatement(sql);
								pstmt.setInt(1, book.getId());
								int delete = pstmt.executeUpdate();
								if (delete > 0) {
									System.out.println("**** 「" + book.getTitle() + "」を返却しました ****");
									System.out.println("");
								}
								pstmt = null;
								sql = null;

								//貸し出しを解除
								sql = "update managebook set loaning = false where id = ?;";
								pstmt = conn.prepareStatement(sql);
								pstmt.setInt(1, book.getId());
								pstmt.executeUpdate();

								pstmt = null;

							} else {
								//本が見つからなかったら
								System.out.println("**** 本が見つかりませんでした ****");
								System.out.println("");
							}

						} else {
							System.out.println("**** タイトルが入力されませんでした ****");
						}
					} else {
						System.out.println("**** 終了します ****");
						System.out.println("");
					}
				}

			} else {
				System.out.println("**** 現在、借りている本はありません ****");
				System.out.println("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InputMismatchException e) {
			e.printStackTrace();
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

	public void overdue() {
		Date current = new Date();
		Connection conn = null;

		//貸し出し中の本とユーザーの情報を取得
		String sql = "select * from managelending ml "
				+ "join managebook mb on ml.book_id = mb.id "
				+ "join manageuser mu on ml.user_id = mu.id;";

		try {
			conn = DriverManager.getConnection(db.getUrl(), db.getName(), db.getPass());
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			System.out.println("**** 延滞している本 ****");
			System.out.println("");
			while (rs.next()) {
				//現在より貸し出し期限が前だったら
				if (rs.getDate("untilReturn").before(current)) {
					System.out.println("------------------------------------------------");
					System.out.println("タイトル :" + rs.getString("title") + " 期限：" + rs.getDate("untilReturn") + " "
							+ " 借りている人 :" + rs.getString("name"));
					System.out.println("------------------------------------------------");
					System.out.println("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
