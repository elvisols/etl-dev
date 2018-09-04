package ng.exelon.etl.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import javax.annotation.PostConstruct;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ng.exelon.etl.model.DtdRecord;
import ng.exelon.etl.util.EtlBindings;

@Slf4j
@Service 
public class DtdProducer implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Autowired
	@Qualifier(EtlBindings.ORACLE_SOURCE)
	private MessageChannel oracleSource;
	
	private static MessageChannel oracleSauce;
	
	@PostConstruct
	public void init() {
		DtdProducer.oracleSauce = oracleSource;
	}
	
	/*
	 * @Scheduled(fixedDelay = 1000)
	 * In this case, the duration between the end of last execution and the start of next execution is fixed. The task always waits until the previous one is finished.
	 * This option should be used when it’s mandatory that the previous execution is completed before running again.
	 * 
	 * @Scheduled(fixedRate = 1000)
	 * the beginning of the task execution doesn’t wait for the completion of the previous execution.
	 * 
	 * @Scheduled(fixedDelay = 1000, initialDelay = 1000)
	 * The task will be executed a first time after the initialDelay value – and it will continue to be executed according to the fixedDelay.
	 * This option comes handy when the task has a set-up that needs to be completed.
	 * 
	 * @Scheduled(cron = "0 15 10 15 * ?")
	 * 
	 * Using parameter without re-compiling and re-deploying the entire app.
	 * A fixedDelay task:
		1
		@Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
		A fixedRate task:
		
		1
		@Scheduled(fixedRateString = "${fixedRate.in.milliseconds}")
		A cron expression based task:
		
		1
		@Scheduled(cron = "${cron.expression}") 
		
		@see http://www.quartz-scheduler.org/documentation/quartz-1.x/tutorials/crontrigger
		
	 */
	
	// Do a dirty hack to pull records form jdbc
	private static String LAST_PULL = "1970-01-01 00:00:01";
	
	@Scheduled(fixedDelay = 5000, initialDelay = 10000)
	public void scheduleFixedDelayTask() throws Exception {
	    log.info("*** New Thread : {} at previous pulled time {}", Thread.currentThread().getName(), DtdProducer.LAST_PULL);
	    try {
	    	startProducer(new String[]{});
	    } catch(Exception e) {
			log.info(">>> No record(s) to pull at this time..." );
			e.printStackTrace();
		}
	    	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void startProducer(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		
		TypeInformation[] types = new TypeInformation[22];
		
		for(int i=0; i < types.length; i++)
			types[i] = TypeInformation.of(String.class);
		
			DataSource<Tuple> input = env.createInput(JDBCInputFormat.buildJDBCInputFormat() 
				.setDrivername("org.postgresql.ds.PGPoolingDataSource") 
				.setDBUrl("jdbc:postgresql://localhost:5432/eagleeye") 
				.setQuery("SELECT " +
						"part_tran_srl_num," + 
						"tran_date," + 
						"tran_id," + 
						"br_code," + 
						"cust_id," + 
						"del_flg," + 
						"entry_date," + 
						"entry_user_id," + 
						"lchg_time," + 
						"lchg_user_id," + 
						"part_tran_type," + 
						"COALESCE(pstd_date,'1970-01-01 00:00:01')," + 
						"pstd_flg," + 
						"pstd_user_id," + 
						"sol_id," + 
						"tran_amt," + 
						"tran_crncy_code," + 
						"tran_particular," + 
						"tran_rmks," + 
						"tran_sub_type," + 
						"value_date," + 
						"acid " +
						"FROM finacle.dtd where value_date >= '" + DtdProducer.LAST_PULL + "' LIMIT 30") //LIMIT 3 OFFSET 2;
				.setUsername("admin")
				.setPassword("password") 
				.finish(), new TupleTypeInfo(types));
			input.map(convertToFlatStrings())
			.returns(String.class).collect().forEach((value) -> {
				String ret = value;
				
				String[] rcArr = ret.split(";;;");
				
				DtdRecord dtdR = new DtdRecord(
						rcArr[0] + "&" + rcArr[2] + "&" + rcArr[1],
						rcArr[0], rcArr[1], rcArr[2], rcArr[3], rcArr[4], rcArr[5], rcArr[6], rcArr[7], rcArr[8], rcArr[9],
						rcArr[10], rcArr[11], rcArr[12], rcArr[13], rcArr[14], rcArr[15], rcArr[16], rcArr[17], rcArr[18], rcArr[19],
						rcArr[20], rcArr[21], Instant.now().toEpochMilli()
				);
				
				Message<DtdRecord> message = MessageBuilder
						.withPayload(dtdR)
						.setHeader(KafkaHeaders.MESSAGE_KEY, dtdR.getEntryUserId())
						.build();
				
				DtdProducer.oracleSauce.send(message);

				DtdProducer.LAST_PULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			});
		env.execute();
	}
	
	@SuppressWarnings("serial")
	private MapFunction<Tuple, String> convertToFlatStrings() throws Exception {
		
		return new MapFunction<Tuple, String>() {
			@Override
			public String map(Tuple value) throws Exception {
				String ret = "";
				for (int i = 0; i < value.getArity(); i++) {
					ret += value.getField(i) + ";;;";
				}
				return ret;
			}
		};
	}

}
