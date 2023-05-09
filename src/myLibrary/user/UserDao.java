package myLibrary.user;

public interface UserDao {
	void create();

	User search(String name);
}
