

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws IOException {
        int Num = 0;
        Socket S = new Socket("localhost", 9999);
        Client client = new Client();


        InputStream inputStream = S.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        OutputStream outputStream = S.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        ArrayList<String> protocollist = Protocol(args); //grabs protocols
        try{
            int size = protocollist.size();
            dataOutputStream.write(size);
            dataOutputStream.flush(); //sends size of protocols to server
        } catch (IOException e) {
            System.out.println("incorrect function/format entered");
        }


        client.SendProtocol(protocollist,S); //sends protocol to server

        int rowlen = dataInputStream.read();
        int columnlen = dataInputStream.read();
        //gets dimensions of list from server


        String[][] NamesList;
        NamesList = GetList(rowlen, columnlen, S); // gets list from server


        try {
            if (protocollist.get(0).equals("1")) {
                client.PrintTotals(NamesList);
            }
            if (protocollist.get(0).equals("2")) {
                try {
                    Num = Integer.parseInt(protocollist.get(1));
                } catch (NumberFormatException e) {
                    System.out.println("An integer is required for the second entry of this function"); //catches incorrect entries such as (join hi)
                }
                client.ShowList(Num, NamesList);
            }
            if (protocollist.get(0).equals("3")) {
                System.out.println("Please enter a first and last name"); //checks that first and last name entered
            }
            if (protocollist.get(0).equals("4")) {
                try {
                    Num = Integer.parseInt(protocollist.get(1));
                } catch (NumberFormatException e) {
                    System.out.println("An integer is required for the second entry of this function");
                }
                JoinMembers(Num, protocollist.get(2), NamesList); //adds new name to desired list
                client.UpdateList(NamesList,S); //sends updated list to Server
            }
            if (protocollist.get(0).equals("5")) {
                System.out.println("Incorrect function entered / format"); //handles incorrect inputs
            }
        } catch (Exception e) {
            System.out.println("Incorrect function entered/format");
        }


    }

    public static String[][] GetList(int row, int column, Socket s) throws IOException {
        String[][] NameList = new String[row][column];
        InputStream inputStream = s.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        for (int i= 0; i < NameList.length; i++) {
            for (int j= 0; j < NameList[i].length; j++) {
                String Current = dataInputStream.readUTF();
                NameList[i][j] = Current;
            }
        }
        return NameList; //gets List from Server
    }

    public static ArrayList<String> Protocol (String[] args) {
        String result;
        ArrayList<String> Protocol = new ArrayList<>();
        if(args[0].equals("totals") && args.length==1){
            result = "1";
            Protocol.add(result);
            return (Protocol);
        }
        if(args[0].equals("list") && args.length == 2){
                result = "2";
                Protocol.add(result);
                Protocol.add(args[1]);
                return (Protocol);
            }
        if (args[0].equals("join") && args.length == 3){
            result = "3";
            Protocol.add(result);
            Protocol.add(args[1]);
            Protocol.add(args[2]);
            return (Protocol);
        }

        if (args[0].equals("join") && args.length == 4){
            result = "4";
            Protocol.add(result);
            Protocol.add(args[1]);
            String FullName = (args[2] + " " + args[3]);
            Protocol.add(FullName); //parses first and second name
            return (Protocol);
        }

        result = "5"; //returns 5 on faulty input
        Protocol.add(result);
        return Protocol; //returns what is being asked of the Code, if join is asked it will return the number of the list and the name into protocol list and if list n it returns n.
    }

    public void PrintTotals(String[][] NamesList) {
        //get names list from server
        int members;
        System.out.println("The number of list(s) is: " + NamesList.length + " With a maximum size of " + (NamesList[0].length));

        for (int i = 0; i < NamesList.length; i++) {
            members = 0;
            for (int j = 0; j<NamesList[i].length; j++){
                if (!NamesList[i][j].equals("")){
                    members += 1;
                }
            }
            System.out.println("List " + (i+1) + " has " + members + " member(s)");
        }
    }
    public void ShowList(int n, String [][] Nameslist){
        try {
            for (int i = 0; i < Nameslist[n-1].length; i++) {
                System.out.println(Nameslist[n - 1][i]);
            }
        } catch (Exception e) {
            System.out.println("List not found"); // catches out of index list
        }
    }

    public static String[][] JoinMembers(int list, String name , String [][] NamesList) {
        try {
            for (int i = 0; i < NamesList[list-1].length; i++) {
                if (NamesList[list - 1][i].equals("")) {
                    NamesList[list - 1][i] = name;
                    System.out.println("Success");
                    return NamesList;
                }
            }
        } catch (Exception e) {
            System.out.println("List not found"); //catches out of index list
        }
        System.out.println("failure"); //if the list is full prints failure
       return NamesList;
    }

    public void SendProtocol(ArrayList<String> Protocols, Socket s) throws IOException {
        OutputStream outputStream = s.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        int ListLen = Protocols.size();
        for (int i = 0; i < ListLen; i++) {
            dataOutputStream.writeUTF(Protocols.get(i));
            dataOutputStream.flush(); // sends the protocol list and size to Server
        }
    }

    public void UpdateList(String [][] NameList, Socket s) throws IOException {
        OutputStream outputStream = s.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        for(int i = 0; i< NameList.length; i++){
            for (int j = 0; j<NameList[i].length; j++){
                dataOutputStream.writeUTF(NameList[i][j]);
                dataOutputStream.flush();
            }//After joining new name sends updated list to server
        }


    }
}

