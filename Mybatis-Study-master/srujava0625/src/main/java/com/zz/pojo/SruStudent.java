package com.zz.pojo;


import lombok.Data;

@Data

public class SruStudent {
    private int sid;
    private String password;
    private String name;
    private String sex;
    private String dataofbirth;
    private String college;
    private String major;
    private String stuclass;

    public SruStudent(int sid, String password, String name, String sex, String dataofbirth, String college, String major, String stuclass) {
        this.sid = sid;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.dataofbirth = dataofbirth;
        this.college = college;
        this.major = major;
        this.stuclass = stuclass;
    }

    @Override
    public String toString() {
        return "SruStudent{" +
                "sid=" + sid +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", dataofbirth='" + dataofbirth + '\'' +
                ", college='" + college + '\'' +
                ", major='" + major + '\'' +
                ", stuclass='" + stuclass + '\'' +
                '}';
    }
}
