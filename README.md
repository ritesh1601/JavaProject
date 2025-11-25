### Institute Marks Management System

`
Go to directory -> JavaProject #in terminal
`

`Compile by :
`
```
javac -cp ".;lib/mysql-connector-j-9.5.0.jar" (Get-ChildItem GroupProject/*.java)
```

`Run code by : 
`

```
java -cp ".;lib/mysql-connector-j-9.5.0.jar" GroupProject.MarksManagement
```

<img width="1021" height="453" alt="image" src="https://github.com/user-attachments/assets/f0578462-7de8-4e1b-8d3c-bb37a2216c1d" />

In this file , change the pass , change it to your local machine mySQL password


**MySQL is required in the local system**



**Create Database in your local system to connect it to the mySQL**

```
create DATABASE MarksManagement;
use MarksManagement;

CREATE TABLE users (
    username VARCHAR(30) PRIMARY KEY,
    password VARCHAR(30) NOT NULL,
    role VARCHAR(20) NOT NULL
);

INSERT INTO users VALUES
('admin', 'admin123', 'admin'),
('teacher1', 'pass123', 'teacher');

CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    roll VARCHAR(20) UNIQUE NOT NULL,
    class VARCHAR(20) NOT NULL
);

CREATE TABLE subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll VARCHAR(20) NOT NULL,
    subject VARCHAR(50) NOT NULL,
    marks INT NOT NULL,
    FOREIGN KEY (roll) REFERENCES students(roll)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll VARCHAR(20) NOT NULL,
    date DATE NOT NULL,
    status ENUM('Present', 'Absent') NOT NULL,
    FOREIGN KEY (roll) REFERENCES students(roll)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
```
