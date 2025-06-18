[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/zZioNIkj)
## CSC 435 Programming Assignment 3 (Spring 2025)
**Jarvis College of Computing and Digital Media - DePaul University**

**Student**: Mackenzie Summers (msummer5@depaul.edu)  
**Solution programming language**: Java

### Requirements
```
sudo apt install maven openjdk-21-jdk
```

### Setup

There are 3 datasets (dataset1, dataset2, dataset3) that you need to use to evaluate the indexing performance of your solution.
Before you can evaluate your solution you need to download the datasets. You can download the datasets from the following link:

https://depauledu-my.sharepoint.com/:f:/g/personal/aorhean_depaul_edu/Ej4obLnAKMdFh1Hidzd1t1oBHY7IvgqXoLdKRg-buoiisw?e=SWLALa

After you finished downloading the datasets copy them to the dataset directory (create the directory if it does not exist).
Here is an example on how you can copy Dataset1 to the remote machine and how to unzip the dataset:

```
remote-computer$ mkdir datasets
local-computer$ scp dataset1.zip cc@<remote-ip>:<path-to-repo>/datasets/.
remote-computer$ cd <path-to-repo>/datasets
remote-computer$ unzip dataset1.zip
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

To run the Java solution (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalEngine <number of worker threads>
```

#### Example

```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalEngine 8
Running with 8 Threads
<index | search | quit>

> index ../datasets/dataset1
Completed indexing 131630300 bytes of data
Completed indexing in 3.73 seconds
Indexing Speed: 35.26 MB/s
> search the
This search has an incorrect term length (Min 4 letters)
> search apple
Search completed in 0.00 seconds
Search results (top 10 out of 122)
* ../datasets/dataset1/folder8/Document11069.txt: 36
* ../datasets/dataset1/folder2/Document101.txt: 34
* ../datasets/dataset1/folder6/Document1084.txt: 22
* ../datasets/dataset1/folder1/Document10072.txt: 20
* ../datasets/dataset1/folder2/folderA/Document10322.txt: 12
* ../datasets/dataset1/folder4/Document10681.txt: 12
* ../datasets/dataset1/folder4/Document1061.txt: 11
* ../datasets/dataset1/folder1/Document10135.txt: 10
* ../datasets/dataset1/folder7/Document10937.txt: 10
* ../datasets/dataset1/folder3/folderB/Document10460.txt: 10
> search blooper
Search completed in 0.00 seconds
Search results (top 0 out of 0)
> quit
```
