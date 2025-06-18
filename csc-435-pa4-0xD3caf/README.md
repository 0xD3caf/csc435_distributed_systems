[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/l_alBCRp)
## CSC 435 Programming Assignment 4 (Spring 2025)
**Jarvis College of Computing and Digital Media - DePaul University**

**Student**: Mackenzie Summers (msummer5@depaul.edu)  
**Solution programming language**: Java

### Requirements
If you are implementing your solution in Java you will need to have Java 21.x and Maven 3.8.x installed on your systems.
On Ubuntu 24.04 LTS you can install Java and Maven using the following commands:

```
sudo apt install maven openjdk-21-jdk
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
> <list | quit>
```

To run the Java client (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
> <connect | get_info | index | search | quit>
```

To run the Java benchmark (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalBenchmark 4 0.0.0.0 9999 datasets/dataset3_client_server/4_clients

```

#### Example (2 clients and 1 server)

**Step 1:** start the server:

Server
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer 9999>
<quit | list>
>
```

**Step 2:** start the clients and connect them to the server:

Client 1
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
> connect 0.0.0.0 9999
Client ID: 1
> 

```

Client 2
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalClient
> connect 0.0.0.0 9999
Client ID: 2
> 
```

**Step 3:** list the connected clients on the server:

Server
```
> list
Client 1:/127.0.0.1:40190
Client 2:/127.0.0.1:40194
> 
```

**Step 4:** index files from the clients:

Client 1
```
> index ../datasets/dataset1_client_server/2_clients/client_1
Completed indexing 67043392 bytes of data
Completed indexing in 20.88 seconds
Indexing Speed: 3.21 MB/s
> 
```

Client 2
```
> index ../datasets/dataset1_client_server/2_clients/client_2
Completed indexing 64586908 bytes of data
Completed indexing in 19.09 seconds
Indexing Speed: 3.38 MB/s
> 


```

**Step 5:** search files from the clients:

Client 1
```
> search apple
Search completed in 0.00 seconds
Search results (top 10 out of 10)
* 2:../datasets/dataset1_client_server/2_clients/client_2/folder8/Document11069.txt: 36
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder2/Document101.txt: 34
* 2:../datasets/dataset1_client_server/2_clients/client_2/folder6/Document1084.txt: 22
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10072.txt: 20
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10681.txt: 12
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10322.txt: 12
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder4/Document1061.txt: 11
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10135.txt: 10
* 2:../datasets/dataset1_client_server/2_clients/client_2/folder7/Document10937.txt: 10
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder3/folderB/Document10460.txt: 10
> 

```

Client 2
```
> search them AND they
Search completed in 0.00 seconds
Search results (top 10 out of 10)
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10553.txt: 6107
* 2:../datasets/dataset1_client_server/2_clients/client_2/folder5/folderB/Document10706.txt: 4390
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder3/Document1039.txt: 3657
* 2:../datasets/dataset1_client_server/2_clients/client_2/folder6/Document10907.txt: 3433
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10657.txt: 3113
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10600.txt: 2996
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder4/Document10633.txt: 2909
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10352.txt: 2453
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder2/folderA/Document10350.txt: 2235
* 1:../datasets/dataset1_client_server/2_clients/client_1/folder1/Document10072.txt: 2206
> 

```

**Step 6:** close and disconnect the clients:

Client 1
```
> quit
```

Client 2
```
> quit
```

**Step 7:** close the server:

Server
```
> quit
```

#### Example (benchmark with 2 clients and 1 server)

**Step 1:** start the server:

Server
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalServer 12345
>
```

**Step 2:** start the benchmark:

Benchmark
```
Connecting Clients...
Client ID: 2
Client ID: 4
Client ID: 3
Client ID: 1
Completed indexing 8297933559 bytes of data
Completed indexing in 185.42 seconds
Indexing Speed: 44.75 MB/s

SEARCH TESTING

SEARCH RESULT: apple
Search completed in 0.00 seconds
Search results (top 10 out of 10)
* Client 1:datasets/dataset3_client_server/4_clients/client_2/folder3/folderB/Document18183.txt: 263
* Client 3:datasets/dataset3_client_server/4_clients/client_3/folder6/Document26132.txt: 147
* Client 3:datasets/dataset3_client_server/4_clients/client_3/folder5/folderC/Document22644.txt: 131
* Client 2:datasets/dataset3_client_server/4_clients/client_1/folder2/Document13286.txt: 109
* Client 2:datasets/dataset3_client_server/4_clients/client_1/folder2/Document13545.txt: 91
* Client 4:datasets/dataset3_client_server/4_clients/client_4/folder8/Document3254.txt: 64
* Client 1:datasets/dataset3_client_server/4_clients/client_2/folder3/folderB/Document17438.txt: 62
* Client 1:datasets/dataset3_client_server/4_clients/client_2/folder3/folderB/Document17439.txt: 58
* Client 4:datasets/dataset3_client_server/4_clients/client_4/folder7/folderC/Document29084.txt: 55
* Client 1:datasets/dataset3_client_server/4_clients/client_2/folder3/Document15664.txt: 47
SEARCH RESULT: there AND their
Search completed in 0.00 seconds
Search results (top 10 out of 10)
* Client 4:datasets/dataset3_client_server/4_clients/client_4/folder8/Document3254.txt: 10227
* Client 4:datasets/dataset3_client_server/4_clients/client_4/folder8/Document3252.txt: 7428
* Client 4:datasets/dataset3_client_server/4_clients/client_4/folder7/folderA/Document3136.txt: 7026
* Client 2:datasets/dataset3_client_server/4_clients/client_1/folder1/Document10706.txt: 6003
* Client 1:datasets/dataset3_client_server/4_clients/client_2/folder3/folderA/Document16955.txt: 5614
* Client 2:datasets/dataset3_client_server/4_clients/client_1/folder2/folderA/Document15476.txt: 5307
* Client 1:datasets/dataset3_client_server/4_clients/client_2/folder4/Document200.txt: 5037
* Client 4:datasets/dataset3_client_server/4_clients/client_4/folder7/folderA/Document28039.txt: 3966
* Client 1:datasets/dataset3_client_server/4_clients/client_2/folder3/Document1610.txt: 3817
* Client 1:datasets/dataset3_client_server/4_clients/client_2/folder4/Document18755.txt: 3315
```

**Step 3:** close the server:

Server
```
> quit
```
