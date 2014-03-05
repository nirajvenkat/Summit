JFLAGS = -classpath .:mysql-connector-java-5.1.17-bin.jar
JC = javac
JVM = java
MAIN = SummitHSS
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = \
	  SummitHSS.java \
	  SummitHSSConsole.java

default: classes
classes: $(CLASSES:.java=.class)
clean:
	$(RM) *.class
