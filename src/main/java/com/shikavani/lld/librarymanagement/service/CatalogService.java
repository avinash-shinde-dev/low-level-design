package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.exception.BookNotFoundException;
import com.shikavani.lld.librarymanagement.models.Book;
import com.shikavani.lld.librarymanagement.repository.CatalogRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class CatalogService implements Searchable<Book> {
    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public Book searchById(String id){
        Objects.requireNonNull(id, "Book id must not be null");
        return catalogRepository.findById(id).orElseThrow(() -> new BookNotFoundException(String.format("Book: %s not present in the catalog", id)));
    }

    @Override
    public List<Book> search(Predicate<Book> criteria) {
        return this.catalogRepository.findAll()
                .stream()
                .filter(criteria)
                .toList();
    }

    @Override
    public Optional<Book> searchFirst(Predicate<Book> criteria) {
        return this.catalogRepository.findAll()
                .stream()
                .filter(criteria)
                .findFirst();
    }

    public void save(Book book){
        Objects.requireNonNull(book, "Book must not be null");
        this.catalogRepository.save(book);
    }

    public void delete(String bookId){
        Objects.requireNonNull(bookId, "id must not be null");
        Book book = this.catalogRepository.delete(bookId);
        if(book != null)
            System.out.printf("Book: %s has been removed from catalog", book.getTitle());
    }

}
