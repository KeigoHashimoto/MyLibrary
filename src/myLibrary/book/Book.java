package myLibrary.book;

public class Book {
	private String title;
	private String author;
	private String isbn;
	private String ndc;
	private int id;
	private boolean loaning;

	public void setLoaning(boolean loaning) {
		this.loaning = loaning;
	}

	public boolean getLoaning() {
		return loaning;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getNdc() {
		return ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}
}
