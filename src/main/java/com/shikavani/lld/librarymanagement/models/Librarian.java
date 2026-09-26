package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.models.membership.Membership;
import com.shikavani.lld.librarymanagement.service.MemberService;
import com.shikavani.lld.librarymanagement.service.BranchService;
import com.shikavani.lld.librarymanagement.service.CatalogService;

// What operations librarian can perform?
// 1. crud book to catalog
public class Librarian {
    private final CatalogService catalogService;
    private final MemberService memberService;
    private final BranchService branchService;

    public Librarian(CatalogService catalogService, MemberService memberService, BranchService branchService) {
        this.catalogService = catalogService;
        this.memberService = memberService;
        this.branchService = branchService;
    }

    void addBook(Book book){
        this.catalogService.save(book);
    }

    void updateBook(Book book){
        this.catalogService.save(book);
    }

    void removeBook(String bookId){
        this.catalogService.delete(bookId);
    }

    void addBookCopy(String branchId, String bookId, Integer count){
        if(count <= 0 ) throw new IllegalArgumentException("count must be positive");
        this.branchService.addBookCopies(branchId, bookId, count);
    }

    void removeBookCopy(String bookCopyId){
        this.branchService.removeBookCopy(bookCopyId);
    }

    void register(Member member){
        this.memberService.register(member);
    }
    void suspend(String memberId){
        this.memberService.suspendMember(memberId);
    }

    void reactive(String memberId){
        this.memberService.reactivate(memberId);
    }

    void setMembership(String memberId, Membership membership){
        this.memberService.setMembership(memberId, membership);
    }
}
