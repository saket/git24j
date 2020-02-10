#include "j_patch.h"
#include "j_common.h"
#include "j_diff.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>

/** int git_patch_from_blobs(git_patch **out, const git_blob *old_blob, const char *old_as_path, const git_blob *new_blob, const char *new_as_path, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBlobs)(JNIEnv *env, jclass obj, jobject out, jlong oldBlobPtr, jstring old_as_path, jlong newBlobPtr, jstring new_as_path, jlong optsPtr)
{
    git_patch *c_out;
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    char *c_new_as_path = j_copy_of_jstring(env, new_as_path, true);
    int r = git_patch_from_blobs(&c_out, (git_blob *)oldBlobPtr, c_old_as_path, (git_blob *)newBlobPtr, c_new_as_path, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_patch_free(c_out);
    free(c_old_as_path);
    free(c_new_as_path);
    return r;
}

/** int git_patch_from_blob_and_buffer(git_patch **out, const git_blob *old_blob, const char *old_as_path, const void *buffer, size_t buffer_len, const char *buffer_as_path, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBlobAndBuffer)(JNIEnv *env, jclass obj, jobject out, jlong oldBlobPtr, jstring old_as_path, jbyteArray buffer, jint bufferLen, jstring buffer_as_path, jlong optsPtr)
{
    git_patch *c_out;
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    int buffer_len;
    unsigned char *c_buffer = j_unsigned_chars_from_java(env, buffer, &buffer_len);
    char *c_buffer_as_path = j_copy_of_jstring(env, buffer_as_path, true);
    int r = git_patch_from_blob_and_buffer(&c_out, (git_blob *)oldBlobPtr, c_old_as_path, (void *)c_buffer, bufferLen, c_buffer_as_path, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_patch_free(c_out);
    free(c_old_as_path);
    (*env)->ReleaseByteArrayElements(env, buffer, (jbyte *)c_buffer, 0);
    free(c_buffer_as_path);
    return r;
}

/** int git_patch_from_buffers(git_patch **out, const void *old_buffer, size_t old_len, const char *old_as_path, const void *new_buffer, size_t new_len, const char *new_as_path, const git_diff_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBuffers)(JNIEnv *env, jclass obj, jobject out, jbyteArray oldBuffer, jint oldLen, jstring old_as_path, jbyteArray newBuffer, jint newLen, jstring new_as_path, jlong optsPtr)
{
    git_patch *c_out;
    int old_buffer_len;
    unsigned char *c_old_buffer = j_unsigned_chars_from_java(env, oldBuffer, &old_buffer_len);
    char *c_old_as_path = j_copy_of_jstring(env, old_as_path, true);
    int new_buffer_len;
    unsigned char *c_new_buffer = j_unsigned_chars_from_java(env, newBuffer, &new_buffer_len);
    char *c_new_as_path = j_copy_of_jstring(env, new_as_path, true);
    int r = git_patch_from_buffers(&c_out, (void *)c_old_buffer, oldLen, c_old_as_path, (void *)c_new_buffer, newLen, c_new_as_path, (git_diff_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    git_patch_free(c_out);
    (*env)->ReleaseByteArrayElements(env, oldBuffer, (jbyte *)c_old_buffer, 0);
    free(c_old_as_path);
    (*env)->ReleaseByteArrayElements(env, newBuffer, (jbyte *)c_new_buffer, 0);
    free(c_new_as_path);
    return r;
}

/** int git_patch_get_hunk(const git_diff_hunk **out, size_t *lines_in_hunk, git_patch *patch, size_t hunk_idx); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniGetHunk)(JNIEnv *env, jclass obj, jobject out, jobject linesInHunk, jlong patchPtr, jint hunkIdx)
{
    const git_diff_hunk *c_out;
    size_t c_lines_in_hunk;
    int r = git_patch_get_hunk(&c_out, &c_lines_in_hunk, (git_patch *)patchPtr, hunkIdx);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    (*env)->CallVoidMethod(env, linesInHunk, jniConstants->midAtomicIntSet, c_lines_in_hunk);
    return r;
}

/** int git_patch_get_line_in_hunk(const git_diff_line **out, git_patch *patch, size_t hunk_idx, size_t line_of_hunk); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniGetLineInHunk)(JNIEnv *env, jclass obj, jobject out, jlong patchPtr, jint hunkIdx, jint lineOfHunk)
{
    const git_diff_line *c_out;
    int r = git_patch_get_line_in_hunk(&c_out, (git_patch *)patchPtr, hunkIdx, lineOfHunk);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    return r;
}

/** int git_patch_print(git_patch *patch, git_diff_line_cb print_cb, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniPrint)(JNIEnv *env, jclass obj, jlong patchPtr, jobject printCb)
{
    j_diff_callback_payload payload = {env};
    payload.lineCb = printCb;
    int r = git_patch_print((git_patch *)patchPtr, j_git_diff_line_cb, &payload);
    return r;
}
