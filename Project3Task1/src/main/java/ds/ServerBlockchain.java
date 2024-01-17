package ds;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

//Shivaani Krishnakumar
//shivaank
//used chatgpt for generating comments
//reference Project2Task4 code for TCP client and server interactions
public class ServerBlockchain {
    static BlockChain mainChain;

    ServerBlockchain() {
        mainChain = new BlockChain();
    }
    public static void main(String args[]) throws NoSuchAlgorithmException {
        System.out.println("Blockchain server running");
        ServerBlockchain serverBlockchain = new ServerBlockchain();
        addGenesisBlock();
        performOperations();
    }

    static void addGenesisBlock() throws NoSuchAlgorithmException {
        Block genesis = new Block(0, BlockChain.getTime(), "Genesis", 2);
        genesis.proofOfWork();
        genesis.setPreviousHash("");
        BlockChain.addBlock(genesis);
    }

    static void performOperations() {
        Socket clientSocket = null;
        boolean exit = false;
        int choice;
        try {
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            int serverPort = 7777;
            ServerSocket aSocket = new ServerSocket(serverPort);
            // Accept connections from client sockets
            clientSocket = aSocket.accept();
            String receivedData;
            Scanner in;
            PrintWriter out;
            RequestMessage requestMessage = null;
            while (true) {
                in = new Scanner(clientSocket.getInputStream());
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                String tx = "";
                int difficulty = 0;
                int index = 0;

                // Check if there's input from the client
                if (in.hasNext()) {
                    System.out.println("We have a visitor");
                    receivedData = in.nextLine();

                    // Parse the received JSON string into a JsonObject
                    JsonObject jsonObject = JsonParser.parseString(receivedData).getAsJsonObject();
                    System.out.println("Request Message" + jsonObject);

                    // Parse choice and other fields from the request JSON object
                    choice = Integer.parseInt(jsonObject.get("operation").toString());

                    if (jsonObject.get("tx") != null) {
                        tx = jsonObject.get("tx").getAsString();
                    }
                    if (jsonObject.get("difficulty") != null) {
                        difficulty = Integer.parseInt(jsonObject.get("difficulty").toString());
                    }
                    if (jsonObject.get("index") != null) {
                        index = Integer.parseInt(jsonObject.get("index").toString());
                    }

                    // Process the request based on the chosen operation
                    switch (choice) {
                        case 0 -> {
                            // Operation Requested: 0 - View basic blockchain status

                            // Retrieve blockchain information
                            int size = mainChain.getChainSize();
                            int difficultyChoice = mainChain.getLatestBlock().getDifficulty();
                            double totalDifficulty = BlockChain.getTotalDifficulty();
                            int hashesPerSecond = mainChain.getHashesPerSecond();
                            double totalExpectedHash = BlockChain.getTotalExpectedHashes();
                            BigInteger nonce = mainChain.getLatestBlock().getNonce();
                            String hash = mainChain.getLatestBlock().calculateHash();

                            // Create a response message with blockchain status
                            ResponseMessage responseMessage = new ResponseMessage(0, size, difficultyChoice, totalDifficulty, hashesPerSecond, totalExpectedHash, nonce, hash);

                            // Print response
                            System.out.println("Response: " + responseMessage.getJsonResponse().toString());

                            // Send the response to the client
                            out.println(responseMessage.getJsonResponse());
                            out.flush();
                            break;
                        }
                        case 1 -> {
                            // Operation Requested: 1 - Add a transaction to the blockchain

                            // Record the start time for measuring execution time
                            Timestamp start = BlockChain.getTime();

                            // Create a new block with the provided transaction and difficulty
                            Block newBlock = new Block(mainChain.getChainSize(), BlockChain.getTime(), tx, difficulty);
                            newBlock.setPreviousHash(mainChain.getChainHash());
                            newBlock.proofOfWork();
                            BlockChain.addBlock(newBlock);

                            // Record the end time for measuring execution time
                            Timestamp end = BlockChain.getTime();
                            long elapsed = (end.getTime() - start.getTime());

                            // Create a response message with the execution time
                            ResponseMessage responseMessage = new ResponseMessage(1, elapsed);

                            // Print response
                            System.out.println("Response: " + responseMessage.getJsonResponse().toString());

                            // Send the response to the client
                            out.println(responseMessage.getJsonResponse());
                            out.flush();
                            break;
                        }
                        case 2 -> {
                            // Operation Requested: 2 - Verify the blockchain

                            // Record the start time for measuring execution time
                            Timestamp start = BlockChain.getTime();

                            // Check the validity of the blockchain
                            String verification = BlockChain.isChainValid();

                            // Record the end time for measuring execution time
                            Timestamp end = BlockChain.getTime();
                            double elapsedTime = (end.getTime() - start.getTime());

                            // Create a response message with the execution time and verification result
                            ResponseMessage responseMessage = new ResponseMessage(2, elapsedTime, verification);

                            // Print response
                            System.out.println("Response: " + responseMessage.getJsonResponse().toString());

                            // Send the response to the client
                            out.println(responseMessage.getJsonResponse());
                            out.flush();
                            break;
                        }
                        case 3 -> {
                            // Operation Requested: 3 - Display the entire blockchain

                            // Create a response message with the blockchain contents
                            ResponseMessage responseMessage = new ResponseMessage(3, mainChain.toString());

                            // Print response
                            System.out.println("Response: " + responseMessage.getJsonResponse().toString());

                            // Send the response to the client
                            out.println(responseMessage.getJsonResponse());
                            out.flush();
                            break;
                        }
                        case 4 -> {
                            // Operation Requested: 4 - Corrupt a block in the chain

                            // Change the data of the specified block
                            mainChain.getBlock(index).setData(tx);

                            // Create a response message with the updated information
                            ResponseMessage responseMessage = new ResponseMessage(4, index, tx);

                            // Print response
                            System.out.println("Response: " + responseMessage.getJsonResponse().toString());

                            // Send the response to the client
                            out.println(responseMessage.getJsonResponse());
                            out.flush();
                            break;
                        }
                        case 5 -> {
                            // Operation Requested: 5 - Repair the chain

                            // Record the start time for measuring execution time
                            Timestamp start = BlockChain.getTime();

                            // Repair the blockchain
                            mainChain.repairChain();

                            // Record the end time for measuring execution time
                            Timestamp end = BlockChain.getTime();
                            double elapsedTime = (end.getTime() - start.getTime());

                            // Create a response message with the execution time
                            ResponseMessage responseMessage = new ResponseMessage(5, elapsedTime);

                            // Print response
                            System.out.println("Response: " + responseMessage.getJsonResponse().toString());

                            // Send the response to the client
                            out.println(responseMessage.getJsonResponse());
                            out.flush();
                            break;
                        }
                        case 6 -> {
                            // Operation Requested: 6 - Exit the client
                            System.out.println("Operation Requested: 6" + "\n");
                            System.out.println("Client shutting. Server is still running!\n");
                            break;
                        }
                    }
                } else {
                    // If no input is available from the client, wait for a new connection
                    clientSocket = aSocket.accept();
                }
            }
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if ( clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }



}
