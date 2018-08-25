package ng.exelon.etl.util;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import ng.exelon.etl.domain.UserStats;
import ng.exelon.etl.service.DtdProducer.DtdRecord;

public interface EtlBindings {
	
	String ORACLE_SOURCE = "oraclesource";
	String ORACLE_SOURCE_IN = "oraclesourcein";
	String USER_STAT_OUT = "userstatout";
	String USER_STAT_IN = "userstatin";
	String USERSTATS_AGGREGATE_STORE = "userstatstore";
	
	@Input(ORACLE_SOURCE_IN)
	KStream<String, DtdRecord> oracleSourceIn();
	
	@Output(ORACLE_SOURCE)
	MessageChannel oracleSource();
	
	@Output(USER_STAT_OUT)
	KStream<String, UserStats> userStatOut();

	@Input(USER_STAT_IN)
	KStream<String, UserStats> userStatIn();

}
