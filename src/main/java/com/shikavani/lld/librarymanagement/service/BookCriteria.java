package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.Genre;
import com.shikavani.lld.librarymanagement.models.Book;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Ready-made search conditions. Each one is a plain java Predicate, so they can be combined
 * WITHOUT changing any existing search code (this is how compound queries are supported):
 *
 *   BookCriteria.inGenre(Genre.SCI_FI).and(BookCriteria.writtenBy("asimov"))          // AND
 *   BookCriteria.titleContains("robot").or(BookCriteria.hasIsbn("123"))               // OR
 *   BookCriteria.inGenre(Genre.NOVEL).negate()                                        // NOT
 *
 * A new kind of search (e.g. by publisher) is just one more small method here.
 */
public final class BookCriteria {
    private BookCriteria() { }

    /** Case-insensitive, partial match. */
    public static Predicate<Book> titleContains(String text) {
        String needle = lower(text);
        return book -> lower(book.getTitle()).contains(needle);
    }

    /** Case-insensitive, partial match on any author. */
    public static Predicate<Book> writtenBy(String text) {
        String needle = lower(text);
        return book -> book.getAuthors().stream().anyMatch(a -> lower(a.name()).contains(needle));
    }

    /** Exact match. */
    public static Predicate<Book> hasIsbn(String isbn) {
        Objects.requireNonNull(isbn, "ISBN must not be null");
        return book -> isbn.equals(book.getIsbn());
    }

    /** Exact match. */
    public static Predicate<Book> inGenre(Genre genre) {
        Objects.requireNonNull(genre, "Genre must not be null");
        return book -> book.getGenres().contains(genre);
    }

    private static String lower(String s) {
        return Objects.requireNonNull(s).toLowerCase(Locale.ROOT);
    }
}
