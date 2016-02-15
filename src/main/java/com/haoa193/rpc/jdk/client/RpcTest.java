package com.haoa193.rpc.jdk.client;

import com.haoa193.rpc.jdk.server.RpcExporter;
import com.haoa193.rpc.jdk.service.EchoService;
import com.haoa193.rpc.jdk.service.EchoServiceImpl;

import java.net.InetSocketAddress;

/**
 * Created by chenyong on 16/2/15.
 */
public class RpcTest {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RpcExporter.exporter("localhost", 8088);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("rpc server started...");


        RpcImporter<EchoService> importer = new RpcImporter<>();
        //为什么是EchoServiceImpl.class
        //应该改为EchoService接口类,然后RpcEXporter中根据接口寻找接口实现类,
        EchoService echoService = importer.importer(EchoServiceImpl.class, new InetSocketAddress("localhost", 8088));
        System.out.println(echoService.echo("Are u OK?"));
    }
}
