package professor;

import java.util.Scanner;
import professor.ProMain;

public class ProLogin {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		String id = "asdf"; // 아이디는 확인을 위해 하드코딩으로 함
		String pw = "1111"; // 비밀번호는 확인을 위해 하드코딩으로 함 
		
		/* 아이디와 비밀번호는 추후에 DB와 연동하여 회원가입도 만들 예정 */
		
		int chance = 0;
		
		while (chance < 3) { // 3번 틀리면 프로그램 종료
			System.out.print("ID : ");
			String scannerId = scanner.nextLine(); // 아이디 입력
			
			System.out.print("PW : ");
			String scannerPw = scanner.nextLine(); // 비밀번호 입력
			
			if(scannerId.equals(id)) { // 아이디가 일치하는지 확인
				if(scannerPw.equals(pw)) { // 비밀번호가 일치하는지 확인
					System.out.println("로그인 성공");
					ProMain proMain = new ProMain(); // 아이디와 비밀번호가 일치하면 로그인 성공
					break;
				}
			}
			else {
				System.out.println("일치하지 않습니다."); // 로그인 실패 메시지 출력
			}
			
			chance ++;
		}
	}
}