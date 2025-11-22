package _GradleProject1;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// メインのクラス（Ex_Last2）のメソッドをテストします
// ※もしEx_Last2クラスがパッケージ化されている場合は、適切なimport文が必要です

public class Ex_Last2Test {
	private Ex_Last2 ex;

    // @BeforeEach: 各テストメソッド実行直前に実行される準備メソッド
    @BeforeEach
    void setUp() {
        // 2. 各テスト実行前にインスタンス化
        ex = new Ex_Last2();
    }
    // ----------------------------------------------------
    // 休憩時間 (calcRestTime) の境界値テスト
    // ----------------------------------------------------

    @Test
    @DisplayName("6時間未満の労働は休憩0分であること")
    void testCalcRestTime_UnderSixHours() {
        // 5時間59分 (359分) -> 期待値 0分
        assertEquals(0, ex.calcRestTime(359));
    }

    @Test
    @DisplayName("ちょうど6時間で休憩45分が適用されること")
    void testCalcRestTime_ExactlySixHours() {
        // 6時間00分 (360分) -> 期待値 45分
        assertEquals(45, ex.calcRestTime(360));
    }

    @Test
    @DisplayName("ちょうど8時間で休憩60分が適用されること")
    void testCalcRestTime_ExactlyEightHours() {
        // 8時間00分 (480分) -> 期待値 60分
        assertEquals(60, ex.calcRestTime(480));
    }
    
    // ----------------------------------------------------
    // 日給 (calcDailySalary) の残業代テスト
    // ----------------------------------------------------
    
    @Test
    @DisplayName("8時間ちょうどの給与は7200円であること")
    void testCalcDailySalary_ExactlyEightHours() {
        // 実労働時間 480分 (休憩後8時間)
        // 8時間 * 900円/時 = 7,200円
        assertEquals(7200, ex.calcDailySalary(480));
    }

    @Test
    @DisplayName("30分の残業代が正しく計算されていること")
    void testCalcDailySalary_WithOvertime() {
        // 実労働時間 8時間30分 = 510分
        // 残業代: 30分 × 15円/分 × 1.25 = 562.5円 -> 整数切り捨てで 562円
        // 合計: 7200 + 562 = 7762円
        int actualWorkingMin = 510;
        int expectedSalary = 7762;
        
        assertEquals(expectedSalary, ex.calcDailySalary(actualWorkingMin));
    }
}