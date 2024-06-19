package org.ladysnake.sincereloyalty;

public interface ExtendedEnchantmentsComponent {
    default boolean impaled$hasRiptide() { return false; }
    default String impaled$trueOwnerName() { return ""; }

    default void impaled$setRiptide(boolean riptide) {}
    default void impaled$setTrueOwner(String owner) {}
}
