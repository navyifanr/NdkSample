//
// Created by Administrator on 12/21/2016.
//

#ifndef COMPRESS_COMPRESS_H
#define COMPRESS_COMPRESS_H

#include "lang.h"
#include <stdlib.h>
#include <android/bitmap.h>

#include <setjmp.h>
#include <jpeglib.h>

#define true 1
#define false 0

//#ifdef __cplusplus
//extern "C" {
//#endif
JNIEXPORT jboolean JNICALL
Java_cn_cfanr_compress_ImageCompress_compressBitmap(JNIEnv *env, jclass type, jobject bitmap,
                                                    jstring dstPath_, jint quality,
                                                    jboolean isOptimize);
//
//#ifdef __cplusplus
//}
//#endif
#endif //COMPRESS_COMPRESS_H_H
