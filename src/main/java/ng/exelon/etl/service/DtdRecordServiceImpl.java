package ng.exelon.etl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ng.exelon.etl.model.DtdRecord;
import ng.exelon.etl.repository.DtdRecordRepository;


@Service
public class DtdRecordServiceImpl implements DtdRecordService {

	@Autowired
	private DtdRecordRepository dtdRecordRepository;
	
	@Override
	public DtdRecord save(DtdRecord dtdRecord) {
		return dtdRecordRepository.save(dtdRecord);
	}

	@Override
	public DtdRecord findOne(String id) {
		return dtdRecordRepository.findById(id).get();
	}

	@Override
	public Page<DtdRecord> findByCustId(String custid, PageRequest pageRequest) {
		return dtdRecordRepository.findByCustId(custid, pageRequest);
	}

	@Override
	public Page<DtdRecord> findByAcid(String acid, PageRequest pageRequest) {
		return dtdRecordRepository.findByAcid(acid, pageRequest);
	}

	@Override
	public Page<DtdRecord> findByPartTranType(String partTranType, PageRequest pageRequest) {
		return dtdRecordRepository.findByPartTranType(partTranType, pageRequest);
	}

	@Override
	public Page<DtdRecord> findByPartTranParticular(String partTranParticular, PageRequest pageRequest) {
		return dtdRecordRepository.findByTranParticular(partTranParticular, pageRequest);
	}

	@Override
	public Page<DtdRecord> findByValueDate(String startDate, String endDate, PageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<DtdRecord> findAll(PageRequest pageRequest) {
		return dtdRecordRepository.findAll(pageRequest);
	}

}
