all : compile execute

compile : Main.class

execute : compile
	sudo systemctl restart tomcat.service
	sleep 5
	wget -S -q -O - 'http://localhost:8080/Main?1' &
	sleep 2
	wget -S -q -O - 'http://localhost:8080/Main?16'

clean :
	rm *.class

%.class : %.java
	javac -d /opt/tomcat/webapps/ROOT/WEB-INF/classes/ -cp /opt/tomcat/lib/servlet-api.jar -g $<


