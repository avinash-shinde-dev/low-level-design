package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.models.Book;
import com.shikavani.lld.librarymanagement.models.BookCopy;
import com.shikavani.lld.librarymanagement.repository.BookCopyRepository;

import java.util.List;
import java.util.function.Predicate;

/** Searches the shared catalog; can optionally narrow the result by branch and/or "available only". */
public class SearchService {
    private final CatalogService catalogService;
    private final BookCopyRepository copyRepository;

    public SearchService(CatalogService catalogService, BookCopyRepository copyRepository) {
        this.catalogService = catalogService;
        this.copyRepository = copyRepository;
    }

    public List<Book> search(Predicate<Book> criteria) {
        return search(criteria, null, false);
    }

    /**
     * @param branchId      null = whole network, otherwise only books that have a copy at this branch
     * @param availableOnly true = only books that have a copy on the shelf right now
     */
    public List<Book> search(Predicate<Book> criteria, String branchId, boolean availableOnly) {
        List<Book> matches = catalogService.getAllBooks().stream().filter(criteria).toList();
        if (branchId == null && !availableOnly) return matches;

        List<BookCopy> copies = copyRepository.findAll();
        return matches.stream()
                .filter(book -> copies.stream().anyMatch(c ->
                        c.getBook().equals(book)
                        && c.getStatus() != BookCopyStatus.REMOVED
                        && (branchId == null || branchId.equals(c.getBranchId()))
                        && (!availableOnly || c.getStatus() == BookCopyStatus.AVAILABLE)))
                .toList();
    }
}
