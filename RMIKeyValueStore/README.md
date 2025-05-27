### **How to Compile and Run the Project**

This project includes a **key-value store** that works using **RMI**.
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

### **2. Running the Server and Client**
#### **Start the Server**
- Open a terminal.
- Run the following command:

  ```
  java -cp out server.KeyValueStoreServer <port>
  ```

- Example:
  ```
  java -cp out server.KeyValueStoreServer 1099
  ```
  This starts the **server** and **RMI Registry** on port **1099**.

#### **Run the Client**
- Open another terminal.
- Run the following command:

  ```
  java -cp out client.Client <hostname/IP> <port>
  ```

- Example:
  ```
  java -cp out client.Client localhost 1099
  ```
  This connects the client to the server running on **localhost:1099**.

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
- To stop the server, press **CTRL + C** or **Stop** from your IDE in the terminal where it is running.

---

Now you are ready to compile and run the project!