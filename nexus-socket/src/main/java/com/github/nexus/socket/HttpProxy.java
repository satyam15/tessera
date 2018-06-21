package com.github.nexus.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URI;
import java.util.Objects;

/**
 * Proxy that acts as an interface to an HTTP Server.
 * Provides methods for creating the HTTP connection, writing a request and receiving the response.
 */
public class HttpProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpProxy.class);

    private final URI serverUri;

    private Socket socket;

    private OutputStream os;

    private InputStream is;
    
    private SocketFactory socketFactory;
    
    /**
     * Connect to specified URL and create read/sendRequest streams.
     */
    public HttpProxy(URI serverUri,SocketFactory socketFactory) {
        this.socketFactory = Objects.requireNonNull(socketFactory);
        this.serverUri = Objects.requireNonNull(serverUri);
    }

    /**
     * Connect to the HTTP server.
     */
    public boolean connect() {
        try {
            socket = socketFactory.create(serverUri);

            this.os = socket.getOutputStream();
            this.is = socket.getInputStream();

            return true;

        } catch (ConnectException ex) {
            return false;

        } catch (IOException ex) {
            LOGGER.error("Failed to connect to URL: {}", serverUri);
            throw new NexusSocketException(ex);
        }
    }

    /**
     * Disconnect from HTTP server and clean up.
     */
    public void disconnect() {
        try {
            is.close();
            os.close();
            socket.close();

        } catch (IOException ex) {
            LOGGER.info("Ignoring exception on HttpProxy disconnect: {}", ex.getMessage());
        }
    }

    /**
     * Write data to the http connection.
     */
    public void sendRequest(byte[] data) {
        LOGGER.info("Sending HTTP request: {}", data);
        try {
            os.write(data);
            os.flush();
        } catch (IOException ex) {
            LOGGER.error("Failed to write to socket");
            throw new NexusSocketException(ex);
        }

    }

    /**
     * Read response from the http connection.
     * Note that an http response will consist of multiple lines.
     */
    public byte[] getResponse() {
        try {
            return InputStreamUtils.readAllBytes(is);
        } catch (IOException ex) {
            LOGGER.error("Failed to read from http socket");
            throw new NexusSocketException(ex);
        }

    }

}
