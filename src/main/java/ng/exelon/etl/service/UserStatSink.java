package ng.exelon.etl.service;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;


import ng.exelon.etl.domain.UserStats;
import ng.exelon.etl.serde.JsonDeserializer;
import ng.exelon.etl.serde.JsonSerializer;
import ng.exelon.etl.serde.WrapperSerde;
import ng.exelon.etl.service.DtdProducer.DtdRecord;
import ng.exelon.etl.util.EtlBindings;

@Component
public class UserStatSink {

	@StreamListener
	@SendTo(EtlBindings.USER_STAT_OUT)
	public KStream[]<?, ?> process(@Input(EtlBindings.USER_STAT_OUT) KStream<String, UserStats> records) {
		return records
				.branch(
						(key, value) -> key.startsWith("A"), /* first predicate  */
					    (key, value) -> key.startsWith("B"), /* second predicate */
					    (key, value) -> true                 /* third predicate  */
					  );
	}
	
	static public final class UserStatsSerde extends WrapperSerde<UserStats> {
        public UserStatsSerde() {
            super(new JsonSerializer<UserStats>(), new JsonDeserializer<UserStats>(UserStats.class));
        }
    }
	
}
