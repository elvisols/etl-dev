package ng.exelon.etl.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "finacle", type = "dtd")
public class DtdRecord {
	
	@Id
	private String id; // this comprises part_tran_srl_num&tran_id&tran_date
	
	private String partTranSrlNum;
    private String tranDate;
    private String tranId;
//    private String amt_reservation_ind;
//    private String bank_code;
    private String brCode;
//    private String crncy_code;
    private String custId;
    private String delFlg;
    private String entryDate;
    private String entryUserId;
//    private String fx_tran_amt;
//    private String gl_sub_head_code;
//    private String instrmnt_alpha;
//    private String instrmnt_date;
//    private String instrmnt_num;
//    private String instrmnt_type;
    private String lchgTime;
    private String lchgUserId;
//    private String module_id;
//    private String navigation_flg;
    private String partTranType;
//    private String prnt_advc_ind;
    private String pstdDate;
    private String pstdFlg;
    private String pstdUserId;
//    private String rate;
//    private String rate_code;
//    private String rcre_time;
//    private String rcre_user_id;
//    private String ref_amt;
//    private String ref_crncy_code;
//    private String ref_num;
//    private String reservation_amt;
//    private String restrict_modify_ind;
//    private String rpt_code;
    private String solId;
    private String tranAmt;
    private String tranCrncyCode;
    private String tranParticular;
//    private String tran_particular_2;
//    private String tran_particular_code;
    private String tranRmks;
    private String tranSubType;
//    private String tran_type;
//    private String trea_rate;
//    private String trea_ref_num;
//    private String ts_cnt;
    private String valueDate;
//    private String vfd_date;
//    private String vfd_user_id;
//    private String voucher_print_flg;
    private String acid;
//    private Key key;
    private long log_time;
    
}
