// stdafx.h : Includedatei fuer Standardsystem-Includedateien
// oder haeufig verwendete projektspezifische Includedateien,
// die nur in unregelmaessigen Abstaenden geaendert werden.
//

#pragma once



#include <stdio.h>

// Plattform defines
#if defined(_WIN32)
	#define WINDOWS 1
#elif defined(__APPLE__) && defined(__MACH__)
	#define APPLE 1
#elif defined(__gnu_linux__) || defined(__linux__)
	#define LINUX 1
#endif

// Compiler defines
#if defined(_MSC_VER)
	#define VISUALSTUDIO 1
#elif defined(__CYGWIN__)
	#define CYGWIN 1
#elif defined(__MINGW32__)
	#define MINGW 1
	#define WINVER 0x0502
#elif defined(__BORLANDC__)
	#define BORLANDC 1
#endif

#if !WINDOWS
    #define INVALID_SOCKET 1
    #define SOCKET_ERROR -1
    #define SD_SEND 1
#endif

// Includes
#if VISUALSTUDIO
	#include "targetver.h"
#endif

#if WINDOWS
	#include <tchar.h>
	#include <ws2tcpip.h> // WinSocket
#elif APPLE
	#include <unistd.h> // for sleep() Function
	#include <sys/types.h>
	#include <sys/socket.h>
	#include <netdb.h>
#else
	#include <sys/types.h>
	#include <sys/socket.h>
	#include <netdb.h>
#endif

// Redefines for safe VS functions
#if MINGW || APPLE || BORLANDC
	#define scanf_s scanf
	#define sscanf_s sscanf
	#define sprintf_s sprintf
	#define strcat_s strcat
#endif


// TODO: Hier auf zusaetzliche Header, die das Programm erfordert, verweisen.
#include <string.h> // for String functions
#include <stdlib.h>

#include "jclient.h"
#include "colors.h"
