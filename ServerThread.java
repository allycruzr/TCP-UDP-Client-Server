package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ServerThread implements Runnable{

    private static int PORT = 9031;

    private Socket socket;
    private  DatagramSocket socketUdp;
    private List<String> clientesOnline;
    private List<String> listaBranca= new ArrayList<>();
    private List<String> listaNegra= new ArrayList<>();
    private HashSet<String> ipsNaopermitidosSet=new HashSet<>(); //todo mudar data type
    private byte[] buf = new byte[1024];

    ServerThread(Socket clientSocket, DatagramSocket socketUdp, List<String> clientesOnline) throws IOException {
        this.socket = clientSocket;
        this.socketUdp = socketUdp;
        this.clientesOnline = clientesOnline;

        try {
            this.listaNegra = readBlackList();
            this.listaBranca = readWhiteList();
        } catch (IOException e) {
            System.out.println("Existem erros nos ficheiros de texto. A desligar o servidor...");
            this.socket.close();
            this.socketUdp.close();
        }
    }
    public void main(String[] args) {
        run();
    }

    public void run(){

        String invalidMsg = "Desculpe, o seu endereço de IP não está autorizado :( Contate o administrador do serviço.";
        String msgOK = "Mensagem enviada.";

        while(true){
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String mensagemRecebida = br.readLine();
                String[] split = mensagemRecebida.split("-");
                switch (split[0]){
                    case "1":
                        System.out.println(socket.getInetAddress().toString().replace("/","") +" está listando clientes online...");
                        if(checkBlock(socket.getInetAddress().getHostAddress())){
                            answerClient(invalidMsg);
                            break;
                        }
                        sendOnlineClients();
                        break;

                    case "2":
                        System.out.print(socket.getInetAddress().getHostName() + " está tentando enviar uma mensagem privada para " + split[1]+"\n");
                        if(checkBlock(socket.getInetAddress().getHostAddress())){
                            answerClient(invalidMsg);
                            break;
                        }
                        sendPrivateMsg(split[1], split[2]); // ip destino e mensagem
                        answerClient(msgOK);
                        break;

                    case "3":
                        System.out.println(socket.getInetAddress().getHostName() + " está tentando enviar uma mensagem a todos os clientes online...");
                        if(checkBlock(socket.getInetAddress().getHostAddress())){
                            answerClient(invalidMsg);
                            break;
                        }
                        sendForAll(split[1]);
                        break;

                    case "4":
                        System.out.println(socket.getInetAddress().getHostName()+" Está tentando listar clientes PERMITIDOS...");
                        if(checkBlock(socket.getInetAddress().getHostAddress())){
                            System.out.println(socket.getInetAddress().getHostAddress());
                            answerClient(invalidMsg);
                            break;
                        }

                        sendList("listaBranca");
                        break;

                    case "5":
                        System.out.println(socket.getInetAddress().toString().replace("/","")+" está tentando listar clientes NÃO PERMITIDOS...");

                        if(checkBlock(socket.getLocalAddress().getHostAddress())){
                            answerClient(invalidMsg);
                            break;
                        }
                        sendList("listaNegra");
                        break;

                    case "99":
                        System.out.println("Desconectando cliente..."+socket.getInetAddress().toString().replace("/",""));
                        br.close();
                        this.socket.close();
                        this.socketUdp.close();
                        break;

                    default:
                        break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public boolean checkBlock(String ip) throws IOException {

        if (ipsNaopermitidosSet.contains(ip)) {
            System.out.println("entrei em ipsset");
            return true;
        }
        if (listaBranca.size() > 0) {
            System.out.println("entrei em for");
            for (int i = 0; i < listaBranca.size(); i++) {
                if (listaBranca.get(i).equals(ip)) {
                    System.out.println("IP válido");
                    return false;
                }
            }
        } else {
            return false;
        }
        System.out.println("Nao entrei nos ifs");
        return true;
    }

    public void answerClient(String msg){
        try {
            ServerThread2 clientHandler2 = new ServerThread2(socket.getInetAddress(), msg);
            clientHandler2.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendOnlineClients() throws SocketException {
        //  try {
        String msg = "";
        String msg0 = "\nLista de clientes online: \n";
        for(int i = 0; i<clientesOnline.size(); i++){
            msg = msg.concat(clientesOnline.get(i).replace("/","")+ '\n');
        }
        ServerThread2 clientHandler2 = new ServerThread2(socket.getInetAddress(), msg0.concat(msg));
        clientHandler2.run();
        //InetAddress address = socket.getInetAddress();
        //byte[] buf= msg.getBytes();
        //DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        // socketUdp.send(packet);
        // } catch (IOException e) {
        //   e.printStackTrace();
        //}
    }

    public void sendPrivateMsg(String ip, String msg) {
        buf = msg.getBytes();
        InetAddress address;
        try{
            address = InetAddress.getByName(ip);
            if(!address.isReachable(3000)){
                answerClient("Não foi possível enviar a mensagem. Utilizador não existe ou está offline.");
                buf = new byte[1024];
            } else {
                ServerThread2 clientHandler2 = new ServerThread2(address, msg);
                clientHandler2.run();}

        }catch(IOException e){
            answerClient("Não foi possível enviar a mensagem");
        }
    }

    public void sendForAll(String msg) {
        for (int i = 0; i < clientesOnline.size(); i++) {
            sendPrivateMsg(clientesOnline.get(i), msg);
        }
    }

    public void sendList(String nomeLista){

        if(nomeLista.equals("listaBranca")){
            try {
                String msg= "OK \n";
                for(int i=0; i<listaBranca.size();i++){
                    msg=msg+"\n"+listaBranca.get(i)+"\n";
                }
                ServerThread2 clientHandler2 = new ServerThread2(socket.getInetAddress(),msg);
                clientHandler2.run();
                //InetAddress address = socket.getInetAddress();
                //byte[] buf= msg.getBytes();
                //DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
                //socketUdp.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(nomeLista.equals("listaNegra")){
            try {
                String msg= "OK \n";
                for(int i=0; i<listaNegra.size();i++){
                    msg=msg+"\n"+listaNegra.get(i)+"\n";
                }
                ServerThread2 clientHandler2=new ServerThread2(socket.getInetAddress(),msg);
                clientHandler2.run();
                // InetAddress address = socket.getInetAddress();
                //byte[] buf= msg.getBytes();
                //DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
                //socketUdp.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public List<String> readWhiteList() throws IOException{
        List<String> ipsNaListaBranca = new ArrayList<>();
        try{
            File file = new File("listaBranca.txt");
            Scanner fileSc = new Scanner(file);
            while (fileSc.hasNextLine()){
                String ip = fileSc.nextLine();
                ipsNaListaBranca.add(ip);
                listaBranca.add(ip);
            }
        }catch (IOException e) {
            File f = new File("listaBranca.txt");
            f.createNewFile();
        }
        return ipsNaListaBranca;
    }

    public List<String> readBlackList() throws IOException{
        List<String> ipsListaNegra = new ArrayList<>();

        try{
            File file = new File("listaNegra.txt");
            Scanner fileSc= new Scanner(file);
            while (fileSc.hasNextLine()){
                String ip = fileSc.nextLine();
                ipsListaNegra.add(ip);
                ipsNaopermitidosSet.add(ip);
            }
        }catch (IOException e) {
            File f = new File("listaNegra.txt");
            f.createNewFile();
        }
        return ipsListaNegra;
    }
}
