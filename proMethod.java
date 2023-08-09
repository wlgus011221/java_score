package project_score;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import project_score.Score;

public class proMethod {
	
	// Field
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	private Score score = new Score();
	
	
	// Constructor
	public proMethod() {
		try {
			// JDBC Driver 등록
			Class.forName("oracle.jdbc.OracleDriver");
			
			// 연결하기
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/orcl", "java", "oracle");
		} catch(Exception e) {
			e.printStackTrace();
			exit();
		}
	}
	
	
	// Method
	public void mainMenu() {
		System.out.println();
		System.out.println("1.입력 | 2.조회 | 3.전체 삭제 | 4.집계표 | 5.종료");
		System.out.println("--------------------------------------------");
		System.out.print("메뉴 선택 : ");
		String menuNo = scanner.nextLine();
		System.out.println();
		
		switch(menuNo) {
			case "1" -> insert();
			case "2" -> search();
			case "3" -> clear();
			case "4" -> read();
			case "5" -> exit();
		}
	}
	
	
	public void insert() {
		// 학생 정보 (이름 / 학번 / 국어 / 수학 / 영어 성적) 입력
		System.out.println("[학생 정보 입력]");
		System.out.print("이름 : ");
		score.setStuName(scanner.nextLine());
		System.out.print("학번 : ");
		score.setStuNo(scanner.nextLine());
		System.out.print("국어 성적 : ");
		score.setKor(scanner.nextInt());
		System.out.print("수학 성적 : ");
		score.setMat(scanner.nextInt());
		System.out.print("영어 성적 : ");
		score.setEng(scanner.nextInt());
		
		// DB에 합 추가
		score.setSum(score.getKor() + score.getMat() + score.getEng());
		
		// DB에 등급 추가
		if((score.getSum() / 3) >= 90) {
			score.setGrade("A");
		} else if((score.getSum() / 3) >= 80) {
			score.setGrade("B");
		} else if((score.getSum() / 3) >= 70) {
			score.setGrade("C");
		} else if((score.getSum() / 3) >= 60) {
			score.setGrade("D");
		} else {
			score.setGrade("F");
		}
		
		scanner.nextLine();
		/* Scanner.nextInt() 메소드가 사용자가 입력한 enter(개행문자) 를 제거하지 않음.
		 * 남겨진 개행문자가 다음 scan.nextLine()의 입력으로 처리되어 곧바로 개행됨
		 * 이를 방지하기 위해서 scanner.nextLine()을 작성하여 개행문자 제거 */
		
		try {
			// 매개변수화된 SQL문 작성
			String sql = "" + "INSERT INTO students (stuName, stuNo, kor, mat, eng, sum, grade) "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
			
			// PreparedStatement 얻기 및 값 지정
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, score.getStuName());
			pstmt.setString(2, score.getStuNo());
			pstmt.setInt(3, score.getKor());
			pstmt.setInt(4, score.getMat());
			pstmt.setInt(5, score.getEng());
			pstmt.setInt(6, score.getSum());
			pstmt.setString(7, score.getGrade());
				
			// SQL문 실행
			pstmt.executeUpdate();
				
			// PreparedStatement 닫기
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	
	
	public void search() {
		System.out.println("[조회할 학생의 학번을 입력하세요.]");
		System.out.print("학번 : ");
		String stuNo = scanner.nextLine();
		
		/* 석차를 추가하고 싶었는데 석차가 1로만 나와서 추후에 해결 필요 */
		
		try {
			String sql = "" + "SELECT stuName, stuNo, kor, mat, eng, sum, sum/3, grade "
					+ "FROM students "
					+ "WHERE stuNo=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, stuNo);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				score.setStuName(rs.getString("stuName"));
				score.setStuNo(rs.getString("stuNo"));
				score.setKor(rs.getInt("kor"));
				score.setMat(rs.getInt("mat"));
				score.setEng(rs.getInt("eng"));
				score.setSum(rs.getInt("sum"));
				score.setGrade(rs.getString("grade"));
				
				System.out.println();
				System.out.println(stuNo + " 학생을 조회했습니다.");
				System.out.println("이름 : " + score.getStuName());
				System.out.println("학번 : " + score.getStuNo());
				System.out.println("국어 : " + score.getKor());
				System.out.println("수학 : " + score.getMat());
				System.out.println("영어 : " + score.getEng());
				System.out.println("합 : " + score.getSum());
				System.out.println("평균 : " + score.getSum() / 3);
				System.out.println("등급 : " + score.getGrade());
				System.out.println();

				// 보조 메뉴 출력
				System.out.println("--------------------------------------------");
				System.out.println("1.수정 | 2.삭제 | 3.메뉴로 돌아가기");
				System.out.print("메뉴 선택 : ");
				String menuNo = scanner.nextLine();
				System.out.println();
					
				if(menuNo.equals("1")) {
					update(score, stuNo);	
				} else if(menuNo.equals("2")) {
					delete(score);
				} else if(menuNo.equals("3")) {
					mainMenu();
				} else {
					System.out.println("1-3 중에서 입력해주세요.");
				}
			} else {
				System.out.println("존재하지 않습니다.");
			}
			rs.close();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	
	
	public void update(Score score, String stuNo) {
		System.out.println("[수정]");
		System.out.print("이름 : ");
		score.setStuName(scanner.nextLine());
		System.out.print("학번 : ");
		score.setStuNo(scanner.nextLine());
		System.out.print("국어 : ");
		score.setKor(scanner.nextInt());
		System.out.print("수학 : ");
		score.setMat(scanner.nextInt());
		System.out.print("영어 : ");
		score.setEng(scanner.nextInt());
		
		// 합
		score.setSum(score.getKor() + score.getMat() + score.getEng());
		
		// 등급 산출
		if((score.getSum() / 3) >= 90) {
			score.setGrade("A");
		} else if((score.getSum() / 3) >= 80) {
			score.setGrade("B");
		} else if((score.getSum() / 3) >= 70) {
			score.setGrade("C");
		} else if((score.getSum() / 3) >= 60) {
			score.setGrade("D");
		} else {
			score.setGrade("F");
		}
		
		scanner.nextLine();
		/* Scanner.nextInt() 메소드가 사용자가 입력한 enter(개행문자) 를 제거하지 않음.
		 * 남겨진 개행문자가 다음 scan.nextLine()의 입력으로 처리되어 곧바로 개행됨
		 * 이를 방지하기 위해서 scanner.nextLine()을 작성하여 개행문자 제거 */
		
		try {
			String sql = "" + "UPDATE students SET stuName=?, stuNo=?, kor=?, mat=?, eng=?, sum=?, grade=? "
					+ "WHERE stuNo=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, score.getStuName());
			pstmt.setString(2, score.getStuNo());
			pstmt.setInt(3, score.getKor());
			pstmt.setInt(4, score.getMat());
			pstmt.setInt(5, score.getEng());
			pstmt.setInt(6, score.getSum());
			pstmt.setString(7, score.getGrade());
			pstmt.setString(8, stuNo);
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	
	
	public void delete(Score score) {
		try {
			String sql = "DELETE FROM students WHERE stuNo=?";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, score.getStuNo());
			
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	
	
	public void clear() {
		System.out.println("입력된 모든 데이터를 삭제하시겠습니까?");
		System.out.println("--------------------------------------------");
		System.out.println("1.Ok | 2.Cancel");
		System.out.print("메뉴 선택 : ");
		String menuNo=scanner.nextLine();
		
		if(menuNo.equals("1")) {
			// 전체 삭제
			try {
				String sql = "TRUNCATE TABLE students";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
				pstmt.close();
			} catch(Exception e) {
				e.printStackTrace();
				exit();
			}
		}
		// 메인 메뉴 메소드로 이동
		mainMenu();
	}
	
	
	public void read() {
		System.out.println();
		System.out.println("1.이름순 | 2.성적순 | 3.학번순 | 4.취소");
		System.out.print("메뉴 선택 : ");
		String menuNo = scanner.nextLine();
		
		switch(menuNo) {
			case "1" -> read_name();
			case "2" -> read_score();
			case "3" -> read_No();
			case "4" -> mainMenu();
		}
	}
	
	
	public void read_name() {
		// 이름순
		
		System.out.println();
		System.out.println("[이름순 조회]");
		System.out.println(" 이름 | 학번 | 국어 | 수학 | 영어 | 합 | 평균 | 등급 | 석차");
		System.out.println("-------------------------------------------------");
		
		try {
			String sql = "" + "SELECT stuName, stuNo, kor, mat, eng, sum, (sum/3), grade, "
					+ "ROW_NUMBER() OVER (ORDER BY sum DESC) AS RANK "
					+ "FROM students "
					+ "ORDER BY stuName";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				score.setStuName(rs.getString("stuName"));
				score.setStuNo(rs.getString("stuNo"));
				score.setKor(rs.getInt("kor"));
				score.setMat(rs.getInt("mat"));
				score.setEng(rs.getInt("eng"));
				score.setSum(rs.getInt("sum"));
				score.setGrade(rs.getString("grade"));
				score.setRank(rs.getInt("RANK"));
				System.out.printf("%-4s %-4s  %-3d  %-3d  %-3d  %-3d  %-3.2f  %s    %d \n", score.getStuName(), score.getStuNo(), score.getKor(), score.getMat(), score.getEng(), score.getSum(), (double)(score.getSum()/3), score.getGrade(), score.getRank());
			}
			rs.close();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	
	
	public void read_score() {
		// 성적순
		
		System.out.println();
		System.out.println("[성적순 조회]");
		System.out.println(" 이름 | 학번 | 국어 | 수학 | 영어 | 합 | 평균 | 등급 | 석차");
		System.out.println("-------------------------------------------------");
		
		try {
			String sql = "" + "SELECT stuName, stuNo, kor, mat, eng, sum, (sum/3), grade, "
					+ "ROW_NUMBER() OVER (ORDER BY sum DESC) AS RANK "
					+ "FROM students";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				score.setStuName(rs.getString("stuName"));
				score.setStuNo(rs.getString("stuNo"));
				score.setKor(rs.getInt("kor"));
				score.setMat(rs.getInt("mat"));
				score.setEng(rs.getInt("eng"));
				score.setSum(rs.getInt("sum"));
				score.setGrade(rs.getString("grade"));
				score.setRank(rs.getInt("RANK"));
				System.out.printf("%-4s %-4s  %-3d  %-3d  %-3d  %-3d  %-3.2f  %s    %d \n", score.getStuName(), score.getStuNo(), score.getKor(), score.getMat(), score.getEng(), score.getSum(), (double)(score.getSum()/3), score.getGrade(), score.getRank());
				//System.out.println(score.getStuName() + " " + score.getStuNo() + " " + score.getKor() + " " + score.getMat() + " " + score.getEng() 
				//+ " " + score.getSum() + " " + (score.getSum()/3) + " " + score.getGrade() + " " + score.getRank());
			}
			rs.close();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	
	
	public void read_No() {
		// 학번순
		
		System.out.println();
		System.out.println("[성적순 조회]");
		System.out.println(" 이름 | 학번 | 국어 | 수학 | 영어 | 합 | 평균 | 등급 | 석차");
		System.out.println("-------------------------------------------------");
		
		try {
			String sql = "" + "SELECT stuName, stuNo, kor, mat, eng, sum, (sum/3), grade, "
					+ "ROW_NUMBER() OVER (ORDER BY sum DESC) AS RANK "
					+ "FROM students "
					+ "ORDER BY stuNo";
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				score.setStuName(rs.getString("stuName"));
				score.setStuNo(rs.getString("stuNo"));
				score.setKor(rs.getInt("kor"));
				score.setMat(rs.getInt("mat"));
				score.setEng(rs.getInt("eng"));
				score.setSum(rs.getInt("sum"));
				score.setGrade(rs.getString("grade"));
				score.setRank(rs.getInt("RANK"));
				System.out.printf("%-4s %-4s  %-3d  %-3d  %-3d  %-3d  %-3.2f  %s    %d \n", score.getStuName(), score.getStuNo(), score.getKor(), score.getMat(), score.getEng(), score.getSum(), (double)(score.getSum()/3), score.getGrade(), score.getRank());
			}
			rs.close();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
			exit();
		}
		mainMenu();
	}
	
	
	public void exit() {
		if(conn != null) {
			try {
				conn.close();
			} catch(SQLException e) {}
		}
		System.out.println("종료합니다.");
		System.exit(0);
	}
}
