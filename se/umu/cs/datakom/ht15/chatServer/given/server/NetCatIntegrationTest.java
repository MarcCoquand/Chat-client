/*package umu.cs.datakom.ht15.chatServer.given.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.gui.Listener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NetCatIntegrationTest {

    private static final long TIMEOUT = 1000;
    private ClientHandle clientHandle;
    private ServerHandle serverHandle;

    private class ClientHandle {

        public final Client client;
        public final BlockingQueue<String> receiveQueue =
                new LinkedBlockingQueue<>();

        public ClientHandle(Client client) {
            this.client = client;
            client.addMessageListener(
                    new Listener<String>() {
                        @Override
                        public void update(String e) {receiveQueue.add(e);}
                    });
        }
    }

    private class ServerHandle {

        public final Server server;
        public final BlockingQueue<String> receiveQueue =
                new LinkedBlockingQueue<>();

        public ServerHandle(Server server) {
            this.server = server;
            server.addMessageListener(
                    new Listener<String>() {
                        @Override
                        public void update(String e) {receiveQueue.add(e);}
                    });
        }
    }

    @Before
    public void setUp() throws Exception {
        serverHandle = new ServerHandle(new Server());
        clientHandle = new ClientHandle(new Client(
                "localhost",
                serverHandle.server.getPortNo()));
    }

    @After
    public void tearDown() throws Exception {
        serverHandle.server.stopThreads();
        clientHandle.client.stopThread();
    }

    @Test(timeout = TIMEOUT)
    public void serverShouldReceiveFromClient() throws Exception {
        String msg = "The msg!";
        clientHandle.client.sendMessage(msg);
        assertEquals(msg, serverHandle.receiveQueue.take());
    }

    @Test(timeout = TIMEOUT)
    public void clientShouldReceiveMessageFromServer() throws Exception {
        String msg = "The message";
        serverHandle.server.sendMessage(msg);
        serverHandle.server.sendMessage(msg);
        assertEquals(msg,clientHandle.receiveQueue.take());
    }

    @Test
    public void clientShouldNotReceiveMessageAfterLeaving() throws Exception {
        clientHandle.client.stopThread();
        serverHandle.server.sendMessage("Another msg!");
        Thread.sleep(100);
        assertEquals(0, clientHandle.receiveQueue.size());
    }

    @Test(timeout = TIMEOUT)
    public void clientShouldReceiveTwoMessagesInOrder() throws Exception {
        String[] msg = {"n1","n2"};
        //serverHandle.server.sendMessage(msg[0]);
        serverHandle.server.sendMessage(msg[0]);
        //serverHandle.server.sendMessage(msg[1]);
        serverHandle.server.sendMessage(msg[1]);
        Thread.sleep(100);
        String[] answer = {clientHandle.receiveQueue.poll(),clientHandle.receiveQueue.poll()};
        assert(msg[0].equals(answer[0]));
        assert(msg[1].equals(answer[1]));
    }
}

*/
