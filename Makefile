JFLAGS = -g
JC = javac
JCR = java

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	hashtagcounter.java \
	FiboHeapHashTag.java \

default: classes

compile: classes

run: classes exec-tests

classes: $(CLASSES:.java=.class)

exec-tests: classes
	$(JCR) hashtagcounter $(arg)

clean: 
	$(RM) *.class *~

.PHONY: default clean classes exec-tests