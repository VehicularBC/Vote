# -*- coding:utf-8 -*-  
import sys
from os import times
import ZKRP

import tkinter as tk
import tkinter.font as font
from tkinter import filedialog

import public
from ecpy.curves import Curve, Point
import time

def verify():

    # open file
    file_path = "ZKRP.txt"
    zkrp = open(file_path, 'r')
    zkrp_content = zkrp.read()
    # print(zkrp_content)

    zkrp.close()

    zkrp_list = zkrp_content.split('\n')
    # print(zkrp_list)

    cv = public.PP_curve
    for i in range(len(zkrp_list)):
        temp = zkrp_list[i].split(' = ')
        if(temp[0] == 'a'):
            # print(temp)
            ZKRP.a = int(temp[1])
        elif(temp[0] == 'b'):
            # print(temp)
            ZKRP.b = int(temp[1])
        elif(temp[0] == 'C'):
            # print(temp)
            val = eval(temp[1])
            C = Point(val[0], val[1], cv)
        elif(temp[0] == 'C\''):
            # print(temp)
            val = eval(temp[1])
            C_prime = Point(val[0], val[1], cv)
        elif(temp[0] == 'C\'\''):
            # print(temp)
            val = eval(temp[1])
            C_prime_prime = Point(val[0], val[1], cv)
        elif(temp[0] == 'C1\''):
            # print(temp)
            val = eval(temp[1])
            C1_prime = Point(val[0], val[1], cv)
        elif(temp[0] == 'C2\''):
            # print(temp)
            val = eval(temp[1])
            C2_prime = Point(val[0], val[1], cv)
        elif(temp[0] == 'R'):
            # print(temp)
            R = int(temp[1])
        elif(temp[0] == 'EL'):
            # print(temp)
            EL = eval(temp[1])
        elif(temp[0] == 'SQR1'):
            # print(temp)
            val = eval(temp[1])
            X = Point(val[4][0], val[4][1], cv)
            SQR1 = (val[0], val[1], val[2], val[3], X)
        elif(temp[0] == 'SQR2'):
            # print(temp)
            val = eval(temp[1])
            X = Point(val[4][0], val[4][1], cv)
            SQR2 = (val[0], val[1], val[2], val[3], X)
        elif(temp[0] == ''):
            continue
        else:
            result_label.configure(text="格式不符！", fg='red')
            return

    result = ZKRP.ZKRP_verify(C, C_prime, C_prime_prime, C1_prime, C2_prime, R, EL, SQR1, SQR2)
    # print(C)
    # print(C_prime)
    # print(C_prime_prime)
    # print(C1_prime)
    # print(C2_prime)
    # print(R)
    # print(EL)
    # print(SQR1)
    # print(SQR2)

    if(result == True):
        result_text = "successfully verifyed" + " " +"secret interval [{}, {}]".format(str(ZKRP.a), str(ZKRP.b))
    else:
        result_text = "failed verifyed" + " " +"secret interval [{}, {}]".format(str(ZKRP.a), str(ZKRP.b))
    # result_label.configure(text=result_text, fg='black')
    # print(result_text)
    return int(ZKRP.a)

def judgement(CAcar_reputation): #判断函数：根据历史+链码计算+记录在区块链中 / +区域的分级 当前时间相关（？） information age/life 
    val1 = CAcar_reputation #自身声誉 X 
    val2 = verify()
    if val1 <= val2:
        return True
    else:
        return False

def main(carep):
    t1 = time.time()
    print(judgement(carep))
    t2 = time.time()
    # print("the verifying time cost is :",format((t2 - t1), '.2f'),"s")

if __name__ == '__main__':
    # print(judgement(carep))
    for i in range(1, len(sys.argv)):  
        rep = int(sys.argv[i])  
    # rep = 60
    main(rep)