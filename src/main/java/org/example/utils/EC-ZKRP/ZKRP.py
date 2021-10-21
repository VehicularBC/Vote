from Crypto.Util import number
import public

a = 0
b = 0

def produce_ZKRP(m):

    global a, b
    G = public.G
    H = public.H
    order = public.order

    #assert (m-a+1)*(b-m+1) > 0

    # 0 
    C, r = public.commit(m)

    # 1
    C1 = C - (a-1) * G
    C2 = (b+1) * G - C
    r_prime = number.getRandomRange(1,order)
    C_prime = (b-m+1) * C1 + r_prime * H

    # 2
    EL = public.EL_proof((b-m+1), -r, r_prime, G ,H ,C1 ,H)

    # 3
    w = number.getRandomRange(1,int(pow(order,1/2)))
    r_prime_prime = number.getRandomRange(1,order)
    C_prime_prime = w**2 * C_prime + r_prime_prime * H

    # 4
    SQR1 = public.SQR_proof(w, r_prime_prime, C_prime, H)

    # 5
    alpha = number.getRandomRange(1,int(pow(order,1/2)))
    M = alpha**2
    temp = ((w**2)*(m-a+1)*(b-m+1))%order
    while M >= temp:
        alpha = number.getRandomRange(1,int(pow(order,1/2)))
        M = alpha**2

    # 6
    R = (w**2)*(m-a+1)*(b-m+1) - M

    # 7
    r1 = number.getRandomRange(1,order)
    r2 = (w**2)*((b-m+1)*r+r_prime)+r_prime_prime-r1

    # 8 # 9
    C1_prime = M * G + r1 * H
    C2_prime = r2 * H

    # 10
    SQR2 = public.SQR_proof(alpha, r1, G, H)

    return C, C_prime, C_prime_prime, C1_prime, C2_prime, R, EL, SQR1, SQR2

def ZKRP_verify(C, C_prime, C_prime_prime, C1_prime, C2_prime, R, EL, SQR1, SQR2):
    global a, b
    G = public.G
    H = public.H

    # 1 # 2
    C1 = C - (a-1) * G
    C2 = (b+1) * G - C

    # 3
    EL_Hash, EL_X, EL_X1, EL_X2 = EL
    vEL = public.v_EL_proof(EL_Hash, EL_X, EL_X1, EL_X2, G, H, C1, H, C2, C_prime)

    # 4
    SQR1_Hash, SQR1_X, SQR1_X1, SQR1_X2, SQR1_F = SQR1
    vSQR1 = public.v_SQR_proof(SQR1_Hash, SQR1_X, SQR1_X1, SQR1_X2, SQR1_F, C_prime, H, C_prime_prime)

    # 5
    vC_prime_prime = C1_prime + C2_prime + R * G

    # 6
    SQR2_Hash, SQR2_X, SQR2_X1, SQR2_X2, SQR2_F = SQR2
    vSQR2 = public.v_SQR_proof(SQR2_Hash, SQR2_X, SQR2_X1, SQR2_X2, SQR2_F, G, H, C1_prime)

    # 7
    vR = (R>0)

    return vEL and vSQR1 and (C_prime_prime == vC_prime_prime) and vSQR2 and vR

'''
Usage example:
C, C_prime, C_prime_prime, C1_prime, C2_prime, R, EL, SQR1, SQR2 = produce_ZKRP(m)
print(ZKRP_verify(C, C_prime, C_prime_prime, C1_prime, C2_prime, R, EL, SQR1, SQR2))
'''


