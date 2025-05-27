### **How to Compile and Run the Project**

This project includes a **key-value store** that works with both **TCP and UDP** communication.
Below are the steps to **compile** and **run** the servers and clients.

---

### **1. Compiling the Java Files**
Before running the project, you need to **compile all Java files**.

1. Open a terminal and navigate to the `src` directory where all Java files are located.
2. Run the following command to compile all files:

   ```
   javac -d out $(find . -name "*.java")
   ```

    - This will compile all Java files and store the compiled class files in the `out` directory.

---

### **2. Running the TCP Server and Client**
#### **Start the TCP Server**
- Open a terminal.
- Run the following command:

  ```
  java -cp out server.TCPServer <port>
  ```

- Example:
  ```
  java -cp out server.TCPServer 9000
  ```
  This starts the **TCP server** on port **9000**.

#### **Run the TCP Client**
- Open another terminal.
- Run the following command:

  ```
  java -cp out client.TCPClient <hostname/IP> <port>
  ```

- Example:
  ```
  java -cp out client.TCPClient localhost 9000
  ```
  This connects the TCP client to the server running on **localhost:9000**.

---

### **3. Running the UDP Server and Client**
#### **Start the UDP Server**
- Open a terminal.
- Run the following command:

  ```
  java -cp out server.UDPServer <port>
  ```

- Example:
  ```
  java -cp out server.UDPServer 8000
  ```
  This starts the **UDP server** on port **8000**.

#### **Run the UDP Client**
- Open another terminal.
- Run the following command:

  ```
  java -cp out client.UDPClient <hostname/IP> <port>
  ```

- Example:
  ```
  java -cp out client.UDPClient localhost 8000
  ```
  This connects the UDP client to the server running on **localhost:8000**.

---

### **4. Using the Clients**
Once the client is running, it is very intuitive:

- **PUT key value** → Stores a value for a given key.
- **GET key** → Retrieves the value of the given key.
- **DELETE key** → Removes the key-value pair from the store.
- **EXIT** → Closes the client.

Example:
```
PUT name John
GET name
DELETE name
EXIT
```

---

### **5. Stopping the Servers**
- To stop the server, press **CTRL + C** in the terminal where it is running.

---

Now you are ready to compile and run the project!