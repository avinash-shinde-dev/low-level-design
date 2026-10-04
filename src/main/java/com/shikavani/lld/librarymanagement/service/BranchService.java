package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.exception.BookCopyNotAvailableException;
import com.shikavani.lld.librarymanagement.exception.BookCopyNotFoundException;
import com.shikavani.lld.librarymanagement.exception.BorrowException;
import com.shikavani.lld.librarymanagement.exception.BranchNotFoundException;
import com.shikavani.lld.librarymanagement.models.Book;
import com.shikavani.lld.librarymanagement.models.BookCopy;
import com.shikavani.lld.librarymanagement.models.Branch;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;
import com.shikavani.lld.librarymanagement.repository.BookCopyRepository;
import com.shikavani.lld.librarymanagement.repository.BranchRepository;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Branches and their physical copies. The catalog (Book) is shared; copies belong to ONE branch.
 * A new branch is just one more row: nothing else needs to change.
 */
public class BranchService {
    private final BranchRepository branchRepository;
    private final CatalogService catalogService;
    private final BookCopyRepository bookCopyRepository;
    private final HoldService holdService;
    private final LockRegistry lock = LockRegistry.getInstance();

    public BranchService(BranchRepository branchRepository, CatalogService catalogService,
                         BookCopyRepository bookCopyRepository, HoldService holdService) {
        this.branchRepository = branchRepository;
        this.catalogService = catalogService;
        this.bookCopyRepository = bookCopyRepository;
        this.holdService = holdService;
    }

    public Branch addBranch(String name) {
        Branch branch = new Branch(UUID.randomUUID().toString(), Objects.requireNonNull(name));
        branchRepository.save(branch);
        return branch;
    }

    public Branch getBranch(String branchId) {
        Objects.requireNonNull(branchId, "Branch id must not be null");
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found: " + branchId));
    }

    /** Adds 'count' new copies at a branch. If members are waiting, the new copies go straight to them. */
    public List<BookCopy> addBookCopies(String branchId, String bookId, int count) {
        if (count <= 0) throw new IllegalArgumentException("count must be positive");
        getBranch(branchId);
        Book book = catalogService.getBook(bookId);

        List<BookCopy> added = new ArrayList<>();
        Lock bookLock = lock.book(bookId);
        bookLock.lock();
        try {
            for (int i = 0; i < count; i++) {
                BookCopy copy = new BookCopy(book, branchId);
                Lock copyLock = lock.bookCopy(copy.getBookCopyId());
                copyLock.lock();
                try {
                    bookCopyRepository.save(copy);
                    holdService.fulfillNextHold(copy);        // no-op when nobody is waiting
                } finally {
                    copyLock.unlock();
                }
                added.add(copy);
            }
        } finally {
            bookLock.unlock();
        }
        return added;
    }

    /** Removes a copy from the inventory. A borrowed (or reserved) copy cannot be removed. */
    public void removeBookCopy(String bookCopyId) {
        Lock copyLock = lock.bookCopy(bookCopyId);
        copyLock.lock();
        try {
            BookCopy copy = getBookCopy(bookCopyId);
            if (copy.getStatus() == BookCopyStatus.BORROWED) {
                throw new BorrowException("This copy is currently borrowed and cannot be removed");
            }
            copy.changeStatus(BookCopyStatus.REMOVED);        // also rejects other invalid cases, e.g. ON_HOLD
            bookCopyRepository.save(copy);
        } finally {
            copyLock.unlock();
        }
    }

    public void saveCopy(BookCopy copy) {
        bookCopyRepository.save(copy);
    }

    public BookCopy getBookCopy(String copyId) {
        return bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new BookCopyNotFoundException("Book copy not found: " + copyId));
    }

    /** For one branch: bookId -> number of copies on the shelf right now. */
    public Map<String, Long> countAvailableBookCopies(String branchId) {
        return bookCopyRepository.findAll().stream()
                .filter(c -> branchId.equals(c.getBranchId()) && c.getStatus() == BookCopyStatus.AVAILABLE)
                .collect(Collectors.groupingBy(c -> c.getBook().getId(), Collectors.counting()));
    }

    /** Any copy of this book on the shelf at this branch, or BookCopyNotAvailableException. */
    public BookCopy findAvailableCopy(String branchId, String bookId) {
        return bookCopyRepository.findAll().stream()
                .filter(c -> branchId.equals(c.getBranchId()))
                .filter(c -> c.getBook().getId().equals(bookId))
                .filter(c -> c.getStatus() == BookCopyStatus.AVAILABLE)
                .findFirst()
                .orElseThrow(() -> new BookCopyNotAvailableException("No copy of this book is available at this branch"));
    }
}
