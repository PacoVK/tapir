package core.tapir;

import java.util.Set;

public record User(
    String name,
    String email,
    String givenName,
    String familyName,
    String preferredUsername,
    Set<String> roles
) {}
