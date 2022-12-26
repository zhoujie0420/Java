package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadTest {

	public static void main(String[] args) {
//		方法一		
		int sum = 0;
//		try {
//			int t = System.in.read();
//			while(t != 13) {
//				sum = sum*10 + t - 48;
//				t = System.in.read();
//			}
//			System.out.println(1.8*sum+32);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
////方法二
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String str = br.readLine();
			System.out.println(1.8*Integer.parseInt(str)+32);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
