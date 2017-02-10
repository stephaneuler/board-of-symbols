// Client.cpp : Definiert den Einstiegspunkt fuer die Konsolenanwendung.
//




#include "stdafx.h"

#if VISUALSTUDIO
    // Need to link with Ws2_32.lib, Mswsock.lib, and Advapi32.lib
    #pragma comment (lib, "Ws2_32.lib")
    #pragma comment (lib, "Mswsock.lib")
    #pragma comment (lib, "AdvApi32.lib")
#endif

#define DEFAULT_BUFLEN 512
#define DEFAULT_PORT "1958"

static int verbose = 0;
static char answer[500];

char *getAnswer( void ) {
    return answer;
}

int terminate( char* text ) {
    if( verbose ) printf("%s", text);
#if WINDOWS
    WSACleanup();
    system( "pause" );
#endif
    return 1;
}


int sendMessageI2(int i, int j ) {
    char buff[100];
    
    sprintf_s( buff, "%d %d \n", i, j );
    
    return sendMessage( buff );
}

int sendMessage(char *sendbuf ) {
    
    char terminateMessage[100];
    
#if WINDOWS
    WSADATA wsaData;
    SOCKET ConnectSocket = INVALID_SOCKET;
#else
    int ConnectSocket = INVALID_SOCKET;
#endif
    
    struct addrinfo *result = NULL,
    *ptr = NULL,
    hints;
    long int iResult;
    int recvbuflen = DEFAULT_BUFLEN;
    char recvbuf[DEFAULT_BUFLEN];
    
#if WINDOWS
    // Initialize Winsock
    iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
    if (iResult != 0) {
        printf("WSAStartup failed with error: %d\n", iResult);
        return 1;
    }
#endif
    
#if WINDOWS
    ZeroMemory( &hints, sizeof(hints) );
#else
    memset(&hints, 0, sizeof hints); // make sure the struct is empty
#endif
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;
    
    // Resolve the server address and port
    iResult = getaddrinfo("localhost", DEFAULT_PORT, &hints, &result);
    if ( iResult != 0 ) {
        printf("getaddrinfo failed with error: %ld\n", iResult);
#if WINDOWS
        WSACleanup();
#endif
        return 1;
    }
    
    // Attempt to connect to an address until one succeeds
    for(ptr=result; ptr != NULL ;ptr=ptr->ai_next) {
        
        // Create a SOCKET for connecting to server
        ConnectSocket = socket(ptr->ai_family, ptr->ai_socktype,
                               ptr->ai_protocol);
        if (ConnectSocket == INVALID_SOCKET) {
#if WINDOWS
            printf("socket failed with error: %ld\n", WSAGetLastError());
            WSACleanup();
#else
            fprintf(stderr, "socket failed, getaddrinfo error: %s\n", gai_strerror((int)iResult));
#endif
            return 1;
        }
        
        // Connect to server.
        iResult = connect( ConnectSocket, ptr->ai_addr, (int)ptr->ai_addrlen);
        if (iResult == SOCKET_ERROR) {
#if WINDOWS
            closesocket(ConnectSocket);
#else
            close(ConnectSocket);
#endif
            ConnectSocket = INVALID_SOCKET;
            continue;
        }
        if( verbose ) printf("socket connected\n");
        break;
    }
    
    freeaddrinfo(result);
    
    if (ConnectSocket == INVALID_SOCKET) {
        sprintf_s(terminateMessage, "%s", "Unable to connect to server!\n");
        terminate(terminateMessage);
    }
    
    // Send an initial buffer
    iResult = send( ConnectSocket, sendbuf, (int)strlen(sendbuf), 0 );
    if (iResult == SOCKET_ERROR) {
#if WINDOWS
        printf("socket failed with error: %ld\n", WSAGetLastError());
        closesocket(ConnectSocket);
        WSACleanup();
#else
        fprintf(stderr, "socket failed, getaddrinfo error: %s\n", gai_strerror((int)iResult));
        close(ConnectSocket);
#endif
        
        return 1;
    }
    
    if( verbose ) printf("Bytes Sent: %ld\n", iResult);
    
    // shutdown the connection since no more data will be sent
    iResult = shutdown(ConnectSocket, SD_SEND);
    if (iResult == SOCKET_ERROR) {
#if WINDOWS
        printf("shutdown failed with error: %ld\n", WSAGetLastError());
        closesocket(ConnectSocket);
        WSACleanup();
#else
        fprintf(stderr, "shutdown failed, getaddrinfo error: %s\n", gai_strerror((int)iResult));
        close(ConnectSocket);
#endif
        return 1;
    }
    
    answer[0] = '\0';
    // Receive until the peer closes the connection
    do {
        
        
        iResult = recv(ConnectSocket, recvbuf, recvbuflen, 0);
        if ( iResult > 0 ) {
            recvbuf[iResult] = '\0';
            if( verbose ) printf("Bytes received: %ld %s\n", iResult, recvbuf);
            strcat_s( answer, recvbuf );
            //printf(">>>%s<<<\n", answer );
        } else if ( iResult == 0 ) {
            if( verbose ) printf("Connection closed\n");
        } else {
#if WINDOWS
            printf("recv failed with error: %d\n", WSAGetLastError());
#else
            fprintf(stderr, "recv failed, getaddrinfo error: %s\n", gai_strerror((int)iResult));
#endif
        }
        
    } while( iResult > 0 );
    
    // cleanup
#if WINDOWS
    closesocket(ConnectSocket);
    WSACleanup();
#else
    close(ConnectSocket);
#endif
    
    return 0;
}


