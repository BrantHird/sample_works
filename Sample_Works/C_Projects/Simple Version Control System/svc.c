#include "svc.h"
#include <stdlib.h>
#include <stdio.h>
#include "string.h"
#include <ctype.h>
#define MAX_FILE 260
#define MAX_NAME 50


static int curr_branch = 0 ;

struct file{
    char* path ;
    int hash ;
    struct file* next;
};

struct program{
    
    struct branch* branches ;
    int n_branches ;
    struct repository* repository;
    struct commit* hidden_commits;
    
};

struct commit {
    
    struct commit* prev;
    struct change* changes;
    int n_changed;
    char* id ;
    char* message;
    struct file* files;
    char* belongs_to;
    int n_files ;
    struct commit* merge_parents;
};

struct change {
    
    char* file ;
    int type ;  // 0 = rm, 1 = add, 2 = mod
    int new_hash ;
    int old_hash ;
    struct change* next ;
    struct change* prev ;
    
};

struct branch {
    int id;
    char* name;
    struct commit* head;
    int n_files;
    struct file* files;
    struct file* removed_files;
    int rm_files;
    struct commit* first_commit ;
    int commits;
    
};

struct repository{
    
    struct file* legacy_files;
    
};



int get_commit_id(struct commit* c){
    
    
    int id = 0 ;
    
    unsigned char name_byte ;
    
    
    // Generate initial hash using commit message
    for(int i = 0 ; i < strlen(c->message) ; i ++){
        name_byte = (unsigned char)c->message[i];
        id = (id + (int)name_byte) % 1000 ;
    }
        
    struct change * this_change = c -> changes ;
    
    
    //Update hash using stored commit changes
    while(this_change!= NULL){
        
        if(this_change->type == 1){
            id = id + 376591;
        }
        else if(this_change -> type == 0){
            id = id + 85973;
        }
        else if(this_change -> type == 2){
            id = id + 9573681;
        }
        
        unsigned char byte ;
        
        //Accounting for change file contents.
        for(int j = 0 ; j < strlen(this_change->file) ; j++){
            
            byte = (unsigned char)this_change->file[j];
            id = (id * ((int)byte % 37)) % 15485863 + 1;
        }
        
        this_change = this_change -> next;
        
    }

    return id ;
    
}


void *svc_init(void) {
    
    //Initialise program struct as helper
    struct program* helperPtr = malloc(sizeof(struct program));
    helperPtr->branches = malloc(sizeof(struct branch));
    helperPtr->n_branches = 1;
    helperPtr->branches[0].id = 0 ;
    helperPtr->branches[0].head = NULL;
    helperPtr->branches[0].n_files = 0 ;
    helperPtr->branches[0].files = NULL ;
    helperPtr->branches[0].removed_files = NULL ;
    helperPtr -> branches[0]. rm_files = 0 ;
    helperPtr ->branches[0].commits = 0;
    helperPtr -> repository = malloc(sizeof(struct repository));
    helperPtr -> repository[0].legacy_files = NULL ;
    helperPtr ->hidden_commits = NULL;
    
    //Default master branch
    char* m = "master" ;
    helperPtr->branches[0].name = malloc(sizeof(char)*MAX_NAME);
    memcpy(helperPtr->branches[0].name, m, sizeof(char)*strlen(m) + 1);
    helperPtr->n_branches = 1;
    
    return helperPtr;
    
}

void free_commit(struct commit* com, struct branch* this_branch, int i){
                 
                 struct commit* tem;
                 while(com != NULL){
                    
                     //Set temporary commit to be freed
                     tem = com ;
                     
                     //If commit doesn't belong to this branch, break immediately.
                     if(strcmp(tem -> belongs_to, this_branch-> name)!= 0){
                         com = NULL ;
                         continue ;
                     }
                     
                     //If commit is the first commit on the branch, break at the end of the loop.
                     if(tem == this_branch -> first_commit){
                         com = NULL ;
                     }
                     
                     else{
                         com = com->prev ;
                     }
                     
                     //If commit has merge parents, free them.
                     if(tem->merge_parents != NULL){
                         free_commit(tem->merge_parents, this_branch, -1);
                     }
                     
                     //Free message, id and belongs_to fields.
                     free(tem -> message);
                     free(tem -> id);
                     free(tem->belongs_to);
                     
                     //Free any stored changes.
                     if(tem->changes != NULL){
                         
                         struct change* chan = tem->changes ;
                         struct change* t;
                         
                         while(chan != NULL){
                             
                             t = chan ;
                             chan = chan -> next ;
                             free(t->file);
                             free(t);
                         }
                     }
                     
                     //Free any stored files.
                     if(tem -> files != NULL){
                         struct file* f = tem -> files ;
                         struct file* tp;
                         
                         while(f!= NULL){
                             tp = f ;
                             f = f -> next;
                             free(tp->path);
                             free(tp);
                         }
                         
                         
                     }
                                     
                     free(tem);
                     
                 }
}




void cleanup(void *helper) {
    
    struct program* h = (struct program *)helper;
    
    if(h->n_branches > 0){
        
        
        //Iterate through all branches.
        for(int i = (h->n_branches - 1) ; i > -1 ; i--){


            struct branch* this_branch = &h->branches[i];
            
            //Free branch files.
            if(this_branch->n_files > 0){
                
                struct file* head = this_branch->files ;
                struct file* temp;
                
                while(head != NULL){
                    temp = head;
                    head = head->next ;
                    free(temp->path);
                    free(temp);
                    
                }
                        
            }
            
            //Free any files flagged for removal by svc_rm.
            if(this_branch -> rm_files != 0){
                
                struct file* curr_file = this_branch -> removed_files ;
                struct file* temp_file ;
                
                while(curr_file != NULL){
                    temp_file = curr_file;
                    curr_file = curr_file -> next;
                    free(temp_file -> path);
                    free(temp_file);
                }
                
                
                
            }
            
            //Free all commits on branch.
            if(this_branch->head != NULL && this_branch->commits > 0){
                
                struct commit* com = this_branch -> head ;
                free_commit(com, this_branch, i);
      
            }
            
            //Free branch name field.
            free(this_branch->name);

            
        }
        
        free(h->branches);
        
    }
    
    
    //Free hidden branch commits using special version of free_commit
    struct commit* f_com = h -> hidden_commits;
    struct commit* t_com;
    
    while(f_com != NULL){
        
        t_com = f_com ;
                        
        f_com = f_com->prev ;
        
        free(t_com -> message);
        free(t_com -> id);
        free(t_com->belongs_to);
        
        if(t_com->changes != NULL){
            
            struct change* f_change = t_com->changes ;
            struct change* te;
            
            while(f_change != NULL){
                
                te = f_change ;
                f_change = f_change -> next ;
                free(te->file);
                free(te);
            }
        }
        
        if(t_com -> files != NULL){
            struct file* fi = t_com -> files ;
            struct file* tpp;
            
            while(fi!= NULL){
                tpp = fi ;
                fi = fi -> next;
                free(tpp->path);
                free(tpp);
            }
            
        }
        
        free(t_com);
        
    }
    
    
    //Free repository and any stored legacy files.
    struct repository* r = h->repository;
    struct file* repo_file = r -> legacy_files ;
    struct file* temp_repo;
    
    while(repo_file != NULL){
        
        temp_repo = repo_file;
        repo_file = repo_file -> next;
        
        
        free(temp_repo->path);
        free(temp_repo);
        
    }
    
    free(h->repository);
        
    
    
    
    
    //Free program pointer.
    free(helper);
    
}

int hash_file(void *helper, char *file_path) {
    
    //Check paramters.
    if(file_path == NULL){
        return -1 ;
    }
    
    FILE * fp = fopen(file_path, "rb");
    
    if(fp == NULL){
        return -2 ;
    }
    
    unsigned char byte ;
    int number = 0;
    
    fseek(fp,0,2);

    
    number = ftell(fp);
    
    fseek(fp,0,SEEK_SET);
    
    int h = 0 ;
    
    
    //Update hash using file contents.
    for(int i = 0 ; i <number; i ++){
        fread(&byte,1,1,fp);
        
        h = h + byte;
    }
    
    
    int n_file_bytes = strlen(file_path);
    
    unsigned char f_byte;
    
    int num = 0 ;
    
    //Update hash using file name.
    for(int i = 0 ; i < n_file_bytes; i ++){
                
        f_byte = (unsigned char)file_path[i];
        num += (int)f_byte;

    }
    
    int hash = 0 ;
    
    //Calculate hash
    hash = (hash + num)% 1000 ;
    hash = (hash + h)% 2000000000;
    
    fclose(fp);
    
    return hash ;
}




void make_change(void* helper, struct file* curr_file, struct commit* new_commit, int type){
    //create a "change" for the file and add it alphabetically to commit changes.
    
    
    
    if(new_commit -> n_changed == 0){
        
        //Changes are empty, add the first change.

        new_commit -> changes = malloc(sizeof(struct change));
        new_commit -> changes -> type = type;
        new_commit -> changes -> file = malloc(sizeof(char)* MAX_FILE + 1);
        new_commit -> changes -> next = NULL ;
        new_commit -> changes -> prev = NULL ;
        
        memcpy(new_commit -> changes -> file, curr_file->path, sizeof(char)* (strlen(curr_file->path)) + 1);
        new_commit -> changes -> old_hash = curr_file->hash ;
                
        new_commit -> changes -> new_hash = hash_file(helper, curr_file->path);
        curr_file -> hash = new_commit -> changes -> new_hash;
        new_commit -> n_changed ++ ;

    }

    else{

        //Changes aren't empty, add the file in the appropriate position such that alphabetical order is maintained.

        struct change * this_change = new_commit -> changes ;
        struct change * prev_change = NULL ;

        while(this_change != NULL){
            
            int len_curr = strlen(curr_file -> path);
            int len_chan = strlen(this_change -> file);
            
            int smallerLen = 0 ;
            
            if(len_curr > len_chan){
                smallerLen = len_chan;
            }
            else{
                smallerLen = len_curr;
            }

            if(toupper(curr_file -> path[0]) < toupper(this_change -> file[0])){
                break;
            }
            
            else if(toupper(curr_file -> path[0]) == toupper(this_change -> file[0])){
                
                int flag = 0 ;
                
                for(int m = 0 ; m < smallerLen ; m ++){
                    
                    if(toupper(curr_file -> path [m]) != toupper(this_change -> file[m])
                       && toupper(curr_file -> path [m])  < toupper(this_change -> file[m]) ){
                        flag = 1;
                        break ;
                    }
                    
                    else if(toupper(curr_file -> path [m]) != toupper(this_change -> file[m])
                            && toupper(curr_file -> path [m])  > toupper(this_change -> file[m])){
                        break;
                    }
                }
                
                if(flag == 1){
                    break ;
                }
            }
            
            prev_change = this_change ;
            this_change = this_change -> next ;
            
        }
        
        
        if(this_change == NULL){
            
            if(prev_change == NULL){
                printf("big bad error");
            }
            
            else{
                
                //add to the end of the list
                prev_change -> next = malloc(sizeof(struct change));
                prev_change -> next -> type = type;
                prev_change -> next -> file = malloc(sizeof(char)*MAX_FILE + 1);
                memcpy(prev_change -> next -> file, curr_file->path, sizeof(char) * strlen(curr_file -> path) + 1);
                prev_change -> next -> new_hash = hash_file(helper, curr_file->path);
                prev_change -> next -> old_hash = curr_file -> hash  ;

                curr_file -> hash = prev_change -> next -> new_hash;


                prev_change -> next -> next = NULL ;
                prev_change -> next -> prev = prev_change;

                
            }
            
        }
        
        
        else if(this_change -> prev == NULL){
            
           //add to the start of the list ;
           struct change* new_change = malloc(sizeof(struct change));
           new_change -> type = type;
           new_change -> file = malloc(sizeof(char)* MAX_FILE + 1);
           memcpy(new_change -> file, curr_file->path, sizeof(char)* (strlen(curr_file->path)) + 1);
           new_change -> new_hash = hash_file(helper, curr_file->path);
           new_change -> old_hash = curr_file -> hash ;
           curr_file -> hash = new_change -> new_hash ;
            
           new_change -> prev = NULL ;
           new_change -> next = this_change ;
           this_change -> prev = new_change ;
           new_commit -> changes  = new_change ;
            
        }
        
        else{
            
            //add to middle of list
            struct change* prev_change = this_change -> prev;

            struct change* new_change = malloc(sizeof(struct change));
            new_change -> type = type;
            new_change -> file = malloc(sizeof(char)* MAX_FILE + 1);
            memcpy(new_change -> file, curr_file->path, sizeof(char)* (strlen(curr_file->path)) + 1);
            new_change -> new_hash = hash_file(helper, curr_file->path);
            new_change -> old_hash = curr_file -> hash ;
            curr_file -> hash = new_change -> new_hash ;
            
            
            new_change -> next = this_change ;
            new_change -> prev = prev_change ;
            prev_change -> next = new_change ;
            this_change -> prev = new_change ;
            
        }
        
        new_commit -> n_changed ++ ;

    }
}




int check_changes(void* helper, struct branch* current_branch){
    
        struct commit* prev_commit = current_branch -> head ;
        struct file* curr_file = current_branch -> files;
    
         while(curr_file != NULL){
             FILE* fp = fopen(curr_file->path, "r");
             if(fp == NULL){
                //file was deleted between commits.
                return 1;
             }
             else {
                 fclose(fp);
                 int found = 0 ;
                 if(prev_commit != NULL){
                     struct file * f1 = prev_commit -> files ;
            
                     while(f1 != NULL){
                         
                         if(strcmp(f1->path, curr_file ->path) == 0){
                             //file exists in previous commit.
                             int hash = hash_file(helper, f1->path);
                             found = 1 ;
                             
                             if(hash != f1->hash){
                                 //file was modified between commits
                                 return 1;
                             }
                         }
                         f1 = f1 -> next ;
                     }
                     if(found == 0){
                         //file was added between commits ;
                         return 1 ;
                     }
                 }
            }
             curr_file = curr_file -> next;
         }
    
         if(current_branch -> rm_files != 0){
             //There are uncommited svc_rm changes.
             return 1;
          }
    return 0;
}





void add_legacy_file(void *helper, struct file* curr_file){
    
    
    //Generate legacy file name using desired file hash.
    int length = snprintf( NULL, 0, "%d", curr_file->hash);
    char* str = malloc( length + 1 );
    snprintf(str, length + 1, "%d", curr_file->hash);
    int n_name = strlen(curr_file->path) + strlen(str) + 1;
    char*f_name = malloc(sizeof(char)*n_name);
    
    //Concantenate file path with file hash.
    strcpy(f_name, curr_file->path);
    strcat(f_name, str);
    
    //Open tracked file for reading and elgacy file for writing.
    FILE* fptr1 = fopen(curr_file -> path, "rb");
    fseek(fptr1, 0, SEEK_SET);
    FILE* fptr2 = fopen(f_name, "w+");
    fseek(fptr2, 0, SEEK_SET);
    
    
    //Copy contents from tracked to legacy.
    unsigned char byte ;
    int number = 0;
    
    fseek(fptr1,0,2);
    number = ftell(fptr1);
    fseek(fptr1,0,SEEK_SET);
    
    
    for(int i = 0 ; i <number; i ++){
        fread(&byte,1,1,fptr1);
        fwrite(&byte,1,1,fptr2);
    }
    
    fclose(fptr1);
    fclose(fptr2);
    

    //Add file to the repository.
    struct program* h = (struct program*)helper ;
    
    struct repository* repo = h->repository;
    
    //If repository is empty, create first entry.
    if(repo->legacy_files == NULL){
        
        repo->legacy_files = malloc(sizeof(struct file));
        repo->legacy_files -> next = NULL;
        repo->legacy_files-> path = malloc(sizeof(char) * (strlen(f_name) + 1 ));
        memcpy(repo-> legacy_files -> path, f_name, sizeof(char) * (strlen(f_name) + 1 ) );
        repo->legacy_files -> hash = curr_file -> hash ;
    }
    
    else{
        
        //If repository already has files, iterate to the end of the list
        //and add new legacy file
        
        struct file* rep_file = repo -> legacy_files;
        
          while(rep_file -> next != NULL){
              rep_file = rep_file -> next;
          }
          
          //Populating file struct.
          rep_file -> next = malloc(sizeof(struct file));
          rep_file -> next -> next = NULL;
          rep_file -> next -> path = malloc(sizeof(char) * (strlen(f_name) + 1 ));
          memcpy(rep_file -> next -> path, f_name, sizeof(char) * (strlen(f_name) + 1 ) );
          rep_file -> next -> hash = curr_file -> hash ;
        
    }
    
    free(f_name);
    free(str);
    
}

char *svc_commit(void *helper, char *message) {
    
    //Create a commit with the message given, and all changes to the files being tracked.
    
    
    //Check paramater
    if(message == NULL){
        return NULL;
    }
    
    
    struct program* h = (struct program*)helper ;
    struct branch * current_branch = &h->branches[curr_branch];
    
    struct commit * new_commit;
    
    //Branch is empty with no previous commits, no changes have occured.
    if(current_branch -> n_files == 0 && current_branch -> head == NULL){
        return NULL ;
    }

    //If no previous commits exist, create new commit at head.
    if(current_branch->head == NULL){
        
          current_branch -> head = malloc(sizeof(struct commit));
          new_commit = current_branch -> head ;
          new_commit -> prev = NULL ;
          new_commit -> changes = NULL ;
          new_commit -> n_changed = 0 ;
          new_commit -> files = NULL ;
          current_branch -> first_commit = current_branch -> head ;
          new_commit -> merge_parents = NULL;

        
    }

    else{
        // Else create commit to be stored appriopriately.
        new_commit = malloc(sizeof(struct commit));
        new_commit -> prev = current_branch -> head ;
        current_branch -> head = new_commit ;
        new_commit -> changes = NULL ;
        new_commit -> n_changed = 0 ;
        new_commit -> files = NULL ;
        new_commit -> merge_parents = NULL ;
        

    }

    //Construct appropriate commit fields.
    new_commit -> message = malloc(sizeof(char)* (strlen(message) + 1));
    memcpy(new_commit->message, message, sizeof(char)*(strlen(message) + 1));
    new_commit -> id = 0 ;
    new_commit->belongs_to = malloc(sizeof(char)* MAX_NAME + 1);
    memcpy(new_commit->belongs_to, current_branch->name, sizeof(char)* strlen(current_branch->name) + 1 );

    
    //Find commit position.
    if(new_commit -> prev == NULL){
        
        //this is the first commit
        struct file* curr_file = current_branch -> files;

        struct file* previous = NULL ;
        
        
        //Iterate through current tracked files.
        while(curr_file != NULL){
            
            //Default type is 'add' for initial commit.
            
            int type = 1;
            
            FILE* fp = fopen(curr_file->path, "r");

            if(fp == NULL){
                
                //File was deleted between commits. Remove from file_list and log change.
                if(previous == NULL){
                    //first file in the list removed, update branch struct.
                    current_branch -> files = curr_file -> next;
                }
                
                else{
                    //cut file out of branch list.
                    previous -> next = curr_file -> next ;
                }
                
                struct file* temp = curr_file;
                
                curr_file = curr_file -> next ;
                                
                current_branch -> n_files -- ;
                
                free(temp->path);
                free(temp);
                
                //Do not log change as this is first commit and file was added then deleted
                // during staging.
                continue ;
                
                
            }
            
            
            //Create appropriate change for current file and store it in new commit
            make_change(helper, curr_file, new_commit, type);
           
            //If change is 'modification' or 'add' type, store the file contents in the repository.
            if(type == 1 || type == 2){
                add_legacy_file(helper, curr_file);
            }
            
            //Iterate through file list.
            previous = curr_file;
            curr_file = curr_file -> next;
        }
        
        
        //Generate and return hash for the commit
        int id = get_commit_id(new_commit);
        
        //New_commit -> id = hash ;
        char charbuf[10];
        memset(charbuf, 0, 10);
        snprintf(charbuf, 10, "%06x", id);
        new_commit -> id = malloc(sizeof(char)* strlen(charbuf) + 1);
        memcpy(new_commit -> id, charbuf, sizeof(char) * strlen(charbuf) + 1);
        
        
        //Copy current tracked files into commit's stored files.
        new_commit -> files = malloc(sizeof(struct file));
        struct file* a_file  = current_branch -> files ;
        struct file* c_file  = new_commit  -> files ;
        new_commit -> n_files = current_branch -> n_files;

        while(a_file != NULL){
            c_file -> path = malloc(sizeof(char)* (strlen(a_file->path) + 1));
            memcpy(c_file->path, a_file->path, sizeof(char)*(strlen(a_file ->path) + 1));
            c_file -> hash = a_file -> hash;

            if(a_file->next != NULL){
                c_file -> next = malloc(sizeof(struct file));
                c_file = c_file -> next;
            }
            else{
                c_file -> next = NULL ;
            }
            a_file = a_file -> next;
        }
        
        //Update number of commits on branch.
        current_branch->commits++ ;
        return new_commit -> id;
    }
    
    
    
    
    else{
        
        
        //This is treated as a subsequent commit, check it belonds to current branch.
        int compare = strcmp(new_commit->prev->belongs_to, current_branch-> name);
        
        //If branch's first commit is NULL (as would be the case when branching/merging)
        // update branch pointer.
        if(compare!=0 && current_branch->first_commit == NULL){
            current_branch->first_commit = new_commit;
        }
        
        
        struct file* curr_file = current_branch -> files;
        struct commit* prev_commit = new_commit -> prev ;
        int changes = 0;
        struct file* previous = NULL ;
        
        // Iterate through tracked files to detect and log changes in commit.
        while(curr_file != NULL){
            
            int type = -1;
            FILE* fp = fopen(curr_file->path, "r");
            
            if(fp == NULL){
                
               //File was deleted between commits WITHOUT svc_rm.
               if(previous == NULL){
                  //first file in the list
                  current_branch -> files = curr_file -> next;
              }
              else{
                  previous -> next = curr_file -> next ;
              }
               //Set appropriate type, create change and increment changes.
               type = 0 ;
               changes ++ ;
               make_change(helper, curr_file, new_commit, type);
               
               struct file* temp = curr_file;
               curr_file = curr_file -> next ;
               
                //Decrement branch files as one was deleted.
               current_branch -> n_files -- ;
                
               free(temp->path);
               free(temp);
               continue ;
            }

            else {
                
                struct file * f1 = prev_commit -> files ;
                
                int found = 0 ;
                 
                while(f1 != NULL){
                    
                    if(strcmp(f1->path, curr_file ->path) == 0){
                        //the file exists in previous commit.
                        int hash = hash_file(helper, curr_file->path);
                        if(hash != f1->hash){
                            //file was modified between commits, update type.
                            type = 2 ;
                        }
                        found = 1;
                    }
                    f1 = f1 -> next ;
                }
    
                if(found == 0){
                    //File was added between commits, update type.
                    type = 1 ;
                }
            }
            
            if(type != -1){
                //A change has been detected above, add to commit changes.
                changes ++ ;
                make_change(helper, curr_file, new_commit, type);
                
                if(type == 1 || type == 2){
                    //If change is modification or addition, add to legacy files.
                    add_legacy_file(helper, curr_file);
                }

            }
            previous = curr_file;
            curr_file = curr_file -> next ;
        }
        
        
        
        struct file* rem_file = current_branch -> removed_files;
        struct file* to_free ;
        
        //Check for any files removed via SVC_RM and add them to changes
        if(current_branch -> rm_files != 0){
            
            while(rem_file != NULL){
                       changes ++ ;
                       make_change(helper, rem_file, new_commit, 0);
                       to_free = rem_file;
                       rem_file = rem_file -> next ;
                
                       //Free the allocated space in rm_files.
                       free(to_free -> path);
                       free(to_free);
                       current_branch -> rm_files -- ;
                   }
        }
        
        //If no changes have been detected, return NULL.
        if(changes == 0){
            current_branch -> head = new_commit -> prev ;
            free(new_commit -> message);
            free(new_commit -> belongs_to);
            free(new_commit);
            return NULL ;
        }
        
        
            //generate and return hash for the commit
            int id = get_commit_id(new_commit);
            
            //new_commit -> id = hash ;
            char charbuf[10];
            memset(charbuf, 0, 10);
            snprintf(charbuf, 10, "%06x", id);
            new_commit -> id = malloc(sizeof(char)* strlen(charbuf) + 1);
            memcpy(new_commit -> id, charbuf, sizeof(char) * strlen(charbuf) + 1);
            
        
            new_commit -> files = malloc(sizeof(struct file));
            struct file* a_file  = current_branch -> files ;
            struct file* c_file  = new_commit  -> files ;
            new_commit -> n_files = current_branch -> n_files;
        
            // Add all currently tracked files to commit file list.
            while(a_file != NULL){
                c_file -> path = malloc(sizeof(char)* (strlen(a_file->path) + 1));
                memcpy(c_file->path, a_file->path, sizeof(char)*(strlen(a_file ->path) + 1));
                c_file -> hash = a_file -> hash;
                if(a_file->next != NULL){
                    c_file -> next = malloc(sizeof(struct file));
                    c_file = c_file -> next;
                }
                else{
                    c_file -> next = NULL ;
                }
                a_file = a_file -> next;
            }
            current_branch->commits++ ;
            return new_commit -> id;
    }
}

void *get_commit(void *helper, char *commit_id) {
    
    //Given a commit_id, return a pointer to the area of memory you stored this commit.
    
    //Check paramter.
    if(commit_id == NULL){
        return NULL ;
    }
    
    struct program* h = (struct program*)helper ;
    struct branch* this_branch;
    
    //Iterate through branches
    for(int i = 0 ; i < h-> n_branches ; i ++){
        this_branch = &h->branches[i];
        
        //Iterate through branch commits.
        if(this_branch -> head != NULL){
            struct commit* this_commit = this_branch -> head ;
            
            while(this_commit != NULL){
                if(strcmp(this_commit->id, commit_id) == 0){

                    //Commit found, return.
                    return this_commit ;
                }
                this_commit = this_commit -> prev;
            }
        }
    }
    
    //Iterate through hidden commits.
    //These are commits which are inaccessible due to svc_reset.
    struct commit * hid_com = h -> hidden_commits ;
    
    while(hid_com != NULL){
        if(strcmp(hid_com -> id, commit_id) == 0){
            
            //Commit found, return.
            return hid_com ;
        }
        hid_com = hid_com -> prev;
    }
    
    //Commit doesn't exist, return NULL.
    return NULL ;
}
    
char **get_prev_commits(void *helper, void *commit, int *n_prev) {
    // Given a pointer to a commit, return a dynamically allocated array.
    
    
    //Check paramters
    if(n_prev == NULL){
        return NULL;
    }
    
    if(commit == NULL){
        int u = 0 ;
        memcpy(n_prev, &u , sizeof(int));
        return NULL ;
    }
    
    struct commit* cm = (struct commit*) commit ;
    char** ret_array ;
    
    // If commit is a merge commit, get merge parents.
    if(cm -> merge_parents != NULL){
        
        int n_parents = 0;
        struct commit * parent = cm->merge_parents ;
        ret_array = malloc(sizeof(char*)* 1);
        
        while(parent != NULL){
            ret_array = realloc(ret_array, sizeof(char*) * (n_parents + 1));
            ret_array[n_parents] = parent -> id  ;
            parent = parent->prev ;
            n_parents ++ ;
        }
        
        memcpy(n_prev, &n_parents, sizeof(int));
    }

    //Else, get previous commit .
    else{
    
        if(cm -> prev == NULL){
            int u = 0 ;
            memcpy(n_prev, &u , sizeof(int));
            return NULL ;
        }
    
        int n_parents = 0;
        struct commit * parent = cm->prev ;
        ret_array = malloc(sizeof(char*)* 1);
        ret_array[n_parents] = parent -> id  ;
        n_parents ++ ;
        memcpy(n_prev, &n_parents, sizeof(int));
    }
    
    return ret_array ;
}

void print_commit(void *helper, char *commit_id) {
    
    //Attempt to retrieve commit.
    struct commit* cm = get_commit(helper, commit_id);
    
    //Check if commit exists.
    if(cm == NULL){
        printf("Invalid commit id\n");
        return ;
    }
    else{
        
        //Print commit in specified format.
        printf("%s [%s]: %s\n", cm->id, cm->belongs_to, cm->message);
        struct change * this_change = cm->changes ;
        
        //Print changes
        while(this_change != NULL){
            
            if(this_change -> type == 1){
                printf("    + %s\n", this_change->file);
            }
            
            else if(this_change -> type == 0){
                printf("    - %s\n", this_change->file);
            }
            
            else if(this_change -> type == 2){
                printf("    / %s [%10d -> %10d]\n", this_change->file, this_change ->old_hash, this_change -> new_hash);
            }
            
            this_change = this_change -> next;
        }
        
        printf("\n");
        printf("    Tracked files (%d):\n", cm ->n_files);
        struct file* f = cm->files ;
        
        //Print tracked files
        while(f!= NULL){
            printf("    [%10d] %s\n", f->hash, f->path);
            f = f -> next ;
        }
        
    }
}

int svc_branch(void *helper, char *branch_name) {
    
    // Create a new branch with the given name.
    
    //Check paramter
    if(branch_name == NULL){
        return -1  ;
    }

    //Check validity of proposed name
    for(int i = 0 ; i < strlen(branch_name) ; i ++){

        if(branch_name[i] < 'a' || branch_name[i] > 'z'){
            if(branch_name[i] < 'A' || branch_name[i] > 'Z'){
                if(branch_name[i] <'0' || branch_name[i] > '9'){
                    if(branch_name[i] != '_' && branch_name[i] != '/' && branch_name[i] != '-'){
                        return -1 ;
                    }
                }
            }
        }
    }
    
    struct program* h = (struct program*)helper ;
    
    //Check if branch already exists
    for(int j = 0 ; j < h->n_branches ; j ++){
        if(strcmp(h->branches[j].name, branch_name) == 0){
            return -2 ;
        }
    }
    
    struct branch * current_branch = &h->branches[curr_branch];
    
    //Check for uncomitted changes
    if(check_changes(helper, current_branch)!= 0){
        return -3 ;
    }
    
    //All checks have passed, update number of branches and add branch
    h->n_branches ++ ;
    h->branches = realloc(h->branches, sizeof(struct branch)* h->n_branches);
    
    struct branch* new_branch = &h->branches[h->n_branches - 1];
    current_branch = &h->branches[curr_branch];
    new_branch -> id = (h->n_branches - 1);
    new_branch -> head = current_branch -> head ;
    new_branch -> first_commit = NULL;
    new_branch -> commits = 0  ;
    new_branch -> n_files = current_branch -> n_files;
    
    
    //Copy tracked files into new branch.
    if(current_branch->n_files > 0){
    new_branch -> files = malloc(sizeof(struct file));
    struct file* copied_file = new_branch -> files ;
    struct file* to_copy = current_branch -> files ;
        
        while(to_copy != NULL){
            copied_file -> path = malloc(sizeof(char)* (MAX_FILE + 1 ));
            memcpy(copied_file->path, to_copy->path, sizeof(char)* (strlen(to_copy->path) + 1));
            copied_file -> hash = to_copy -> hash ;
            if(to_copy->next != NULL){
                copied_file -> next = malloc(sizeof(struct file));
                copied_file = copied_file->next;
            }
            else{
                copied_file->next = NULL ;
            }
            to_copy = to_copy -> next ;
        }
    }
    else{
        new_branch -> files = NULL ;
    }
    
    //Finish setting up branch.
    new_branch -> removed_files = NULL ;
    new_branch -> rm_files = 0 ;
    new_branch -> name = malloc(sizeof(char)*MAX_NAME);
    memcpy(new_branch->name, branch_name, sizeof(char) * (strlen(branch_name) +1 )) ;
    
    return 0;
    
}


void recreate_branch(void* helper, struct file* recreate){
                    
        //File is a legacy file (was removed or modified) and must be recreated.
        remove(recreate->path);
        int length = snprintf( NULL, 0, "%d", recreate->hash);
        char* str = malloc( length + 1 );
        snprintf(str, length + 1, "%d", recreate->hash);
        int n_name = strlen(recreate->path) + strlen(str) + 1;
        char*f_name = malloc(sizeof(char)*n_name);
        strcpy(f_name, recreate->path);
        strcat(f_name, str);
    
        //Open legacy file using generated hash path and write contents to tracked file.
        FILE* fptr1 = fopen(f_name, "rb");
        fseek(fptr1, 0, SEEK_SET);

        FILE* fptr2 = fopen(recreate->path, "w+");
        fseek(fptr2, 0, SEEK_SET);
    
        unsigned char byte ;
    
        int number = 0;
      
        fseek(fptr1,0,2);
        number = ftell(fptr1);
        fseek(fptr1,0,SEEK_SET);
      
      
        for(int i = 0 ; i <number; i ++){
          fread(&byte,1,1,fptr1);
          fwrite(&byte,1,1,fptr2);
        }
    
        fclose(fptr1);
        fclose(fptr2);
    
        free(f_name);
        free(str);
}


int svc_checkout(void *helper, char *branch_name) {
    
    
    //Make this branch the active one.
    
    //Check parameter
    if(branch_name == NULL){
        return -1;
    }
    
    int f = 0 ;
    int index = 0 ;
    struct program* h = (struct program*)helper ;
    
    //Find branch in program
    for(int j = 0 ; j < h->n_branches ; j ++){
          if(strcmp(h->branches[j].name, branch_name) == 0){
              f =  1 ;
              index = j ;
          }
      }
    
    //If branch could not be found, return appropriate result.
    if(f == 0){
        return -1 ;
    }
    
    
    struct branch * current_branch = &h->branches[curr_branch];
    
    //If there are uncommited changes, return appropriate result.
    if(check_changes(helper, current_branch)!= 0){
        return -2 ;
    }
    
    //Update current branch
    curr_branch = index ;
    struct commit * dest_commit = h->branches[curr_branch].head;
    struct file* recreate = dest_commit -> files ;
                   
       //Reset tracked files to reflect program state of branch.
       while(recreate != NULL){
           
           //iterate files to be recreated.
           struct file* curr_file = current_branch -> files ;
           int found = 0 ;
           int recover = 0 ;
           
           while(curr_file != NULL){
               //check if the file is in current files;
               if(strcmp(curr_file -> path, recreate -> path) == 0){
                   //old file is still in tracked files within branch
                   found = 1 ;
                                      
                   if(curr_file -> hash != recreate -> hash){
                       //old file has a different hash to the current file -> it has been changed ;
                       recover = 1 ;
                   }
               }
               curr_file = curr_file -> next ;
           }
           
           if(found == 0){
               //old file is not in current files -> it was deleted or removed
               recover = 1 ;
           }
           if(recover == 1){
               // old file needs to be reset or recreated.
               recreate_branch(helper, recreate);
           }
           recreate = recreate -> next;
       }
    
    return 0;
}

char **list_branches(void *helper, int *n_branches) {
    //  Print all the branches in the order they were created.
    
    //Check parameter
    if(n_branches == NULL){
        return NULL ;
    }
    
    struct program* h = (struct program*)helper ;
    char** ret_array = malloc(sizeof(char*)* h->n_branches);
    
    //Iterate through branches, print and store.
    for(int j = 0 ; j < h->n_branches ; j ++){
        struct branch* b = &h->branches[j];
        ret_array[j] =  b -> name ;
        printf("%s\n", b->name);
    }
    
    //Copy appropriate number into result.
    memcpy(n_branches, &h->n_branches, sizeof(int));
    
    //Return array list of branches
    return ret_array;
}


int svc_add(void *helper, char *file_name) {
            
    // This is a notification that a file with the name file_name should be added to version control.
    
    
    //Check parameters.
    if(file_name == NULL){
        return -1 ;
    }
    
    struct program* h = (struct program*)helper ;
    struct branch * current_branch = &h->branches[curr_branch];
    
    //Check if the file already exists in tracked files.
    if(current_branch -> n_files > 0){
        
        struct file * file_struct = current_branch -> files ;
        while(file_struct != NULL){
            
            if(strcmp(file_struct->path, file_name) == 0 ){
                return -2 ;
            }
            file_struct = file_struct -> next ;
        }
    }
    
    //Check if file path is valid
    FILE* fp = fopen(file_name, "r");
    if(fp==NULL){
        return -3 ;
    }
    fclose(fp);
    
    
    struct file * find_file = current_branch -> removed_files;
    struct file * previous_file = NULL ;
    
    //Check if file was flagged as removed by SVC_RM
    //If so, update rm_files so that file is no longer flagged.
    if(current_branch -> rm_files != 0){
        
        while(find_file != NULL){
           
            if(strcmp(find_file->path, file_name) == 0){
                if(previous_file == NULL){
                    current_branch -> removed_files = find_file -> next ;
                }
                else{
                    previous_file -> next = find_file  -> next ;
                }
                free(find_file ->path);
                free(find_file);
                current_branch -> rm_files -- ;
                break ;
            }
            
            previous_file = find_file ;
            find_file = find_file-> next;
        }
    }
    
    //Generate hash for file
    int hash = hash_file(helper, file_name);
    
    
    //Construct file struct appropriately.
    //Add file into current branch tracked files.
    //Update number of tracked files.
    if(current_branch -> n_files == 0){
        current_branch -> n_files = 1;
        current_branch -> files = malloc(sizeof(struct file));
        current_branch -> files -> path = malloc(sizeof(char)*MAX_FILE + 1);
        current_branch -> files -> hash = hash ;
        current_branch -> files -> next = NULL ;
        memcpy(current_branch -> files-> path , file_name, sizeof(char)*(strlen(file_name) + 1 ));
        
    }
    
    else{
        current_branch -> n_files ++;
        struct file * file_struct = current_branch -> files ;
        while(file_struct->next != NULL){
            file_struct = file_struct -> next ;
        }
        
        file_struct -> next = malloc(sizeof(struct file));
        file_struct -> next -> path = malloc(sizeof(char)*MAX_FILE + 1);
        file_struct -> next -> hash = hash;
        file_struct-> next -> next = NULL ;
        memcpy(file_struct -> next -> path, file_name, sizeof(char)* (strlen(file_name) + 1));
    }
    
    //Return generated hash
    return hash;
}

int svc_rm(void *helper, char *file_name) {
//    This is a notification that a file with the name file_name should be removed from the version control system.

        // Check parameters.
        if(file_name == NULL){
            return -1 ;
        }
     
    
        struct program* h = (struct program*)helper ;
        struct branch * current_branch = &h->branches[curr_branch];

    
        //Iterate through branch files
        if(current_branch -> n_files > 0){
        
        struct file * file_struct = current_branch -> files ;
        struct file * previous = NULL;
                
        while(file_struct != NULL){
            
            if(strcmp(file_struct->path, file_name) == 0 ){
                //Found the file to remove
                struct file* removed_file;
                
                //Flag file as removed in branch->removed files.
                if(current_branch -> removed_files == NULL || current_branch -> rm_files == 0 ){
                    //First entry in branch list.
                    current_branch -> removed_files = malloc(sizeof(struct file));
                    removed_file = current_branch -> removed_files;
                }
                
                else{
                    
                    //Subsequent entry.
                    struct file* temp_file = current_branch ->removed_files;
                    while(temp_file -> next != NULL){
                        //Get file at end of list.
                        temp_file = temp_file -> next;
                    }
                    
                    temp_file -> next = malloc(sizeof(struct file));
                    removed_file = temp_file -> next;
                }
                
                //Set up removed file and increment list count.
                removed_file -> next = NULL ;
                removed_file -> hash = file_struct -> hash ;
                removed_file -> path = malloc(sizeof(char)* (strlen(file_struct -> path) + 1));
                memcpy(removed_file -> path, file_struct -> path, sizeof(char)* (strlen(file_struct -> path) + 1));
                current_branch-> rm_files ++ ;
                
                //Generate Hash
                int hash = file_struct-> hash;
                
                
                //Remove struct from tracked files.
                if(previous == NULL){
                    current_branch -> files = file_struct -> next ;
                }
                else{
                    previous -> next = file_struct -> next ;
                }
                
                free(file_struct->path);
                free(file_struct);
                current_branch -> n_files -- ;
                
                return hash ;
                
                
            }
            
            previous = file_struct;
            file_struct = file_struct -> next ;

            
        }
    }
    
    return -2;
}

void recreate_files(void* helper, struct file* recreate, int type, struct file* dest){
    
    
    //Recreate files fiven a destination pointer.
    dest->path = malloc(sizeof(char) * strlen(recreate->path) + 1);
    dest->hash = recreate->hash ;
    memcpy(dest->path, recreate->path, sizeof(char) * strlen(recreate->path) + 1);
    dest->next = NULL ;
            
    if(type == 1){
        
        // file is a legacy file (was removed or modified) and must be recreated
        remove(recreate->path);
        int length = snprintf( NULL, 0, "%d", recreate->hash);
        char* str = malloc( length + 1 );
        snprintf(str, length + 1, "%d", recreate->hash);
        int n_name = strlen(recreate->path) + strlen(str) + 1;
        char*f_name = malloc(sizeof(char)*n_name);
        strcpy(f_name, recreate->path);
        strcat(f_name, str);
        
        FILE* fptr1 = fopen(f_name, "r");
        FILE* fptr2 = fopen(recreate->path, "w+");
        
        //Copy legacy file contents into recreated file
        char c = fgetc(fptr1);
       
         while (c != EOF)
         {
             fputc(c, fptr2);
             c = fgetc(fptr1);
         }
       
       fclose(fptr1);
       fclose(fptr2);
       free(f_name);
       free(str);
        
    }
 
}

int svc_reset(void *helper, char *commit_id) {
    
    //Reset the current branch to the commit with the id given, discarding any uncommitted changes.
    
    //Commit uncommitted changes.
    svc_commit(helper, "final");
    
    //Check paramters.
    if(commit_id == NULL){
        return -1 ;
    }
    
    struct program* h = (struct program*)helper ;
    struct branch * current_branch = &h->branches[curr_branch];

       
    //Iterate through commits to find specified commit
    struct commit* this_commit = current_branch -> head ;
    struct commit* dest_commit = NULL ;
    
    while(this_commit != NULL){
        if(strcmp(this_commit->id, commit_id) == 0){
            dest_commit = this_commit;
            break;
        }
        this_commit = this_commit -> prev;
    }
    
    //If not found, return appropriate result.
    if(dest_commit == NULL){
        return -2 ;
    }
    
    //iterate files to be recreated at reset commit.
    struct file* recreate = dest_commit -> files ;
    int new_n_files = 0 ;
    struct file* new_list = NULL ;
    
    while(recreate != NULL){
        
        struct file* curr_file = current_branch -> files ;
        int found = 0 ;
        int recover = 0 ;
        
        while(curr_file != NULL){
            //check if the file is in current files;
            if(strcmp(curr_file -> path, recreate -> path) == 0){
                //Desired file is still in tracked files within branch
                found = 1 ;
                //Check if current version of file is different to desired state.
                if(curr_file -> hash != recreate -> hash){
                    //Desired file has a different hash to the current file -> it has been changed ;
                    recover = 1 ;
                }
            }
            curr_file = curr_file -> next ;
        }
        
        //Check if file was found in current files.
        if(found == 0){
            //Desired file is not in current files -> it was deleted or removed
            recover = 1 ;
        }
        
        //Create a list to store recreated files in reset commit.
        if(new_list == NULL){
            new_list = malloc(sizeof(struct file));
            new_list -> path = NULL ;
            new_list -> hash = 0 ;
            new_list -> next = NULL ;
            recreate_files(helper, recreate, recover, new_list);
        }
        
        //Add to list if list already exists.
        else{
            struct file* new_file = new_list ;
            while(new_file -> next != NULL){
                new_file = new_file -> next ;
            }
            
            //Set up file struct
            new_file-> next = malloc(sizeof(struct file));
            new_file -> next -> path = NULL ;
            new_file -> next -> hash = 0 ;
            recreate_files(helper, recreate, recover, new_file->next);
        }
        
        //Increment number of tracked files
        new_n_files ++ ;
        
        recreate = recreate -> next;
    }
    
    //Free branch -> files
    struct file* free_file = current_branch -> files ;
    struct file* placeholder ;
    
    while(free_file != NULL){
        placeholder = free_file;
        free_file = free_file -> next ;
        
        free(placeholder->path);
        free(placeholder);
    }
    
    current_branch -> n_files = new_n_files;

     
    //Change branch->files ptr
    current_branch -> files = new_list ;
    
    
    //Free branch -> head && all prev up to commit
    struct commit* com = current_branch -> head ;
    struct commit* tem;
       
    
    while(com != NULL){
        tem = com ;
        com = com -> prev ;
        if(strcmp(com->id, commit_id)==0){
            tem -> prev = NULL;
            break ;
        }
    }
    
    //Add commits which were detattched by reset to 'hidden commits' in program.
    struct commit* hidden_commit  = h -> hidden_commits;
    
    if(h -> hidden_commits == NULL){
        h -> hidden_commits = current_branch -> head ;
    }
    
    else{
        while(hidden_commit -> prev != NULL){
            hidden_commit = hidden_commit -> prev;
        }
        hidden_commit -> prev = current_branch -> head ;
    }
        
    //Change branch -> head to point to new commit
    current_branch->head = dest_commit;
    return 0;
}

int resolve(struct file* curr_file, struct resolution* resolutions, int i){
        //Given a resolution and the file to resolve, update the file.
    
       remove(curr_file->path);

       //If resolved file is NULL, leave the file deleted and move on.
       if(resolutions[i].resolved_file == NULL ){
           return 1;
       }


       //Else copy contents from resolved file to original file.
       FILE* fptr1 = fopen(resolutions[i].resolved_file, "rb");
       fseek(fptr1, 0, SEEK_SET);

       FILE* fptr2 = fopen(curr_file->path, "wb+");
       fseek(fptr2, 0, SEEK_SET);

       unsigned char byte ;
       int number = 0;
          
       fseek(fptr1,0,2);
       number = ftell(fptr1);
       fseek(fptr1,0,SEEK_SET);
          
       for(int i = 0 ; i <number; i ++){
           fread(&byte,1,1,fptr1);
           fwrite(&byte,1,1,fptr2);
       }
          
       fclose(fptr1);
       fclose(fptr2);
    
    return 0 ;
    
    
}










char *svc_merge(void *helper, char *branch_name, struct resolution *resolutions, int n_resolutions) {
    //merge the branch with the name branch_name into the current branch.
    
    
    //If branch_name is NULL, print Invalid branch name and return NULL.
    if(branch_name == NULL){
        printf("Invalid branch name\n");
        return NULL ;
    }
    
    struct program* h = (struct program*)helper ;
    struct branch* found_branch = NULL;
    int found = 0 ;
    
    
    //Find branch to merge and store a pointer to this struct.
    for(int i = 0 ; i < h -> n_branches; i ++ ){
        if(strcmp(h->branches[i].name, branch_name) == 0){
            //found the branch
            found = 1;
            found_branch  = &h->branches[i];
        }
    }
    
    //If no such branch exists, print Branch not found and return NULL.
    if(found == 0){
        printf("Branch not found\n");
        return NULL ;
    }
    
    struct branch * current_branch = &h->branches[curr_branch];
    

    //If the given name is the currently checked out branch, print Cannot merge a branch with itself and return NULL.
    if(current_branch == found_branch){
        printf("Cannot merge a branch with itself\n");
        return NULL ;
    }
    
    //If there are uncommitted changes, print Changes must be committed and return NULL.
    if(check_changes(helper, current_branch)!= 0){
        printf("Changes must be committed\n");
        return NULL;
    }
 
    
    struct file* to_add = found_branch -> files;
    
    int n_files = current_branch -> n_files ;
    
    
    //Iterate through files in merge branch and check that they are
    //appropriately represented in the currently tracked files
    while(to_add != NULL){
        struct file* curr_file = current_branch -> files ;
        int found = 0 ;
        int diff = 0 ;
        
        while(curr_file != NULL){
            if(strcmp(curr_file->path, to_add->path ) == 0 ){
                //file already exists in tracked branch;
                  for(int h = 0 ; h < n_resolutions ; h ++){
                      //Check if conflicting file is in resolutions arary
                      if(strcmp(curr_file->path, resolutions[h].file_name) == 0){
                          diff = 1;
                      }
                     
                 }
                found = 1;
                break ;
            }
            
            curr_file = curr_file -> next;
        }
        
        
        if(found == 1 && diff == 1){
            //file is in current_files, adjust entry to point to resolution file.
            for(int i = 0 ; i < n_resolutions ; i ++){
                //Iterate through resolutions to find appropriate resolution and update file.
                if(strcmp(resolutions[i].file_name, curr_file -> path) == 0){
                    int flag = resolve(curr_file, resolutions, i);
                    
                    if(flag == 1){
                        break ;
                    }
                }
            }
            
        }
        
        else if(found == 0){
            //file isnt in current_files, add to end of list.
            int deleted = 0;
            
            //Check if file has a resolution within the array.
            for(int i = 0 ; i < n_resolutions ; i ++){
                                
                if(strcmp(resolutions[i].file_name, to_add -> path) == 0){
                    remove(to_add->path);
                
                    if(resolutions[i].resolved_file == NULL ){
                        deleted = 1;
                        break ;
                    }
                
                    FILE* fptr1 = fopen(resolutions[i].resolved_file, "rb");
                    fseek(fptr1, 0, SEEK_SET);
                    FILE* fptr2 = fopen(to_add->path, "wb+");
                    fseek(fptr2, 0, SEEK_SET);
                    unsigned char byte ;
                    int number = 0;
                         
                    fseek(fptr1,0,2);
                    number = ftell(fptr1);
                    fseek(fptr1,0,SEEK_SET);
                         
                    for(int i = 0 ; i <number; i ++){
                        fread(&byte,1,1,fptr1);
                        fwrite(&byte,1,1,fptr2);
                    }
                         
                    fclose(fptr1);
                    fclose(fptr2);
                }
            }
            
            //If file wasnt deleted, add to tracked files files by copying from merge branch.
            if(deleted == 0){
                n_files ++ ;
                FILE* fp = fopen(to_add->path, "rb");
                
                //Check file to copy from merge branch is not invalid.
                if(fp == NULL){
                    recreate_branch(helper, to_add);
                }
                else{
                    fclose(fp);
                }
                //Copy from merge branch to current branch.
                svc_add(helper, to_add -> path);
            }
            
        }
        
        to_add = to_add -> next;
    }
    
    
    //Check if any extra resolutions are present.
    for(int y = 0 ; y < n_resolutions  ; y ++){
        struct file* checker = found_branch -> files ;
        struct file* ch = current_branch -> files;
        int other_finder = 0;
        int finder = 0 ;
        
        while(checker != NULL){
            if(strcmp(checker->path, resolutions[y].file_name) == 0){
                finder = 1 ;
            }
            checker = checker -> next ;
        }
        
        while(ch != NULL){
            if(strcmp(ch->path, resolutions[y].file_name) == 0){
                other_finder = 1 ;
                break;
            }
            ch = ch->next;
        }
        
        //Special case: file is already in tracked files but needs to be altered
        if(finder == 0 && other_finder == 1){
            remove(ch->path);
            if(resolutions[y].resolved_file == NULL ){
                break ;
            }

            FILE* fptr1 = fopen(resolutions[y].resolved_file, "rb");
            fseek(fptr1, 0, SEEK_SET);
            
            FILE* fptr2 = fopen(ch->path, "wb+");
            fseek(fptr2, 0, SEEK_SET);

            if(fptr1 == NULL || fptr2 == NULL){
                //Don't copy, invalid file.
            }

            else{

               unsigned char byte ;
               int number = 0;
               fseek(fptr1,0,2);
               number = ftell(fptr1);
               fseek(fptr1,0,SEEK_SET);

               for(int i = 0 ; i <number; i ++){
                   fread(&byte,1,1,fptr1);
                   fwrite(&byte,1,1,fptr2);
               }

               fclose(fptr1);
               fclose(fptr2);

            }
        }
        
    }
     
    //Create specialised merge commit message
    int length = snprintf( NULL, 0, "Merged branch %s", branch_name);
    char* str = malloc( length + 1 );
    snprintf(str, length + 1, "Merged branch %s", branch_name);
    current_branch -> n_files = n_files ;
    char* ret_id = svc_commit(helper, str);
    free(str);
    
    
    //Create a pair of special "merge parents" for current commit.
    //First copy in current branch head.
    struct commit * merge_commit = current_branch -> head ;
    struct commit* to_copy = current_branch -> head -> prev ;
    
    merge_commit -> merge_parents = malloc(sizeof(struct commit));
    merge_commit -> merge_parents -> prev = NULL;
    merge_commit -> merge_parents -> n_changed =  to_copy -> n_changed ;
    merge_commit -> merge_parents -> n_files = to_copy -> n_files ;
    merge_commit -> merge_parents -> changes = NULL ;
    merge_commit -> merge_parents -> merge_parents = NULL;
    merge_commit -> merge_parents -> id = malloc(sizeof(char)*strlen(to_copy->id) + 1);
    memcpy(merge_commit -> merge_parents -> id, to_copy -> id, sizeof(char)*strlen(to_copy->id) + 1 );
    merge_commit -> merge_parents -> message = malloc(sizeof(char)*strlen(to_copy->message) + 1);
    memcpy(merge_commit -> merge_parents -> message, to_copy -> message, sizeof(char)*strlen(to_copy->message) + 1 );
    merge_commit -> merge_parents -> belongs_to = malloc(sizeof(char)*strlen(current_branch->name) + 1);
    memcpy(merge_commit -> merge_parents -> belongs_to, current_branch->name, sizeof(char)*strlen(current_branch->name) + 1 );
    merge_commit -> merge_parents -> files = NULL ;
    
    //Second, copy in merge branch head.
    to_copy = found_branch -> head;
    struct commit * t = merge_commit -> merge_parents ;
    t -> prev = malloc(sizeof(struct commit));
    t -> prev-> prev = NULL;
    t -> prev -> n_changed =  to_copy -> n_changed ;
    t -> prev -> n_files = to_copy -> n_files ;
    t -> prev -> changes = NULL ;
    t -> prev -> merge_parents = NULL ;
    t -> prev -> id = malloc(sizeof(char)*strlen(to_copy->id) + 1);
    memcpy(t -> prev -> id, to_copy -> id, sizeof(char)*strlen(to_copy->id) + 1 );
    t -> prev -> message = malloc(sizeof(char)*strlen(to_copy->message) + 1);
    memcpy(t -> prev -> message, to_copy -> message, sizeof(char)*strlen(to_copy->message) + 1 );
    t -> prev -> belongs_to = malloc(sizeof(char)*strlen(current_branch->name) + 1);
    memcpy(t -> prev -> belongs_to, current_branch->name, sizeof(char)*strlen(current_branch->name) + 1 );
    t -> prev -> files = NULL ;
    
    //Print appropriate message
    printf("Merge successful\n");
    
    //Return commit id
    return ret_id;
}

