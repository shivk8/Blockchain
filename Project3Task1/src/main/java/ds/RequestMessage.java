package ds;

import com.google.gson.JsonObject;

// Author: Shivaani Krishnakumar
// Username: shivaank
// This class defines a RequestMessage for handling requests in a network communication system.
// It includes various constructors for different request scenarios.

public class RequestMessage {

    private JsonObject jsonRequest;

    // Default constructor
    public RequestMessage() {
    }

    // Constructor for request with only the operation code
    public RequestMessage(int operation) {
        jsonRequest = new JsonObject();
        jsonRequest.addProperty("operation", operation);
    }

    // Constructor for request with an operation code, an option, and transaction data
    public RequestMessage(int operation, int option, String tx) {
        jsonRequest = new JsonObject();
        jsonRequest.addProperty("operation", operation);

        if (operation == 1) {
            jsonRequest.addProperty("difficulty", option);
        } else if (operation == 4) {
            jsonRequest.addProperty("index", option);
        }
        jsonRequest.addProperty("tx", tx);
    }

    // Getter method for obtaining the JSON request
    public JsonObject getJsonRequest() {
        return jsonRequest;
    }
}
