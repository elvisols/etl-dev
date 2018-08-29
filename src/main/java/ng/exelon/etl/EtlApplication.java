package ng.exelon.etl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

import ng.exelon.etl.util.EtlBindings;

@EnableScheduling
@EnableBinding(EtlBindings.class) // @EnableBinding(value={Orders.class, Payment.class}
@SpringBootApplication
public class EtlApplication {

	public static void main(String[] args) {
		SpringApplication.run(EtlApplication.class, args);
	}
	
}
