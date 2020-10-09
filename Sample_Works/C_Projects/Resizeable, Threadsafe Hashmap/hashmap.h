#ifndef HASHMAP_H
#define HASHMAP_H

#include <stdlib.h>
#include <pthread.h>
#include <string.h>



struct hash_map {
    int (*compare)(void*,void*);
    size_t(*hash_function)(void*);
    void (*rm_key)(void*) ;
    void (*rm_value)(void*);
    pthread_mutex_t program_lock;
    size_t entries ;
    struct entry** hash_map;
    size_t max_entries ;
    size_t n_nodes;
    size_t old_max ;
    struct entry** old_map;


    
};

struct entry{
    struct node* head ;
};

struct node{
    void* v;
    void* k;
    struct node* next;
};





struct hash_map* hash_map_new(size_t (*hash)(void*), int (*cmp)(void*,void*),
    void (*key_destruct)(void*), void (*value_destruct)(void*));

void hash_map_put_entry_move(struct hash_map* map, void* k, void* v);

void hash_map_remove_entry(struct hash_map* map, void* k);

void* hash_map_get_value_ref(struct hash_map* map, void* k);

void hash_map_destroy(struct hash_map* map);

#endif
