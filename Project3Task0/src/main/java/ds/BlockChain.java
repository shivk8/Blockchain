package ds;

import com.google.gson.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

//Shivaani Krishnakumar
//shivaank
//used chatgpt for generating comments

public class BlockChain {
    public static ArrayList<Block> blockchain;
    public static int hashesPerSecond;
    public static String chainHash;
    /**
     * Constructor for the BlockChain class.
     * Initializes the blockchain and computes hashes per second.
     */
    public BlockChain() {
        blockchain = new ArrayList<Block>();
        chainHash = "";
        try {
            computeHashesPerSecond();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            // If there's an error computing hashes per second, throw a runtime exception.
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //Blockchain javadoc question
        /*
            As the difficulty level increases, the time taken to mine a block increases. This is because as we become very specific about
            the hash value in terms of the number of leading zeros, it becomes computationally intensive in terms of the number of iterations
            before we find the nonce. As a result, time taken to add a block to the chain goes up.
            Time taken to verify the chain is relatively affected less as the difficulty level increases. Because here we do not have to recompute PoW, instead
            just check for the leading zeros.
            However, repairing becomes cumbersome and the time taken increases as the difficulty level increases. This is because we have to recompute PoW for each block.
        */
        // Create a new instance of the BlockChain class.
        BlockChain mainChain = new BlockChain();

        // Create and add the Genesis block.
        Block genesis = new Block(0, getTime(), "Genesis", 2);
        genesis.proofOfWork();
        genesis.setPreviousHash("");
        addBlock(genesis);

        // Initialize variables for user interaction.
        boolean exit = false;
        int choice;

        // Create a scanner to get input from the user.
        Scanner sc = new Scanner(System.in);

        // Main program loop for user interaction.
        while (!exit) {
            // Display the user menu.
            displayMenu();

            // Prompt the user to enter a choice.
            System.out.print("Enter a choice: ");

            if (sc.hasNextInt()) {
                // Read the user's choice.
                choice = sc.nextInt();
                sc.nextLine();

                // Process the user's choice.
                switch (choice) {
                    case 0 -> {
                        // Display basic blockchain status.
                        System.out.println("View basic blockchain status.");
                        System.out.println("Current size of chain: " + blockchain.size());
                        System.out.println("Difficulty of most recent block: " + blockchain.get(blockchain.size() - 1).getDifficulty());
                        System.out.println("Total difficulty for all blocks: " + getTotalDifficulty());
                        System.out.println("Approximate hashes per second on this machine: " + hashesPerSecond);
                        System.out.println("Expected number of hashes required for the whole chain: " + getTotalExpectedHashes());
                        System.out.println("Nonce for most recent block: " + blockchain.get(blockchain.size() - 1).getNonce());
                        System.out.println("Chain hash: " + chainHash);
                        break;
                    }
                    case 1 -> {
                        // Add a transaction to the blockchain.
                        System.out.println("Add a transaction to the blockchain.");
                        System.out.println("Enter difficulty > 0");
                        int difficulty = sc.nextInt();
                        sc.nextLine();
                        System.out.println("Enter transaction");
                        String transaction = sc.nextLine();
                        Timestamp start = getTime();
                        Block newBlock = new Block(blockchain.size(), getTime(), transaction, difficulty);
                        newBlock.setPreviousHash(chainHash);
                        newBlock.proofOfWork();
                        Timestamp end = getTime();
                        addBlock(newBlock);
                        System.out.println("Total execution time to add this block was: " + (end.getTime() - start.getTime()) + " milliseconds");
                        break;
                    }
                    case 2 -> {
                        // Verify the blockchain.
                        System.out.println("Verify the blockchain.");
                        Timestamp start = getTime();
                        System.out.println(isChainValid());
                        Timestamp end = getTime();
                        System.out.println("Total execution time required to verify the chain was " + (end.getTime() - start.getTime()) + " milliseconds");
                        break;
                    }
                    case 3 -> {
                        // View the blockchain.
                        System.out.println("View the blockchain.");
                        System.out.println(mainChain.toString());
                        break;
                    }
                    case 4 -> {
                        // Corrupt the chain.
                        System.out.println("Corrupt the chain.");
                        System.out.println("Enter block ID of block to Corrupt");
                        int blockID = sc.nextInt();
                        sc.nextLine();
                        System.out.println("Enter new data for block " + blockID);
                        String newData = sc.nextLine();
                        blockchain.get(blockID).setData(newData);
                        System.out.println("Block " + blockID + " now holds " + newData);
                        break;
                    }
                    case 5 -> {
                        // Repair the corrupted chain.
                        System.out.println("Repair the corrupted chain.");
                        Timestamp start = getTime();
                        mainChain.repairChain();
                        Timestamp end = getTime();
                        System.out.println("Total execution time to add this block was: " + (end.getTime() - start.getTime()) + " milliseconds");
                        break;
                    }
                    case 6 -> {
                        // Exit the program.
                        exit = true;
                        break;
                    }
                }
            } else {
                // Invalid input from the user.
                System.out.println("Invalid input");
                sc.next();
                continue;
            }
        }
    }


    private void setChainHash(String hash) {
        // Set the chainHash to the provided hash value.
        chainHash = hash;
    }

    // Chain hash getter
    public java.lang.String getChainHash(){
        // Get the current chainHash value.
        return chainHash;
    }

    // Time getter
    public static java.sql.Timestamp getTime(){
        // Get the current system timestamp.
        return new Timestamp(System.currentTimeMillis());
    }

    // Latest block getter
    public Block getLatestBlock(){
        // Get and return the latest block in the blockchain.
        int i = blockchain.size() - 1;
        return blockchain.get(i);
    }

    // Chain size getter
    public int getChainSize(){
        // Get and return the size of the blockchain.
        return blockchain.size();
    }

    // Gets a block at a specified index
    public Block getBlock(int i){
        // Get and return the block at the specified index.
        return blockchain.get(i);
    }

    // Gets the total difficulty of all blocks in the blockchain
    public static int getTotalDifficulty(){
        int runningtotal = 0;
        for(int i = 0; i < blockchain.size(); i++){
            runningtotal = runningtotal + blockchain.get(i).getDifficulty();
        }
        return runningtotal;
    }

    // Gets the total expected number of hashes required for the whole chain
    public static double getTotalExpectedHashes(){
        double total = 0;
        for(int i = 0; i < blockchain.size(); i++){
            total = total + Math.pow(16, blockchain.get(i).getDifficulty());
        }
        return total;
    }

    // Repairs the chain, updating previous hashes and recomputing proof of work
    public void repairChain() throws NoSuchAlgorithmException {
        // When there's only one block in the chain
        if(blockchain.size() == 1) {
            // Reset the previous hash and recompute proof of work.
            blockchain.get(0).setPreviousHash("");
            blockchain.get(0).proofOfWork();
        }
        // For longer chains
        if(blockchain.size() > 1) {
            for (int i = 1; i < blockchain.size(); i++) {
                // Reset the previous hash and recompute proof of work for each block.
                blockchain.get(i).setPreviousHash(blockchain.get(i-1).calculateHash());
                blockchain.get(i).proofOfWork();
            }
        }

        // Reset the chainhash based on the last block's hash.
        chainHash = blockchain.get(blockchain.size()-1).calculateHash();
    }

    // Verifies the validity of the blockchain
    public static String isChainValid() throws NoSuchAlgorithmException {
        String result = "TRUE";
        if (blockchain.size() == 0){
            return "FALSE";
        } else if (blockchain.size() == 1) {
            if (!blockchain.get(0).calculateHash().equals(chainHash)) {
                return "Chain mismatch";
            } else {
                if (!checkReqZero(blockchain.get(0))) {
                    return "Node 0"  + " is invalid." + "Does not start with " + blockchain.get(0).getDifficulty() + " zeros";
                } else {
                    return "Chain verification: TRUE";
                }
            }
        } else {
            for (int i = 1; i < blockchain.size(); i++){
                if (!blockchain.get(i).getPreviousHash().equals(blockchain.get(i-1).calculateHash())){
                    return "Previous hash mismatch";
                } else {
                    if (!checkReqZero(blockchain.get(i))){
                        return "Node " + i + " is invalid." + "Does not start with " + blockchain.get(i).getDifficulty() + " zeros";
                    }
                }
                if (i == blockchain.size() - 1){
                    if (!blockchain.get(i).calculateHash().equals(chainHash)){
                        return "Chain mismatch";
                    } else {
                        return "Chain verification: TRUE";
                    }
                }
            }
        }
        return result;
    }

    // Checks if the hash of a block starts with the required number of zeros
    public static boolean checkReqZero(Block b) throws NoSuchAlgorithmException {
        String hashVal = "";
        String reqStartVal = "0".repeat(Math.max(0, b.getDifficulty()));
        hashVal = b.calculateHash();
        return hashVal.startsWith(reqStartVal);
    }

    // Gets the number of hashes computed per second
    public int getHashesPerSecond(){
        return hashesPerSecond;
    }

    // Computes the number of hashes per second
    public static void computeHashesPerSecond() throws UnsupportedEncodingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Timestamp t1 = getTime();
        MessageDigest msg = MessageDigest.getInstance("SHA-256");
        String dataToHash = "00000000";

        for(int i = 0; i < 2000000; i++){
            byte[] hash = msg.digest(dataToHash.getBytes());
        }
        Timestamp t2 = getTime();

        double diff = t2.getTime() - t1.getTime();

        // Compute and set the number of hashes per second.
        hashesPerSecond = (int) (2000000/diff);
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

    @Override
    public String toString() {
        // Create a GsonBuilder for pretty printing JSON.
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        JsonObject jsonObject = new JsonObject();
        JsonArray dsChainArray = new JsonArray();

        // Iterate through the blockchain and add each block as a JSON element to dsChainArray.
        for (int i = 0; i < blockchain.size(); i++) {
            Block block = blockchain.get(i);
            dsChainArray.add(new JsonParser().parse(block.toString()));
        }

        // Convert dsChainArray to a JSON string and replace any backslashes with an empty string.
        String val = dsChainArray.toString().replace("\\","");
        jsonObject.addProperty("ds_chain", val);
        jsonObject.addProperty("chainHash", chainHash);

        // Convert the JSON object to a JSON string using Gson.
        String jsonString = gson.toJson(jsonObject);

        // Replace any backslashes in the JSON string.
        jsonString = jsonString.replace("\\", "");

        // Replace the default Gson formatting to separate elements on new lines.
        return jsonString.replace("},{", "},\n{");
    }

    //add block to blockchain
    public static void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        chainHash = newBlock.calculateHash();
        blockchain.add(newBlock);
    }

}
