package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.Genre;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class Book {
    private final String id;
    private String title;
    private String isbn;
    private final Set<Author> authors;
    private final Set<Genre> genres;
    private Publisher publisher;
    private String publicationYear;

    public Book(String title, String isbn, Set<Author> authors, Set<Genre> genres, Publisher publisher, String publicationYear) {
        this.id = UUID.randomUUID().toString();
        this.title = Objects.requireNonNull(title, "Title is required");
        this.isbn = Objects.requireNonNull(isbn, "ISBN is required");
        this.authors = Objects.requireNonNull(authors, "Author must not be null");
        this.genres = genres;
        this.publisher = Objects.requireNonNull(publisher, "Publisher details are required");
        this.publicationYear = Objects.requireNonNull(publicationYear, "Publication Year is required");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = Objects.requireNonNull(isbn);
    }

    public Set<Author> getAuthors() {
        return Set.copyOf(authors);
    }

    public void addAuthor(Author author){
        Objects.requireNonNull(author, "Author must not be null");
        this.authors.add(author);
    }

    public Set<Genre> getGenres() {
        return Set.copyOf(genres);
    }

    public void addGenre(Genre genre){
        Objects.requireNonNull(genre, "Genre must not be null");
        this.genres.add(genre);
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = Objects.requireNonNull(publisher);
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = Objects.requireNonNull(publicationYear);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, isbn);
    }
}
