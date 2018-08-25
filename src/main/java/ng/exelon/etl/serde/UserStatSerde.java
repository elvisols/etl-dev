package ng.exelon.etl.serde;

import ng.exelon.etl.domain.UserStats;


public class UserStatSerde extends WrapperSerde<UserStats> {
	public UserStatSerde() {
		super(new JsonSerializer<UserStats>(), new JsonDeserializer<UserStats>(UserStats.class));
	}
}