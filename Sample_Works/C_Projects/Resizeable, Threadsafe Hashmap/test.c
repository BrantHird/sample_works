#include <stdlib.h>
#include "hashmap.c"
#include <stdio.h>

size_t hash(void* n){
    
    size_t ret = 2;
    return ret;
    
}

int cmp(void* n,void* o){
    return 1 ;
}

void key_destruct(void* n){
    return ;
}

void value_destruct(void* n){
    return ;
}


int main(){
    
    struct hash_map* my_map = hash_map_new(&hash, &cmp, &key_destruct, &value_destruct);

    printf("%p", my_map);
    
    hash_map_destroy(my_map);
    
    return 0 ;
}
