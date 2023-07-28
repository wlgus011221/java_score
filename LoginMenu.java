/* 로그인 창 */

import professor.ProJoin;
import professor.ProLogin;
import student.StuLogin;
import java.util.Scanner;


public class LoginMenu {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		boolean exit = true;
		while(exit) {
			System.out.println("1.교수 로그인 | 2.학생 로그인 | 3.교수 회원가입 | 4.종료");
			int a = scanner.nextInt();
			switch(a) {
				case 1:
					ProLogin proLogin = new ProLogin();
					exit = false;
					break;
				case 2:
					StuLogin stuLogin = new StuLogin();
					exit = false;
					break;
				case 3:
					ProJoin proJoin = new ProJoin();
					exit = false;
					break;
				case 4:
					System.out.println("프로그램을 종료합니다.");
					exit = false;
					break;
				default:
					System.out.println("1~4 숫자 중에서 입력해주세요.");
					break;
			}
		}
	}
}
