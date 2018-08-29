package ng.exelon.etl.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import ng.exelon.etl.model.DtdRecord;

public interface DtdRecordService {

	DtdRecord save(DtdRecord dtdRecord);

    DtdRecord findOne(String id);

    Page<DtdRecord> findByCustId(String custid, PageRequest pageRequest);
    
    Page<DtdRecord> findByAcid(String acid, PageRequest pageRequest);

    Page<DtdRecord> findByPartTranType(String partTranType, PageRequest pageRequest);
    
    Page<DtdRecord> findByPartTranParticular(String partTranParticular, PageRequest pageRequest);
    
    Page<DtdRecord> findByValueDate(String startDate, String endDate, PageRequest pageRequest);

    Page<DtdRecord> findAll(PageRequest pageRequest);
//    Iterable<DtdRecord> findAll(PageRequest pageRequest);
    
}
