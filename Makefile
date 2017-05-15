
JCC = javac

default: Main.class Visitor.class SymbolTable.class Node.class Key.class

Main.class: Main.java
	$(JCC) Main.java

Visitor.class: Visitor.java
	$(JCC) Visitor.java

SymbolTable.class: SymbolTable.java
	$(JCC) SymbolTable.java

Node.class: Node.java
	$(JCC) Node.java

Key.class: Key.java
	$(JCC) Key.java



clean:
	$(RM) *.class
