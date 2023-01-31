

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
    public static void main( String[] args ) throws IOException {
        int num = 0;
        int num1 = 0;

        try {
            // Parse the string argument into an integer value.
            num = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException nfe) {
            //catches invalid number input
            System.out.println("The first argument must be an integer.");
            System.exit(1);
        }

        try {
            num1 = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException nfe) {
            //catches invalid number input
            System.out.println("The second argument must be an integer.");
            System.exit(1);
        }
        ServerSocket ss = new ServerSocket(9999);
        Server myserver = new Server();
        String[][] NameList = myserver.initList(num, num1);
        File file = new File("log.txt");
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        //setting up Server sockets,initializing logs file and initial list
        //write.print clears file on every server run


        while (true) {
            Socket s = ss.accept();
            InetSocketAddress socketAddress = (InetSocketAddress) s.getRemoteSocketAddress();
            String clientIpAddress = socketAddress.getAddress().getHostAddress();


            OutputStream outputStream = s.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.write(num);
            dataOutputStream.flush();

            dataOutputStream.write(num1);
            dataOutputStream.flush(); //sends array dimensions to client

            myserver.SendList(NameList,s); //sends the current list to client



            InputStream inputStream = s.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            int ProtocolsLen = dataInputStream.read();
            String [] Protocols = new String[ProtocolsLen];
            for(int i =0; i<ProtocolsLen; i++){
                String Current = dataInputStream.readUTF();
                Protocols[i] = Current;
            }
            //copies protocol from client to understand what is being done by the client
            if(Protocols[0].equals("4")){
                NameList = UpdateList(num,num1,s);
            }//if client asks to add name namelist is updated

            myserver.Log(Protocols,clientIpAddress,file);//logs what the client attempted to do


        }



    }




    public void SendList (String[][] NameList , Socket s) throws IOException {
        OutputStream outputStream = s.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        for(int i = 0; i< NameList.length; i++){
            for (int j = 0; j<NameList[i].length; j++){
                dataOutputStream.writeUTF(NameList[i][j]);
                dataOutputStream.flush();
            }
        }
        //Loops through current list and sends it to user

    }

    public void Log(String[] Protocols, String IP,File file) throws IOException {
        Date thisDate = new Date();

        FileWriter fw = new FileWriter(file, true);
        PrintWriter pw = new PrintWriter(fw);
        String Request;
        if(Protocols[0].equals("1")){
            Request = "Client asked for totals";
            pw.println((thisDate) + "|" + IP + "|" + Request);
            pw.flush();
        }
        if(Protocols[0].equals("2")){
            Request = "Client asked to view elements in list: " + Protocols[1];
            pw.println((thisDate) + "|" + IP + "|" + Request);
            pw.flush();
        }
        if(Protocols[0].equals("4")){
            Request = "Client asked to add " + Protocols[2] + " to list " + Protocols[1];
            pw.println((thisDate) + "|" + IP + "|" + Request);
            pw.flush();
        }
        if(Protocols[0].equals("5")){
            Request = "Client entered wrong input";
            pw.println((thisDate) + "|" + IP + "|" + Request);
            pw.flush();
        }
    }

    public static String[][] UpdateList(int row, int column, Socket s) throws IOException {
        String[][] NewNameList = new String[row][column];
        InputStream inputStream = s.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream); //reads new list from client
        for (int i= 0; i < NewNameList.length; i++) {
            for (int j= 0; j < NewNameList[i].length; j++) {
                String Current = dataInputStream.readUTF();
                NewNameList[i][j] = Current; //updates list
            }
        }
        return NewNameList; //returns new list
    }

    public String[][] initList(int ArrayNum, int ArrayLen) {

        String[][] NamesList = new String[ArrayNum][ArrayLen];
        for (int i = 0; i <= ArrayNum - 1; i++) {
            for (int j = 0; j <= ArrayLen - 1; j++) {
                if (NamesList[i][j] == null) {
                    NamesList[i][j] = "";
                }
            }
        }//initializes an empty list
        return NamesList;
    }



}