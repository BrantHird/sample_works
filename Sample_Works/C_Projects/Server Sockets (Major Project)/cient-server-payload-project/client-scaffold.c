#include <stdlib.h>
#include <sys/socket.h>
#include <assert.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <pthread.h>
#include <sys/time.h>
#include <time.h>

//#include "compression.h"

#define MULTIPLEX_CON (4)
#define BANDWIDTH (15000)

#define BYTE_TO_BINARY_PATTERN "%c%c%c%c%c%c%c%c"
#define BYTE_TO_BINARY(byte)  \
  (byte & 0x80 ? '1' : '0'), \
  (byte & 0x40 ? '1' : '0'), \
  (byte & 0x20 ? '1' : '0'), \
  (byte & 0x10 ? '1' : '0'), \
  (byte & 0x08 ? '1' : '0'), \
  (byte & 0x04 ? '1' : '0'), \
  (byte & 0x02 ? '1' : '0'), \
  (byte & 0x01 ? '1' : '0')

int connection[8];
//struct dict *dict;
struct timeval start;

void *return_thread(void *arg) {

    int c = *(int *)arg;
    uint8_t h;
    uint64_t p_len;
    uint64_t p_len_be;
    uint64_t offset;
    uint64_t offset_be;
    uint64_t length;
    uint64_t length_be;
    uint32_t session_id;
    uint64_t limit_bandwidth = BANDWIDTH;
    struct timeval end;
    int i = 0;
    double time_spent = 0;
    struct timespec sleep_time;
    while (1) {

        // Set cancelbility
        pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL);

        // Get header
        recv(connection[c], &h, sizeof(uint8_t), 0);

        // Get payload length
        recv(connection[c], &p_len_be, sizeof(uint64_t), 0);
        p_len = be64toh(p_len_be);

        // Get payload
        uint8_t *payload = malloc(p_len);
        recv(connection[c], payload, p_len, 0);

        // // Decompress it
        // struct decomp_return *d = decompress(payload, p_len, dict->d_dict);
        // memcpy(&session_id, d->payload, sizeof(uint32_t));
        // memcpy(&offset_be, d->payload + sizeof(uint32_t), sizeof(uint64_t));
        // memcpy(&length_be, d->payload + 3*sizeof(uint32_t), sizeof(uint64_t));
        //
        // offset = be64toh(offset_be);
        // length = be64toh(length_be);
        //
        // // for (size_t j = 0; j < d->len - 3*sizeof(uint32_t); j++) {
        // //     printf("%c", *(d->payload + 3*sizeof(uint32_t) + j));
        // // }
        // printf("Connection %d recieved payload of size %4ld from Session %d, Offset %5ld, Length %ld\n",
        //         c, p_len, session_id, offset, length);
        sleep_time.tv_sec = p_len/limit_bandwidth;
        sleep_time.tv_nsec = (1000000000*p_len)/limit_bandwidth - 1000000000 * sleep_time.tv_sec;
        nanosleep(&sleep_time, NULL);
        //printf("Sleeping for: %ld.%08lds\n", sleep_time.tv_sec, sleep_time.tv_nsec);
        gettimeofday(&end, NULL);
        time_spent = (end.tv_sec - start.tv_sec) + (double)(end.tv_usec - start.tv_usec)/1000000;
        printf("Time elapsed is %5fs\n", time_spent);
        i++;
    }
}


int main(int argc, char ** argv) {
    // Usage: ./client <dotted quad ipv4 address xxx.xxx.xxx.xxx> <port number>
    // For example: ./client 127.0.0.1 8888
    assert(argc == 3);

    // Convert port to a long
    long rawport = strtol(argv[2], NULL, 10);

    // Port is 16-bit unsigned integer, so 0-65535. Port 0 is special and cannot be used.
    assert(rawport > 0);
    assert(rawport < 65536);
// Set cancelbility
    pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL);
    // Convert port number to network byte order
    uint16_t port = htons((uint16_t)rawport);

    // struct in_addr is how the address is stored
    struct in_addr inaddr;

    // Convert address from dotted quad notation (e.g. 127.0.0.1) to network byte order integer, and also save it to the struct
    // Access the integer representation itself with the s_addr field of struct in_addr
    inet_pton(AF_INET, argv[1], &inaddr);

    // Create socket, and check for error
    // AF_INET = this is an IPv4 socket
    // SOCK_STREAM = this is a TCP socket
    // connection is already a file descriptor. It just isn't connected to anything yet.
    // create array of connections
    int num_con = 8;
    for (int i = 0; i < num_con; i++) {
        connection[i] = socket(AF_INET, SOCK_STREAM, 0);
        if (connection[i] < 0) {
            perror("socket");
            return 1;
        }
    }

        // struct sockaddr_in is how you specify a network address
    // Because this is the client, this represents our connection destination
    // However, you will use a very similar construct on the server, to represent the address where the server listens
    // sin_family = AF_INET = this is an IPv4 address
    // sin_port = the port number goes here
    // sin_addr = the address (as a struct in_addr) goes here
    struct sockaddr_in destination;
    destination.sin_family = AF_INET;
    destination.sin_port = port;
    destination.sin_addr = inaddr;

    // connect(2) actually makes the connection. Check for error.
    for (int i = 0; i < num_con; i++) {
        if (connect(connection[i], (struct sockaddr *)&destination, sizeof(destination)) != 0) {
            perror("connect");
            return 1;
        }
    }

    // Get the dictionary
    //dict = create_dictionary();


    uint8_t h = 0x6 << 4;
    char filename[] = "alice29.txt";
    uint64_t p_len = strlen(filename) + 21;
    uint32_t session_id = 15645685;
    uint64_t offset = 0;
    uint64_t length = 150000;


    uint64_t p_len_be = htobe64(p_len);
    uint64_t offset_be = htobe64(offset);
    uint64_t length_be = htobe64(length);

    int nun_retr = MULTIPLEX_CON;
    pthread_t *threads = malloc(nun_retr * sizeof(pthread_t));
    gettimeofday(&start, NULL);
    for (int i = 0; i < nun_retr; i++) {
        write(connection[i], &h, sizeof(uint8_t));
        write(connection[i], &p_len_be, sizeof(uint64_t));
        write(connection[i], &session_id, sizeof(uint32_t));
        write(connection[i], &offset_be, sizeof(uint64_t));
        write(connection[i], &length_be, sizeof(uint64_t));
        write(connection[i], filename, strlen(filename)+1);
        int *arg = malloc(sizeof(int));
        *arg = i;
        pthread_create(threads+i,NULL, return_thread, (void *)arg);
        //sleep(1);
    }



    // // Create data to send
    // char payload[] = "plrabn12.txt";
    // p_len = 13 + 20;
    //
    // // Send a file retrieval listing
    // h = 0x6 << 4 | 1 << 2;
    // p_len = htobe64(p_len);
    // uint32_t ses = 123456;
    // uint64_t offset = 0;
    // uint64_t length = 10;
    // offset = htobe64(offset);
    // length = htobe64(length);
    // write(connection[0], &h, sizeof(uint8_t));
    // write(connection[0], &p_len, sizeof(uint64_t));
    // write(connection[0], &ses, sizeof(uint32_t));
    // write(connection[0], &offset, sizeof(uint64_t));
    // write(connection[0], &length, sizeof(uint64_t));
    // write(connection[0], payload, 13);
    // char *ret = malloc(100000);
    // recv(connection[0], ret, 39, 0);
    // for (size_t i = 0; i < 39; i++) {
    //     printf("%c", ret[i]);
    // }
    // printf("\n");



    //hiu
    // Shutdown command
    sleep(20);
    for (int i = 0; i < nun_retr; i++) {
        pthread_cancel(*(threads+i));
        pthread_join(*(threads+i), NULL);
    }

    printf("Sending shutdown command\n");
    h = 0x8 << 4;
    p_len = htobe64(0);
    write(connection[1], &h, sizeof(uint8_t));
    write(connection[1], &p_len, sizeof(uint64_t));

    sleep(1);
    for (int i = 0; i < num_con; i++) {
        close(connection[i]);
    }

    return 0;
}
