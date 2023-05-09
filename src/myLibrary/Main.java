package myLibrary;

import java.util.Scanner;

import myLibrary.book.BookDao;
import myLibrary.book.BookFactory;
import myLibrary.librarian.LibrarianDao;
import myLibrary.librarian.LibrarianFactory;

public class Main {

	public static void main(String[] args) {
		LibrarianDao librarian = LibrarianFactory.factory();
		BookDao bookDao = BookFactory.factory();
		Scanner scan = new Scanner(System.in);
		librarian.greet();

		boolean end = false;

		while (end != true) {
			System.out.println("**** コマンドを選択して下さい ****");
			System.out.println("1:ユーザー情報 2:本を借りる 3:本を返す 4:本の検索 5:貸し出し期間超過リスト 6:終了");
			int selectNumber = scan.nextInt();

			switch (selectNumber) {
			case 1:
				librarian.confirmUserRegistrationInfomation();
				break;
			case 2:
				librarian.lending();
				break;
			case 3:
				librarian.returnBook();
				break;
			case 4:
				bookDao.search();
				break;
			case 5:
				librarian.overdue();
				break;
			default:
				System.out.println("終了します");
				end = true;
				break;
			}
		}
	}
}
