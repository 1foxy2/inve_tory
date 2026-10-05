package net.foxy.inve_tory.api;

public interface ExtendedContainer {
    default boolean inve_tory$isDisabled(int index) {
        return false;
    }
}
