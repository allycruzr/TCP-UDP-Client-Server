package test;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static ServerThread clientHandler;
    private static Thread thread;

    public static void main (String[] args) throws Exception{
        // Socket socket = null;
        int serverPort = 6500;
        ServerSocket tcpserver = new ServerSocket(serverPort);
        DatagramSocket udpServer = new DatagramSocket(serverPort);
        List<String> clientesOnline = new ArrayList<>();
        System.out.println("Servidor ouvindo no porto: " + serverPort);
        System.out.println("Aguardando clientes... ");
        while(true){
            Socket clientTcpSocket = tcpserver.accept();
            String clientAdrr = (((InetSocketAddress) clientTcpSocket.getRemoteSocketAddress()).getAddress()).toString();
            clientesOnline.add(clientAdrr);
            System.out.println("Novo cliente online: " + clientAdrr.replace("/",""));
            clientHandler = new ServerThread(clientTcpSocket, udpServer, clientesOnline);
            thread = new Thread(clientHandler);
            thread.start();
        }
    }
}
