JDKPATH = /usr
LIBPATH = RelOperator/lib/heap.jar:RelOperator/lib/bufmgr.jar:RelOperator/lib/diskmgr.jar:RelOperator/lib/index.jar

CLASSPATH = .:..:$(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac 
JAVA  = $(JDKPATH)/bin/java 

PROGS = xx

all: $(PROGS)

compile:
	$(JAVAC) -cp $(CLASSPATH) -d RelOperator/bin RelOperator/src/*/*.java

xx : compile
	$(JAVA) -cp $(CLASSPATH):RelOperator/bin tests.QEPTest RelOperator/src/tests/SampleData
