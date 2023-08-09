package project_score;

import lombok.Data;

@Data

public class Score {
	private String stuName;
	private String stuNo;
	private int kor;
	private int mat;
	private int eng;
	private int sum;
	private String grade;
	private int rank;
}
