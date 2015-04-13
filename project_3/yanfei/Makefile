JDKPATH = /usr
LIBPATH = lib/heap.jar:lib/bufmgr.jar:lib/diskmgr.jar:lib/index.jar

CLASSPATH = .:..:$(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac 
JAVA  = $(JDKPATH)/bin/java 

PROGS = xx

all: $(PROGS)

compile:
	$(JAVAC) -J-XX:-UseCompressedClassPointers -cp $(CLASSPATH) -d bin src/*/*.java

xx : compile
	$(JAVA) -XX:-UseCompressedClassPointers -cp $(CLASSPATH):bin tests.QEPTest ./src/tests/SampleData/
