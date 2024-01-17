Project Objective:

Distinguish between functional and non-functional characteristics of distributed systems.
Understand how blockchains technology works by creating a blockchain API that a remote client interacts with.
Note that the blockchain implemented here is not true to real blockchains, which are decentralized and include P2P communication. This blockchain also does not include Merkle Trees, it simply serves as a foundation of understanding.
Use JSON messaging for communicaiton through TCP.
Tasks:

Build a standalone blockchain that allows the user to:
View basic blockchain status. This includes:
Number of transactions on the chain
Difficulty of the most recent block
Total difficulty for all blocks
Hashes per second of the current machine
Expected hashes for whole chain
Nonce for the most recent block
Hash of the whole chain
Add a transaction to the chain. For the purposes of this simple blockchain, a transaction is simply a string that says something like "Bob pays Joe 50 Coins". The user also chooses a difficulty in this step, which is the number of leading 0's in the hash of the block.
Verify the blockchain.
Corrupt the chain.
Repair the chain and eliminate corruption.
Implement the blockchain completed in Task 1 on a remote server.
Create a client that communicates with the Blockchain server through TCP sockets using JSON messages.
Topics/Skills covered:

Blockchain fundamentals
Hashing
Proof-of-work
TCP communication
JSON messages
Non-functional Requirements (performance, usability, reliability, etc)
