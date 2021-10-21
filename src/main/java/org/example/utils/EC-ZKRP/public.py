from ecpy.curves import Curve, Point
from Crypto.Util import number
from Crypto.Hash import SHA224

# Setup
PP_curve = Curve.get_curve('secp224k1')
G = PP_curve.generator
order = PP_curve.order
#s = number.getRandomRange(1,order)
s = 8182000592777353751903852887622504240075063861943136840340484739900
H = s * G
# print(order)
def commit(msg):
    r = number.getRandomRange(1,order)
    Commit = msg * G + r * H
    return Commit, r

def com_verify(c, msg, r):
    return c == msg * G + r * H

def EL_proof(msg, s, r, G1, H1, G2, H2):
    # random pick u, v1, v2
    u = number.getRandomRange(1,order)
    v1 = number.getRandomRange(1,order)
    v2 = number.getRandomRange(1,order)
    # compute C1, C2
    C1 = u * G1 + v1 * H1
    C2 = u * G2 + v2 * H2
    # H = Hash(C1 || C2)
    str_C1 = str(C1)
    str_C2 = str(C2)
    C = str_C1 + str_C2
    hash_object = SHA224.new(C.encode('utf-8'))
    Hash = int(hash_object.hexdigest(),16)
    # compute X, X1, X2
    X = u + Hash * msg
    X1 = v1 + Hash * s
    X2 = v2 + Hash * r
    return Hash, X, X1, X2

def v_EL_proof(Hash, X, X1, X2, G1, H1, G2, H2, A, B):
    # vC1 would be equal to C1. So would vC2.
    vC1 = X * G1 + X1 * H1 + (-Hash) * A
    vC2 = X * G2 + X2 * H2 + (-Hash) * B
    str1 = str(vC1)
    str2 = str(vC2)
    vC = str1 + str2
    hash_object = SHA224.new(vC.encode('utf-8'))
    vHash = int(hash_object.hexdigest(),16)
    return Hash == vHash

def SQR_proof(alpha, r1, G, H):
    # random pick r2
    r2 = number.getRandomRange(1,order)
    # compute F
    F = alpha * G + r2 * H
    # compute r3
    r3 = r1 - r2 * alpha
    # compute E' (E' = E in this case)
    E_prime = alpha * F + r3 * H
    Hash,X,X1,X2 = EL_proof(alpha, r2, r3, G, H, F, H)
    return (Hash, X, X1, X2, F)

def v_SQR_proof(Hash, X, X1, X2, F, G, H, E):
    return v_EL_proof(Hash, X, X1, X2, G, H, F, H, F, E)

'''
# EL proof example:
A = 1 * G + 2 * H
B = 1 * (10*G) + 3 * (11*H)
Hash,X,X1,X2 = EL_proof(1 ,2 ,3 ,G , H, 10*G, 11*H)
print(v_EL_proof(Hash, X, X1, X2, G, H, 10*G, 11*H, A, B))
'''

'''
# SQR proof example:
alpha = 2
E = 4 * G + 2 * H
Hash, X, X1, X2, F = SQR_proof(alpha, 2, G, H)
print(v_SQR_proof(Hash, X, X1, X2, F, G, H, E))
'''
