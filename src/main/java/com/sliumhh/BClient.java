package com.sliumhh;

import java.io.IOException;

/**
 *
 * @author: sliu
 * @date: 2020/3/8 0008 20:12
 **/
public class BClient {
    public static void main(String[] args) throws IOException {
        NIOClient nioClient = new NIOClient();
        nioClient.start("clientB");
    }
}
