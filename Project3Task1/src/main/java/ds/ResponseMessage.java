package ds;

import com.google.gson.JsonObject;
import java.math.BigInteger;

// Author: Shivaani Krishnakumar
// Username: shivaank
// This class defines a ResponseMessage for handling responses in a network communication system.
// It includes various constructors for different response scenarios.

public class ResponseMessage {

    private JsonObject jsonResponse;

    // Default constructor
    public ResponseMessage() {
    }

    // Constructor for response with only the operation code
    public ResponseMessage(int operation) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
    }

    // Constructor for response with an operation code and elapsed time
    public ResponseMessage(int operation, double elapsedTime) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
        jsonResponse.addProperty("elapsedTime", elapsedTime);
    }

    // Constructor for response with an operation code, block index, and transaction data
    public ResponseMessage(int operation, int index, String tx) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
        jsonResponse.addProperty("index", index);
        jsonResponse.addProperty("tx", tx.replace("\\", "")); // Remove backslashes
    }

    // Constructor for response with an operation code and the entire blockchain
    public ResponseMessage(int operation, String wholechain) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
        jsonResponse.addProperty("wholeChain", wholechain.replace("\\", "")); // Remove backslashes
    }

    // Constructor for response with an operation code, elapsed time, and a verification status
    public ResponseMessage(int operation, double elapsedTime, String verification) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);
        jsonResponse.addProperty("elapsedTime", elapsedTime);
        jsonResponse.addProperty("verificationStatus", verification.replace("\\", "")); // Remove backslashes
    }

    // Constructor for a detailed response with various attributes
    public ResponseMessage(int operation, int size, int difficulty, double totalDifficulty, int hashesPerSecond,
                           double totalExpectedHashes, BigInteger nonce, String hash) {
        jsonResponse = new JsonObject();
        jsonResponse.addProperty("operation", operation);

        // Add all requested information to a JsonObject
        jsonResponse.addProperty("size", size);
        jsonResponse.addProperty("difficulty", difficulty);
        jsonResponse.addProperty("totalDifficulty", totalDifficulty);
        jsonResponse.addProperty("hashesPerSecond", hashesPerSecond);
        jsonResponse.addProperty("totalExpectedHashes", totalExpectedHashes);
        jsonResponse.addProperty("nonce", nonce);
        jsonResponse.addProperty("hash", hash.replace("\\", "")); // Remove backslashes
    }

    // Getter method for obtaining the JSON response
    public JsonObject getJsonResponse() {
        return jsonResponse;
    }
}
