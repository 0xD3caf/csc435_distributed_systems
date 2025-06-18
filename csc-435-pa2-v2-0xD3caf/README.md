[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/SQAN-eDa)
## CSC 435 Programming Assignment 2 (Spring 2025)
**Jarvis College of Computing and Digital Media - DePaul University**

**Student**: Mackenzie Summers (msummer5@depaul.edu)  
**Solution programming language**: Java

Note * I had to increase the Memory of the VM from 4gb to 8gb or java runs out of memory.
### Requirements

If you are implementing your solution in C++ you will need to have GCC 14.x and CMake 3.28.x installed on your system.
On Ubuntu 24.04 LTS you can install GCC and set it as default compiler using the following commands:

```
sudo apt install build-essential cmake g++-14 gcc-14 cmake
sudo update-alternatives --remove-all gcc
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-13 130
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-14 140
sudo update-alternatives --remove-all g++
sudo update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-13 130
sudo update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-14 140
```

If you are implementing your solution in Java you will need to have Java 21.x and Maven 3.8.x installed on your systems.
On Ubuntu 24.04 LTS you can install Java and Maven using the following commands:

```
sudo apt install maven openjdk-21-jdk
```

### Setup

There are 3 datasets (dataset1, dataset2, dataset3) that you need to use to evaluate the indexing performance of your solution.
Before you can evaluate your solution you need to download the datasets.
You can download the datasets from the following link:

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
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.FileRetrievalEngine
```