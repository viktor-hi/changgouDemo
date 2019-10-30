package cn.chen.util;

import cn.chen.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  FastDFS操作工具
 *  实现信息获取、文件上传、文件下载、文件删除的相关操作
 * @author haixin
 * @time 2019-10-29
 */
public class FastDFSClient {
    //初始化Tracker配置信息
    static {
        try {
            //1、获取配置文件路径-filePath = new ClassPathResource("fdfs_client.conf").getPath()
            String filePath = new ClassPathResource("fdfs_client.conf").getPath();
            //2、加载配置文件-ClientGlobal.init(配置文件路径)
            ClientGlobal.init(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 创建TrackerServer对象
     * @return TrackerServer
     */
    public static TrackerServer getTrackerServer(){
        TrackerServer trackerServer = null;
        try {
            //3、创建一个TrackerClient对象。直接new一个。
            TrackerClient trackerClient = new TrackerClient();
            //4、使用TrackerClient对象创建连接，getConnection获得一个TrackerServer对象。
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trackerServer;
    }
    /**
     * 创建StorageClient对象
     * @return StorageClient
     */
    public static StorageClient getStorageClient(){
        //5、创建一个StorageClient对象，直接new一个，需要两个参数TrackerServer对象、null
        StorageClient storageClient = new StorageClient(getTrackerServer(),null);
        return storageClient;
    }
    /**
     * 文件上传
     * @param fastDFSFile 上传包装对象
     * @return String[存储的组名，存储的路径]
     */
    public static String[] upload(FastDFSFile fastDFSFile){
        String[] uploadResult = null;
        try {
            //附加参数
            NameValuePair[] meta_list = new NameValuePair[1];
            //文件作者
            meta_list[0] = new NameValuePair("author",fastDFSFile.getAuthor());
            //上传文件
            //upload_file(文件字节数组，文件扩展名,附加参数)
            uploadResult = getStorageClient().upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), meta_list);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadResult;
    }

    /**
     * 根据组名和文件名取得文件信息，文件地址为内网地址
     * @param groupname
     * @param filename
     * @return
     */
    public static FileInfo getFileInfo(String groupname,String filename){

        FileInfo file_info = null;
        try {
            file_info  = getStorageClient().get_file_info(groupname, filename);
            return file_info;
        } catch (Exception e) {
            e.printStackTrace();
        }
            return file_info;
    }

    /**
     * 根据组名和文件名取得文件输入流
     * @param groupname
     * @param filename
     * @return
     */
    public static InputStream downFile(String groupname,String filename){
        ByteArrayInputStream inputStream = null;
        try {
            byte[] bytes = getStorageClient().download_file(groupname, filename);
            inputStream = new ByteArrayInputStream(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public static void delete(String groupname,String filename){
        try {
            getStorageClient().delete_file(groupname,filename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取组名
     * @param groupname
     * @return
     */
    public static StorageServer getStorageServer(String groupname){
        StorageServer storeStorage = null ;
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            storeStorage = trackerClient.getStoreStorage(connection, groupname);
            return storeStorage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storeStorage;
    }

    
    public static StorageServer getServerInfo(String groupname, String remoteFilename){
        StorageServer fetchStorage = null ;
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            fetchStorage = trackerClient.getFetchStorage(connection, groupname, remoteFilename);
            return fetchStorage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fetchStorage;
    }
    //测试方法
    public static void main(String[] args) {
        //group1/M00/00/00/wKjThF1pAcKAYNhxAA832942OCg928.jpg
        FileInfo info = getFileInfo("group1", "M00/00/00/wKgACl24NaWAUh0zAAGgLl1xpc450.jpeg");
        StorageServer storageServer = getStorageServer("group1");
        System.out.println(storageServer.getInetSocketAddress());
        System.out.println(info);
    }
}
