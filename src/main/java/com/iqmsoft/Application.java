
package com.iqmsoft;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

@SpringBootApplication
@EnableNeo4jRepositories
public class Application {

	private final static Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner demo(PersonRepository personRepository) {
		return args -> {

			personRepository.deleteAll();

			Person test1 = new Person("Test1");
			Person test2 = new Person("Test2");
			Person test3 = new Person("Test3");

			List<Person> team = Arrays.asList(test1, test2, test3);

			log.info("Before linking up with Neo4j...");

			team.stream().forEach(person -> log.info("\t" + person.toString()));

			personRepository.save(test1);
			personRepository.save(test2);
			personRepository.save(test3);

			test1 = personRepository.findByName(test1.getName());
			test1.worksWith(test2);
			test1.worksWith(test3);
			personRepository.save(test1);

			test2 = personRepository.findByName(test2.getName());
			test2.worksWith(test3);
			
			personRepository.save(test2);

			
			log.info("Lookup each person by name...");
			team.stream().forEach(person -> log.info(
					"\t" + personRepository.findByName(person.getName()).toString()));
		};
	}

	
    @Bean
    public Configuration configuration() {
        Configuration config = new Configuration();
        config
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
                .setURI("file:///var/tmp/graph.db");
        return config;
    }

	

	@Bean
	public SessionFactory sessionFactory() {
		return new SessionFactory(configuration(), "com.iqmsoft");
	}

    @Bean
    public Neo4jTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new Neo4jTransactionManager(sessionFactory);
    }

}
