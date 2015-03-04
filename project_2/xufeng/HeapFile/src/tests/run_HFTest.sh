javac -J-XX:-UseCompressedClassPointers -cp .:..:../../lib/heapAssign.jar:../heap HFTest.java
java -XX:-UseCompressedClassPointers HFTest
