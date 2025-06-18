[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/JRxkKq8u)
## CSC 435 Programming Assignment 5 (Spring 2025)
**Jarvis College of Computing and Digital Media - DePaul University**

**Student**: Mackenzie Summers (msummer5@depaul.edu)  
**Solution programming language**: Java

### Requirements
If you are implementing your solution in Java you will need to have Java 21.x and Maven 3.8.x installed on your systems. On Ubuntu 24.04 LTS you can install Java and Maven using the following commands:

```
sudo apt install openjdk-21-jdk maven
```

### Setup

There are 3 datasets (dataset1_client_server, dataset2_client_server, dataset3_client_server) that you need to use to evaluate the indexing performance of your solution.
Before you can evaluate your solution you need to download the datasets. You can download the datasets from the following link:

https://depauledu-my.sharepoint.com/:f:/g/personal/aorhean_depaul_edu/Ej4obLnAKMdFh1Hidzd1t1oBHY7IvgqXoLdKRg-buoiisw?e=SWLALa

After you finished downloading the datasets copy them to the dataset directory (create the directory if it does not exist).
Here is an example on how you can copy Dataset1 to the remote machine and how to unzip the dataset:

```
remote-computer$ mkdir datasets
local-computer$ scp dataset1_client_server.zip cc@<remote-ip>:<path-to-repo>/datasets/.
remote-computer$ cd <path-to-repo>/datasets
remote-computer$ unzip dataset1_client_server.zip
```

### Java solution
#### How to build/compile

To build the Java solution use the following commands:
```
cd app-java
mvn compile
mvn package
```

#### How to run application

To run the Java server (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer <port>
> <quit>
```

To run the Java client (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient 
<index | search | quit | connect | get_info>
```

To run the Java benchmark (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalBenchmark <number of clients> <server IP> <server port> <dataset path>
```

#### Example (2 clients and 1 server)

**Step 1:** start the server:

Server
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer 12345
Initializing server processing engine...
<quit>
> 
```

**Step 2:** start the clients and connect them to the server:

Client 1
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
<index | search | quit | connect | get_info>

> connect 127.0.0.1 12345
Client Connected with ID: 1
> get_info
Client ID: 1
> 
```

Client 2
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
<index | search | quit | connect | get_info>

> connect 127.0.0.1 12345
Client Connected with ID: 2
> get_info
Client ID: 2
> 
```

**Step 3:** index files from the clients:

Client 1
```
> index ../datasets/dataset1_client_server/2_clients/client_1                           
Completed indexing 67043392 bytes of data
Completed indexing in 9.35 seconds
Indexing Speed: 7.17 MB/s
> 
```

Client 2
```
> index ../datasets/dataset1_client_server/2_clients/client_2
Completed indexing 64586908 bytes of data
Completed indexing in 7.77 seconds
Indexing Speed: 8.31 MB/s
> 
```

**Step 4:** search files from the clients:

Client 1
```
> search apple
Search completed in 0.03 seconds
Search results (top 10 out of 10)
* 2../datasets/dataset1_client_server/2_clients/client_2/folder8/Document11069.txt: 36
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/Document101.txt: 34
* 2../datasets/dataset1_client_server/2_clients/client_2/folder6/Document1084.txt: 22
* 1../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10072.txt: 20
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10681.txt: 12
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10322.txt: 12
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document1061.txt: 11
* 1../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10135.txt: 10
* 2../datasets/dataset1_client_server/2_clients/client_2/folder7/Document10937.txt: 10
* 1../datasets/dataset1_client_server/2_clients/client_1/folder3/folderB/Document10460.txt: 10
> search they AND them 
Search completed in 0.01 seconds
Search results (top 10 out of 10)
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt: 6107
* 2../datasets/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10706.txt: 4390
* 1../datasets/dataset1_client_server/2_clients/client_1/folder3/Document1039.txt: 3657
* 2../datasets/dataset1_client_server/2_clients/client_2/folder6/Document10907.txt: 3433
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10657.txt: 3113
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10600.txt: 2996
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10633.txt: 2909
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10352.txt: 2453
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10350.txt: 2235
* 1../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10072.txt: 2206
>
```

Client 2
```
> search people AND them
Search completed in 0.02 seconds
Search results (top 10 out of 10)
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt: 3889
* 2../datasets/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10706.txt: 2313
* 1../datasets/dataset1_client_server/2_clients/client_1/folder3/Document1039.txt: 1749
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10657.txt: 1542
* 2../datasets/dataset1_client_server/2_clients/client_2/folder6/Document10907.txt: 1503
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10633.txt: 1367
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10600.txt: 1197
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10352.txt: 1130
* 2../datasets/dataset1_client_server/2_clients/client_2/folder8/Document11080.txt: 1119
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10351.txt: 909
> search tell AND show AND mine
Search completed in 0.00 seconds
Search results (top 10 out of 10)
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10662.txt: 1088
* 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt: 308
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/Document10212.txt: 297
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10322.txt: 251
* 1../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10064.txt: 242
* 1../datasets/dataset1_client_server/2_clients/client_1/folder3/Document10379.txt: 233
* 1../datasets/dataset1_client_server/2_clients/client_1/folder3/folderA/Document10449.txt: 230
* 1../datasets/dataset1_client_server/2_clients/client_1/folder2/Document10148.txt: 227
* 2../datasets/dataset1_client_server/2_clients/client_2/folder6/Document10745.txt: 225
* 1../datasets/dataset1_client_server/2_clients/client_1/folder3/folderB/Document10462.txt: 189
> 
```

**Step 5:** close and disconnect the clients:

Client 1
```
> quit
```

Client 2
```
> quit
```

**Step 6:** close the server:

Server
```
> quit
```

#### Example (benchmark with 2 clients and 1 server)

**Step 1:** start the server:

Server
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer 12345
Initializing server processing engine...
<quit>
> 
```

**Step 2:** start the benchmark:

Benchmark
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalBenchmark 2 0.0.0.0 12345 ../datasets/dataset1_client_server/2_clients/
Starting benchmark...
Connecting Clients
Client Connected with ID: 1
Client Connected with ID: 2
Completed indexing 131630300 bytes of data
Completed indexing in 12.02 seconds
Indexing Speed: 10.95 MB/s

SEARCH TESTING

SEARCH RESULT: apple
Search completed in 0.02 seconds
Search results (top 10 out of 10)
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder8/Document11069.txt: 36
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder2/Document101.txt: 34
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder6/Document1084.txt: 22
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10072.txt: 20
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10322.txt: 12
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10681.txt: 12
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document1061.txt: 11
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10135.txt: 10
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder7/Document10937.txt: 10
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder8/Document11168.txt: 10

SEARCH RESULT: there AND their
Search completed in 0.00 seconds
Search results (top 10 out of 10)
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10706.txt: 6003
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder6/Document10907.txt: 2715
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10600.txt: 2519
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder3/Document1039.txt: 2465
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10633.txt: 2318
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt: 2299
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10657.txt: 2199
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10700.txt: 1998
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10351.txt: 1911
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10352.txt: 1856

SEARCH RESULT: meet AND tell AND show
Search completed in 0.00 seconds
Search results (top 10 out of 10)
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder2/Document10212.txt: 301
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10662.txt: 265
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder3/Document10379.txt: 258
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10322.txt: 243
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10706.txt: 232
* Client 2../datasets/dataset1_client_server/2_clients/client_2/folder6/Document10745.txt: 225
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder3/folderB/Document10509.txt: 206
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10082.txt: 192
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt: 191
* Client 1../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10064.txt: 189

```

**Step 3:** close the server:

Server
```
> quit
```
