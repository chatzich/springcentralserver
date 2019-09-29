FROM maven
RUN git clone https://github.com/ironexmaiden/springcentralserver.git
RUN cd springcentralserver && mvn install
	

# add directly the jar
COPY springcentralserver/target/*.jar /app.jar

# to create a modification date
RUN sh -c 'touch /app.jar'

# creates a mount point
VOLUME /tmp

CMD ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]

EXPOSE 8080
