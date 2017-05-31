
JCC = javac

default: Main.class Visitor.class SymbolTable.class Node.class Key.class Quad.class QuadManager.class IRelement.class Assembly.class ScopeTemp.class Temp.class

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

QuadManager.class: QuadManager.java
	$(JCC) QuadManager.java


Quad.class: Quad.java
	$(JCC) Quad.java


IRelement.class: IRelement.java
	$(JCC) IRelement.java

Assembly.class: Assembly.java
	$(JCC) Assembly.java

ScopeTemp.class: ScopeTemp.java
	$(JCC) ScopeTemp.java

Temp.class: Temp.java
	$(JCC) Temp.java


clean:
	$(RM) *.class
