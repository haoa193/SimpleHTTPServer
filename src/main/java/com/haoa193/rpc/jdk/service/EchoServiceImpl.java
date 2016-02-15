package com.haoa193.rpc.jdk.service;

/**
 * Created by chenyong on 16/2/15.
 */
public class EchoServiceImpl implements EchoService {
    public String echo(String ping) {
        return ping !=null ? ping + "-> I am OK." : "I am OK.s";
    }
}
