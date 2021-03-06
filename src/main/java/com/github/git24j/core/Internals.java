package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class Internals {
    @FunctionalInterface
    interface JFCallback {
        int accept(long ptr, float f);
    }

    @FunctionalInterface
    interface JJCallback {
        int accept(long ptr1, long ptr2);
    }

    @FunctionalInterface
    interface JCallback {
        int accept(long ptr);
    }

    @FunctionalInterface
    interface JJJCallback {
        int accept(long ptr1, long ptr2, long ptr3);
    }

    @FunctionalInterface
    interface BArrCallback {
        int accept(byte[] rawid);
    }

    @FunctionalInterface
    interface BArrBarrCallback {
        int accept(byte[] id1, byte[] id2);
    }

    @FunctionalInterface
    interface ISJJJCallback {
        int accept(int flag, String str, long ptr1, long ptr2, long ptr3);
    }

    @FunctionalInterface
    interface SIICallback {
        int accept(String s, int i1, int i2);
    }

    @FunctionalInterface
    interface SJCallback {
        int accept(String s, long ptr);
    }

    @FunctionalInterface
    interface ASICallback {
        int accept(AtomicLong out, String str, int i);
    }

    @FunctionalInterface
    interface ALSSCallback {
        int accept(AtomicLong outRemote, long repoPtr, String name, String url);
    }

    /** Class to hold */
    static class OidArray {
        private final List<Oid> _oids = new ArrayList<>();

        void add(byte[] oidRaw) {
            _oids.add(Oid.of(oidRaw));
        }

        public List<Oid> getOids() {
            return _oids;
        }
    }
}
