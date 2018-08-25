package ng.exelon.etl.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import ng.exelon.etl.service.DtdProducer.DtdKey;
import ng.exelon.etl.service.DtdProducer.DtdRecord;

public class DtdKeySerializer implements Serializer<DtdKey> {

	@Override
	public void configure(Map configs, boolean isKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] serialize(String topic, DtdKey data) {
		byte[] retVal = null;
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
	      retVal = objectMapper.writeValueAsString(data).getBytes();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return retVal;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
}