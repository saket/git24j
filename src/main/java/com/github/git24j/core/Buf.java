package com.github.git24j.core;

import java.util.Optional;

public class Buf {
    private String ptr;
    private int asize;
    private int size;

    /** Get internal buffer, generally only the substr up to size is meaningful. */
    String getPtr() {
        return ptr;
    }

    public void setPtr(String ptr) {
        this.ptr = ptr;
    }

    public int getAsize() {
        return asize;
    }

    public void setAsize(int asize) {
        this.asize = asize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Optional<String> getString() {
        if (ptr == null || size == 0) {
            return Optional.empty();
        }
        return Optional.of(ptr.substring(0, size));
    }

    @Override
    public String toString() {
        return getString().orElse("");
    }
}
