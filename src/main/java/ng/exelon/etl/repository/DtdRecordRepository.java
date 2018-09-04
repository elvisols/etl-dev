package ng.exelon.etl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import ng.exelon.etl.model.DtdRecord;

@Repository
public interface DtdRecordRepository extends ElasticsearchRepository<DtdRecord, String>{
	
	Page<DtdRecord> findByCustId(String custId, Pageable pageable);
	
	Page<DtdRecord> findByAcid(String acid, Pageable pageable);
	
	Page<DtdRecord> findByPartTranType(String partTranType, Pageable pageable);
	
	@Query("{"
			+ "\"bool\": "
				+ "{\"must\": ["
				+ 	"{\"match\": {\"tranParticular\": \"?0\"}}"
				+ "]}"
		+ "}")
	Page<DtdRecord> findByTranParticular(String partTranParticular, Pageable pageable);
	
//	@Query("{"
//		+ "\"filter\": {" 
//			+ "\"range\": {" 
//			+	"\"valueDate\": {"
//			+		"\"gte\": ?0,"
//			+		"\"lte\": ?1"
//			+	"}"
//			+ "}"
//		+ "}"
//	+ "}")
	Page<DtdRecord> findByValueDate(String startDate, String endDate, Pageable pageable);
	
	Page<DtdRecord> findAll(Pageable pageable);
	
//	GET /finacle/_search
//	GET _cat/indices
//	GET /finacle/dtd/_search
//	{
//	  "query": {
//			"match": {"tranParticular": "DEDUCTION JUNE 2018"}
//		}
//	}

//	Page<Article> findByAuthorsName(String name, Pageable pageable);
//
//    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
//    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
//
//    @Query("{\"bool\": {\"must\": {\"match_all\": {}}, \"filter\": {\"term\": {\"tags\": \"?0\" }}}}")
//    Page<Article> findByFilteredTagQuery(String tag, Pageable pageable);
//
//    @Query("{\"bool\": {\"must\": {\"match\": {\"authors.name\": \"?0\"}}, \"filter\": {\"term\": {\"tags\": \"?1\" }}}}")
//    Page<Article> findByAuthorsNameAndFilteredTagQuery(String name, String tag, Pageable pageable);
    
}
