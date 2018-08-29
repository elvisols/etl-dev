package ng.exelon.etl.service;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;


import ng.exelon.etl.domain.UserStats;
import ng.exelon.etl.serde.UserStatSerde;
import ng.exelon.etl.service.DtdProducer.DtdRecord;
import ng.exelon.etl.util.EtlBindings;

@Component
public class OracleSink {

	private static int count = 0;
	
	@StreamListener
	@SendTo(EtlBindings.USER_STAT_OUT)
	public KStream<String, UserStats> process(@Input(EtlBindings.ORACLE_SOURCE_IN) KStream<String, DtdRecord> records) {
		return records
			.groupByKey()
			.windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(6)).advanceBy(TimeUnit.SECONDS.toMillis(1)))
			.aggregate(
				UserStats::new,
				(key, dtdRecord, userObj) -> userObj.compute(dtdRecord),
				Materialized.<String, UserStats, WindowStore<Bytes, byte[]>>as(EtlBindings.USERSTATS_AGGREGATE_STORE)
    			.withKeySerde(Serdes.String())
                .withValueSerde(new UserStatSerde()))
			.filter((key, value) -> ((value.getTime() - key.window().start()) >= 5000 ? true : false))
			.toStream((key, value) -> (key.key()))
			.filter((k, v) -> v != null)
//			.peek((key, value) -> System.out.println(" Peeking key:" + key + " value:" + value))
			.mapValues((userstat) -> userstat.computeZscore());
	}
	
//	@StreamListener
//	public void process1(@Input(EtlBindings.ORACLE_SOURCE_IN) KStream<String, DtdRecord> records) {
//		records
//			.peek((key, value) -> System.out.println("key: " + key + " value: " + value));
//	}
	
}
