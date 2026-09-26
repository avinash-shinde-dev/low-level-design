package com.shikavani.lld.librarymanagement.models;

import java.util.List;

public record Publisher(String id, String name, List<Book> publishedBooks) { }
