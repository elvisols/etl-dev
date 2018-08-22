package ng.exelon.etl.serde;

import ng.exelon.etl.service.DtdProducer.DtdRecord;


public class DtdRecordSerde extends WrapperSerde<DtdRecord> {
	public DtdRecordSerde() {
		super(new JsonSerializer<DtdRecord>(), new JsonDeserializer<DtdRecord>(DtdRecord.class));
	}
}