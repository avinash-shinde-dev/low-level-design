package com.shikavani.lld.librarymanagement.models;

import java.util.List;
import java.util.Objects;

public record Author(String id, String name, List<Book> books)  { }
