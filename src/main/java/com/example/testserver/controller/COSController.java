package com.example.testserver.controller;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;

import java.io.*;
import java.util.LinkedList;
import java.util.List;


public class COSController {
    private static COSController cosController=new COSController();
    private static COSClient cosClient;
    String secretId = "AKID97pGQJwINBj00jBNIwIODl22lCMFsIRJ";
    String secretKey = "dz3qNdcJkwqTpvkIZankPnvlKcePs41g";
    private COSController(){
        // 1 初始化用户身份信息（secretId, secretKey）。
// SECRETID和SECRETKEY请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
// 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
// clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-nanjing");
        ClientConfig clientConfig = new ClientConfig(region);
// 这里建议设置使用 https 协议
// 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
// 3 生成 cos 客户端。
        cosClient = new COSClient(cred, clientConfig);
    }

    public static boolean upload(String remotekey, String filepath) throws FileNotFoundException {
        // 指定要上传的文件
        File localFile = new File(filepath);
// 指定文件将要存放的存储桶
        String bucketName = "huangzhiwei-1304995366";
// 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, remotekey,localFile);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return true;
    }
    public static boolean upload(String remotekey, InputStream inputStream) throws IOException {
// 指定文件将要存放的存储桶
        String bucketName = "huangzhiwei-1304995366";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(inputStream.available());
// 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, remotekey, inputStream, metadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return true;
    }
    public static List<String> getObjlist(String remotefolder){
        List<String> res = new LinkedList<>();
// 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = "huangzhiwei-1304995366";

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
// 设置 bucket 名称
        listObjectsRequest.setBucketName(bucketName);
// prefix 表示列出的对象名以 prefix 为前缀
// 这里填要列出的目录的相对 bucket 的路径
        listObjectsRequest.setPrefix(remotefolder);
// delimiter 表示目录的截断符, 例如：设置为 / 则表示对象名遇到 / 就当做一级目录）
        listObjectsRequest.setDelimiter("/");
// 设置最大遍历出多少个对象, 一次 listobject 最大支持1000
        listObjectsRequest.setMaxKeys(1000);

// 保存每次列出的结果
        ObjectListing objectListing = null;

        do {
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosServiceException e) {
                e.printStackTrace();
                return null;
            } catch (CosClientException e) {
                e.printStackTrace();
                return null;
            }

            // 这里保存列出来的子目录
            List<String> commonPrefixs = objectListing.getCommonPrefixes();
            for (String commonPrefix : commonPrefixs) {
                System.out.println(commonPrefix);
            }

            // 这里保存列出的对象列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                // 对象的 key
                String key = cosObjectSummary.getKey();
                // 对象的etag
                String etag = cosObjectSummary.getETag();
                // 对象的长度
                long fileSize = cosObjectSummary.getSize();
                // 对象的存储类型
                String storageClasses = cosObjectSummary.getStorageClass();
                res.add("https://huangzhiwei-1304995366.cos-website.ap-nanjing.myqcloud.com/"+key);
            }

            // 标记下一次开始的位置
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());
        return res;
    }
}
