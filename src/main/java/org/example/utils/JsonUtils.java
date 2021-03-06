package org.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class JsonUtils {
    /* 通信逻辑
        1 广播入网
        2 认证车回复UID
        3 白板车汇聚UID再次请求
        4 认证车回复wallet
     */
    private String type;  // 报文类型
    @JSONField(name="Type")
    public String getType() { return type; }

    private int errCode;  // 错误代码
    @JSONField(name="errCode")
    public int getErrCode() { return errCode; }
    @JSONField(name="errCode")
    public void setErrCode(int _code) { this.errCode = _code; }

    private String userID;  // 用户ID
    @JSONField(name="userID")
    public String getUserID() { return userID; }

    // 留白
    private long repTime;  // 白板车得到上一区域声誉值的时间戳(information/life age)

    private String commit;  // 车辆入网携带信誉值加密信息
    @JSONField(name="commit")
    public String getCommit() { return commit; }

    private String[] uList;  // 车辆再次申请入网发送UID集合
    @JSONField(name="uList")
    public String[] getUList() { return uList; }
    
    private String walletContent;  // 车辆认证成功后.id文件内容
    @JSONField(name="wC")
    public String getWID() { return walletContent; }

    private long curTime;  // 当前消息时间戳
    @JSONField(name="curTime")
    public long getCurTime() { return curTime; }


    public JsonUtils(String type, int errCode, String userID, String commit, String[] uList, String walletContent) {
        super();
        this.curTime = System.currentTimeMillis();

        this.type = type;
        this.errCode = errCode;
        this.userID = userID;
        this.commit = commit;
        this.uList = uList;
        this.walletContent = walletContent;
    }
}