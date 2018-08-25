package ng.exelon.etl.serde;

import ng.exelon.etl.service.DtdProducer.DtdKey;


public class DtdKeySerde extends WrapperSerde<DtdKey> {
	public DtdKeySerde() {
		super(new JsonSerializer<DtdKey>(), new JsonDeserializer<DtdKey>(DtdKey.class));
	}
}