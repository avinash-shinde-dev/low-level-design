package com.shikavani.lld.librarymanagement.models;

import java.util.List;
import java.util.Objects;

public record Author(String id, String name, List<Book> books)  {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id) && this.name.equalsIgnoreCase(author.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
