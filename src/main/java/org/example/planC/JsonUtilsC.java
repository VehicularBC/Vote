package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class JsonUtilsC {
    /* 通信逻辑
        0 RSU间通信
        1 切换告知
        2 返回token
        3 身份索取
        4 返回身份
     */
    private String type;  // 报文类型
    @JSONField(name="Type")
    public String getType() { return type; }
    @JSONField(name="Type")
    public void setType(String _type) { this.type = _type; }

    private int errCode;  // 错误代码
    @JSONField(name="errCode")
    public int getErrCode() { return errCode; }

    private String userID;  // 用户ID
    @JSONField(name="userID")
    public String getUserID() { return userID; }

//    private int hashtoken;
//    @JSONField(name="hashtoken")
//    public int getHashtoken() { return hashtoken; }

    private int hash;
    @JSONField(name="hash")
    public int getHash() { return hash; }

    public void setHash(int _hash) { this.hash = _hash; }

    // 留白
    private long repTime;  // 白板车得到上一区域声誉值的时间戳(information/life age)

    private String commit;  // 车辆入网携带信誉值加密信息
    @JSONField(name="commit")
    public String getCommit() { return commit; }


    private String token;  // 车辆认证成功后.id文件内容
    @JSONField(name="token")
    public String getToken() { return token; }
    @JSONField(name="token")
    public void setToken(String _token) { this.token = _token; }

    private String walletContent;  // 车辆认证成功后.id文件内容
    @JSONField(name="wc")
    public String getWC() { return walletContent; }
    @JSONField(name="wc")
    public void setWC(String _wc) { this.walletContent = _wc; }

    private long curTime;  // 当前消息时间戳
    @JSONField(name="curTime")
    public long getCurTime() { return curTime; }

    private long requestTime;  // 当前消息时间戳
    @JSONField(name="requestTime")
    public long getRequestTime() { return curTime; }
    public void setRequestTime(long _requestTime) { this.requestTime = _requestTime; }

    private int reputation;  // 用户信誉值
    @JSONField(name="reputation")
    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }

    public JsonUtilsC(String type, int errCode, String curNodeName) {
        super();
        this.curTime = System.currentTimeMillis();

        this.type = type;
        this.errCode = errCode;
        this.userID = curNodeName;
    }
}