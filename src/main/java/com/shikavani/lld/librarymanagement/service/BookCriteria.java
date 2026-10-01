package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.Genre;
import com.shikavani.lld.librarymanagement.models.Author;
import com.shikavani.lld.librarymanagement.models.Book;

import java.util.Objects;
import java.util.function.Predicate;

public final class BookCriteria {

    private BookCriteria(){}

    public static Predicate<Book> hasTitle(String title){
        Objects.requireNonNull(title, "Title must not be null");
        return book -> title.equalsIgnoreCase(book.getTitle());
    }

    public static Predicate<Book> hasIsbn(String isbn){
        Objects.requireNonNull(isbn, "ISBN must not be null");
        return book -> isbn.equals(book.getIsbn());
    }

    public static Predicate<Book> inGenre(Genre genre){
        Objects.requireNonNull(genre, "Genre must not be null");
        return book -> book.getGenres().contains(genre);
    }

    public static Predicate<Book> writtenBy(Author author){
        Objects.requireNonNull(author, "Author must not be null");
        return book -> book.getAuthors().contains(author);
    }

}
