{
  "name": "test-network-org1",
  "version": "1.0.0",
  "client": {
    "organization": "Org1",
    "connection": {
      "timeout": {
        "peer": {
          "endorser": "300"
        },
        "orderer": "300"
      }
    }
  },
  "channels": {
    "mychannel": {
      "orderers": [
        "orderer.example.com"
      ],
      "peers": {
        "peer0.org1.example.com": {
          "endorsingPeer": true,
          "chaincodeQuery": true,
          "ledgerQuery": true,
          "eventSource": true
        },
        "peer0.org2.example.com": {
          "endorsingPeer": true,
          "chaincodeQuery": true,
          "ledgerQuery": true,
          "eventSource": true
        }
      }
    }
  },
  "organizations": {
    "Org1": {
      "mspid": "Org1MSP",
      "peers": [
        "peer0.org1.example.com"
      ],
      "certificateAuthorities": [
        "ca.org1.example.com"
      ]
    },
    "Org2": {
      "mspid": "Org2MSP",
      "peers": [
        "peer0.org2.example.com"
      ],
      "certificateAuthorities": [
        "ca.org2.example.com"
      ]
    }
  },
  "orderers": {
    "orderer.example.com": {
      "url": "grpcs://192.168.2.73:7050",
      "mspid": "OrdererMSP",
      "grpcOptions": {
        "ssl-target-name-override": "orderer.example.com",
        "hostnameOverride": "orderer.example.com"
      },
      "tlsCACerts": {
        "path": "src/main/resources/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/ca.crt"
      },
      "adminPrivateKeyPEM": {
        "path": "src/main/resources/crypto-config/ordererOrganizations/example.com/users/Admin@example.com/msp/keystore/b213148c37bf7453bb82fdeddf2e08028038cd74fa29c74b6e36231bd106e2b3_sk "
      },
      "signedCertPEM": {
        "path": "src/main/resources/crypto-config/ordererOrganizations/example.com/users/Admin@example.com/msp/signcerts/cert.pem"
      }
    }
  },
  "peers": {
    "peer0.org1.example.com": {
      "url": "grpcs://192.168.2.73:7051",
      "grpcOptions": {
        "ssl-target-name-override": "peer0.org1.example.com",
        "hostnameOverride": "peer0.org1.example.com",
        "request-timeout": 120001
      },
      "tlsCACerts": {
        "path": "src/main/resources/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt"
      }
    },
    "peer0.org2.example.com": {
      "url": "grpcs://192.168.2.73:9051",
      "grpcOptions": {
        "ssl-target-name-override": "peer0.org2.example.com",
        "hostnameOverride": "peer0.org2.example.com",
        "request-timeout": 120001
      },
      "tlsCACerts": {
        "path": "src/main/resources/crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt"
      }
    }
  },
  "certificateAuthorities": {
    "ca.org1.example.com": {
      "url": "https://192.168.2.73:7054",
      "httpOptions": {
        "verify": true
      },
      "tlsCACerts": {
        "path": "src/main/resources/crypto-config/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem"
      },
      "registrar": [
        {
          "enrollId": "admin",
          "enrollSecret": "adminpw"
        }
      ]
    },
    "ca.org2.example.com": {
      "url": "https://192.168.2.73:7054",
      "httpOptions": {
        "verify": true
      },
      "tlsCACerts": {
        "path": "src/main/resources/crypto-config/peerOrganizations/org2.example.com/ca/ca.org2.example.com-cert.pem"
      },
      "registrar": [
        {
          "enrollId": "admin",
          "enrollSecret": "adminpw"
        }
      ]
    }
  }
}
