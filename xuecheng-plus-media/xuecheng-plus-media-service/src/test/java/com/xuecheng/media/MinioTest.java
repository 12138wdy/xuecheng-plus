package com.xuecheng.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * minio测试
 */
public class MinioTest {

    static MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.65:9000")
            .credentials("minioadmin", "minioadmin")
            .build();


    @Test
    public void uploadMinioTest() throws Exception {

        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();

        }

        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("D:\\images\\3.jpg")
                .object("test/01/小呆呆")
//                .contentType()  指定文件类型
                .build();


        minioClient.uploadObject(uploadObjectArgs);

        System.out.println("上传成功");
    }

    /**
     * 删除文件
     *
     * @throws Exception
     */
    @Test
    public void removeMinioTest() throws Exception {


        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("小呆呆")
                .build();


        minioClient.removeObject(removeObjectArgs);


    }


    /**
     * 查询文件
     *
     * @throws Exception
     */
    @Test
    public void getMinioTest() throws Exception {


        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("test/01/小呆呆")
                .build();


        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\java\\1.jpg"));

        IOUtils.copy(inputStream, outputStream);

        //MD5校验
        String mded5Hex = DigestUtils.md5Hex(inputStream);
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\java\\1.jpg"));
        String mded5Hex1 = DigestUtils.md5Hex(fileInputStream);
        if (mded5Hex1.equals(mded5Hex)) {
            System.out.println("下载成功");
        }


    }

}
