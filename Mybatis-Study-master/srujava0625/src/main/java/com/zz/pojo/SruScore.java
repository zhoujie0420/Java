package com.zz.pojo;
import lombok.Data;

@Data
public class SruScore {
    private int sid;
    private int cid;
    private float score;

    public SruScore(int sid, int cid, float score) {
        this.sid = sid;
        this.cid = cid;
        this.score = score;
    }
}
