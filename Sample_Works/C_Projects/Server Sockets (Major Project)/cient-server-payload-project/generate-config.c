#include <sys/socket.h>
#include <assert.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <inttypes.h>
#include <stdint.h>

int main(int argc, char ** argv) {
    // Usage: ./generate-config <dotted quad ipv4 address xxx.xxx.xxx.xxx> <port number> <target directory>
    // For example: ./generate-config 127.0.0.1 8888 /this/is/my/directory
    // Configuration file will be output to standard output. Redirect it using the shell to save to a file
    assert(argc == 4);

    // Convert port to a long
    long rawport = strtol(argv[2], NULL, 10);

    // Port is 16-bit unsigned integer, so 0-65535. Port 0 is special and cannot be used.
    assert(rawport > 0);
    assert(rawport < 65536);

    // Convert port number to network byte order
    uint16_t port = htons((uint16_t)rawport);

    // struct in_addr is how the address is stored
    struct in_addr inaddr;

    // Convert address from dotted quad notation (e.g. 127.0.0.1) to network byte order integer, and also save it to the struct
    // Access the integer representation itself with the s_addr field of struct in_addr
    inet_pton(AF_INET, argv[1], &inaddr);

    // First 4 bytes: address in network byte order
    fwrite(&(inaddr.s_addr), sizeof(inaddr.s_addr), 1, stdout);

    // Next 2 bytes: port in network byte order
    fwrite(&port, sizeof(port), 1, stdout);

    // Remainder: directory as non-NULL terminated ASCII
    fwrite(argv[3], strlen(argv[3]), 1, stdout);

    return 0;
}
