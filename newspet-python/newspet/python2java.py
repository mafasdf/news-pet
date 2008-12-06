import socket

class Python2Java(object):
    
    def __init__(self, host, port):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.host = host
        self.port = port
    
    def _connect(self):
        self.socket.connect( (self.host, self.port) )
        
    def _disconnect(self):
        self.socket.shutdown(socket.SHUT_RDWR)
        self.socket.close()
        
    def send(self, message):
        self._connect()
        bytes_sent = 0
        while bytes_sent < len(message):
            bytes_sent += self.socket.send(unicode(message[bytes_sent:]))
            print 'Bytes sent this iteration: %d' % bytes_sent
        print bytes_sent
        self._disconnect()
        
    