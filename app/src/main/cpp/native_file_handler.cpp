#include <jni.h>
#include "log.h"
#include <string>


const char *PASSWORD = "pw";
long getFileSize(char* filePath);

extern "C"
JNIEXPORT jboolean JNICALL
Java_cn_cfanr_ndksample_utils_FileUtils_fileEncrypt(JNIEnv *env, jclass type, jstring normalFilePath_,
                                                    jstring encryptFilePath_) {
    const char *normalFilePath = env->GetStringUTFChars(normalFilePath_, 0);
    const char *encryptFilePath = env->GetStringUTFChars(encryptFilePath_, 0);

    int passwordLen = strlen(PASSWORD);

    LOGE("要加密的文件的路径 = %s , 加密后的文件的路径 = %s", normalFilePath, encryptFilePath);

    //读文件指针
    FILE *frp = fopen(normalFilePath, "rb");
    // 写文件指针
    FILE *fwp = fopen(encryptFilePath, "wb");

    if (frp == NULL) {
        LOGE("文件不存在");
        return JNI_FALSE;
    }
    if (fwp == NULL) {
        LOGE("没有写权限");
        return JNI_FALSE;
    }

    // 边读边写边加密
    int buffer;
    int index = 0;
    while ((buffer = fgetc(frp)) != EOF) {
        // write
        fputc(buffer ^ *(PASSWORD + (index % passwordLen)), fwp);  //异或的方式加密
        index++;
    }
    // 关闭文件流
    fclose(fwp);
    fclose(frp);

    LOGE("文件加密成功");

    env->ReleaseStringUTFChars(normalFilePath_, normalFilePath);
    env->ReleaseStringUTFChars(encryptFilePath_, encryptFilePath);

    return JNI_TRUE;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_cn_cfanr_ndksample_utils_FileUtils_fileDecode(JNIEnv *env, jclass type, jstring encryptFilePath_,
                                                   jstring decodeFilePath_) {
    const char *encryptFilePath = env->GetStringUTFChars(encryptFilePath_, 0);
    const char *decodeFilePath = env->GetStringUTFChars(decodeFilePath_, 0);

    int passwordLen = strlen(PASSWORD);
    // 打开文件
    FILE *frp = fopen(encryptFilePath, "rb");
    FILE *fwp = fopen(decodeFilePath, "wb");
    if (frp == NULL) {
        LOGE("文件不存在");
        return JNI_FALSE;
    }
    if (fwp == NULL) {
        LOGE("没有写权限");
        return JNI_FALSE;
    }
    // 读取文件
    int buffer;
    int index = 0;
    while ((buffer = fgetc(frp)) != EOF) {
        // 写文件
        fputc(buffer ^ *(PASSWORD + (index % passwordLen)), fwp);
        index++;
    }
    LOGE("文件解密成功");
    // 关闭流
    fclose(fwp);
    fclose(frp);

    env->ReleaseStringUTFChars(encryptFilePath_, encryptFilePath);
    env->ReleaseStringUTFChars(decodeFilePath_, decodeFilePath);

    return JNI_TRUE;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_cn_cfanr_ndksample_utils_FileUtils_fileSplit(JNIEnv *env, jclass type, jstring splitFilePath_,
                                                  jstring suffix_, jint fileNum) {
    const char *splitFilePath = env->GetStringUTFChars(splitFilePath_, 0);
    const char *suffix = env->GetStringUTFChars(suffix_, 0);

    //要分割文件，首先得到分割文件的路径列表，动态申请内存存储路径列表
    char** split_path_list = (char**)malloc(sizeof(char*) * fileNum);

    //得到文件大小
    long file_size = getFileSize((char *) splitFilePath);

    //得到路径字符长度
    int file_path_str_len = strlen(splitFilePath);

    //组合路径
    char file_path[file_path_str_len + 5];
    strcpy(file_path, splitFilePath);
    strtok(file_path, ".");
    strcat(file_path, "_%d");
    strcat(file_path, suffix);

    int i = 0;
    for(; i < fileNum; ++i) {
        //申请单个文件的路径动态内存存储
        split_path_list[i] = (char*)malloc(sizeof(char) * 128);
        //组合单个路径
        sprintf(split_path_list[i], file_path, (i+1));
        LOGE("%s", split_path_list[i]);
    }

    LOGE("文件大小 = %ld", file_size);
    LOGE("文件路径 = %s", splitFilePath);

    //读文件
    FILE* fp = fopen(splitFilePath, "rb");
    if(fp == NULL) {
        LOGE("文件不存在，或文件不可读");
        return JNI_FALSE;
    }

    //能整除，平均分割
    if(file_size % fileNum) {
        //单个文件大小
        int part_file_size = file_size / fileNum;
        LOGE("单个文件大小 = %d", part_file_size);
        int i = 0;
        //分割多少个文件就分段读多少次
        for(; i < fileNum; i++) {
            //写文件
            FILE* fwp = fopen(split_path_list[i], "wb");
            if(fwp == NULL) {
                LOGE("没有文件写入权限");
                return JNI_FALSE;
            }
            int j = 0;
            //单个文件有多大，就读写多少次
            for(; j < part_file_size; j++) {
                //边读边写
                fputc(fgetc(fp), fwp);
            }
            //关闭文件流
            fclose(fwp);
        }
    } else {  //不能整除
        int part_file_size = file_size / (fileNum -1);
        LOGE("单个文件大小 = %d", part_file_size);
        int i = 0;
        for(; i < fileNum - 1; i++) {
            //写文件
            FILE* fwp = fopen(split_path_list[i], "wb");
            if(fwp == NULL) {
                LOGE("没有文件写入权限");
                return JNI_FALSE;
            }

            int j = 0;
            for(; j < part_file_size; j++) {
                //边读边写
                fputc(fgetc(fp), fwp);
            }
            //关闭流
            fclose(fwp);
        }
        //剩余部分
        FILE* last_fwp = fopen(split_path_list[fileNum -1], "wb");
        i = 0;
        for(; i < file_size % (fileNum - 1); i++) {
            fputc(fgetc(fp), last_fwp);
        }
        //关闭流
        fclose(last_fwp);
    }
    //关闭文件流
    fclose(fp);
    //释放动态内存
    i = 0;
    for(; i < fileNum; i++) {
        free(split_path_list[i]);
    }
    free(split_path_list);

    env->ReleaseStringUTFChars(splitFilePath_, splitFilePath);
    env->ReleaseStringUTFChars(suffix_, suffix);
    return JNI_TRUE;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_cn_cfanr_ndksample_utils_FileUtils_fileMerge(JNIEnv *env, jclass type, jstring splitFilePath_, jstring splitSuffix_,
        jstring mergeSuffix_, jint fileNum) {
    const char *splitFilePath = env->GetStringUTFChars(splitFilePath_, 0);
    const char *splitSuffix = env->GetStringUTFChars(splitSuffix_, 0);
    const char *mergeSuffix = env->GetStringUTFChars(mergeSuffix_, 0);

    //1.申请 split 文件路径列表动态内存
    char** split_path_list = (char**)malloc(sizeof(char*) * fileNum);

    //2.组装 split 文件路径
    int split_file_path_len = strlen(splitFilePath);
    int split_file_path_suffix_len = strlen(splitSuffix);
    char split_file_path[split_file_path_len + split_file_path_suffix_len];
    strcpy(split_file_path, splitFilePath);
    strtok(split_file_path, ".");
    strcat(split_file_path, "_%d");
    strcat(split_file_path, splitSuffix);

    //3.组装 merge 文件路径
    int merge_file_path_len = strlen(mergeSuffix);
    char merge_file_path[split_file_path_len + merge_file_path_len];
    strcpy(merge_file_path, splitFilePath);
    strtok(merge_file_path, ".");
    strcat(merge_file_path, mergeSuffix);

    LOGE("merge文件路径 = %s", merge_file_path);

    //4.循环得到 split 文件路径列表
    int file_path_str_len = strlen(split_file_path);
    int i = 0;
    for(; i < fileNum; i++) {
        split_path_list[i] = (char*)malloc(sizeof(char) * file_path_str_len);
        sprintf(split_path_list[i], split_file_path, i+1);
        LOGE("split文件路径列表 = %s", split_path_list[i]);
    }

    //5.创建并打开merge file
    FILE* merge_fwp = fopen(merge_file_path, "wb");

    //6.边读边写，读多个文件，写入一个文件
    i = 0;
    for(; i < fileNum; i++) {
        FILE* split_frp = fopen(split_path_list[i], "rb");
        if(split_frp == NULL) {
            LOGE("文件不存在，或没有读文件权限");
            return JNI_FALSE;
        }
        long part_split_file_size = getFileSize(split_path_list[i]);
        int j = 0;
        for(; j < part_split_file_size; j++) {
            fputc(fgetc(split_frp), merge_fwp);
        }

        //关闭流
        fclose(split_frp);
        //每合并一个文件，就删除它
        remove(split_path_list[i]);
    }
    //关闭文件流
    fclose(merge_fwp);

    //释放动态内存
    i = 0;
    for(; i < fileNum; i++) {
        free(split_path_list[i]);
    }

    free(split_path_list);
    LOGE("文件合并完成");

    env->ReleaseStringUTFChars(splitFilePath_, splitFilePath);
    env->ReleaseStringUTFChars(splitSuffix_, splitSuffix);
    env->ReleaseStringUTFChars(mergeSuffix_, mergeSuffix);
    return JNI_TRUE;
}

/*获取文件的大小*/
long getFileSize(char* filePath) {
    FILE* fp = fopen(filePath, "rb");
    if(fp == NULL) {
        LOGE("文件不存在，可能没有读文件的权限");
    }
    fseek(fp, 0, SEEK_END);
    return ftell(fp);
}