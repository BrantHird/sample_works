#include <assert.h>
#include "svc.c"
#include<stdio.h>

int main() {
    
    FILE* fp = fopen("COMP2017/svc.c", "w");
    char* kol = "#include ""svc.h""\n void *svc_init(void) {\n// TODO: implement\n}\n";
    fwrite(kol, sizeof(char)*strlen(kol) + 1, 1 ,fp);
    fclose(fp);
    
    void *helper = svc_init();
     
    int a = svc_add(helper, "COMP2017/svc.h");
    printf("%d\n", a);

    int b = svc_add(helper, "COMP2017/svc.c");
    printf("%d\n", b);

    
    char* c = svc_commit(helper, "Initial commit");
    printf("%s\n", c);
    
    print_commit(helper, c);
    printf("real hash for svc.c : %d\n", hash_file(helper,"COMP2017/svc.c" ));
    printf("real hash for svc.h : %d\n", hash_file(helper,"COMP2017/svc.h" ));
    
    
    int d = svc_branch(helper, "random_branch");
    printf("%d\n", d);

    
    int e = svc_checkout(helper, "random_branch");
    printf("%d\n", e);
    
    
    fp = fopen("COMP2017/svc.c", "w");
    
    char*message = "#include ""svc.h""\nvoid *svc_init(void) {\nreturn NULL;\n }\n";
    
    fwrite(message, sizeof(char)*strlen(message) + 1, 1 ,fp);
    
    fclose(fp);
    
    
    int f =svc_rm(helper, "COMP2017/svc.h");
    printf("%d\n", f);

    
    char* g = svc_commit(helper, "Implemented svc_init");
    printf("%s\n",g);
    
    print_commit(helper, g);
    
    printf("real hash for svc.c : %d\n", hash_file(helper,"COMP2017/svc.c" ));
    printf("real hash for svc.h : %d\n", hash_file(helper,"COMP2017/svc.h" ));



    
    struct program * h = (struct program*)helper ;
    
    int i = svc_reset(helper, "7b3e30");
    printf("i: %d\n", i);
    
    fp = fopen("COMP2017/svc.c", "w");
    
    char* m = "#include ""svc.h""\nvoid *svc_init(void) {\nreturn NULL;\n }\n";
    
    fwrite(m, sizeof(char)*strlen(m) + 1, 1 ,fp);
    
    fclose(fp);
    
    char* j = svc_commit(helper, "Implemented svc_init");
    printf("%s\n", j);
    
    print_commit(helper, j);
    
    
    printf("real hash for svc.c : %d\n", hash_file(helper,"COMP2017/svc.c" ));
    printf("real hash for svc.h : %d\n", hash_file(helper,"COMP2017/svc.h" ));


    
    void* commit = get_commit(helper, j);
    
    int n_prev;
    char **prev_commits = get_prev_commits(helper, commit, &n_prev);
    printf("%d\n", n_prev);
    
    int k = svc_checkout(helper, "master");
    printf("%d\n",k);
    
    fp = fopen("resolutions/svc.c", "w+");
    
    char*mem = "#include ""svc.h""\nvoid *svc_init(void) {\nreturn NULL;\n }\n";
    
    fwrite(mem, sizeof(char)*strlen(mem) + 1, 1 ,fp);
    
    fclose(fp);
    
    struct resolution* resolutions = malloc(sizeof(struct resolution));
    resolutions -> file_name = "COMP2017/svc.c";
    resolutions -> resolved_file = "resolutions/svc.c";
    
    char* l = svc_merge(helper, "random_branch", resolutions, 1);
    
    printf("%s\n", l);
    
    print_commit(helper, l);
    
    int numa;
    void *commie = get_commit(helper, "48eac3");
    char **prevcommies = get_prev_commits(helper, commie, &numa);
    
    for(int i = 0 ; i < numa ; i ++){
        printf("c%d: %s\n", i, prevcommies[i]);
    }

    
    
    // TODO: write your own tests here
    // Hint: you can use assert(EXPRESSION) if you want
    // e.g.  assert((2 + 3) == 5);
    
    
    printf("%d\n", hash_file(helper, "d.out"));
    
    cleanup(helper);
    
    return 0;
}

