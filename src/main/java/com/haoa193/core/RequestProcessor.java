package com.haoa193.core;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Date;

/**
 * Created by chenyong on 2016/1/5.
 */
public class RequestProcessor implements Runnable {

    private final Socket connection;
    private String indexFileName = "index.html";
    private final File rootDirectory;
    private static final String HTTPVERSION_PREFIX = "HTTP/";

    public RequestProcessor(File rootDirectory, String indexFileName, Socket connection) {

        if (rootDirectory.isFile()) {
            throw new IllegalArgumentException("rootDirectory must be a directory not a file");
        }

        this.rootDirectory = rootDirectory;
        this.connection = connection;

        if (indexFileName != null) {
            this.indexFileName = indexFileName;
        }
    }

    @Override
    public void run() {


        try {

            String root = rootDirectory.getCanonicalPath();

            OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
            Writer out = new OutputStreamWriter(raw);

            Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));


            StringBuilder requestLine = new StringBuilder();

            while (true) {
                int c = in.read();
                if (c == '\r' || c == '\n' || c == -1) {
                    break;
                }
                requestLine.append((char) c);
            }
            String get = requestLine.toString();

            String[] tokens = get.split("\\s+");

            String method = tokens[0];

            String version = "";

            if (method.equals("GET")) {
                String requestFile = tokens[1];

                if (requestFile.endsWith("/")) {
                    requestFile +=indexFileName;
                }
                if (tokens.length > 2){
                    version = tokens[2];
                }

                File file = new File(rootDirectory, requestFile);

                if (file.canRead() && file.getCanonicalPath().startsWith(root)) {

                    byte[] theFile = Files.readAllBytes(file.toPath());
                    String contentType = URLConnection.getFileNameMap().getContentTypeFor(requestFile);

                    if (version.startsWith(HTTPVERSION_PREFIX)) {

                        sendHTTPHeader(out, "HTTP/1.0 200 OK", contentType, theFile.length);

                    }
                    //文件可能是图像或者其他二进制文件，所以需要使用底层输出流（字节）而不是writer
                    raw.write(theFile);
                    raw.flush();

                }else{//找不到文件
                    String body = new StringBuilder()
                            .append("<html>\r\n")
                            .append("<head><title>404 File Not Found</title></head>\r\n")
                            .append("<body><h1>HTTP 404 error: File Not Found.</h1></body>\r\n")
                            .append("/html\r\n")
                            .toString();

                    if (version.startsWith(HTTPVERSION_PREFIX)) {

                        sendHTTPHeader(out, "HTTP/1.0 404 File Not Found", "text/html;charset=utf-8", body.length());

                    }
                    out.write(body);
                    out.flush();

                }



            }else{//不是GET方法的请求，返回“未实现”

                String body = new StringBuilder()
                        .append("<html>\r\n")
                        .append("<head><title>501 Not Implemented</title></head>\r\n")
                        .append("<body><h1>HTTP 501 error: 501 Not Implemented.</h1></body>\r\n")
                        .append("/html\r\n")
                        .toString();

                if (version.startsWith(HTTPVERSION_PREFIX)) {

                    sendHTTPHeader(out, "HTTP/1.0 501 501 Not Implemented", "text/html;charset=utf-8", body.length());

                }
                out.write(body);
                out.flush();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendHTTPHeader(Writer out, String responseCode, String contentType, int length) throws IOException {

        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");

        out.write("Server: HTTPServer 1.0\r\n");
        out.write("Content-Length: " + length + "\r\n");
        out.write("Content-Type: " + contentType + "\r\n");
        out.flush();
    }
}
