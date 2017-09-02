all : compile execute

compile : Main.class

execute : compile
	sudo systemctl restart tomcat.service
	google-chrome http://localhost:8080/Main

clean :
	rm *.class

%.class : %.java
	javac -d /opt/tomcat/webapps/ROOT/WEB-INF/classes/ -cp /opt/tomcat/lib/servlet-api.jar -g $<


