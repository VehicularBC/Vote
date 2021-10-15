# EC-ZKRP

_A zero-knowledge range proof scheme based on ellipic curve_

By using the ZKRP, the prover can convince the verifier that a secret exactly **between a specified range without reavling the secret**.

## Python Package
- [ecpy](https://pypi.org/project/ECPy/) - elliptic curve 
```pip install ECPy```
- [pycryptodome](https://pycryptodome.readthedocs.io/en/latest/src/api.html) - hash function and random number
```pip install pycryptodome```

## How to produce a ZKRP proof
1. Download [**Python 3**](https://www.python.org/downloads/)
![Python](https://www.python.org/static/img/python-logo@2x.png)
2. Install **Python packages** mentioned above
3. Execute **"ZKRP_produce.py"** to produce a ZKRP proof
4. The proof file is **"ZKRP.txt"**

## How to verify a ZKRP proof
1. Execute **"ZKRP_verify.py"**
2. Choose the proof file **"ZKRP.txt"**
