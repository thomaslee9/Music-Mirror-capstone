import socket

def start_server(port=5000):
    # Create a socket object
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Get local machine name
    host = socket.gethostname()

    # Bind to the port
    server_socket.bind((host, port))

    # Queue up to 5 requests
    server_socket.listen(5)

    print(f"Server is listening on port {port}")

    while True:
        # Establish a connection
        client_socket, addr = server_socket.accept()

        print(f"Got a connection from {addr}")
        try:
            message = client_socket.recv(1024).decode('utf-8')
            print(f"Received from client: {message}")

            # You can send a response back to client if needed
            # client_socket.send('Message received'.encode('utf-8'))

        except Exception as e:
            print(f"Exception in client connection: {str(e)}")

        finally:
            client_socket.close()

if __name__ == '__main__':
    start_server()
