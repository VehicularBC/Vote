20210706:实现套接字传输信息以及文件传输

    主函数(test)逻辑：\
        1.以白板车辆身份入网，向某节点发送请求(信息包括userName)\
        2.ca车辆进行投票，并根据结果申请合理身份。若不合理（拒绝或者出错）返回"0"，合理返回"1"+".id"文件内容
        3.白板车辆若可入网并成功收到身份，转为ca车辆；否则入网失败

其中客户端代码为blankCar.java，服务器端代码为caCar.java。SocketThread.java文件是线程代码，服务端处理逻辑都位于该文件中

参考链接：<br>
1.套接字：https://blog.csdn.net/kangkangwanwan/article/details/78839822 <br>
2.文件传输：https://zhuanlan.zhihu.com/p/79377902