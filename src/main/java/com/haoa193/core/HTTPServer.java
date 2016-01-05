package com.haoa193.core;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by chenyong on 2016/1/5.
 */
public class HTTPServer {

    private static final Logger LOGGER = Logger.getLogger(HTTPServer.class.getCanonicalName());

    private final File rootDirectory;
    private final int NUM_THREADs = 50;
    private final String INDEX_FILE="index.html";
    private final int port;

    public HTTPServer(File rootDirectory, int port) {
        if (rootDirectory.isFile()) {
            throw new RuntimeException("rootDirector must be a directory not a file");
        }
        try {
            rootDirectory = rootDirectory.getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.rootDirectory = rootDirectory;
        this.port = port;

    }

    public void start() throws IOException {

        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADs);

        try (ServerSocket server = new ServerSocket(port)) {

            LOGGER.info("Accepting request on port : " + server.getLocalPort());
            LOGGER.info("Local socket address : " + server.getLocalSocketAddress());

            while (true) {

                try {
                    Socket connection = server.accept();

                    Runnable r = new RequestProcessor(rootDirectory, INDEX_FILE, connection);

                    pool.submit(r);


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }


    }


    public static void main(String[] args) {


        try {
            File rootDirectory = new File(args[0]);
            int port;
            try {
                port = Integer.parseInt(args[1]);

                if (port < 0 || port > 65535) {
                    port = 80;
                }
            } catch (NumberFormatException e) {
                port = 80;
            }

            HTTPServer webserver = new HTTPServer(rootDirectory, port);
            webserver.start();


        } catch (ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
            LOGGER.warning("Usage: java HTTPServer rootDirectory port");
        } catch (IOException e) {
            LOGGER.severe("Could not start the server.");
        }


    }


}
