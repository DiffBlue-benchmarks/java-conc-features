all : compile execute

compile : Main.class

execute : compile
	java -ea Main arg1 arg2

clean :
	rm *.class

%.class : %.java
	javac -g $<
