package ng.exelon.etl.service;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ng.exelon.etl.controllers.MessagesController;
import ng.exelon.etl.domain.UserStats;
import ng.exelon.etl.service.DtdProducer.DtdRecord;
import ng.exelon.etl.util.EtlBindings;

@Slf4j
@Component
public class UserStatSink {
	
	@Autowired
    private SimpMessagingTemplate template;
	
	@SuppressWarnings("unchecked")
	@StreamListener
	@SendTo({EtlBindings.CREDIT_EXCEPTION_OUT, EtlBindings.DEBIT_EXCEPTION_OUT, EtlBindings.NORMAL_STAT_OUT})
	public KStream<String, DtdRecord>[] process(@Input(EtlBindings.USER_STAT_IN) KStream<String, UserStats> records) {
		KStream<String, DtdRecord>[] outputs = new KStream[3];
		records.foreach((key, value) ->  {
			value.setCreditAmountList(null);
			value.setDebitAmountList(null);
			value.setSubList(null);
			value.setCreditSubList(null);
			value.setDebitSubList(null);
			value.setCreditAvg(value.getCreditMean());
			value.setDebitAvg(value.getDebitMean());
			
//			{"creditCount":5,"debitCount":4,"creditSum":206850.0,"debitSum":515252.5, 
//				"creditMean":41370.0,"debitMean":128813.125, "creditMax":100000.0,"debitMax":510200.0,"creditMin":850.0,"debitMin":2.5,
//				"creditZscore":-0.9825501901882161,"debitZscore":-0.5622676580429679,
//				"creditZscoreM":-0.6745,"debitZscoreM":0.6680890445222611,
//				"creditZscoreRatio":"67.4","debitZscoreRatio":"42.6","creditZscoreRatioM":"50.0","debitZscoreRatioM":"49.6",
//				"timestamp":"2018-08-28T11:05:18.912Z","time":1535454318210,"user":"CDCI",
//				"dtdRecord":{"part_tran_srl_num":"1","tran_date":"2018-07-18","tran_id":"S1418698","br_code":"060","cust_id":"A78206317","del_flg":"N","entry_date":"2018-07-18 00:00:00.0","entry_user_id":"CDCI","lchg_time":"2018-07-18","lchg_user_id":"SYSTEM","part_tran_type":"D","pstd_date":"2018-07-18 00:00:00.0","pstd_flg":"Y","pstd_user_id":"CDCI","sol_id":"060","tran_amt":"5000.0","tran_crncy_code":"NGN","tran_particular":"Wema USSD Transfer|BABA NIMOTA|UBA","tran_rmks":"000017180718072244041330121743","tran_sub_type":"CI","value_date":"2018-07-18 00:00:00.0","acid":"WB2171763","log_time":1535454318210},"tmpVariance":1.939577578171875E11}

			this.template.convertAndSend("/topic/statistics", value);
		});
		
		KStream<String, UserStats>[] branches = records
				.branch(
					(k, v) -> { /* credit exception predicate  */
						if(v.getCreditZscoreRatio() != null && v.getCreditZscoreRatioM() != null) {
				    		return (Double.parseDouble(v.getCreditZscoreRatio()) > 70 && Double.parseDouble(v.getCreditZscoreRatioM()) > 70);
				    	} else {
				    		return false;
				    	}
					},
				    (k, v) -> { /* debit exception predicate */
				    	if(v.getDebitZscoreRatio() != null && v.getDebitZscoreRatioM() != null) {
				    		return (Double.parseDouble(v.getDebitZscoreRatio()) > 70 && Double.parseDouble(v.getDebitZscoreRatioM()) > 70);
				    	} else {
				    		return false;
				    	}
				    }, 	
				    (k, v) -> true  /* normal statistics predicate  */
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
