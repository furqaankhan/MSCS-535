package edu.mscs535.securedirectory.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SearchForm {

    @NotBlank(message = "Enter a search term.")
    @Size(min = 2, max = 50, message = "Search terms must contain 2 to 50 characters.")
    @Pattern(regexp = "[A-Za-z0-9 .'-]+", message = "Use letters, numbers, spaces, periods, apostrophes, or hyphens.")
    private String query = "";

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query == null ? null : query.strip();
    }
}
