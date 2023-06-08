package com.demo.ftp.commonsNet.service;

import org.springframework.http.ResponseEntity;

import java.io.InputStream;


public interface FTPClientService {

    ResponseEntity<Object> download(String remoteFileName, String localFileName, String remoteDir) throws Exception;

    boolean upload(InputStream inputStream, String originName, String remoteDir);
}
