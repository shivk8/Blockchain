package ds;

import com.google.gson.JsonObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

// Author: Shivaani Krishnakumar
// Username: shivaank
// This class defines a Block for use in a blockchain. It includes methods to calculate a hash,
// perform proof of work, and get and set various block attributes.

public class Block {

    private int index;
    private Timestamp timeStamp;
    private String data;
    private int difficulty;
    private String previousHash;
    private BigInteger nonce;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    // Constructor to initialize the block
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        timeStamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        nonce = BigInteger.ZERO;
        previousHash = "";
    }

    // Helper method to convert bytes to a hexadecimal string
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    // Method to calculate the hash of the block
    public String calculateHash() throws NoSuchAlgorithmException {
        String dataToHash = index + timeStamp.toString() + data + previousHash + nonce + difficulty;
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        byte[] hashB = md.digest(dataToHash.getBytes());
        return bytesToHex(hashB);
    }

    // Method to perform proof of work and return the resulting hash
    public String proofOfWork() throws NoSuchAlgorithmException {
        boolean found = false;
        String hashVal = "";
        String reqStartVal = "";
        for (int i = 0; i < difficulty; i++) {
            reqStartVal += "0";
        }
        while (!found) {
            hashVal = calculateHash();
            if (!hashVal.startsWith(reqStartVal)) {
                nonce = nonce.add(BigInteger.ONE);
            } else {
                found = true;
            }
        }
        return hashVal;
    }

    // Getter and setter methods for block attributes
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    // Method to represent the block as a JSON-like string
    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("time stamp", String.valueOf(timeStamp));
        jsonObject.addProperty("Tx", data);
        jsonObject.addProperty("PrevHash", previousHash);
        jsonObject.addProperty("nonce", nonce);
        jsonObject.addProperty("difficulty", difficulty);

        // Create a string from the JSON object and remove backslashes
        return jsonObject.toString().replace("\\", "");
    }
}
