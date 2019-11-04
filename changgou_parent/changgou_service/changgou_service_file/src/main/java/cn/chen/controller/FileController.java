package cn.chen.controller;

import cn.chen.file.FastDFSFile;
import cn.chen.util.FastDFSClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author haixin
 * @time 2019-10-29
 */
@RestController
@CrossOrigin
public class FileController {

    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        try {
            FastDFSFile fastDFSFile = new FastDFSFile(file.getOriginalFilename(), file.getBytes(), StringUtils.getFilenameExtension(file.getOriginalFilename()));
            String[] upload = FastDFSClient.upload(fastDFSFile);
            String url = FastDFSClient.getTrackerUrl() + upload[0] + "/" + upload[1];
            return new Result(true, StatusCode.OK,url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


}
