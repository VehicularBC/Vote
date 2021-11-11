package org.example;     //将Token存为Json文件

import com.alibaba.fastjson.annotation.JSONField;

public class JsonToken {



    private String userID;  // 用户ID
    @JSONField(name="userID")
    public String getUserID() { return userID; }

    private int reputation;  // 用户信誉值
    @JSONField(name="reputation")
    public int getReputation() { return reputation; }

    private long requestTime;  //  白板车请求时间
    @JSONField(name="repTime")
    public long getRequestTime() { return requestTime; }
    public void setRequestTime(long repTime) { this.requestTime = requestTime; }

    public JsonToken( String userID, int reputation, long requestTime) {   //sheng cheng han shu
        super();
      //  this.type = type;
        this.requestTime = requestTime;
        this.userID = userID;
        this.reputation = reputation;
    }


}
