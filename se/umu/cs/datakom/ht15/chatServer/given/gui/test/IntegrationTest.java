package umu.cs.datakom.ht15.chatServer.given.gui.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.gui.Listener;
import umu.cs.datakom.ht15.chatServer.given.server.Client;
import umu.cs.datakom.ht15.chatServer.given.server.Server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by marccoquand on 05/10/15.
 */
public class IntegrationTest {
    private static final long TIMEOUT = 1000;
    private ClientHandle clientHandle;
    private ServerHandle serverHandle;

    private class ClientHandle {
        public final Client client;
        public final BlockingQueue<String> receiveQueue =
                new LinkedBlockingQueue<>();

        public ClientHandle(Client client) {
            this.client = client;
            /*
            client.addMessageListener(
                    new Listener<String>() {
                        @Override
                        public void update(String e) {receiveQueue.add(e);}
                    });
                    */
        }
    }

    private class ServerHandle {
        public final Server server;
        public final BlockingQueue<String> receiveQueue =
                new LinkedBlockingQueue<>();

        public ServerHandle(Server server) {
            this.server = server;
            /*
            server.addMessageListener(
                    new Listener<String>() {
                        @Override
                        public void update(String e) {receiveQueue.add(e);}
                    });
                    */
        }
    }

    /*
    @Before
    public void setUp() throws Exception {
        serverHandle = new ServerHandle(new Server());
        clientHandle = new ClientHandle(new Client(
                "localhost",
                serverHandle.server.getPortNo()));
    }

    @Test(timeout = TIMEOUT)
    public void clientShouldReceiveMessageFromServer() throws Exception {
        String msg = "The message";
        serverHandle.server.sendMessage(msg);
        serverHandle.server.sendMessage(msg);
        assertEquals(msg,clientHandle.receiveQueue.take());
    }
    */
}
