package ng.exelon.etl.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import ng.exelon.etl.domain.UserStats;

public class UserStatDeserializer implements Deserializer<UserStats> {

	@Override
	public void configure(Map configs, boolean isKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserStats deserialize(String topic, byte[] data) {
		ObjectMapper mapper = new ObjectMapper();
		UserStats record = null;
	    try {
	      record = mapper.readValue(data, UserStats.class);
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
