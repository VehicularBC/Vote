使用不同端口模拟两个RSU以及一辆待认证车辆

车辆使用端口：10999

RSU1（前一区域RSU）使用端口：8888

RSU2（下一区域RSU）使用端口：9999

调用RSU1：RSUPlanc.main(8888);

调用RSU2：RSUPlanc.main(9999);

调用车辆：ClientPlanC.main(null);