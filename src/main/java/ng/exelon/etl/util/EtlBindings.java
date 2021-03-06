package ng.exelon.etl.util;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import ng.exelon.etl.domain.UserStats;
import ng.exelon.etl.model.DtdRecord;

public interface EtlBindings {
	
	String ORACLE_SOURCE = "oraclesource";
	String ORACLE_SOURCE_IN = "oraclesourcein";
	String USER_STAT_OUT = "userstatout";
	String USER_STAT_IN = "userstatin";
	String CREDIT_EXCEPTION_OUT = "creditexceptionout";
	String DEBIT_EXCEPTION_OUT = "debitexceptionout";
	String NORMAL_STAT_OUT = "normalstatout";
	String USERSTATS_AGGREGATE_STORE = "userstatstore";
	
	@Input(ORACLE_SOURCE_IN)
	KStream<String, DtdRecord> oracleSourceIn();
	
	@Output(ORACLE_SOURCE)
	MessageChannel oracleSource();
	
	@Output(USER_STAT_OUT)
	KStream<String, UserStats> userStatOut();

	@Input(USER_STAT_IN)
	KStream<String, UserStats> userStatIn();
	
	@Output(CREDIT_EXCEPTION_OUT)
	KStream<String, UserStats> creditExceptionOut();
	
	@Output(DEBIT_EXCEPTION_OUT)
	KStream<String, UserStats> debitExceptionOut();
	
	@Output(NORMAL_STAT_OUT)
	KStream<String, UserStats> normalStatOut();

}
