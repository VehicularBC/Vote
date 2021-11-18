

车辆IP以及端口：192.168.1.75：10999

RSU1（前一区域RSU）使用IP及端口：192.168.1.132：8888

RSU2（下一区域RSU）使用IP及端口：192.168.1.76：9999

调用RSU1：RSUPlanc.main(8888);

调用RSU2：RSUPlanc.main(9999);

调用车辆：ClientPlanC.main(null);
