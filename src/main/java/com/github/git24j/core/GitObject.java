package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;

/** Generic git object: {@code Commit}, {@code Tag}, {@code Tree} or {@code Blob} */
public class GitObject extends CAutoReleasable {
    // weak referenced git_object won't be free-ed in the finalizer

    protected GitObject(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }

    static native void jniFree(long objPtr);

    static native int jniType(long objPtr);

    static native void jniId(long objPtr, Oid oid);

    static native int jniShortId(Buf buf, long objPtr);

    static native int jniLookup(AtomicLong outObj, long repoPtr, Oid oid, int objType);

    static native int jniLookupPrefix(
            AtomicLong outObj, long repoPtr, Oid oid, int len, int objType);

    static native long jniOwner(long objPtr);

    static native int jniPeel(AtomicLong outObj, long objPtr, int objType);

    static native int jniDup(AtomicLong outObj, long objPtr);

    /**
     * Create Right object according to GitObject's type. We need this because c does not have
     * inheritance hierarchy.
     *
     * @throws IllegalStateException if object is invalid or has invalid memory address.
     */
    static GitObject create(long objPtr) {
        if (objPtr == 0) {
            throw new IllegalStateException("object address is NULL, has it been closed?");
        }
        return GitObject.create(objPtr, Type.valueOf(jniType(objPtr)));
    }

    static GitObject create(long objPtr, Type type) {
        switch (type) {
            case INVALID:
                throw new IllegalStateException("invalid git object");
            case COMMIT:
                return new Commit(false, objPtr);
            case BLOB:
                return new Blob(false, objPtr);
            case TAG:
                return new Tag(false, objPtr);
            case TREE:
                return new Tree(false, objPtr);
            default:
                return new GitObject(false, objPtr);
        }
    }

    /**
     * Lookup a reference to one of the objects in a repository.
     *
     * @param repository the repository to look up the object
     * @param oid the unique identifier for the object
     * @param type the type of the object
     * @return found object, throw error if lookup failed.
     * @throws GitException git errors
     * @throws IllegalStateException required objects are not open or have been closed.
     */
    @Nonnull
    public static GitObject lookup(
            @Nonnull Repository repository, @Nonnull Oid oid, @Nonnull Type type) {
        AtomicLong outObj = new AtomicLong();
        if (oid.isShortId()) {
            Error.throwIfNeeded(
                    jniLookupPrefix(
                            outObj,
                            repository.getRawPointer(),
                            oid,
                            oid.getEffectiveSize(),
                            type.value));
        } else {
            Error.throwIfNeeded(jniLookup(outObj, repository.getRawPointer(), oid, type.value));
        }
        return GitObject.create(outObj.get(), type);
    }

    /**
     * Lookup a reference to one of the objects in a repository, given a prefix of its identifier
     * (short id).
     *
     * @param repository the repository to look up the object
     * @param oid a short identifier for the object
     * @param len the length of the short identifier
     * @param type the type of the object
     * @return looked-up object
     * @deprecated in preference to {@code lookup} which already handles the short oid case.
     */
    public static GitObject lookupPrefix(Repository repository, Oid oid, int len, Type type) {
        AtomicLong outObj = new AtomicLong();
        Error.throwIfNeeded(
                jniLookupPrefix(outObj, repository.getRawPointer(), oid, len, type.value));
        return GitObject.create(outObj.get(), type);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    public Type type() {
        return Type.valueOf(jniType(_rawPtr.get()));
    }

    /**
     * Get the id (SHA1) of a repository object
     *
     * @return the SHA1 id
     */
    public Oid id() {
        Oid oid = new Oid();
        jniId(getRawPointer(), oid);
        return oid;
    }

    /**
     * Get a short abbreviated OID string for the object
     *
     * @return Buffer that short id string was written into.
     * @throws GitException git error.
     */
    public Buf shortId() {
        Buf buf = new Buf();
        Error.throwIfNeeded(jniShortId(buf, _rawPtr.get()));
        return buf;
    }

    /**
     * Recursively peel an object until an object of the specified type is met.
     *
     * @param targetType The {@link GitObject.Type} of the requested object.
     * @return Peeled GitObject (need to be closed to avoid resource leak).
     */
    public GitObject peel(Type targetType) {
        AtomicLong outPtr = new AtomicLong();
        Error.throwIfNeeded(jniPeel(outPtr, getRawPointer(), targetType.value));
        return new GitObject(false, outPtr.get());
    }

    /**
     * Create an copy of a Git object. The copy must be explicitly closed or it will leak.
     *
     * @return copy of the object.
     */
    public GitObject dup() {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniDup(out, getRawPointer()));
        return GitObject.create(out.get());
    }

    /**
     * Get the repository that owns this object. Calling close on the returned pointer will
     * invalidate the actual object.
     *
     * @return the repository who owns this object
     */
    public Repository owner() {
        return Repository.ofRaw(jniOwner(_rawPtr.get()));
    }

    public enum Type {
        ANY(-2),
        INVALID(-1),
        COMMIT(1),
        TREE(2),
        BLOB(3),
        TAG(4),
        OFS_DELTA(6),
        ERF_DELTA(7),
        ;
        private final int value;

        private Type(int value) {
            this.value = value;
        }

        static Type valueOf(int iVal) {
            for (Type x : Type.values()) {
                if (x.value == iVal) {
                    return x;
                }
            }
            return INVALID;
        }

        /** Get associated value. */
        public int getCode() {
            return value;
        }
    }
}
