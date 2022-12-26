package ioTest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationTest {

	public static void main(String[] args) {
		FileOutputStream fs = null;
		ObjectOutputStream op = null;
		Student[] students = new Student[3];
		try {
			fs = new FileOutputStream("E:\\IOTest\\objectSeri.dat");
			op = new ObjectOutputStream(fs);
			students[0] = new Student(1,"张三",22);
			students[1] = new Student(2,"王五",20);
			students[2] = new Student(3,"李四",19);
			op.writeObject(students);
			op.flush();
			System.out.println("成功！");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(op != null) {
				try {
					op.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

}

class Student implements Serializable{
	private int id;
	private String name;
	private transient int age;
	public Student() {}
	public Student(int id,String name,int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}