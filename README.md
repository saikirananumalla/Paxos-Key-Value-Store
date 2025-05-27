# 🗝 Paxos-Based Distributed Key-Value Store

A fault-tolerant, distributed key-value store built in Java using multithreading, **Remote Method Invocation (RMI)**, and the **Paxos consensus algorithm**. This system ensures consistency across multiple replicas and handles node failures gracefully using simulated thread crashes and health checks.

---

## 🔍 Overview

This project was developed as part of a Distributed Systems course and focuses on practical implementation of consensus protocols. It uses Paxos for achieving agreement between replicated servers, ensuring the correct value is committed even during crashes or network partitions.

---

## ⚙️ Features

- 💾 Key-Value storage with **PUT**, **GET**, and **DELETE**
- 📡 **Java RMI** for inter-replica communication
- 🔁 **Paxos algorithm** to handle replica failures and ensure consistency
- 🧠 Simulated node/thread failure and restart logic
- 🧵 Multithreaded servers for concurrent request handling
- 🧪 Modular design for easy extension and testing

---

## 🛠 Tech Stack

- **Language:** Java
- **Communication:** Java RMI
- **Consensus Protocol:** Paxos (multi-threaded roles)
- **Build:** Manual (javac)
- **Architecture:** Peer-to-peer replica coordination

---

## 📦 Compilation

Open a terminal and navigate to the `src` directory, then run:

```bash
javac -d out $(find . -name "*.java")
