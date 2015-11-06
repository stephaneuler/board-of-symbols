// Client.cpp : Definiert den Einstiegspunkt für die Konsolenanwendung.
//



#include "stdafx.h"

// Need to link with Ws2_32.lib, Mswsock.lib, and Advapi32.lib
#pragma comment (lib, "Ws2_32.lib")
#pragma comment (lib, "Mswsock.lib")
#pragma comment (lib, "AdvApi32.lib")

#define DEFAULT_BUFLEN 512
#define DEFAULT_PORT "1958"

static int verbose = 0;
static char answer[500];

char *getAnswer( void ) {
	return answer;
}

int terminate( char* text ) {
	if( verbose ) printf(text);
	WSACleanup();
	system( "pause" );
	return 1;
}


int sendMessageI2(int i, int j ) {
	char buff[100];
	sprintf_s( buff, "%d %d \n", i, j );

	return sendMessage( buff );
}

int sendMessage(char *sendbuf ) {
	WSADATA wsaData;
	SOCKET ConnectSocket = INVALID_SOCKET;
	struct addrinfo *result = NULL,
		*ptr = NULL,
		hints;
	int iResult;
	int recvbuflen = DEFAULT_BUFLEN;
	char recvbuf[DEFAULT_BUFLEN];

	// Initialize Winsock
	iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
	if (iResult != 0) {
		printf("WSAStartup failed with error: %d\n", iResult);
		return 1;
	}

	ZeroMemory( &hints, sizeof(hints) );
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_protocol = IPPROTO_TCP;

	// Resolve the server address and port
	iResult = getaddrinfo("localhost", DEFAULT_PORT, &hints, &result);
	if ( iResult != 0 ) {
		printf("getaddrinfo failed with error: %d\n", iResult);
		WSACleanup();
		return 1;
	}

	// Attempt to connect to an address until one succeeds
	for(ptr=result; ptr != NULL ;ptr=ptr->ai_next) {

		// Create a SOCKET for connecting to server
		ConnectSocket = socket(ptr->ai_family, ptr->ai_socktype, 
			ptr->ai_protocol);
		if (ConnectSocket == INVALID_SOCKET) {
			printf("socket failed with error: %ld\n", WSAGetLastError());
			WSACleanup();
			return 1;
		}

		// Connect to server.
		iResult = connect( ConnectSocket, ptr->ai_addr, (int)ptr->ai_addrlen);
		if (iResult == SOCKET_ERROR) {
			closesocket(ConnectSocket);
			ConnectSocket = INVALID_SOCKET;
			continue;
		}
		if( verbose ) printf("socket connected\n");
		break;
	}

	freeaddrinfo(result);

	if (ConnectSocket == INVALID_SOCKET) {
		terminate("Unable to connect to server!\n");
	}

	// Send an initial buffer
	iResult = send( ConnectSocket, sendbuf, (int)strlen(sendbuf), 0 );
	if (iResult == SOCKET_ERROR) {
		printf("send failed with error: %d\n", WSAGetLastError());
		closesocket(ConnectSocket);
		WSACleanup();
		return 1;
	}

	if( verbose ) printf("Bytes Sent: %ld\n", iResult);

	// shutdown the connection since no more data will be sent
	iResult = shutdown(ConnectSocket, SD_SEND);
	if (iResult == SOCKET_ERROR) {
		printf("shutdown failed with error: %d\n", WSAGetLastError());
		closesocket(ConnectSocket);
		WSACleanup();
		return 1;
	}

	answer[0] = '\0';
	// Receive until the peer closes the connection
	do {


		iResult = recv(ConnectSocket, recvbuf, recvbuflen, 0);
		if ( iResult > 0 ) {
			recvbuf[iResult] = '\0';
			if( verbose ) printf("Bytes received: %d %s\n", iResult, recvbuf);
			strcat_s( answer, recvbuf );
			//printf(">>>%s<<<\n", answer );
		} else if ( iResult == 0 ) {
			if( verbose ) printf("Connection closed\n");
		} else
			printf("recv failed with error: %d\n", WSAGetLastError());

	} while( iResult > 0 );

	// cleanup
	closesocket(ConnectSocket);
	WSACleanup();

	return 0;
}
