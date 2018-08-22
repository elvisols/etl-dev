package ng.exelon.etl.util;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

//import ng.exelon.etl.service.DtdProducer.DtdRecord;

public interface EtlBindings {
	
	String ORACLE_SOURCE = "oraclesourc";
	String ORACLE_SOURCE_IN = "oraclesourcein";
	
	@Input(ORACLE_SOURCE_IN)
	KStream<String, String> oracleSourceIn();
	
	@Output(ORACLE_SOURCE)
	MessageChannel oracleSource();

}
