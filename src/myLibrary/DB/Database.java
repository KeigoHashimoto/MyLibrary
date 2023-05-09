package myLibrary.DB;

public class Database {
	private String url;
	private String name;
	private String pass;

	public Database(String url, String name, String pass) {
		this.url = url;
		this.name = name;
		this.pass = pass;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public String getPass() {
		return pass;
	}
}
