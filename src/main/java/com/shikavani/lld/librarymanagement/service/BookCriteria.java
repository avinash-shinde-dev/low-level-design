package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.Genre;
import com.shikavani.lld.librarymanagement.models.Author;
import com.shikavani.lld.librarymanagement.models.Book;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class BookCriteria {

    private BookCriteria(){}

    public static Predicate<Book> hasTitle(String title){
        Objects.requireNonNull(title, "Title must not be null");
        return book -> book.getTitle().toLowerCase().contains(title.toLowerCase());
    }

    public static Predicate<Book> hasIsbn(String isbn){
        Objects.requireNonNull(isbn, "ISBN must not be null");
        return book -> isbn.equals(book.getIsbn());
    }

    public static Predicate<Book> inGenre(Genre genre){
        Objects.requireNonNull(genre, "Genre must not be null");
        return book -> book.getGenres().contains(genre);
    }

    // Case Insensitive partial matching
    public static Predicate<Book> writtenBy(Author author){
        Objects.requireNonNull(author, "Author must not be null");
        return book ->  book.getAuthors()
                .stream()
                .map(Author::name)
                .anyMatch(name -> name.toLowerCase().contains(author.name()));

    }

}
