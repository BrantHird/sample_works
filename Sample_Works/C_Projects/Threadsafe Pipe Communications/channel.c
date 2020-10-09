#include <stdlib.h>
#include "channel.h"
#include <unistd.h>
#include <stdio.h>


void channel_init(struct receiver* recv, struct sender* sender, 
  size_t msg_sz) {
    
    if(recv == NULL || sender == NULL){
        return ;
    }
    
    int fd[2];
    
    if(pipe(fd) == - 1){
        printf("failed to create pipe\n");
        return ;
    }
    
    recv -> channel_ref = fd[0];
    recv -> msg_sz = msg_sz ;
    sender -> channel_ref = fd[1];
    sender -> msg_sz = msg_sz ;
    
}

void channel_get(struct receiver* channel, void* data) {
    
    if(data == NULL || channel == NULL){
        return ;
    }
    
    read(channel->channel_ref, data, channel -> msg_sz) ;

}

void channel_send(struct sender* channel, void* data) {
    
    if(data == NULL || channel == NULL){
        return ;
    }
    
    write(channel -> channel_ref, data, channel -> msg_sz);

}

void sender_dup(struct sender* dest, struct sender* src) {
    
    if(dest == NULL || src == NULL){
        return ;
    }
    
    dest -> msg_sz = src -> msg_sz;
    dest -> channel_ref = src -> channel_ref;
	
}

void channel_destroy(struct receiver* recv, struct sender* sender) {
    
	
}

