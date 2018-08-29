package ng.exelon.etl.controllers.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import ng.exelon.etl.model.DtdRecord;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
public class DtdRecordDTO {
	
	public  List<DtdRecord> dtdRecord;
	public  int size;
	public  int number;
	public  int numberOfElements;
	public  int totalPages;
	public  long totalElements;
	public  int pageNumber;
	public  int pageSize;

}
