import socket
import sys

# minimal demo: connect to BoS-Server and send one command

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_address = ('localhost', 1958)
sock.connect(server_address)

# color symbol 2 blue
command = '3 255\n'

sock.sendall( bytes( command, 'UTF-8' ) )
sock.close()


