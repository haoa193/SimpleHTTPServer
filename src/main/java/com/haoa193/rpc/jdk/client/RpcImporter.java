package com.haoa193.rpc.jdk.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by chenyong on 16/2/15.
 */
public class RpcImporter<S> {


    public S importer(final Class<?> serviceClass, final InetSocketAddress address) {

        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass.getInterfaces()[0]}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = null;
                ObjectOutputStream outputStream = null;
                ObjectInputStream inputStream = null;
                try {
                    socket = new Socket();
                    socket.connect(address);
                    outputStream = new ObjectOutputStream(socket.getOutputStream());

                    outputStream.writeUTF(serviceClass.getName());
                    outputStream.writeUTF(method.getName());
                    outputStream.writeObject(method.getParameterTypes());
                    outputStream.writeObject(args);
//                    outputStream.flush();

                    inputStream = new ObjectInputStream(socket.getInputStream());

                    return inputStream.readObject();


                } catch (Exception e) {

                    e.printStackTrace();
                }finally {

                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }

                    if (socket != null) {
                        socket.close();
                    }
                }

                return null;
            }
        });
    }
}
