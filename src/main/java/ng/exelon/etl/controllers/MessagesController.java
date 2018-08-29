package ng.exelon.etl.controllers;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ng.exelon.etl.domain.UserStats;
import ng.exelon.etl.serde.UserStatDeserializer;

@Slf4j
@RestController
public class MessagesController {

	private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    @Autowired
    private SimpMessagingTemplate template;

//    @KafkaListener(id = "stats", topics = "${topic.statistics}", containerFactory = "userStatContainerFactory")
	public void allStat(ConsumerRecord<String, UserStats> consumerRecord) {
//    	UserStatDeserializer usd = new UserStatDeserializer();
//        log.info(">>----->>>> received key={}, data='{}'", deserialize(consumerRecord.key()), deserialize(consumerRecord.value()));
//        log.info(">>----->>>> received key={}, data='{}'", consumerRecord.key(), consumerRecord.value());
//        log.info(">>----->>>> received key={}, data='{}'", consumerRecord.key(), consumerRecord.value());
//        log.info(">>----->>>> received data='{}'", foo);
//        this.template.convertAndSend("/topic/statistics", consumerRecord.value());
        latch.countDown();
//        usd.close();
    }
    
    private static UserStats deserialize(final byte[] objectData) {
        return (UserStats) SerializationUtils.deserialize(objectData);
    }
    
}
