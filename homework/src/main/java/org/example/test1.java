package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class test1 {	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in) );
        String s = br.readLine();
        while (s != null) {
            s = br.readLine();
            int cel = Integer.parseInt(s);
            double fahre = 1.8 * cel + 32;
            System.out.println("华氏度是：" + fahre);
            br.close();
        }

}
    }
