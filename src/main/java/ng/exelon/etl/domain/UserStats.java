package ng.exelon.etl.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import ng.exelon.etl.service.DtdProducer.DtdRecord;


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
	double tmpVariance;
	
	public UserStats() {	}
	
	public UserStats compute(DtdRecord dtdRecord) {
		if(dtdRecord.getPart_tran_type().equals("C")) {
			// Credit transactions
			if (creditCount == 0) this.creditMin = Double.parseDouble(dtdRecord.getTran_amt());
	        
	        this.creditCount++;
	        
	        this.creditAmountList.add(Double.parseDouble(dtdRecord.getTran_amt()));
	        
	        this.creditSum += Double.parseDouble(dtdRecord.getTran_amt());
	        
	        this.creditMin = Math.min(this.creditMin, Double.parseDouble(dtdRecord.getTran_amt()));
	        
	        this.creditMax = Math.max(this.creditMax, Double.parseDouble(dtdRecord.getTran_amt()));
	        
	        this.creditMean = this.creditSum / this.creditCount;
	        
		} else {
			// Debit transactions
			if (debitCount == 0) this.debitMin = Double.parseDouble(dtdRecord.getTran_amt());
	        
	        this.debitCount++;
	        
	        this.debitAmountList.add(Double.parseDouble(dtdRecord.getTran_amt()));
	        
	        this.debitSum += Double.parseDouble(dtdRecord.getTran_amt());
	        
	        this.debitMin = Math.min(this.debitMin, Double.parseDouble(dtdRecord.getTran_amt()));
	        
	        this.debitMax = Math.max(this.debitMax, Double.parseDouble(dtdRecord.getTran_amt()));
	        
	        this.debitMean = this.debitSum / this.debitCount;
		}

		this.time = Instant.parse(dtdRecord.getValue_date()).toEpochMilli();
		
        this.timestamp = Instant.now().toString();
        
        this.user = dtdRecord.getEntry_user_id();
        
        return this;
	}
	
	public UserStats computeZscore() {
		// get median
		this.creditMedian = getMedian(this.creditAmountList);
		this.debitMedian = getMedian(this.debitAmountList);
		
		// get variance
		// Samples with low variance have data that is clustered closely about the mean.
		// Samples with high variance have data that is spread far from the mean.
		this.creditVariance = getVariance(this.creditAmountList, this.creditMedian, this.creditMean, this.creditCount);
		this.creditSubList = this.subList;
		this.debitVariance = getVariance(this.debitAmountList, this.debitMedian, this.debitMean, this.debitCount);
		this.debitSubList = this.subList;
		
		// get standard deviation
		this.creditSd =  Math.sqrt(this.creditVariance);
		this.debitSd =  Math.sqrt(this.debitVariance);

		// get median absolute deviation
		this.creditMad = getMedianAbsDeviation(creditSubList);
		this.debitMad = getMedianAbsDeviation(debitSubList);
		
		/*
		 * Calculating modified z-score as against the standard z-score
		 * z-score = Remember, a z-score is a measure of how many standard deviations a data point is away from the mean.
		 * A negative z-score indicates that the data point is less than the mean, and a positive z-score indicates the data point in question is larger than the mean.
		 */
		this.creditZscore = this.creditSd == 0.0 ? 0.0 : (this.creditAmountList.get(this.creditAmountList.size() - 1) - this.creditMean) / this.creditSd;
		this.debitZscore = this.debitSd == 0.0 ? 0.0 : (this.debitAmountList.get(this.debitAmountList.size() - 1) - this.debitMean) / this.debitSd;
		this.creditZscoreM = this.creditMad == 0.0 ? 0.0 : 0.6745 * (this.creditAmountList.get(this.creditAmountList.size() - 1) - this.creditMedian) / this.creditMad;
		this.debitZscoreM = this.debitMad == 0.0 ? 0.0 : 0.6745 * (this.debitAmountList.get(this.debitAmountList.size() - 1) - this.debitMedian) / this.debitMad;
		
		this.creditZscoreRatio = String.format("%.1f", zScoreToPercentile(this.creditZscore)) + "%";
		this.debitZscoreRatio = String.format("%.1f", zScoreToPercentile(this.debitZscore)) + "%";
		this.creditZscoreRatioM = String.format("%.1f", zScoreToPercentile(this.creditZscoreM)) + "%";
		this.debitZscoreRatioM = String.format("%.1f", zScoreToPercentile(this.debitZscoreM)) + "%";
        
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
