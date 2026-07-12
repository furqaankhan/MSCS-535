package edu.mscs535.securedirectory.account;

public record UserAccount(String username, String passwordHash, boolean enabled) {
}
