package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerThread2 extends Thread{
    private DatagramSocket UdpSocket;
    private boolean running;
    private byte[] buf = new byte[1024];
    private InetAddress address;
    private String msg;

    public ServerThread2(InetAddress address, String msg) throws SocketException {
        UdpSocket = new DatagramSocket();
        this.address = address;
        this.msg = msg;
    }

    public void run() {
        running = true;
        while (running) {
            try {
                DatagramPacket packet;
                buf = msg.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, 9031);
                System.out.println("Mensagem enviada para o IP "+address.getHostName());
                UdpSocket.send(packet);
                running = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        UdpSocket.close();
    }

    public static void main(String[] args) throws SocketException {
    }
}

