package com.zz.pojo;

import lombok.Data;


@Data
public class SruAdmin {
    private int mid;
    private String password;
    private String name;
    private String sex;
    private String  dataofbirth;

    public SruAdmin(int mid, String password, String name, String sex, String  dataofbirth) {
        this.mid = mid;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.dataofbirth = dataofbirth;
    }
}
