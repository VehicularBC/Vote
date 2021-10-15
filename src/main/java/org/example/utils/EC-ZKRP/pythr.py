import socket
import sys
import threading
import numpy as np
import ZKRP_verify as zv

def main():
    serversocket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    host = socket.gethostname()
    port = 12300
    serversocket.bind((host, port))

    serversocket.listen(5)
    myaddr = serversocket.getsockname()
    print("server address:%s" %str(myaddr))

    while True:
        clientsocket,addr = serversocket.accept()
        print("connecting:%s" % str(addr))
        try:
            t = ServerThreading(clientsocket)
            t.start()
            pass
        except Exception as identifier:
            print(identifier)
            pass
        pass
        # serversocket.close()
    pass

class ServerThreading(threading.Thread):
    def __init__(self, clientsocket,rec = 60,encoding="utf-8"):
        threading.Thread.__init__(self)
        self._socket = clientsocket
        self._rec = rec
        self._encoding = encoding
        pass

    def run(self):
        print("Opening Threading......")
        try:
            msg = ''
            while True:
                receive = self._socket.recv(self._rec)
                msg += receive.decode(self._encoding)
                if msg.strip().endswith('over'):
                    mag = msg[:-4]
                    break
            
            tempmsg = int(mag)
            print(tempmsg)
            sendmsg = zv.judgement(tempmsg)

            self._socket.send(("%s"%sendmsg).encode(self._encoding))
            pass
        except Exception as identifier:
            self._socket.send("500".encode(self._encoding))
            print(identifier)
            pass
        finally:
            self._socket.close()
        print("judging completed")

        pass

    def __del__(self):
        pass

if __name__ == "__main__":
    main()
