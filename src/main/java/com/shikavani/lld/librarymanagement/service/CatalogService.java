package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.exception.BookInUseException;
import com.shikavani.lld.librarymanagement.exception.BookNotFoundException;
import com.shikavani.lld.librarymanagement.models.Book;
import com.shikavani.lld.librarymanagement.models.BookCopy;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;
import com.shikavani.lld.librarymanagement.repository.BookCopyRepository;
import com.shikavani.lld.librarymanagement.repository.CatalogRepository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * The shared catalog (same for every branch). Add / update / remove books here.
 * (Role checks are intentionally left out for now: in a real system only librarians would call these.)
 */
public class CatalogService {
    private final CatalogRepository catalogRepository;
    private final TransactionService transactionService;
    private final HoldService holdService;
    private final BookCopyRepository copyRepository;
    private final LockRegistry lock = LockRegistry.getInstance();

    public CatalogService(CatalogRepository catalogRepository, TransactionService transactionService,
                          HoldService holdService, BookCopyRepository copyRepository) {
        this.catalogRepository = catalogRepository;
        this.transactionService = transactionService;
        this.holdService = holdService;
        this.copyRepository = copyRepository;
    }

    public Book getBook(String bookId) {
        Objects.requireNonNull(bookId, "Book id must not be null");
        return catalogRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found in the catalog: " + bookId));
    }

    public List<Book> getAllBooks() {
        return catalogRepository.findAll();
    }

    /** Librarians are rare and this is not on the borrow path, so a simple 'synchronized' is fine here (keeps ISBNs unique). */
    public synchronized void addBook(Book book) {
        Objects.requireNonNull(book, "Book must not be null");
        requireIsbnNotUsedByAnotherBook(book);
        catalogRepository.save(book);
    }

    /** Edit the Book with its setters first (title, isbn...), then call this to store the change. */
    public synchronized void updateBook(Book book) {
        getBook(book.getId());                       // must already exist
        requireIsbnNotUsedByAnotherBook(book);
        catalogRepository.save(book);
    }

    /** Only allowed when nobody has the book out and nobody is waiting for it. Its leftover copies are removed too. */
    public void removeBook(String bookId) {
        Lock bookLock = lock.book(bookId);
        bookLock.lock();
        try {
            getBook(bookId);
            if (transactionService.hasActiveLoanForBook(bookId)) {
                throw new BookInUseException("Cannot remove the book: it is currently borrowed");
            }
            if (holdService.hasActiveHolds(bookId)) {
                throw new BookInUseException("Cannot remove the book: members are waiting for it");
            }
            for (BookCopy copy : copyRepository.findAll()) {
                if (!copy.getBook().getId().equals(bookId) || copy.getStatus() == BookCopyStatus.REMOVED) continue;
                Lock copyLock = lock.bookCopy(copy.getBookCopyId());
                copyLock.lock();
                try {
                    copy.changeStatus(BookCopyStatus.REMOVED);   // only AVAILABLE / LOST copies are left at this point
                } finally {
                    copyLock.unlock();
                }
            }
            catalogRepository.delete(bookId);
        } finally {
            bookLock.unlock();
        }
    }

    private void requireIsbnNotUsedByAnotherBook(Book book) {
        boolean clash = catalogRepository.findAll().stream()
                .anyMatch(other -> other.getIsbn().equals(book.getIsbn()) && !other.getId().equals(book.getId()));
        if (clash) throw new IllegalArgumentException("Another book already uses ISBN " + book.getIsbn());
    }
}
