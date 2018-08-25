package ng.exelon.etl.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import ng.exelon.etl.service.DtdProducer.DtdRecord;

public class DtdRecordDeserializer implements Deserializer<DtdRecord> {

	@Override
	public void configure(Map configs, boolean isKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DtdRecord deserialize(String topic, byte[] data) {
		ObjectMapper mapper = new ObjectMapper();
	    DtdRecord record = null;
	    try {
	      record = mapper.readValue(data, DtdRecord.class);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return record;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
}
