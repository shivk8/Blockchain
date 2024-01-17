package ds;

import com.google.gson.JsonObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.lang.String;

// Shivaani Krishnakumar
// shivaank
// Used chatgpt for generating comments

public class Block {
    private int index;
    private Timestamp timeStamp;
    private String data;
    private int difficulty;
    private String previousHash;
    private BigInteger nonce;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        timeStamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        nonce = BigInteger.ZERO;
        previousHash = "";
    }

    // Reused code from lab1 to convert bytes to hexadecimal representation
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String calculateHash() throws NoSuchAlgorithmException {
        // Calculate the hash of the block based on its components
        String dataToHash = index + timeStamp.toString() + data + previousHash + nonce + difficulty;
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        byte[] hashB = md.digest(dataToHash.getBytes());
        return bytesToHex(hashB);
    }

    public String proofOfWork() throws NoSuchAlgorithmException {
        // Perform proof of work to find a hash that meets the required difficulty level
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

    @Override
    public String toString() {
        // Create a JSON representation of the block's attributes and remove backslashes
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("time stamp", String.valueOf(timeStamp));
        jsonObject.addProperty("Tx", data);
        jsonObject.addProperty("PrevHash", previousHash);
        jsonObject.addProperty("nonce", nonce);
        jsonObject.addProperty("difficulty", difficulty);

        // Create a string from the JSON object and remove backslashes
        return jsonObject.toString().replace("\\","");
    }
}
