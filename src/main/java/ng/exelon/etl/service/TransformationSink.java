package ng.exelon.etl.service;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import ng.exelon.etl.domain.UserStats;
import ng.exelon.etl.model.DtdRecord;
import ng.exelon.etl.serde.UserStatSerde;
import ng.exelon.etl.util.EtlBindings;


/*
 * Join co-partitioning requirements:
 * 
 * Input data must be co-partitioned when joining. This ensures that input records with the same key, from both sides of the join, 
 * are delivered to the same stream task during processing. It is the responsibility of the user to ensure data co-partitioning when joining.
 */
//@Component
public class TransformationSink {

//	@Autowired
//	DtdRecordService dtdService;
//	
//	@StreamListener
//	@SendTo(EtlBindings.USER_STAT_OUT)
//	public KStream<String, UserStats> process(@Input(EtlBindings.ORACLE_SOURCE_IN) KStream<String, DtdRecord> records) {
//		// push to ES
////		records.foreach((k, v) -> {
////			dtdService.save(v);
////		});
//		
//		return records
//			.groupByKey()
//			.windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(6)).advanceBy(TimeUnit.SECONDS.toMillis(1)))
//			.aggregate(
//				UserStats::new,
//				(key, dtdRecord, userObj) -> userObj.compute(dtdRecord),
//				Materialized.<String, UserStats, WindowStore<Bytes, byte[]>>as(EtlBindings.USERSTATS_AGGREGATE_STORE)
//    			.withKeySerde(Serdes.String())
//                .withValueSerde(new UserStatSerde()))
//			.filter((key, value) -> ((value.getTime() - key.window().start()) >= 5000 ? true : false))
//			.toStream((key, value) -> (key.key()))
//			.filter((k, v) -> v != null)
////			.peek((key, value) -> System.out.println(" Peeking key:" + key + " value:" + value))
//			.mapValues((userstat) -> userstat.computeZscore());
//	}
	
//	@StreamListener
//	public void process1(@Input(EtlBindings.ORACLE_SOURCE_IN) KStream<String, DtdRecord> records) {
//		records
//			.peek((key, value) -> System.out.println("key: " + key + " value: " + value));
//	}
	
}
