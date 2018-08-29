package ng.exelon.etl.serde;

import ng.exelon.etl.model.DtdRecord;

public class DtdRecordSerde extends WrapperSerde<DtdRecord> {
	public DtdRecordSerde() {
		super(new JsonSerializer<DtdRecord>(), new JsonDeserializer<DtdRecord>(DtdRecord.class));
	}
}