 Data Backup and Recovery System

Project Overview

The **Data Backup and Recovery System** is a Java-based application designed to securely store, manage, and restore data using multiple formats such as Binary, CSV, and JSON.

This project demonstrates real-world concepts like serialization, file handling, compression, and version control.

 Features

*  Object Serialization (Binary Backup)
*  CSV Import & Export
* JSON Support (Jackson/Gson)
*  Backup Version Management
* File Compression (ZIP)
*  Automatic Backup Scheduling
*  File Comparison & Merge
*  Recovery Point Management

---

 Technologies Used

* **Java (JDK 8+)**
* **File Handling & Serialization**
* **JSON Libraries (Jackson / Gson)**
* **Collections Framework**

---
 Setup Instructions

### 1️⃣ Clone Repository

```
git clone https://github.com/YOUR-USERNAME/data-backup-system.git
```

### 2️⃣ Navigate to Project

```
cd data-backup-system
```

### 3️⃣ Compile

```
javac Main.java
```

### 4️⃣ Run

```
java Main
```

---

##  Backup Formats

| Format | Extension | Description                |
| ------ | --------- | -------------------------- |
| Binary | `.dat`    | Fast and efficient storage |
| CSV    | `.csv`    | Human-readable format      |
| JSON   | `.json`   | Web-friendly format        |
| ZIP    | `.zip`    | Compressed backup          |

---

## 🔄 How It Works

### 🔹 Backup Process

1. User inputs data
2. Data is serialized
3. Stored in selected format
4. Version is created

### 🔹 Restore Process

1. Select backup version
2. Deserialize file
3. Recover original data

---

## 🧪 Testing

| Test Case        | Result   |
| ---------------- | -------- |
| Backup Creation  | ✅ Passed |
| Restore Function | ✅ Passed |
| CSV Export       | ✅ Passed |
| JSON Export      | ✅ Passed |
| Compression      | ✅ Passed |

---

## 📸 Screenshots

*(Add your screenshots here)*

* Menu Interface
* Backup Creation
* Restore Process
* Version List

---

##  Future Enhancements

*  Data Encryption (AES)
*  Cloud Backup Integration (AWS)
* GUI using Java Swing / JavaFX
* Notification System

---

##  Author
Sri Vishnu


