package com.java.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.java.Model.CheckAnswerResponse.WordComparison;

/**
 * Utility để so sánh văn bản và tính điểm
 */
public class TextComparisonUtil {

    /**
     * Chuẩn hóa text: lowercase, remove punctuation, trim
     */
    public static String normalizeText(String text) {
        if (text == null) return "";
        
        return text.toLowerCase()
                   .replaceAll("[^a-z0-9\\s]", "") // Xóa dấu câu
                   .replaceAll("\\s+", " ")        // Chuẩn hóa khoảng trắng
                   .trim();
    }

    /**
     * Tính điểm dựa trên độ giống nhau (0-100)
     */
    public static BigDecimal calculateScore(String correctText, String userText) {
        String correct = normalizeText(correctText);
        String user = normalizeText(userText);

        if (correct.isEmpty()) return BigDecimal.ZERO;
        if (correct.equals(user)) return new BigDecimal("100.00");

        String[] correctWords = correct.split("\\s+");
        String[] userWords = user.split("\\s+");

        int correctCount = 0;
        int totalWords = correctWords.length;

        // Đếm số từ đúng
        Set<String> userWordSet = new HashSet<>(Arrays.asList(userWords));
        for (String word : correctWords) {
            if (userWordSet.contains(word)) {
                correctCount++;
            }
        }

        // Tính điểm
        double scoreValue = ((double) correctCount / totalWords) * 100;
        return new BigDecimal(scoreValue).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * So sánh chi tiết từng từ
     */
    public static List<WordComparison> compareWords(String correctText, String userText) {
        List<WordComparison> comparisons = new ArrayList<>();
        
        String correct = normalizeText(correctText);
        String user = normalizeText(userText);

        String[] correctWords = correct.split("\\s+");
        String[] userWords = user.split("\\s+");

        List<String> userWordList = new ArrayList<>(Arrays.asList(userWords));

        // Duyệt qua từng từ đúng
        for (String correctWord : correctWords) {
            if (userWordList.contains(correctWord)) {
                comparisons.add(new WordComparison(correctWord, "correct"));
                userWordList.remove(correctWord); // Xóa để tránh trùng
            } else {
                comparisons.add(new WordComparison(correctWord, "missing"));
            }
        }

        // Từ thừa (user gõ nhưng không có trong đáp án)
        for (String extraWord : userWordList) {
            comparisons.add(new WordComparison(extraWord, "extra"));
        }

        return comparisons;
    }

    /**
     * Tính Levenshtein Distance (độ tương đồng nâng cao)
     */
    public static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], 
                                   Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Tính độ tương đồng phần trăm (dựa trên Levenshtein)
     */
    public static BigDecimal calculateSimilarity(String s1, String s2) {
        String str1 = normalizeText(s1);
        String str2 = normalizeText(s2);

        if (str1.equals(str2)) return new BigDecimal("100.00");

        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) return new BigDecimal("100.00");

        int distance = levenshteinDistance(str1, str2);
        double similarity = (1.0 - (double) distance / maxLength) * 100;

        return new BigDecimal(similarity).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Tạo feedback dựa trên điểm
     */
    public static String generateFeedback(BigDecimal score) {
        double scoreValue = score.doubleValue();

        if (scoreValue == 100) {
            return "Perfect! Hoàn hảo!";
        } else if (scoreValue >= 90) {
            return "Excellent! Xuất sắc!";
        } else if (scoreValue >= 80) {
            return "Very Good! Rất tốt!";
        } else if (scoreValue >= 70) {
            return "Good! Tốt!";
        } else if (scoreValue >= 60) {
            return "Fair. Khá, cần cố gắng thêm!";
        } else if (scoreValue >= 50) {
            return "Pass. Đạt, nhưng cần luyện tập nhiều hơn!";
        } else {
            return "Need more practice. Cần luyện tập nhiều hơn!";
        }
    }
}