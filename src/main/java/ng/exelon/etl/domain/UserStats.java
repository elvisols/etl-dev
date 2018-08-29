package ng.exelon.etl.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import ng.exelon.etl.model.DtdRecord;

@Data
public class UserStats {
	private int creditCount;
	private int debitCount;
	private double creditSum; 
	private double debitSum; 
	private List<Double> creditAmountList = new ArrayList<Double>();
	private List<Double> debitAmountList = new ArrayList<Double>();
	private List<Double> subList;
	private List<Double> creditSubList = new ArrayList<Double>();
	private List<Double> debitSubList = new ArrayList<Double>();
	private double creditAvg; 
	private double debitAvg; 
	private double creditMean; 
	private double debitMean; 
	private double creditMedian; 
	private double debitMedian; 
	private double creditMad; // median absolute deviation 
	private double debitMad; // median absolute deviation 
	private double creditMax;
	private double debitMax;
	private double creditMin;
	private double debitMin;
	private double creditVariance;
	private double debitVariance;
	private double creditSd; // standard deviation
	private double debitSd; // standard deviation
	private double creditZscore;
	private double debitZscore;
	private double creditZscoreM;
	private double debitZscoreM;
	private String creditZscoreRatio;
	private String debitZscoreRatio;
	private String creditZscoreRatioM;
	private String debitZscoreRatioM;
	private String timestamp;
	private long time;
	private String user;
//	@JsonIgnore
	private DtdRecord dtdRecord;
	double tmpVariance;
	
	public UserStats() {	}
	
	public UserStats compute(DtdRecord dtdRecord) {
//		System.out.println("dtdRecord: " + dtdRecord);
		if(dtdRecord.getPartTranType().equals("C")) {
			// Credit transactions
			if (creditCount == 0) this.creditMin = Double.parseDouble(dtdRecord.getTranAmt());
	        
	        this.creditCount++;
	        
	        this.creditAmountList.add(Double.parseDouble(dtdRecord.getTranAmt()));
	        
	        this.creditSum += Double.parseDouble(dtdRecord.getTranAmt());
	        
	        this.creditMin = Math.min(this.creditMin, Double.parseDouble(dtdRecord.getTranAmt()));
	        
	        this.creditMax = Math.max(this.creditMax, Double.parseDouble(dtdRecord.getTranAmt()));
	        
	        this.creditMean = this.creditSum / this.creditCount;
	        
		} else {
			// Debit transactions
			if (debitCount == 0) this.debitMin = Double.parseDouble(dtdRecord.getTranAmt());
	        
	        this.debitCount++;
	        
	        this.debitAmountList.add(Double.parseDouble(dtdRecord.getTranAmt()));
	        
	        this.debitSum += Double.parseDouble(dtdRecord.getTranAmt());
	        
	        this.debitMin = Math.min(this.debitMin, Double.parseDouble(dtdRecord.getTranAmt()));
	        
	        this.debitMax = Math.max(this.debitMax, Double.parseDouble(dtdRecord.getTranAmt()));
	        
	        this.debitMean = this.debitSum / this.debitCount;
		}
		
//		System.out.println("Counts now: " + creditCount + " " + debitCount);
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss");

//		try {
//		    Date d = f.parse(dtdRecord.getValue_date());
//		    this.time = d.getTime();
////		    System.out.println("#####################  this.time = " + this.time);
//		} catch (ParseException e) {
//		    e.printStackTrace();
//		}
		this.time = dtdRecord.getLog_time();
		
        this.timestamp = Instant.now().toString();
        
        this.user = dtdRecord.getEntryUserId();
        
        this.dtdRecord = dtdRecord;
        
        return this;
	}
	
	public UserStats computeZscore() {
		if(!this.creditAmountList.isEmpty()) {
			// get median
			this.creditMedian = getMedian(this.creditAmountList);
			
			// get variance
			// Samples with low variance have data that is clustered closely about the mean.
			// Samples with high variance have data that is spread far from the mean.
			this.creditVariance = getVariance(this.creditAmountList, this.creditMedian, this.creditMean, this.creditCount);
			this.creditSubList = this.subList;
			
			// get standard deviation
			this.creditSd =  Math.sqrt(this.creditVariance);

			// get median absolute deviation
			this.creditMad = getMedianAbsDeviation(creditSubList);
			
			/*
			 * Calculating modified z-score as against the standard z-score
			 * z-score = Remember, a z-score is a measure of how many standard deviations a data point is away from the mean.
			 * A negative z-score indicates that the data point is less than the mean, and a positive z-score indicates the data point in question is larger than the mean.
			 */
			this.creditZscore = this.creditSd == 0.0 ? 0.0 : (this.creditAmountList.get(this.creditAmountList.size() - 1) - this.creditMean) / this.creditSd;
			this.creditZscoreM = this.creditMad == 0.0 ? 0.0 : 0.6745 * (this.creditAmountList.get(this.creditAmountList.size() - 1) - this.creditMedian) / this.creditMad;
			
			this.creditZscoreRatio = String.format("%.1f", zScoreToPercentile(this.creditZscore));
			this.creditZscoreRatioM = String.format("%.1f", zScoreToPercentile(this.creditZscoreM));
		} 
		
		if(!this.debitAmountList.isEmpty()) {
			// get median
			this.debitMedian = getMedian(this.debitAmountList);
			
			// get variance
			// Samples with low variance have data that is clustered closely about the mean.
			// Samples with high variance have data that is spread far from the mean.
			this.debitVariance = getVariance(this.debitAmountList, this.debitMedian, this.debitMean, this.debitCount);
			this.debitSubList = this.subList;
			
			// get standard deviation
			this.debitSd =  Math.sqrt(this.debitVariance);

			// get median absolute deviation
			this.debitMad = getMedianAbsDeviation(debitSubList);
			
			/*
			 * Calculating modified z-score as against the standard z-score
			 * z-score = Remember, a z-score is a measure of how many standard deviations a data point is away from the mean.
			 * A negative z-score indicates that the data point is less than the mean, and a positive z-score indicates the data point in question is larger than the mean.
			 */
			this.debitZscore = this.debitSd == 0.0 ? 0.0 : (this.debitAmountList.get(this.debitAmountList.size() - 1) - this.debitMean) / this.debitSd;
			this.debitZscoreM = this.debitMad == 0.0 ? 0.0 : 0.6745 * (this.debitAmountList.get(this.debitAmountList.size() - 1) - this.debitMedian) / this.debitMad;
			
			this.debitZscoreRatio = String.format("%.1f", zScoreToPercentile(this.debitZscore));
			this.debitZscoreRatioM = String.format("%.1f", zScoreToPercentile(this.debitZscoreM));
//			this.debitZscoreRatio = String.format("%.1f", zScoreToPercentile(this.debitZscore)) + "%";
//			this.debitZscoreRatioM = String.format("%.1f", zScoreToPercentile(this.debitZscoreM)) + "%";
		}
		
		return this;
	}
	
	// get median
	public double getMedian(List<Double> amountlist) {
		Double[] amtArray = amountlist.toArray(new Double[amountlist.size()]);
		Arrays.sort(amtArray);
		return (amtArray[amtArray.length/2] + amtArray[(amtArray.length-1)/2]) / 2;
	}
	
	// get median absolute deviation
	public double getMedianAbsDeviation(List<Double> subList) {
		Double[] subArray = subList.toArray(new Double[this.subList.size()]);
		Arrays.sort(subArray);
		return (subArray[subArray.length/2] + subArray[(subArray.length-1)/2]) / 2;
	}
	
	// get variance
	public double getVariance(List<Double> amountlist, double median, double mean, int count) {
		this.subList = new ArrayList<Double>();
		this.tmpVariance = 0.0;
		amountlist.forEach((amt) -> {
			this.subList.add(Math.abs(amt - median));
			this.tmpVariance +=  Math.pow(amt - mean, 2);
		});
		
		return tmpVariance / count;
	}
	
	public double zScoreToPercentile(double zScore)
	{
		double percentile = 0.0;
		
		NormalDistribution dist = new NormalDistribution();
		percentile = dist.cumulativeProbability(zScore) * 100;
		
		if(zScore < 0.0) {
			percentile = ((50 - percentile) / 50) * 100;
		} else if(zScore > 0.0) {
			percentile = ((percentile - 50) / 50) * 100;
		} else {
			percentile = 0.0;
		}
		
		return percentile;
	}

	public long getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "UserStats [creditCount=" + creditCount + ", debitCount=" + debitCount + ", creditSum=" + creditSum
				+ ", debitSum=" + debitSum + ", creditAvg=" + creditAvg + ", debitAvg=" + debitAvg + ", creditMean="
				+ creditMean + ", debitMean=" + debitMean + ", creditMedian=" + creditMedian + ", debitMedian="
				+ debitMedian + ", creditMad=" + creditMad + ", debitMad=" + debitMad + ", creditMax=" + creditMax
				+ ", debitMax=" + debitMax + ", creditMin=" + creditMin + ", debitMin=" + debitMin + ", creditVariance="
				+ creditVariance + ", debitVariance=" + debitVariance + ", creditSd=" + creditSd + ", debitSd="
				+ debitSd + ", creditZscore=" + creditZscore + ", debitZscore=" + debitZscore + ", creditZscoreM="
				+ creditZscoreM + ", debitZscoreM=" + debitZscoreM + ", creditZscoreRatio=" + creditZscoreRatio
				+ ", debitZscoreRatio=" + debitZscoreRatio + ", creditZscoreRatioM=" + creditZscoreRatioM
				+ ", debitZscoreRatioM=" + debitZscoreRatioM + ", timestamp=" + timestamp + ", time=" + time + ", user="
				+ user + "]";
	}
	
//	double roundTwoDecimals(double d) { 
//	      DecimalFormat twoDForm = new DecimalFormat("#.##"); 
//	      return Double.valueOf(twoDForm.format(d));
//	} 
	
}
