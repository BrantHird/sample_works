#include <stdlib.h>
#include "hashmap.h"
#include <stdio.h>


struct hash_map* hash_map_new(size_t (*hash)(void*), int (*cmp)(void*,void*),
    void (*key_destruct)(void*), void (*value_destruct)(void*)) {
        
    if(hash == NULL || cmp == NULL){
        return NULL;
    }
    
    if(key_destruct == NULL || value_destruct == NULL){
        return NULL;
    }
    
    struct hash_map* ret = malloc(sizeof(struct hash_map));
    ret -> compare = cmp ;
    ret -> hash_function = hash ;
    ret -> rm_key = key_destruct ;
    ret -> rm_value = value_destruct;
    ret -> entries = 0 ;
    ret -> max_entries = 15 ;
    ret -> n_nodes = 0 ;
    
    pthread_mutex_init(&(ret->program_lock),NULL);
    
    ret -> hash_map = malloc(ret->max_entries*sizeof(struct entry));
    memset(ret->hash_map,0,ret->max_entries*sizeof(struct entry));
    
    ret -> old_map = ret -> hash_map ;
    ret -> old_max = ret -> max_entries;

    
    return ret ;
}




struct node* make_node(void* val, void* key){
    
    struct node* ret = malloc(sizeof(struct node));
    ret -> v = val;
    ret -> k = key;
    ret -> next = NULL ;
    
    return ret;
}


struct entry* make_entry(){
    
    struct entry * ret = malloc(sizeof(struct entry));
            
    ret->head = NULL ;
    
    return ret;
    
}

void resize(struct hash_map* map){
    
       struct entry** old_map = map -> hash_map;
       
       map -> hash_map  = malloc(map->max_entries*4*sizeof(struct hash_map));
       
       for(size_t j = 0 ; j < map->max_entries*4 ; j++){
              map->hash_map[j] = NULL ;
       }
       
       for(size_t i = 0; i < map->max_entries; i ++){
           
           if(old_map[i] != NULL){
                           
               struct node* search_node = old_map[i] -> head;
               struct node* temp_node ;
               
               while(search_node != NULL){
                   
                   temp_node = search_node;
                   search_node = search_node -> next ;
                   
                   size_t hash = map->hash_function(temp_node->k);
                   
                   hash = hash % (map->max_entries*4);
                   
                   if(map->hash_map[hash] == NULL){
                       struct entry* new_entry = make_entry();
                       map->hash_map[hash] = new_entry;
                   }
                   
                   temp_node -> next = map->hash_map[hash] -> head;
                   
                   map->hash_map[hash] -> head = temp_node;
               }
               
               free(old_map[i]);
           }
           
       }
    
       free(old_map);
       
       map -> max_entries = map->max_entries*4;

    
}


void hash_map_put_entry_move(struct hash_map* map, void* k, void* v) {
    pthread_mutex_lock(&(map->program_lock));
    
    if(map == NULL){
        pthread_mutex_unlock(&(map->program_lock));
        return;
    }
    
    size_t hash = map->hash_function(k);
    hash = hash % map->max_entries;
    
    struct node* new_node = make_node(v,k);
    
    if(map->hash_map[hash] == NULL){
        if(map->hash_map[hash] == NULL){
            struct entry* new_entry = make_entry();
            map->hash_map[hash] = new_entry;
            map -> entries ++ ;
        }

    }
    
    else{
        
        struct node* check = map ->hash_map[hash] -> head;
        struct node* prev = NULL;
        
        while(check!= NULL){

            if(map->compare(k, check->k) ==  1){
                          
                  if(prev != NULL){
                      prev -> next = check -> next ;
                  }
                
                  else{
                      map->hash_map[hash] -> head = check -> next ;
                  }
                  
                  map->rm_value(check->v);
                  map->rm_key(check->k);
                  free(check);
                
                  break ;
            }
            
            prev = check ;
            check = check -> next ;
        
        }
    }
    

    new_node -> next = map->hash_map[hash] -> head;
    map->hash_map[hash] -> head = new_node;
    map ->n_nodes ++;
    
    if(map->entries >= (map->max_entries/3)*2 || map -> n_nodes > map -> max_entries){
            resize(map);
    }
    
    
    pthread_mutex_unlock(&(map->program_lock));
}

void hash_map_remove_entry(struct hash_map* map, void* k) {
    pthread_mutex_lock(&(map->program_lock));
    
    size_t hash = map->hash_function(k);
    hash = hash % map->max_entries;
    
    if(map->hash_map[hash] == NULL){
        pthread_mutex_unlock(&(map->program_lock));
        return ;
    }
    
    struct node* check = map->hash_map[hash]->head;
    struct node* prev = NULL;
    
            
    while(check!= NULL){
        
        if(map -> compare(k, check-> k)){
            
            if(prev != NULL){
                prev -> next = check -> next ;
            }
            else{
                map->hash_map[hash] -> head = check -> next;
            }
            
            map->rm_value(check->v);
            map->rm_key(check->k);
            free(check);
            pthread_mutex_unlock(&(map->program_lock));
            return ;
        }
        
        prev = check ;
        check = check -> next ;
    }
    
    pthread_mutex_unlock(&(map->program_lock));
    
}

void* hash_map_get_value_ref(struct hash_map* map, void* k) {
    
    pthread_mutex_lock(&(map->program_lock));
    
    if(k == NULL || map == NULL){
        pthread_mutex_unlock(&(map->program_lock));
        return NULL ;
    }

    size_t hash = map->hash_function(k);
    hash = hash % map->max_entries;
    
    if(map->hash_map[hash] == NULL || map->hash_map[hash] -> head == NULL){
        
        pthread_mutex_unlock(&(map->program_lock));
        
        return NULL ;
    }
    
    
    struct node* check = map->hash_map[hash]->head;
    
    while(check!= NULL){
        
        if(map -> compare(k, check-> k)){
            pthread_mutex_unlock(&(map->program_lock));
            return check->v ;
        }
            
        check = check -> next;
    }
            

    pthread_mutex_unlock(&(map->program_lock));
    return NULL;
}

void hash_map_destroy(struct hash_map* map) {
    
    if(map == NULL){
        return ;
    }
    
    for(size_t i = 0 ; i < map->max_entries; i ++){
        if(map->hash_map[i] != NULL){
            
            struct node* this_node = map->hash_map[i] -> head ;
            struct node* temp;
            
            while(this_node != NULL){
                temp = this_node ;
                this_node = this_node -> next;
                map ->rm_value(temp->v);
                map->rm_key(temp->k);
                free(temp);
            }
            free(map->hash_map[i]);
        }

    }
    
    free(map->hash_map);
    free(map);
}
