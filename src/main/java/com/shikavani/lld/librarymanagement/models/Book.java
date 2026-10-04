package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.Genre;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/** A title in the shared catalog (all branches see the same Book). Physical items are BookCopy. */
public final class Book {
    private final String id = UUID.randomUUID().toString();
    private final BigDecimal price;                  // used by the fine cap
    private final Set<Author> authors;
    private final Set<Genre> genres;
    private volatile String title;
    private volatile String isbn;
    private volatile Publisher publisher;
    private volatile int publicationYear;

    public Book(String title, String isbn, Set<Author> authors, Set<Genre> genres,
                BigDecimal price, Publisher publisher, int publicationYear) {
        this.title = Objects.requireNonNull(title, "Title is required");
        this.isbn = Objects.requireNonNull(isbn, "ISBN is required");
        this.publisher = Objects.requireNonNull(publisher, "Publisher is required");
        this.price = Objects.requireNonNull(price, "Price is required");
        this.publicationYear = publicationYear;
        if (authors == null || authors.isEmpty()) throw new IllegalArgumentException("At least one author is required");
        if (genres == null || genres.isEmpty()) throw new IllegalArgumentException("At least one genre is required");
        // copy into our own thread-safe sets so callers can't change them behind our back
        this.authors = new CopyOnWriteArraySet<>(authors);
        this.genres = new CopyOnWriteArraySet<>(genres);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public Publisher getPublisher() { return publisher; }
    public int getPublicationYear() { return publicationYear; }
    public BigDecimal getPrice() { return price; }
    public Set<Author> getAuthors() { return Set.copyOf(authors); }
    public Set<Genre> getGenres() { return Set.copyOf(genres); }

    public void setTitle(String title) { this.title = Objects.requireNonNull(title); }
    public void setIsbn(String isbn) { this.isbn = Objects.requireNonNull(isbn); }
    public void setPublisher(Publisher publisher) { this.publisher = Objects.requireNonNull(publisher); }
    public void setPublicationYear(int year) { this.publicationYear = year; }
    public void addAuthor(Author author) { authors.add(Objects.requireNonNull(author)); }
    public void addGenre(Genre genre) { genres.add(Objects.requireNonNull(genre)); }

    // equality by id only: title/isbn can be edited, so they must not be part of equals/hashCode
    @Override public boolean equals(Object o) { return o instanceof Book b && id.equals(b.id); }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() { return title; }
}
