package com.zz.pojo;

import lombok.Data;

@Data
public class SruTeacher {
    private int tid;
    private String password;
    private String name;
    private String sex;
    private String dataofbirth;
    private String college;
    private String course;
    private String email;

    public SruTeacher(int tid,String password, String name, String sex, String dataofbirth, String college, String course, String email) {
        this.tid = tid;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.dataofbirth = dataofbirth;
        this.college = college;
        this.course = course;
        this.email = email;
    }
}
