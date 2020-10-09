#include <stdio.h>
#include <stdlib.h>
#include "rc.h"
#include "rc.c"

typedef struct command command_t;

struct command {
    char* str;
    int (*exe)();
};


int rc_example_1() {
    
    struct strong_ref* m = rc_alloc(NULL, sizeof(int), NULL);
    
    printf("mcount1: %zu\n", m->entry.count);
    printf("%zu\n", m->entry.n_deps);
    printf("%zu\n", m->entry.dep_capacity);
    
    
    for(size_t i = 0 ; i < m->entry.n_deps; i++){
        
        printf("Depandancy POSITION ONE: %zu\n", m->entry.dep_list[i]);
        
    }

    
    
    struct strong_ref * p = rc_alloc(NULL,sizeof(int),m);
    
    printf("\nmcount2: %zu\n", m->entry.count);
    printf("%zu\n", m->entry.n_deps);
    printf("%zu\n", m->entry.dep_capacity);

    
    printf("%zu\n", p->entry.count);
    printf("%zu\n", p->entry.n_deps);
    printf("%zu\n", p->entry.dep_capacity);

    *((int*) p->ptr) = 1 ;
    *((int*) m->ptr) = 1 ;
    
    printf("here : %d\n", *((int*)p->ptr));
    printf("here: %d\n", *((int*)m->ptr));
    
    
    for(size_t i = 0 ; i < m->entry.n_deps; i++){
        
        printf("Depandancy POSITION : %zu\n", m->entry.dep_list[i]);
        
    }
    
    for(size_t i = 0 ; i < p->entry.n_deps; i++){
           
           printf("Depandancy POSITION : %zu\n", m->entry.dep_list[i]);
           
    }



    
    struct weak_ref w = rc_downgrade(m);
    
    
    for(size_t i = 0 ; i < m->entry.n_deps; i++){
        
        printf("Depandancy POSITION I9DSADSA: %zu\n", m->entry.dep_list[i]);
        
    }
    
    
    
    
    
    printf("\nmcount3: %zu\n", m->entry.count);
    printf("%zu\n", m->entry.n_deps);
    printf("%zu\n", m->entry.dep_capacity);

     
     printf("%zu\n", p->entry.count);
     printf("%zu\n", p->entry.n_deps);
     printf("%zu\n", p->entry.dep_capacity);
    


    rc_cleanup();
    
    

    struct strong_ref* tittiesILOVETHEM = rc_upgrade(w);
    
    if(tittiesILOVETHEM!=NULL){
        printf("kill youirself");
    }
    
    
    
    
    
    
    return 0 ;
}

int rc_example_2() {
    int pass = 0;
    int expected = 3;

    

    struct strong_ref* m = rc_alloc(NULL, sizeof(int), NULL);
    struct strong_ref* a = rc_alloc(m->ptr, 0, NULL);
    
    *((int*)m->ptr) = 100;
    
    struct weak_ref w = rc_downgrade(m);
    
    struct strong_ref* p = rc_upgrade(w);
    


    if(p != NULL) {
        pass++;
    }
    
    int v = *((int*)p->ptr);

    if(v == 100) {
        
      pass++;
        
    }
    
    rc_downgrade(p);
    
    w = rc_downgrade(a);

    p = rc_upgrade(w);
    
    if(p == NULL) {
        
        pass++;
    }
    
    return pass == expected;
}

command_t tests[] = {
  { "rc_example_1", rc_example_1 },
  { "rc_example_2", rc_example_2 },
};


int main(int argc, char** argv) {
  int test_n = sizeof(tests) / sizeof(command_t);
  if(argc >= 2) {
        for(int i = 0; i < test_n; i++) {
            if(strcmp(argv[1], tests[i].str) == 0) {
                if(tests[i].exe()) {
                  fprintf(stdout, "%s Passed\n", tests[i].str);
                } else {
                  fprintf(stdout, "%s Failed\n", tests[i].str);
                }
            }
        }
    }
}
