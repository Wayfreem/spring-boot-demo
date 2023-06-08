package com.demo.ftp.commonsNet.service;

import java.io.InputStream;


public interface FTPClientService {

    void download(String remoteFileName, String localFileName, String remoteDir);

    boolean upload(InputStream inputStream, String originName, String remoteDir);
}
