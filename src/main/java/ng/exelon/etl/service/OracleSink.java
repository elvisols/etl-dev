package ng.exelon.etl.service;

import java.time.Instant;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ng.exelon.etl.service.DtdProducer.DtdRecord;
import ng.exelon.etl.util.EtlBindings;

@Slf4j
@Component
public class OracleSink {

	@StreamListener
	public void process(@Input(EtlBindings.ORACLE_SOURCE_IN) KStream<String, DtdRecord> records) {
		records
		.peek((key, value) -> log.info("Realtime Value received = {} {} @{}", key, value, Instant.now()));
	}
	
}
