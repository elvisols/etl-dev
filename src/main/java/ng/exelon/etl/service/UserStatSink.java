package ng.exelon.etl.service;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;


import ng.exelon.etl.domain.UserStats;
import ng.exelon.etl.service.DtdProducer.DtdRecord;
import ng.exelon.etl.util.EtlBindings;

@Component
public class UserStatSink {

	@SuppressWarnings("unchecked")
	@StreamListener
	@SendTo({EtlBindings.CREDIT_EXCEPTION_OUT, EtlBindings.DEBIT_EXCEPTION_OUT, EtlBindings.NORMAL_STAT_OUT})
	public KStream<String, DtdRecord>[] process(@Input(EtlBindings.USER_STAT_IN) KStream<String, UserStats> records) {
		KStream<String, DtdRecord>[] outputs = new KStream[3];
		
		KStream<String, UserStats>[] branches = records
				.branch(
					(k, v) -> v.getCreditZscore() > 80, /* credit exception predicate  */
				    (k, v) -> v.getDebitZscore() > 80, 	/* debit exception predicate */
				    (k, v) -> true                 		/* normal statistics predicate  */
				  );
		
		outputs[0] = branches[0]
						.mapValues(value -> value.getDtdRecord());
		outputs[1] = branches[1]
				.mapValues(value -> value.getDtdRecord());
		outputs[2] = branches[2]
				.mapValues(value -> value.getDtdRecord());
		
		return outputs;
	}
	
}
