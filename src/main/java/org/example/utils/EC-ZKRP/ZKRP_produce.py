# -*- coding:utf-8 -*-  
import ZKRP
import sys

def produce(secret_value):

    SV  = int(secret_value)

    val1 = SV
    val2 = SV + 10
    val3 = SV - 10

    m = int(val1)
    ZKRP.b = int(val2)
    ZKRP.a = int(val3)

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
    return content

def main(rep):
    print(produce(rep))


if __name__ == '__main__':
    for i in range(1, len(sys.argv)):  
        rep = int(sys.argv[i])  
    # rep = 60

    main(rep)
