package myLibrary.book;

public interface BookDao {
	void regist();

	void search();

	Book search(String title);
}
