package com.github.git24j.core;

public class WriteStream extends CAutoCloseable {
    WriteStream(long rawPointer) {
        super(rawPointer);
    }

    @Override
    protected void releaseOnce(long cPtr) {
        jniClose(cPtr);
        jniFree(cPtr);
    }

    static native int jniFree(long wsPtr);

    static native int jniClose(long wsPtr);

    /** TODO: figure out what's meaning of the return. */
    static native int jniWrite(long wsPtr, byte[] content);

    public int write(byte[] content) {
        return jniWrite(getRawPointer(), content);
    }
}
