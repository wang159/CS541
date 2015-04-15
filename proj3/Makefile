SRCPATH=src
BINPATH=bin
LIBPATH=lib/heap.jar:lib/bufmgr.jar:lib/diskmgr.jar:lib/index.jar

JAVAC = javac -d $(BINPATH) -classpath $(BINPATH):$(LIBPATH)
JAVA  = java -classpath $(BINPATH):$(LIBPATH)

all: compile tests

compile:
	$(JAVAC) $(SRCPATH)/*/*.java

tests:
	$(JAVA) tests.ROTest
