package com.zz.pojo;

import lombok.Data;

@Data
public class SruCourse {
    private int cid;
    private String cname;
    private int tid;
    private String stuclass;

    public SruCourse(int cid, String cname, int tid, String stuclass) {
        this.cid = cid;
        this.cname = cname;
        this.tid = tid;
        this.stuclass = stuclass;
    }
}
