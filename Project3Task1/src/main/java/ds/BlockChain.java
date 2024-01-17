package ds;

import com.google.gson.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

// Author: Shivaani Krishnakumar
// Username: shivaank
// This class defines a blockchain and its operations. It includes methods for managing the blockchain,
// verifying its integrity, performing proof of work, and more.

public class BlockChain {
    public static ArrayList<Block> blockchain;
    public static int hashesPerSecond;
    public static String chainHash;

    public BlockChain() {
        blockchain = new ArrayList<Block>();
        chainHash = "";

        // Calculate and set hashes per second
        try {
            computeHashesPerSecond();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Setter for the chain hash
    private void setChainHash(String hash) {
        chainHash = hash;
    }

    // Chain hash getter
    public String getChainHash() {
        return chainHash;
    }

    // Time getter
    public static Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    // Latest block getter
    public Block getLatestBlock() {
        int i = blockchain.size() - 1;
        return blockchain.get(i);
    }

    // Chain size getter
    public int getChainSize() {
        return blockchain.size();
    }

    // Gets block at ID
    public Block getBlock(int i) {
        return blockchain.get(i);
    }

    // Gets total difficulty
    public static int getTotalDifficulty() {
        int runningtotal = 0;
        for (int i = 0; i < blockchain.size(); i++) {
            runningtotal = runningtotal + blockchain.get(i).getDifficulty();
        }
        return runningtotal;
    }

    public static double getTotalExpectedHashes() {
        double total = 0;
        for (int i = 0; i < blockchain.size(); i++) {
            total = total + Math.pow(16, blockchain.get(i).getDifficulty());
        }
        return total;
    }

    public void repairChain() throws NoSuchAlgorithmException {
        // When only one block is there
        if (blockchain.size() == 1) {
            // Reset previous hash and recompute proof of work
            blockchain.get(0).setPreviousHash("");
            blockchain.get(0).proofOfWork();
        }
        // For longer chains
        if (blockchain.size() > 1) {
            for (int i = 1; i < blockchain.size(); i++) {
                // Reset previous hash and recompute proof of work
                blockchain.get(i).setPreviousHash(blockchain.get(i - 1).calculateHash());
                blockchain.get(i).proofOfWork();
            }
        }

        // Reset chain hash
        chainHash = blockchain.get(blockchain.size() - 1).calculateHash();
    }

    public static String isChainValid() throws NoSuchAlgorithmException {
        String result = "TRUE";
        if (blockchain.size() == 0) {
            return "FALSE";
        } else if (blockchain.size() == 1) {
            if (!blockchain.get(0).calculateHash().equals(chainHash)) {
                return "Chain mismatch";
            } else {
                if (!checkReqZero(blockchain.get(0))) {
                    return "Proof of work error!!!";
                } else {
                    return "Chain verified: TRUE";
                }
            }
        } else {
            for (int i = 1; i < blockchain.size(); i++) {
                if (!blockchain.get(i).getPreviousHash().equals(blockchain.get(i - 1).calculateHash())) {
                    return "Previous hash mismatch";
                } else {
                    if (!checkReqZero(blockchain.get(i))) {
                        return "Node " + i + " is invalid." + "Does not start with " + blockchain.get(i).getDifficulty() + " zeros";
                    }
                }
                if (i == blockchain.size() - 1) {
                    if (!blockchain.get(i).calculateHash().equals(chainHash)) {
                        return "Chain mismatch";
                    } else {
                        return "Chain verified: TRUE";
                    }
                }
            }
        }
        return result;
    }

    public static boolean checkReqZero(Block b) throws NoSuchAlgorithmException {
        String hashVal = "";
        String reqStartVal = "0".repeat(Math.max(0, b.getDifficulty()));
        hashVal = b.calculateHash();
        return hashVal.startsWith(reqStartVal);
    }

    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    public static void computeHashesPerSecond() throws UnsupportedEncodingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Timestamp t1 = getTime();
        MessageDigest msg = MessageDigest.getInstance("SHA-256");
        String dataToHash = "00000000";

        for (int i = 0; i < 2000000; i++) {
            byte[] hash = msg.digest(dataToHash.getBytes());
        }
        Timestamp t2 = getTime();

        double diff = t2.getTime() - t1.getTime();

        hashesPerSecond = (int) (2000000 / diff);
    }

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
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        JsonObject jsonObject = new JsonObject();
        JsonArray dsChainArray = new JsonArray();

        for (int i = 0; i < blockchain.size(); i++) {
            Block block = blockchain.get(i);
            System.out.println(block.toString());
            dsChainArray.add(new JsonParser().parse(block.toString()));
        }
        String val = dsChainArray.toString().replace("\\", "");
        jsonObject.addProperty("ds_chain", val);
        jsonObject.addProperty("chainHash", chainHash);

        String jsonString = gson.toJson(jsonObject);

        // Replace any backslashes in the JSON string
        jsonString = jsonString.replace("\\", "");

        // Replace the default Gson formatting to separate elements on new lines
        return jsonString.replace("},{", "},\n{");
    }

    public static void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        chainHash = newBlock.calculateHash();
        blockchain.add(newBlock);
    }
}
