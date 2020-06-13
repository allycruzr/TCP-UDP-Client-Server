package test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Client {
    static byte[] buf = new byte[1024];
    private static DatagramSocket socketUdp;
    private static boolean running = true;
    //private boolean servidorEstadoOnline=true;
    private Socket socket;
    private String addressServer;
    private InetAddress address;

    public Client(String address) throws SocketException,
            UnknownHostException {
        this.addressServer = address;
        //  socketUdp = new DatagramSocket();
        this.address = InetAddress.getByName(address);
    }

    public static String received() throws IOException {

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socketUdp.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public static void main(String args[]) throws Exception {
        // if(args.length!=1){
        //   System.err.println("usage: java EchoClientThread <host>");
        // System.exit(1);
        // }

        System.out.println("MENU CLIENTE\n" +
                "0 - Menu Inicial\n" +
                "1 - Listar utilizadores online\n" +
                "2 - Enviar mensagem a um utilizador\n" +
                "3 - Enviar mensagem a todos os utilizadores\n" +
                "4 - lista branca de utilizadores\n" +
                "5 - lista negra de utilizadores\n" +
                "99 - Sair\n");
        String host = args[0];
        String cmd, cmd2, cmd3, msg;
        Socket socket = new Socket(host, 6500);
        socketUdp = new DatagramSocket(9031);
        PrintStream output = new PrintStream(socket.getOutputStream(), true);

        while (true) {

            /*byte[] receiveBuffer = new byte[1024];
            byte[] sendBuffer = new byte[1024];
            DatagramPacket rcvdpkt = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            //socketUdp.receive(rcvdpkt);
            InetAddress IP = rcvdpkt.getAddress();
            int portno = rcvdpkt.getPort();
            String clientData = new String (rcvdpkt.getData());
            System.out.println("Cliente: " + clientData);
            System.out.println("Servidor: " + clientData);
            Scanner scan = new Scanner(System.in);
            String serverData = scan.nextLine();
            sendBuffer = serverData.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, IP, portno); */

            Scanner scan = new Scanner(System.in);
            System.out.println("Opção?");
            System.out.print(host + ":" + 6500 + "&>");
            cmd = scan.nextLine();
            if (cmd.equals("0")) {
                System.out.println("MENU CLIENTE\n" +
                        "0 - Menu Inicial\n" +
                        "1 - Listar utilizadores online\n" +
                        "2 - Enviar mensagem a um utilizador\n" +
                        "3 - Enviar mensagem a todos os utilizadores\n" +
                        "4 - lista branca de utilizadores\n" +
                        "5 - lista negra de utilizadores\n" +
                        "99 - Sair\n");
            }
            if (cmd.equals("1")) {
                output.println(1);
                System.out.println(received());
            }
            if (cmd.equals("2")) {
                System.out.println("Utilizador?: ");
                cmd2 = scan.nextLine();
                System.out.println("Mensagem?: ");
                cmd3 = scan.nextLine();
                msg = cmd + "-" + cmd2 + "-" + cmd3;
                output.println(msg);
                System.out.println(received());
            }
            if (cmd.equals("3")) {
                System.out.println("Mensagem?: ");
                cmd2 = scan.nextLine();
                msg = cmd + "-" + cmd2;
                output.println(msg);
                System.out.println(received());
            }
            if (cmd.equals("4")) {
                output.println(4);
                System.out.println(received());

            }
            if (cmd.equals("5")) {
                output.println(5);
                System.out.println(received());
            }
            if (cmd.equalsIgnoreCase("99")) {
                System.out.println("a sair...");
                break;
            }

        }
        running = false;
        output.close();
        socket.close();
        socketUdp.close();
    }
}