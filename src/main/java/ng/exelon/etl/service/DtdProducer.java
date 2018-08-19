package ng.exelon.etl.service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.exelon.etl.BootstrapDatabase;

@Slf4j
@Service
public class DtdProducer {
	
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
	
	private static ObjectMapper MAPPER = new ObjectMapper();
	
	private static String LAST_PULL = "1970-01-01 00:00:01";
//	private static String timeThen = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());;
	
	@Scheduled(fixedDelay = 5000, initialDelay = 10000)
	public void scheduleFixedDelayTask() throws Exception {
	    log.info("*** New Thread : {} at previous pulled time {}", Thread.currentThread().getName(), DtdProducer.LAST_PULL);
	    startProducer(new String[]{});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void startProducer(String[] args) throws Exception {
		ParameterTool parameters = ParameterTool.fromArgs(args);
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		

//		TypeInformation[] types = new TypeInformation[53];
//
//		for(int i=0; i < types.length; i++)
//			types[i] = TypeInformation.of(String.class);
		
		TypeInformation<DtdRecord> types = TypeExtractor.createTypeInfo(DtdRecord.class);

		DataSource<Map> input = env.createInput(JDBCInputFormat.buildJDBCInputFormat() 
				.setDrivername(BootstrapDatabase.DB_DRIVER) 
				.setDBUrl(BootstrapDatabase.DB_CONNECTION) 
				.setQuery("SELECT * FROM fin.dtd where value_date >= '" + DtdProducer.LAST_PULL + "'") 
				.setUsername(BootstrapDatabase.DB_USER) 
				.setPassword(BootstrapDatabase.DB_PASSWORD) 
				.finish(), types);//(DtdRecord.class, Arrays.asList("", "")));
//				.finish(), new TupleTypeInfo(types));
//		input.output(kafkaOutput())
		input.map(convertToFlatStrings())
			.returns(DtdRecord.class)
			.output(kafkaOutput())
			.withParameters(parameters.getConfiguration());
		env.execute();
		DtdProducer.LAST_PULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		log.info(">>>TimeThen now is " + DtdProducer.LAST_PULL);
	}
	
	@SuppressWarnings("serial")
	private MapFunction<Tuple, DtdRecord> convertToFlatStrings() {
		return new MapFunction<Tuple, DtdRecord>() {
			@Override
			public DtdRecord map(Tuple value) throws Exception {
				log.info("Map value = " + value);
//				return value;
//				value.entrySet().stream().forEach(x -> {
////					x;
//					System.out.println("Value = " + x);
////					x;
//				});
//				String ret = "";
//				for (int i = 0; i < value.size(); i++) {
//					ret += value.entrySet().stream().map(arg0) + ";;;";
//				}
				return null;
			}
		};
	}

	@SuppressWarnings("serial")
	private static OutputFormat<DtdRecord> kafkaOutput() {
		return new OutputFormat<DtdRecord>() {

			private Producer<String, String> producer;

			private String topic;

			private boolean mock;

			private Properties props;

			@Override
			public void configure(Configuration parameters) {
				topic = parameters.getString("kafka.topic", "sample-topic");
				mock = parameters.getBoolean("kafka.mock", false);
				props = new Properties();
				props.put("bootstrap.servers", parameters.getString("bootstrap.servers", "localhost:9092"));
				props.put("acks", parameters.getString("acks", "all"));
				props.put("retries", parameters.getInteger("retries", 0));
				props.put("batch.size", parameters.getInteger("batch.size", 16384));
				props.put("linger.ms", parameters.getInteger("linger.ms", 1));
				props.put("buffer.memory", parameters.getInteger("buffer.memory", 33554432));
				props.put("key.serializer", parameters.getString("key.serializer",
						"org.apache.kafka.common.serialization.StringSerializer"));
				props.put("value.serializer", parameters.getString("value.serializer",
						"org.apache.kafka.common.serialization.StringSerializer"));
			}

			@Override
			public void open(int taskNumber, int numTasks) throws IOException {
				if (mock) {
					producer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
				} else {
					producer = new KafkaProducer<>(props);
				}
			}

			@Override
			public void writeRecord(DtdRecord rc) throws IOException {
//				String[] rcArr = rc.split(";;;");
//				
//				DtdRecord.Key cRKey = new DtdRecord.Key(
//						rcArr[0], rcArr[1], rcArr[2]
//				);
//				
//				DtdRecord cR = new DtdRecord(
//						rcArr[0], rcArr[1], rcArr[2], rcArr[3], rcArr[4], rcArr[5], rcArr[6], rcArr[7], rcArr[8], rcArr[9],
//						rcArr[10], rcArr[11], rcArr[12], rcArr[13], rcArr[14], rcArr[15], rcArr[16], rcArr[17], rcArr[18], rcArr[19],
//						rcArr[20], rcArr[21], rcArr[22], rcArr[23], rcArr[24], rcArr[25], rcArr[26], rcArr[27], rcArr[28], rcArr[29],
//						rcArr[30], rcArr[31], rcArr[32], rcArr[33], rcArr[34], rcArr[35], rcArr[36], rcArr[37], rcArr[38], rcArr[39],
//						rcArr[40], rcArr[41], rcArr[42], rcArr[43], rcArr[44], rcArr[45], rcArr[46], rcArr[47], rcArr[48], rcArr[49],
//						rcArr[50], rcArr[51], rcArr[52], cRKey
//				);
//				ProducerRecord<String, String> record = new ProducerRecord<>(topic, cR.getTran_id(), DtdProducer.MAPPER.writeValueAsString(cR));
				ProducerRecord<String, String> record = new ProducerRecord<>(topic, rc.getTran_id(), DtdProducer.MAPPER.writeValueAsString(rc));
				producer.send(record);
				
			}

			@Override
			public void close() throws IOException {
				producer.close();
			}
		};
	}
	 
	@Data
	@AllArgsConstructor
	private static class DtdRecord {
		private String part_tran_srl_num;
	    private String tran_date;
	    private String tran_id;
	    private String amt_reservation_ind;
	    private String bank_code;
	    private String br_code;
	    private String crncy_code;
	    private String cust_id;
	    private String del_flg;
	    private String entry_date;
	    private String entry_user_id;
	    private String fx_tran_amt;
	    private String gl_sub_head_code;
	    private String instrmnt_alpha;
	    private String instrmnt_date;
	    private String instrmnt_num;
	    private String instrmnt_type;
	    private String lchg_time;
	    private String lchg_user_id;
	    private String module_id;
	    private String navigation_flg;
	    private String part_tran_type;
	    private String prnt_advc_ind;
	    private String pstd_date;
	    private String pstd_flg;
	    private String pstd_user_id;
	    private String rate;
	    private String rate_code;
	    private String rcre_time;
	    private String rcre_user_id;
	    private String ref_amt;
	    private String ref_crncy_code;
	    private String ref_num;
	    private String reservation_amt;
	    private String restrict_modify_ind;
	    private String rpt_code;
	    private String sol_id;
	    private String tran_amt;
	    private String tran_crncy_code;
	    private String tran_particular;
	    private String tran_particular_2;
	    private String tran_particular_code;
	    private String tran_rmks;
	    private String tran_sub_type;
	    private String tran_type;
	    private String trea_rate;
	    private String trea_ref_num;
	    private String ts_cnt;
	    private String value_date;
	    private String vfd_date;
	    private String vfd_user_id;
	    private String voucher_print_flg;
	    private String acid;
	    private Key key;
	    
	    @Data
		@AllArgsConstructor
	    private static class Key {
	    	private String part_tran_srl_num;
		    private String tran_date;
		    private String tran_id;
	    }
	}


}
