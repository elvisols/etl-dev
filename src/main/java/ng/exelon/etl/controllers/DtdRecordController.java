package ng.exelon.etl.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ng.exelon.etl.controllers.DTO.DtdRecordDTO;
import ng.exelon.etl.model.DtdRecord;
import ng.exelon.etl.service.DtdRecordService;

@RestController
@RequestMapping("api/dtd")
//@Api(value = "DtdRecord")
public class DtdRecordController {


	@Autowired
	DtdRecordService dtdService;
	
	/**
	* This method fetches all dtd records
	*
	* @param void
	* @method GET
	* @return result as list of dtd object
	*/
	@GetMapping(produces=MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get all DTD Records")
	public DtdRecordDTO findDtdRecords(@RequestParam(value = "page", defaultValue="0") int page, @RequestParam(value = "size", defaultValue="10") int size) {
		Page<DtdRecord> dtdRecords = dtdService.findAll(PageRequest.of(page, size));
		DtdRecordDTO dtdRecordDTO = new DtdRecordDTO(dtdRecords.getContent(), dtdRecords.getSize(), dtdRecords.getNumber(), 
				dtdRecords.getNumberOfElements(), dtdRecords.getTotalPages(), dtdRecords.getTotalElements(), 
				dtdRecords.getPageable().getPageNumber(), dtdRecords.getPageable().getPageSize());

		return dtdRecordDTO;
	}
	
	/**
	 * This method fetches all dtd records by cust id
	 *
	 * @param void
	 * @method GET
	 * @return result as list of dtd object
	 */
	@GetMapping(value="/{custId}", produces=MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get all DTD Records by Cust id")
	public DtdRecordDTO findDtdRecordsByCustId(@PathVariable String custid, @RequestParam(value = "page", defaultValue="0") int page, @RequestParam(value = "size", defaultValue="10") int size) {
		Page<DtdRecord> dtdRecords = dtdService.findByCustId(custid, PageRequest.of(page, size));
		DtdRecordDTO dtdRecordDTO = new DtdRecordDTO(dtdRecords.getContent(), dtdRecords.getSize(), dtdRecords.getNumber(), 
				dtdRecords.getNumberOfElements(), dtdRecords.getTotalPages(), dtdRecords.getTotalElements(), 
				dtdRecords.getPageable().getPageNumber(), dtdRecords.getPageable().getPageSize());
		
		return dtdRecordDTO;
	}
	
	/**
	 * This method fetches all dtd records by acid
	 *
	 * @param void
	 * @method GET
	 * @return result as list of dtd object
	 */
	@GetMapping(value="/{acid}", produces=MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get all DTD Records by acid id")
	public DtdRecordDTO findDtdRecordsByAcidId(@PathVariable String acid, @RequestParam(value = "page", defaultValue="0") int page, @RequestParam(value = "size", defaultValue="10") int size) {
		Page<DtdRecord> dtdRecords = dtdService.findByAcid(acid, PageRequest.of(page, size));
		DtdRecordDTO dtdRecordDTO = new DtdRecordDTO(dtdRecords.getContent(), dtdRecords.getSize(), dtdRecords.getNumber(), 
				dtdRecords.getNumberOfElements(), dtdRecords.getTotalPages(), dtdRecords.getTotalElements(), 
				dtdRecords.getPageable().getPageNumber(), dtdRecords.getPageable().getPageSize());
		
		return dtdRecordDTO;
	}
	
	/**
	 * This method fetches all dtd records by PartTranType
	 *
	 * @param void
	 * @method GET
	 * @return result as list of dtd object
	 */
	@GetMapping(value="/{partTranType}", produces=MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get all DTD Records by PartTranType")
	public DtdRecordDTO findDtdRecordsByPartTranType(@PathVariable String partTranType, @RequestParam(value = "page", defaultValue="0") int page, @RequestParam(value = "size", defaultValue="10") int size) {
		Page<DtdRecord> dtdRecords = dtdService.findByPartTranType(partTranType, PageRequest.of(page, size));
		DtdRecordDTO dtdRecordDTO = new DtdRecordDTO(dtdRecords.getContent(), dtdRecords.getSize(), dtdRecords.getNumber(), 
				dtdRecords.getNumberOfElements(), dtdRecords.getTotalPages(), dtdRecords.getTotalElements(), 
				dtdRecords.getPageable().getPageNumber(), dtdRecords.getPageable().getPageSize());
		
		return dtdRecordDTO;
	}
	
	/**
	 * This method fetches all dtd records by PartTranParticular
	 *
	 * @param void
	 * @method GET
	 * @return result as list of dtd object
	 */
	@GetMapping(value="/{partTranParticular}", produces=MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get all DTD Records by PartTranParticular")
	public DtdRecordDTO findDtdRecordsByPartTranParticular(@PathVariable String partTranParticular, @RequestParam(value = "page", defaultValue="0") int page, @RequestParam(value = "size", defaultValue="10") int size) {
		Page<DtdRecord> dtdRecords = dtdService.findByPartTranParticular(partTranParticular, PageRequest.of(page, size));
		DtdRecordDTO dtdRecordDTO = new DtdRecordDTO(dtdRecords.getContent(), dtdRecords.getSize(), dtdRecords.getNumber(), 
				dtdRecords.getNumberOfElements(), dtdRecords.getTotalPages(), dtdRecords.getTotalElements(), 
				dtdRecords.getPageable().getPageNumber(), dtdRecords.getPageable().getPageSize());
		
		return dtdRecordDTO;
	}
	
	/**
	 * This method fetches all dtd records by ValueDate
	 *
	 * @param void
	 * @method GET
	 * @return result as list of dtd object
	 */
	@GetMapping(value="/valueDate", produces=MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get all DTD Records by ValueDate")
	public DtdRecordDTO findDtdRecordsByValueDate(
			@RequestParam(value = "start-date", required = true) String startDate, @RequestParam(value = "end-date", required = true) String endDate,
			@RequestParam(value = "page", defaultValue="0") int page, @RequestParam(value = "size", defaultValue="10") int size) {
		Page<DtdRecord> dtdRecords = dtdService.findByValueDate(startDate, endDate, PageRequest.of(page, size));
		DtdRecordDTO dtdRecordDTO = new DtdRecordDTO(dtdRecords.getContent(), dtdRecords.getSize(), dtdRecords.getNumber(), 
				dtdRecords.getNumberOfElements(), dtdRecords.getTotalPages(), dtdRecords.getTotalElements(), 
				dtdRecords.getPageable().getPageNumber(), dtdRecords.getPageable().getPageSize());
		
		return dtdRecordDTO;
	}
	
	/**
	* This method returns a single dtd record.
	*
	* @param <b>dtdId</b> this is the id of the dtd record to retrieve.
	* @method GET
	* @return result, as a dtd object
	* @throws StockNotFoundException, if dtd record for the specified id cannot be retrieved.
	*/
	@GetMapping(value="/{dtdId}", produces=MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(value = "Get a single dtd by id")
	public DtdRecord findStockById(@PathVariable String dtdId)  {
		return dtdService.findOne(dtdId);
	}
	
}
