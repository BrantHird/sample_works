
#include "rc.h"
#include <stdio.h>
#include "string.h"
#include <stdlib.h>

static struct strong_ref** refList ;
static size_t refDex = 0;

struct strong_ref* rc_alloc(void* ptr, size_t n, struct strong_ref* dep) {

    
    /* If the ptr argument is NULL and deps is NULL, it will return a new
    * allocation
    */
    
    if(ptr == NULL && dep == NULL){
        
        if(refDex == 0){
            refList = malloc(sizeof(struct strong_ref*));
            refList[refDex] = malloc(sizeof(struct strong_ref));
        }
        
       else if(refDex > 0){
            refList = realloc(refList, sizeof(struct strong_ref*) * (refDex + 1));
            refList[refDex] = malloc(sizeof(struct strong_ref));
        }

        struct strong_ref * sR = malloc(sizeof(struct strong_ref));
        sR->entry.count = 1;
        sR-> entry.n_deps = 0;
        sR -> entry.dep_capacity = 0;
        sR -> entry.dep_list = NULL;
        sR->ptr = NULL ;
        
        memcpy(refList[refDex], sR, sizeof(struct strong_ref));
        refList[refDex]->ptr = malloc(n);
        
        
        refDex ++;
        free(sR);
        return refList[refDex - 1];
    }
    
    /* * If the ptr argument is NULL and deps is not NULL, it will return
       * a new allocation but the count will correlate to the dependency
       * if the dependency is deallocated the reference count on the object will
       * decrement
       */
    
    
    else if(ptr==NULL && dep!=NULL) {

        if(refDex > 0){
            refList = realloc(refList, sizeof(struct strong_ref*) * (refDex + 1));
            refList[refDex] = malloc(sizeof(struct strong_ref));
        }
        

        //Finding the reference that I am dependant on.
       for(size_t i = 0 ; i < refDex ; i ++){
            if(refList[i]->entry.count != 0){
                
                if(refList[i]->ptr == dep -> ptr){
                    
                    struct strong_ref * sR = malloc(sizeof(struct strong_ref));

                    sR->entry.count = refList[i]->entry.count;
                    sR-> entry.n_deps = 0;
                    sR -> entry.dep_capacity = 0;
                    sR -> entry.dep_list = NULL;
                    sR -> ptr = NULL;

                    memcpy(refList[refDex], sR, sizeof(struct strong_ref));
                    refList[refDex]->ptr = malloc(n);
                    
                    
                    refList[i]->entry.n_deps ++;
                    refList[i]->entry.dep_capacity++;
                    
                    if(refList[i]->entry.dep_capacity == 1){
                        refList[i]->entry.dep_list = malloc(sizeof(size_t));
                    }

                    else{
                         refList[i]->entry.dep_list = realloc(refList[i]->entry.dep_list,sizeof(size_t)*refList[i]->entry.dep_capacity);
                    }
                    
                    refList[i] -> entry.dep_list[refList[i]->entry.dep_capacity - 1] = refDex ;
                    
                    free(sR);
                                       
                    
                    
                    
                }
            }
        }


        refDex ++ ;
        
        return refList[refDex - 1];

    
    }
    
    
    /* If the ptr argument is not NULL and an entry exists, it will increment
    *  the reference count of the allocation and return a strong_ref pointer*/
    
    else if(ptr!= NULL && dep == NULL){
        
        for(size_t i = 0 ; i < refDex ; i ++){
            
            if(refList[i]->entry.count != 0){
                
                if(refList[i]->ptr == ptr){
                    //Found the ptr
                    refList[i]->entry.count ++ ;
                    return refList[i];
                }
                
            }
            
            
        }
        
        
    }
        
    
   
    
    
    /* If the ptr argument is not NULL and an entry exists, it will increment
    *  the reference count of the allocation and return a strong_ref pointer*/
    
    
    return NULL ;
    
}


struct weak_ref rc_downgrade(struct strong_ref* ref) {
    
    
    
    if(ref == NULL){
        struct weak_ref badResult = { 0xFFFFFFFFFFFFFFFF };
        return badResult;
    }
    
    else if(ref->entry.count < 1){
        struct weak_ref badResult = { 0xFFFFFFFFFFFFFFFF };
        return badResult;
        
    }
    
    
    
    for(size_t i = 0 ; i < refDex ; i ++){
        
        if(refList[i]->entry.count != 0){
            
            if(refList[i]-> ptr == ref->ptr){
                //Found the ref in question
                struct weak_ref r = {i};
                
                //Decrement the count after the downgrade
                refList[i] -> entry.count -- ;
                
                if(refList[i]->entry.count == 0){
                    
                        struct weak_ref badResult = { 0xFFFFFFFFFFFFFFFF };
                    
                        for(size_t d = 0 ; d < refList[i]->entry.dep_capacity ; d ++){
                            
                            size_t index = refList[i]->entry.dep_list[d];
                            
                            if(refList[index]->entry.count > 0){
                                refList[index] -> entry.count -- ;
                            }
                            
                        }
                        return badResult;
                }

                for(size_t d = 0 ; d < refList[i]->entry.dep_capacity ; d ++){
                    
                    size_t index = refList[i]->entry.dep_list[d];
                    
                    if(refList[index]->entry.count > 0){
                        refList[index] -> entry.count -- ;
                    }
                    
                }
                
                
                return r;
            }
            
        }
    }
    
    struct weak_ref badResult = { 0xFFFFFFFFFFFFFFFF };
    return badResult;
    
}

struct strong_ref* rc_upgrade(struct weak_ref ref) {
    
    if(ref.entry_id == 0xFFFFFFFFFFFFFFFF){
        return NULL ;
    }
    
    if(ref.entry_id > refDex - 1 || refDex == 68){
        return NULL ;
    }
    
    else if(refList[ref.entry_id]->entry.count == 0){
        return NULL ;
    }
    
    else{
        
        refList[ref.entry_id]->entry.count ++ ;
        return refList[ref.entry_id];
        
    }
    
    return NULL ;
    
}


void rc_cleanup() {
    
    
    for(size_t i = 0 ; i < refDex ; i ++){
            
            free(refList[i]->ptr);
            
            if(refList[i]->entry.dep_capacity > 0){
                
                free(refList[i]->entry.dep_list);
                
            }
        
        free(refList[i]);
        
    }
    
    free(refList);
    
    refDex =  68 ;

}





