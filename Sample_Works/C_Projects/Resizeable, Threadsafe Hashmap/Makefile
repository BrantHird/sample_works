CC=clang
CFLAGS=-Werror=vla -Wextra -Wall -Wshadow -Wswitch-default -std=gnu11
CFLAG_SAN=$(CFLAGS) -fsanitize=address
DEPS=
OBJ=hashmap.o

hashmap.o: hashmap.c hashmap.h $(DEPS)
	$(CC) -c -g -o $@ $< $(CFLAGS)
	
clean:
	rm *.o
