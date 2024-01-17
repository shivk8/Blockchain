package ds;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Scanner;

//Shivaani Krishnakumar
//shivaank
//used chatgpt for generating comments
//reference Project2Task4 code for TCP client and server interactions
public class ClientBlockchain {
    static InetAddress aHost;

    // Specify the server's port number.
    static int serverPort;

    // Create a DatagramSocket for sending and receiving UDP packets.
    static Socket aSocket = null;

    public static void main(String args[]) {
        System.out.println("The Blockchain client is running.");
        try {

            // Create a BufferedReader to read lines of text from the console.
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            // Enter a loop to read lines of text from the console and send them to the server.
            String nextLine;
            //get server port number
            serverPort = 7777;
            //open socket for connection to server port
            aSocket = new Socket("localhost", serverPort);

            RequestMessage requestMessage = null;
            ResponseMessage responseMessage = null;

            BufferedReader in = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(aSocket.getOutputStream())));

            boolean exit = false;
            int choice;
            //get input from user for choice
            Scanner sc = new Scanner(System.in);
            Object rObject = null;
            while (!exit) {
                displayMenu(); // Display the menu of available options
                System.out.print("Enter a choice: ");

                if (sc.hasNextInt()) {
                    choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 0 -> {
                            // Option 0: Request to view basic blockchain status
                            requestMessage = new RequestMessage(0);
                            out.println(requestMessage.getJsonRequest());
                            out.flush();

                            // Receive and display the response
                            String response = in.readLine();
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            System.out.println("Response Message: " + jsonObject.toString().replace("\\", ""));
                            break;
                        }
                        case 1 -> {
                            // Option 1: Request to add a transaction to the blockchain
                            System.out.println("Add a transaction to the blockchain.");
                            System.out.println("Enter difficulty > 0");
                            int difficulty = sc.nextInt();
                            sc.nextLine();
                            System.out.println("Enter transaction");
                            String transaction = sc.nextLine();

                            // Create a request message and send it to the server
                            requestMessage = new RequestMessage(1, difficulty, transaction);
                            out.println(requestMessage.getJsonRequest());
                            out.flush();

                            // Receive and display the response
                            String response = in.readLine();
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            System.out.println("Response Message: " + jsonObject.toString().replace("\\", ""));
                            break;
                        }
                        case 2 -> {
                            // Option 2: Request to add a transaction to the blockchain (no details provided)
                            System.out.println("Add a transaction to the blockchain.");
                            requestMessage = new RequestMessage(2);
                            out.println(requestMessage.getJsonRequest());
                            out.flush();

                            // Receive and display the response
                            String response = in.readLine();
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            System.out.println("Response Message: " + jsonObject.toString().replace("\\", ""));
                            break;
                        }
                        case 3 -> {
                            // Option 3: Request to display the entire blockchain
                            System.out.println("Display the entire blockchain.");
                            requestMessage = new RequestMessage(3);
                            out.println(requestMessage.getJsonRequest());
                            out.flush();

                            // Receive and display the response
                            String response = in.readLine();
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            System.out.println("Response Message: " + jsonObject.toString().replace("\\", ""));
                            break;
                        }
                        case 4 -> {
                            // Option 4: Request to corrupt a block in the chain
                            System.out.println("Corrupt the chain.");
                            System.out.println("Enter block ID of block to Corrupt");
                            int blockID = sc.nextInt();
                            sc.nextLine();
                            System.out.println("Enter new data for block " + blockID);
                            String newData = sc.nextLine();

                            // Create a request message and send it to the server
                            requestMessage = new RequestMessage(4, blockID, newData);
                            out.println(requestMessage.getJsonRequest());
                            out.flush();

                            // Receive and display the response
                            String response = in.readLine();
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            System.out.println("Response Message: " + jsonObject.toString().replace("\\", ""));
                            break;
                        }
                        case 5 -> {
                            // Option 5: Request to repair the chain
                            System.out.println("Repair the chain.");
                            requestMessage = new RequestMessage(5);
                            out.println(requestMessage.getJsonRequest());
                            out.flush();

                            // Receive and display the response
                            String response = in.readLine();
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            System.out.println("Response Message: " + jsonObject.toString().replace("\\", ""));
                            break;
                        }
                        case 6 -> {
                            // Option 6: Request to exit the client
                            requestMessage = new RequestMessage(6);
                            out.println(requestMessage.getJsonRequest());
                            out.flush();
                            exit = true;
                        }
                    }
                } else {
                    // Invalid input
                    System.out.println("Invalid input");
                    sc.next();
                    continue;
                }
            }


        } catch (SocketException e) {
            // Handle SocketException (related to socket errors).
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            // Handle IOException (related to input/output errors).
            System.out.println("IO Exception: " + e.getMessage());
        }

    }

    //display menu
    private static void displayMenu() {
        System.out.println("0. View basic blockchain status.");
        System.out.println("1. Add a transaction to the blockchain.");
        System.out.println("2. Verify the blockchain.");
        System.out.println("3. View the blockchain.");
        System.out.println("4. Corrupt the chain.");
        System.out.println("5. Hide the corruption by repairing the chain.");
        System.out.println("6. Exit client.");
    }

}
