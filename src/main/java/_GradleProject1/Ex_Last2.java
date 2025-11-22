package _GradleProject1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Ex_Last2 {
	
	public static void main(String[] args) {
		/*-----------------------------------------------------------------
		 * WorkingResult.csv(１ヶ月の労働実績)を読み込んで１ヶ月の給与の総額を算出して出力
		 * 
		 * 
		 * コマンドライン引数から出勤時間と退勤時間を取得する
		 *　時給900円　１分単位で給与支払われる
		 *　小数点以下切り捨て
		 *　労働時間が6時間〜８時間の場合45分休憩　８時間超える場合は１時間休憩　休憩中は給与無し
		 *　実労働時間が８時間を超える場合超過分に対して1.25倍の給与支払い
		 *　BigDecimalによる誤差の考慮不要　
		 -----------------------------------------------------------------*/
		final String WORKING_RESULT_FILE_PATH = "src/main/resources/WorkingResult.csv";
		//final String WORKING_RESULT_FILE_PATH = "src/WorkingResult.csv";  //『プロジェクトフォルダ/』 がカレントディレクトリになるからそこからの相対パス
		List<String> workingResults = new ArrayList<String>(); //ファイルから読み込んだデータの格納用
		Ex_Last2 ex_Last2 = new Ex_Last2();
		ex_Last2.setListData(workingResults,WORKING_RESULT_FILE_PATH); //workingResultsにファイルのデータを格納
		
		//CSVから取得したデータから１ヶ月の給与その総額を算出
		int monthSalary = ex_Last2.calcMonthSalary(workingResults); //月給

		System.out.println("月給は" + monthSalary); 
		
		
	}
	/*-----------------------------------------------------------------
	 * CSVからデータを取得してリストに格納
	 * 引数：List<String> workingResults ファイルから読み込んだデータリスト
	 * 戻り値：無し
	 -----------------------------------------------------------------*/
	
	public void setListData(List<String> list,String filePath) {
		//CVSからデータを取得
		try (BufferedReader br = new BufferedReader(new FileReader( new File(filePath)));){ //WorkingResult.csvの読み込み準備
			
			String workingResultData = "";
			
			while ((workingResultData = br.readLine()) != null) {
	            list.add(workingResultData);
	        }

		}catch(IOException e) {
			// 例外をラップして実行時例外としてスローし、mainメソッドで捕捉できるようにする
	        throw new RuntimeException("ファイル読み込み中にエラーが発生しました: " + filePath, e);
		}

	}
	
	/*-----------------------------------------------------------------
	 * CSVから取得したデータから１ヶ月の給与その総額を算出
	 * 引数：List<String> workingResults ファイルから読み込んだデータリスト
	 * 戻り値：int monthSalary 月給
	 -----------------------------------------------------------------*/
	
	public int calcMonthSalary(List<String> workingResults) {
		final String COMMA = ","; //カンマ
		
		//CSVから取得したデータから１ヶ月の給与その総額を算出
		int monthSalary = 0; //月給
				
		for(int i = 0 ; i < workingResults.size(); i++) {
			String workingRecode = workingResults.get(i); //一行目のデータを取得
			String[] forSplitRecode = workingRecode.split(COMMA); // splitメソッドを用いてカンマ区切りで文字列を分解＆配列にれぞれ格納
					
			//出社時間と退勤時間を取得して格納
			LocalTime startTime = LocalTime.parse(forSplitRecode[1]); // 出社時間
			LocalTime finishTime = LocalTime.parse(forSplitRecode[2]); //退社時間
			
			Duration duration = Duration.between(startTime, finishTime);
			int workMin = (int)(duration.toMinutes()); // 労働時間を分単位で取得
			int restTime = calcRestTime(workMin); //休憩時間の取得
			int actualWorkingMin = workMin - restTime; //実労働時間(分単位)			
					
			monthSalary += calcDailySalary(actualWorkingMin); //月給に＋＋
		}
		return monthSalary;
	}
	
	 /* -----------------------------------------------------------------
	  *休憩時間の算出
	  *引数：int workMin 労働時間(分)
	  *戻り値：int lesttime
	 -----------------------------------------------------------------*/
	public int calcRestTime(int workMin) {
		final int SMALL_REST = 45; //休憩
		final int BIG_REST = 60; //休憩			
		final int OVERTIME_BORDER_MINUTES = 480; // 8時間
		final int MINIMUM_REST_TIME_MINUTES = 360; // 6時間
		int restTime = 0;
		
		if(workMin >= MINIMUM_REST_TIME_MINUTES && workMin < OVERTIME_BORDER_MINUTES) { //労働時間が6時間〜８時間の場合
			restTime = SMALL_REST;
		}else if(workMin >= OVERTIME_BORDER_MINUTES ) { //労働時間が8以上の場合
			restTime = BIG_REST;
		}else { //0~6時間の労働の場合
			restTime = 0;
		}
		
		return restTime;
	}
	
	
	/*-----------------------------------------------------------------
	 * 総労働時間から日給の計算
	 * 引数：int actualWorkingMin 実労働時間
	 * 戻り値：int dailySalary 日給
	 -----------------------------------------------------------------*/
	
	public int calcDailySalary(int actualWorkingMin) {
		//日給の算出
		//実労働時間が８時間を超える場合，超過分を残業ボーナスとして追加

		final int HOUR_SALARY = 900; //時給
		final int MIN_SALARY = HOUR_SALARY / 60; //分給
		final double OVERTIME_WORK_BONUS = 1.25; //残業ボーナス率
		final int OVERTIME_BORDER_MINUTE = 480;
		final int REGULAR_WORK_HOUR = 8;
		
		int dailySalary =  0; //日給
		int overWorkFare = 0; //残業代
		int overWorkTime = 0; //残業時間
		if(actualWorkingMin >= OVERTIME_BORDER_MINUTE) { //労働時間が8時間を超えている場合
			overWorkTime = actualWorkingMin - OVERTIME_BORDER_MINUTE; //残業時間（分）
			overWorkFare = (int) (overWorkTime * MIN_SALARY * OVERTIME_WORK_BONUS); //残業代（残業時間(分)*分給*残業ボーナス倍率）
			dailySalary = REGULAR_WORK_HOUR * HOUR_SALARY + overWorkFare; //日給 (残業時間基準値時間 * 時給 + 残業代)
		}else {
			dailySalary = (int)(actualWorkingMin * MIN_SALARY); //日給
		}
		
		return dailySalary;
	}
	
	
}
