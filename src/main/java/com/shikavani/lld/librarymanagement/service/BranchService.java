package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.exception.BookCopyNotAvailableException;
import com.shikavani.lld.librarymanagement.models.Book;
import com.shikavani.lld.librarymanagement.models.BookCopy;
import com.shikavani.lld.librarymanagement.models.Branch;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;
import com.shikavani.lld.librarymanagement.repository.BookCopyRepository;
import com.shikavani.lld.librarymanagement.repository.BranchRepository;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

public class BranchService {

    private final BranchRepository branchRepository;
    private final BookCopyRepository bookCopyRepository;
    private final CatalogService catalogService;
    private final HoldService holdService;
    private final LockRegistry lock = LockRegistry.getInstance();

    public BranchService(BranchRepository branchRepository, CatalogService catalogService, BookCopyRepository bookCopyRepository, MemberService memberService, HoldService holdService) {
        this.branchRepository = Objects.requireNonNull(branchRepository, "branch repository must not be null");
        this.bookCopyRepository = Objects.requireNonNull(bookCopyRepository, "Book copy repository must not be null");
        this.catalogService = Objects.requireNonNull(catalogService, "CatalogService must not be null");
        this.holdService = Objects.requireNonNull(holdService, "Hold Service must not be null");
    }

    public void addBookCopies(String branchId, String bookId, Integer count){
        Lock lockOnBook = lock.title(bookId);
        lockOnBook.lock();
        try {
            Book book = this.catalogService.searchById(bookId);
            List<BookCopy> copies = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                copies.add(new BookCopy(book, branchId));
            }
            // add it to repository
            for(BookCopy copy: copies){
                Lock lockOnCopy = lock.bookCopy(copy.getBookCopyId());
                lockOnCopy.lock();
                try {
                    bookCopyRepository.save(copy);
                    holdService.offerToQueueIfAny(copy);
                }finally {
                    lockOnCopy.unlock();
                }
            }
        } finally {
            lockOnBook.unlock();
        }

    }

    // What if librarian remove the book copy id which borrow service is issueing
    public void removeBookCopy(String bookCopyId){
        Lock lockOnCopy = lock.bookCopy(bookCopyId);
        lockOnCopy.lock();
        try{
            this.bookCopyRepository.delete(bookCopyId);
        }finally {
            lockOnCopy.unlock();
        }
    }

    public void saveCopy(BookCopy copy){
        this.bookCopyRepository.save(copy);
    }

    public BookCopy getBookCopy(final String id){
        return this.bookCopyRepository.findById(id).orElseThrow(() -> new BookCopyNotAvailableException("Book copy didn't find in"));
    }

    /**
     *
     * @param branchId
     * @return count of available book copies against the book in the branch
     */
    public Map<String, Long> countAvailableBookCopies(String branchId){
        return this.bookCopyRepository.findAll()
                .stream()
                .filter(bookCopy -> branchId.equals(bookCopy.getBranchId()))
                .collect(Collectors.groupingBy(bookCopy ->
                        bookCopy.getBook().getId(),
                        Collectors.counting()
                ));
    }

    public BookCopy getAnyAvailableBookCopyFromBranch(String branchId, Book book) {
        return this.bookCopyRepository.findAll()
                .stream()
                .filter(bookCopy -> branchId.equals(bookCopy.getBranchId()))
                .filter(bookCopy -> BookCopyStatus.AVAILABLE.equals(bookCopy.getStatus()))
                .filter(bookCopy -> book.equals(bookCopy.getBook()))
                .findFirst()
                .orElseThrow(() -> new BookCopyNotAvailableException("Book copy is not available"));
    }

    public Branch getBranch(String branchId){
        Objects.requireNonNull(branchId, "Branch Id must not be null");
        return this.branchRepository.findById(branchId).orElseThrow(() -> new NoSuchElementException("Branch does not exists"));
    }


}
