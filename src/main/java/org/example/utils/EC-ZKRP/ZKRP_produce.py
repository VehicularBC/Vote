# -*- coding:utf-8 -*-  
import ZKRP
import tkinter as tk
import tkinter.font as font
import time

def produce(secret_value,upper,bounder):

    val1 = secret_value
    val2 = upper
    val3 = bounder

    m = int(val1)
    ZKRP.b = int(val2)
    ZKRP.a = int(val3)

    '''
    if( (m-ZKRP.a+1) * (ZKRP.b-m+1) <= 0 or ZKRP.b < ZKRP.a):
        result_label.configure(text="請輸入正確內容！",fg='red')
        return
    '''

    C, C_prime, C_prime_prime, C1_prime, C2_prime, R, EL, SQR1, SQR2 = ZKRP.produce_ZKRP(m)

    SQR1_Hash, SQR1_X, SQR1_X1, SQR1_X2, SQR1_F = SQR1
    SQR1_text = '({}, {}, {}, {}, {})'.format(SQR1_Hash, SQR1_X, SQR1_X1, SQR1_X2, SQR1_F)

    SQR2_Hash, SQR2_X, SQR2_X1, SQR2_X2, SQR2_F = SQR2
    SQR2_text = '({}, {}, {}, {}, {})'.format(SQR2_Hash, SQR2_X, SQR2_X1, SQR2_X2, SQR2_F)

    content = '{} = {}\n'.format('a', str(ZKRP.a)) \
        + '{} = {}\n'.format('b', str(ZKRP.b)) \
        + '{} = {}\n'.format('C', str(C)) \
        + '{} = {}\n'.format('C\'', str(C_prime)) \
        + '{} = {}\n'.format('C\'\'', str(C_prime_prime)) \
        + '{} = {}\n'.format('C1\'', str(C1_prime)) \
        + '{} = {}\n'.format('C2\'', str(C2_prime)) \
        + '{} = {}\n'.format('R', str(R)) \
        + '{} = {}\n'.format('EL', str(EL)) \
        + '{} = {}\n'.format('SQR1', SQR1_text) \
        + '{} = {}\n'.format('SQR2', SQR2_text)
    # with open('./ZKRP.txt', 'w') as f:
    #     f.write(content)

    result_text = "successfully create commitment\n" + "secret interval：[{}, {}]\n".format(str(ZKRP.a), str(ZKRP.b))
    # result_label.configure(text=result_text, fg='black')
    print(result_text)
    return content

t1 = time.time()
Content = produce(73.54, 80, 60)
t2 = time.time()
print("the production time cost is :",t2 - t1)
print("the content is:" + Content)
