
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <stdint.h>
#include <pthread.h>
#include <arpa/inet.h>
#include <dirent.h>
#include <math.h>
#define READ_LENGTH 15000


uint64_t convert_to_long(uint8_t* argument){
    // Function utilising bitwise operators and bit shifting to convert an array of 8 bytes to a 64bit integer.
    uint64_t ret = (((int64_t)argument[0] << 56) & 0xFF00000000000000U)
    | (((int64_t)argument[1] << 48) & 0x00FF000000000000U)
    | (((int64_t)argument[2] << 40) & 0x0000FF0000000000U)
    | (((int64_t)argument[3] << 32) & 0x000000FF00000000U)
    | ((argument[4] << 24) & 0x00000000FF000000U)
    | ((argument[5] << 16) & 0x0000000000FF0000U)
    | ((argument[6] <<  8) & 0x000000000000FF00U)
    | (argument[7]        & 0x00000000000000FFU);
    
    return ret;
}

void cleanup_dict(unsigned char** dict, int* sizes){
    //Function to free memory allocated to the program dictionary.
    for(int c = 0 ; c < 256 ; c++){
        free(dict[c]);
    }
    free(dict);
    free(sizes);
}


struct session_data{
    //Struct to store data relating to the current session (used in file retrieval).
    uint8_t session_id[4] ;
    uint8_t session_start[8];
    uint8_t session_length[8] ;
    //finished variable is used to check if all data has been sent.
    int finished;
    uint64_t current_offset;
    uint64_t final_offset;
    struct session_data* next;
};


struct node{
    //Struct to store nodes within huffman tree.
    struct node* left_node;
    struct node* right_node;
    uint8_t byte;
    //Variable to indicate whether a byte is stored in this node.
    int indicator;
};

struct con_data {
    //Struct to store data relating to a specific connection.
    int socketfd;
    int serverfd;
    char* target_directory;
    unsigned char** dict ;
    int* sizes;
    struct node** huffman_tree;
    struct session_data** session_data;
};


void session_destroy(struct con_data* data){
    //Function to free all memory associated with the linked list of session_data structs.
    struct session_data* session_data;
    struct session_data* temp;
    session_data = (*data->session_data);
    
    while(session_data!= NULL){
        temp = session_data;
        session_data = session_data -> next;
        free(temp);
    }
}


void error_call(struct con_data* data){
    //Error call function
    uint8_t buffer[9] = {0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    write(data->socketfd, buffer, 9);
    close(data->socketfd);
    free(data);
}


void construct_tree(unsigned char** dict, int* sizes, struct node** huffman_tree){
//Function to construct huffman tree based on the given dictionary.
    uint8_t x = 0 ;
    
    for(int i = 0 ; i < 256 ; i ++){
        //One loop for each entry (256 total).
        struct node* curr_node = *huffman_tree;
        unsigned char* code = dict[i];
        int size = sizes[i];
        for(int j = 0 ; j < size ; j ++){
         //One loop for each bit within current entry.
            if(code[j] == 1){
                //If bit is a one, iterate to the right child.
                if(curr_node -> right_node == NULL){
                    //If child doesnt exist yet, make it.
                    curr_node -> right_node = malloc(sizeof(struct node));
                    curr_node -> right_node -> left_node = NULL;
                    curr_node -> right_node -> right_node = NULL;
                    curr_node -> right_node -> byte = 0;
                    curr_node -> right_node -> indicator = 0;
                    curr_node = curr_node -> right_node;
                }
                else{
                    curr_node = curr_node -> right_node;
                }
            }
            
            else if(code[j] == 0){
                //If bit is a zero, iterate to left child.
                if(curr_node -> left_node == NULL){
                    //If child doesnt exist yet, make it.
                     curr_node -> left_node = malloc(sizeof(struct node));
                     curr_node -> left_node -> left_node = NULL;
                     curr_node -> left_node -> right_node = NULL;
                     curr_node -> left_node -> byte = 0;
                     curr_node -> left_node -> indicator = 0;
                     curr_node = curr_node -> left_node;
                 }
                 else{
                     curr_node = curr_node -> left_node;
                 }
             
            }
            
            else{
                //If bit is not zero or one, something seriously wrong has occured.
                printf("Error: a bit is not zero during tree construction");
            }
        }
        
        //After loop through size is completed, place byte at this node in the tree.
        curr_node -> byte = x;
        //Set indicator variable to signify that this node stores a byte.
        curr_node -> indicator = 1;
        //Increment the byte.
        x++ ;
        
    }
    
}

void recursive_destroy(struct node* node){
    //Function to recursively free all memory contained within an implemented huffman tree.
    if(node == NULL){
        return ;
    }
    
    else{
        recursive_destroy(node->left_node);
        recursive_destroy(node->right_node);
        free(node);
    }
    
}

uint8_t compress_payload(struct con_data* data, uint8_t* payload, uint64_t true_size, unsigned char** return_holder, uint8_t* size_holder, uint64_t* offset_holder){
//Function to compress a given payload.
    uint8_t * compressed_payload = NULL;
    
    uint64_t current_offset = 0;
    
    for(int index = 0 ; index < true_size ; index++){
        //Loop through each byte in the payload.
        uint8_t this_byte = payload[index];
        
        if(compressed_payload == NULL){
            //If compression destination does not exist yet, create it.
            compressed_payload = malloc(data->sizes[this_byte]);
        }
        else{
            //If compression destination does exist, increase its size to accomodate new data.
            //Acquire size based on current byte.
            compressed_payload = realloc(compressed_payload, current_offset + data -> sizes[this_byte]);
        }
        
        //Copy the code for the current byte from the constructed dictionary into the compression destination.
        memcpy(&compressed_payload[current_offset], data->dict[this_byte], data->sizes[this_byte]);
        
        current_offset += data->sizes[this_byte];
    }
    
    //Calculate the bits remaining when converting complete bit array into bytes.
    uint8_t modulus = current_offset % 8;
    
    //Round out the required bits using padded zeros.
    uint8_t padding = 8 - modulus;
    if(padding == 8){
        padding = 0;
    }
    compressed_payload = realloc(compressed_payload, current_offset + padding);
    for(int pad = 0 ; pad < padding ; pad++){
        compressed_payload[current_offset + pad] = 0;
    }
    
    //Update the current offset.
    current_offset += padding;
    
    //Create an array to hold the resultant bytes.
    (*return_holder) = malloc(current_offset/8);
    
    memset((*return_holder), 0, current_offset/8);
    
    
    //For each set of 8 bits, convert to bytes and store in resultant array.
    for (int count =0; count<current_offset; count+=8) {
        for(int gh = 0 ; gh < 8 ; gh ++){
             (*return_holder) [(count/8)] =  (*return_holder) [count/8] + (compressed_payload[count + gh] * pow(2,(7-gh)));
        }
    }
    
    //Update offset to reflect length of BYTE array.
    current_offset = current_offset/8;
    
    //Increase length by one to account for padding length byte.
    current_offset = current_offset + 1;
    
    //Convert length from 64bit integer to array of 8bit intergers.
    uint8_t out[8] = {current_offset >>56,current_offset >>48,current_offset >>40,current_offset >>32,current_offset >>24,current_offset >>16,current_offset >>8,current_offset};
    
    //Memcpy results into parameter arrays to be used outside the current function.
    memcpy(size_holder, out,sizeof(uint8_t)*8);
    memcpy(offset_holder, &current_offset,sizeof(uint64_t));
    
    free(compressed_payload);

    //Return amount of padding used.
    return padding;
}



void* echo(struct con_data* data, int to_compress, int compressed, uint64_t true_size, uint8_t* payload_size){
//Function to execute Echo Command
    uint8_t * payload = malloc(true_size);
    int r = 0;
    
    //Recieve the payload.
    if((r = recv(data->socketfd, payload, true_size, MSG_WAITALL)) < 1){
        free(payload);
        error_call(data);
        return NULL ;
    }
    
    uint8_t res_type;
    
    if(to_compress == 1 && compressed == 0){
        //If compression is required and payload is not compressed, compress payload using previous compress_payload function.
        
        res_type = 0x18 ;
        
        unsigned char** return_payload = malloc(sizeof(char*));
        uint8_t*  size_holder = malloc(sizeof(uint8_t)*8);
        
        uint64_t* current_offset  = malloc(sizeof(uint64_t));

        uint8_t padding = compress_payload(data,payload,true_size, return_payload, size_holder, current_offset);
        
        //Send result to client.
        write(data->socketfd, &res_type, 1);

        write(data->socketfd, size_holder, 8);
        
        write(data->socketfd, (*return_payload), (*current_offset) -1);
        
        write(data->socketfd, &padding, 1);
        
        
        //Free allocated data.
        free(size_holder);
        
        free(current_offset);
        
        free((*return_payload));
        
        free(return_payload);
        
        free(payload);
        
    }
    
    else if((to_compress == 1 && compressed == 1) || (to_compress == 0 && compressed == 1)){
        //If payload is compressed and can be returned as is, set the appropriate typebits and send to client
        res_type = 0x18 ;
        write(data->socketfd, &res_type, 1);
        write(data->socketfd, payload_size, 8);
        write(data->socketfd, payload, true_size);
        free(payload);
    }
    
    else{
        //If payload is not compressed and does not need to be, return to client.
        res_type = 0x10 ;
        write(data->socketfd, &res_type, 1);
        write(data->socketfd, payload_size, 8);
        write(data->socketfd, payload, true_size);
        free(payload);
    }
    
    return NULL ;
    
}

void* list_directories(struct con_data* data, int to_compress, int compressed, uint64_t true_size, uint8_t* payload_size){
//Function to execute the List Directories command.
             DIR * dir = opendir(data -> target_directory);
             
             if(dir == NULL){
                 printf("couldnt open directory");
             }
             
             //Initialise struct to deal with directory contents.
             struct dirent * dirEnt;
             char** sub_list =  NULL;
             int i =  0;
             uint64_t size = 0;
    
             while ((dirEnt = readdir(dir)) != NULL){
                 //Loop through directory contents.
                 
                 if(dirEnt-> d_type != DT_DIR){
                     //If Contents is of appropriate type, add to sub_directories list (sub_list)
                      if(sub_list == NULL){
                          //If sub list hasn't been created, allocate it memory.
                          sub_list = malloc(sizeof(char*));
                          sub_list[i] = malloc(sizeof(char) * strlen(dirEnt->d_name) + 1);
                          memcpy(sub_list[i], dirEnt -> d_name, sizeof(char) * strlen(dirEnt->d_name) + 1);
                          size += sizeof(char) * strlen(dirEnt -> d_name) + 1;
                      }
                      else{
                          //Else, increase size of sub list to include current sub_directory.
                          sub_list = realloc(sub_list, sizeof(char*) * (i + 1));
                          sub_list[i] = malloc(sizeof(char) * strlen(dirEnt->d_name) + 1);
                          memcpy(sub_list[i], dirEnt -> d_name, sizeof(char) * strlen(dirEnt->d_name) + 1);
                          size += sizeof(char) * strlen(dirEnt -> d_name) + 1;
                      }
                      i++ ;
                 }
             }
             
             
             if(to_compress == 1 && compressed == 0){
                 //If payload requires compression, set appropriate type bit and utilise a variation of the compress_payload function to compress datqa.
                 uint8_t res_type = 0x38;
                 uint8_t * compressed_payload = NULL;
                 uint64_t current_offset = 0;
        
                 for(int index = 0 ; index < i ; index++){
                 //Loop through length of sub list.
                         for(int dir_index = 0 ; dir_index < strlen(sub_list[index]) + 1; dir_index ++) {
                            //Loop through current entry (+1 for appended null byte)
                            uint8_t this_byte = sub_list[index][dir_index];
                             
                             //Add bits to compressed payload (as in compress_payload function).
                              if(compressed_payload == NULL){
                                  compressed_payload = malloc(data->sizes[this_byte]);
                              }
                              else{
                                  compressed_payload = realloc(compressed_payload, current_offset + data -> sizes[this_byte]);
                              }
                              memcpy(&compressed_payload[current_offset], data->dict[this_byte], data->sizes[this_byte]);
                              current_offset += data->sizes[this_byte];
                       }
                 }
                 
                 //Send client the return type byte.
                 write(data->socketfd, &res_type, 1);
    
                 //Calculate and append padding.
                 uint8_t modulus = current_offset%8;
                 uint8_t padding = 8 - modulus;
                 if(padding == 8){
                     padding = 0;
                 }
                 compressed_payload = realloc(compressed_payload, current_offset + padding);
                 for(int pad = 0 ; pad < padding ; pad++){
                     compressed_payload[current_offset + pad] = 0;
                 }
                 current_offset += padding ;
                 
                 //Create return payload to hold compressed bytes.
                 unsigned char* return_payload = malloc(current_offset/8 + i);
                 memset(return_payload, 0, current_offset/8 + i);
                                   
                 //For each set of 8bits contained within compressed_payload, convert to byte to be stored in return pointer.
                 for (int count =0; count<current_offset; count+=8) {
                     for(int gh = 0 ; gh < 8 ; gh ++){
                         return_payload[(count/8)] = return_payload[count/8] + (compressed_payload[count + gh] * pow(2,(7-gh)));
                     }
                                                   
                 }
                 
                 current_offset = current_offset/8;
                 current_offset = current_offset + 1;
                 uint8_t out[8] = {current_offset >>56,current_offset >>48,current_offset >>40,current_offset >>32,current_offset >>24,current_offset >>16,current_offset >>8,current_offset};
                 
                 //Send to client.
                 write(data->socketfd, out, 8);
                 write(data->socketfd, return_payload, current_offset -1);
                 write(data->socketfd, &padding, 1);
                 
                 //Free allocated memory.
                 free(compressed_payload);
                 free(return_payload);
                 for(int j = 0 ; j < i ; j ++){
                       free(sub_list[j]);
                 }
                 free(sub_list);
             }
             
             else if(to_compress == 1 && compressed == 1){
                 //If payload requires compression and is compressed, send to client.
                 uint8_t type = 0x38;
                 uint8_t out[8] = {size>>56,size>>48,size>>40,size>>32,size>>24,size>>16,size>>8,size};
                 write(data->socketfd, &type, 1);
                 write(data->socketfd, out, 8);
                 
                 //Free allocated memory.
                 for(int j = 0 ; j < i ; j ++){
                     write(data->socketfd, sub_list[j], sizeof(char)*(strlen(sub_list[j]) + 1));
                     free(sub_list[j]);
                 }
                 free(sub_list);
             }
             
             else{
                 //If payload is not compressed and does not require compression, send to client.
                 uint8_t type = 0x30;
                 uint8_t out[8] = {size>>56,size>>48,size>>40,size>>32,size>>24,size>>16,size>>8,size};
                 write(data->socketfd, &type, 1);
                 write(data->socketfd, out, 8);
                 
                 //Free allocated memory
                 for(int j = 0 ; j < i ; j ++){
                     write(data->socketfd, sub_list[j], sizeof(char)*(strlen(sub_list[j]) + 1));
                     free(sub_list[j]);
                 }
                 free(sub_list);
             }
    
             //Close directory
             closedir(dir);
             return NULL;
    
}

int find_filesize(struct con_data* data, int to_compress, int compressed, uint64_t true_size, uint8_t* payload_size){
//Function to execute file_size command
    
                unsigned char* payload = malloc(true_size);
                int r = 0;
                             
                //Retrieve payload.
                if((r = recv(data->socketfd, payload, true_size, MSG_WAITALL)) < 1){
                     free(payload);
                     error_call(data);
                     return -1 ;
                }
                
                //Construct absolute path to given file.
                char* absolute_path = malloc(sizeof(char) * (strlen((char*)data->target_directory) + strlen((char*)payload)+ 2) );
                memcpy(absolute_path, data->target_directory, strlen(data->target_directory));
                char slash = '/';
                int string_size = strlen(data->target_directory);
                memcpy(&absolute_path[string_size], &slash, 1);
                memcpy(&absolute_path[string_size + 1], payload, true_size);
                
                //Validate and open file.
                FILE* sizeptr = fopen(absolute_path, "r");
                if(sizeptr == NULL){
                    free(absolute_path);
                    free(payload);
                    error_call(data);
                    return -1 ;
                }
                
                //Acquire total file size.
                fseek(sizeptr, 0, SEEK_END);
                uint64_t file_size = ftell(sizeptr);
                
                //Free allocated memory.
                free(absolute_path);
                free(payload);
                
                if(!(to_compress == 1 && compressed == 0)){
                    //If payload does not need to be compresed, send to client.
                    uint8_t f_size[8] = {file_size>>56,file_size>>48,file_size>>40,file_size>>32,file_size>>24,file_size>>16,file_size>>8,file_size};
                    uint8_t type = 0 ;
                    
                    if(to_compress  == 0 && compressed == 0){
                        type = 0x50;
                    }
                    else{
                        type = 0x58;
                    }

                    uint64_t size = 8 ;
                    uint8_t out[8] = {size>>56,size>>48,size>>40,size>>32,size>>24,size>>16,size>>8,size};
                    write(data->socketfd, &type, 1);
                    write(data->socketfd, out, 8);
                    write(data->socketfd, f_size, 8);
                    return 0;

                }
                
                else{
                   //If payload requires compression, compress with compression function and send to client.
                   uint8_t res_type = 0x58;
                   uint8_t payload[8] = {file_size>>56,file_size>>48,file_size>>40,file_size>>32,file_size>>24,file_size>>16,file_size>>8,file_size};
                   unsigned char** return_payload = malloc(sizeof(char*));
                   uint8_t*  size_holder = malloc(sizeof(uint8_t)*8);
                   uint64_t* current_offset  = malloc(sizeof(uint64_t));
                   uint8_t padding = compress_payload(data,payload,8, return_payload, size_holder, current_offset);
                   write(data->socketfd, &res_type, 1);
                   write(data->socketfd, size_holder, 8);
                   write(data->socketfd, (*return_payload), (*current_offset) -1);
                   write(data->socketfd, &padding, 1);
                   
                   //Free allocated memory.
                   free(size_holder);
                   free(current_offset);
                   free((*return_payload));
                   free(return_payload);
                   return 0 ;
                }
}


struct session_data* check_session(struct con_data* data, uint8_t* session_id, uint8_t* starting_offset, uint8_t* retrieve_length, unsigned char* payload, int to_compress){
//Function to check for an existing session and, if not found, create new session and return appropriate pointer.
        struct session_data* session_data = (*data->session_data);
        struct session_data* prev_session = NULL ;
    
        while(session_data != NULL){
        //Iterate through shared memory to check for a matching sessionID and file range.
            int found = 1;
            for(int ind = 0 ; ind < 4 ; ind ++){
                if((session_data -> session_id)[ind] != session_id[ind]){
                    //If current sessionID byte does not match expected, indicate found = 0.
                    found = 0;
                    break;
                }
            }
            
            for(int x = 0 ; x < 8 ; x ++){
                if(session_data -> session_start[x] != starting_offset[x]){
                    //If current session_start byte does not match expected, indicate found = 0.
                    found = 0 ;
                    break ;
                }
                
                if(session_data -> session_length[x] != retrieve_length[x]){
                    //If current session_length byte does not match expected, indicate found = 0.
                    found = 0 ;
                    break;
                }
            }
            
            if(found == 1){
            //If a matching session is found, return it's data.
               return session_data;
            }
            
            prev_session = session_data;
            session_data = session_data -> next;
        }
        
    
        if(prev_session == NULL){
         //If this is the first session to be created in shared memory, allocate it using the pointer stored in the data struct.
            (*data->session_data) = malloc(sizeof(struct session_data));
            memcpy((*data->session_data)->session_id, session_id, sizeof(uint8_t)*4);
            memcpy((*data->session_data)->session_start, starting_offset, sizeof(uint8_t)*8);
            memcpy((*data->session_data)->session_length, retrieve_length, sizeof(uint8_t)*8);
            (*data->session_data)-> next = NULL ;
            prev_session  = (*data->session_data);
            prev_session -> finished = 0 ;
            prev_session -> current_offset = convert_to_long(prev_session -> session_start);
            uint64_t read_len = convert_to_long(prev_session -> session_length);
            prev_session -> final_offset = prev_session -> current_offset + read_len;
        }
    
        else{
            //If this is a subsequent session to be added to shared memory, allocate it using the pointer of the previous session.
            prev_session -> next = malloc(sizeof(struct session_data));
            memcpy(prev_session -> next ->session_id, session_id, sizeof(uint8_t)*4);
            memcpy(prev_session -> next ->session_start, starting_offset, sizeof(uint8_t)*8);
            memcpy(prev_session -> next ->session_length, retrieve_length, sizeof(uint8_t)*8);
            prev_session -> next -> next = NULL;
            prev_session  = prev_session -> next;
            prev_session -> finished = 0 ;
            prev_session -> current_offset = convert_to_long(prev_session -> session_start);
            uint64_t read_len = convert_to_long(prev_session -> session_length);
            prev_session -> final_offset = prev_session -> current_offset + read_len;
        }
    
        //Return created session.
        return prev_session;
    
}



int retrieve_file(struct con_data* data, int to_compress, int compressed, uint64_t true_size, uint8_t* payload_size){
//Function to execute the retieve file command.
        int r = 0 ;

        if(compressed == 0){
        //If payload is not compressed, proceed without decompression
                
                //Allocate and retrieve required fields.
                unsigned char* payload = malloc(true_size - 20);
                uint8_t* session_id = malloc(sizeof(uint8_t)* 4);
                uint8_t* starting_offset = malloc(sizeof(uint8_t)*8);
                uint8_t* retrieve_length = malloc(sizeof(uint8_t)*8);

                if((r = recv(data->socketfd, session_id, 4, MSG_WAITALL)) < 1){
                       free(payload);
                       error_call(data);
                       return -1 ;
                }
            
                if((r = recv(data->socketfd, starting_offset, 8, MSG_WAITALL)) < 1){
                       free(payload);
                       error_call(data);
                       return -1  ;
                }
                 
                if((r = recv(data->socketfd, retrieve_length, 8, MSG_WAITALL)) < 1){
                       free(payload);
                       error_call(data);
                       return -1  ;
                }
                              
                if((r = recv(data->socketfd, payload, true_size - 20, MSG_WAITALL)) < 1){
                      free(payload);
                      error_call(data);
                      return -1  ;
                }
            
            int checker = 0 ;
                        
            while(1){
            //Continuosly loop, reading READ_LENGTH bytes at a time from file and sending to client until total retrieve length is read and sent.
            
                //Obtain session data from database of sessions.
                struct session_data* q = check_session(data, session_id, starting_offset, retrieve_length, payload, to_compress);
                
                if(q -> finished == 1 && checker == 0){
                    //If session is finished sending, send empty payload.
                    uint8_t res_type = 0;
                    if(to_compress == 0){
                         res_type = 0x70;
                    }
                    else{
                          res_type = 0x78;
                    }
                    uint64_t total_size = 0;
                    uint8_t t_size[8] = { total_size >>56, total_size >>48, total_size >>40, total_size >>32, total_size >>24, total_size >>16, total_size >>8, total_size};
                    write(data->socketfd, &res_type, 1);
                    write(data->socketfd, t_size, 8);
                    
                    //Free allocated memory.
                    free(payload);
                    free(session_id);
                    free(starting_offset);
                    free(retrieve_length);
                    free(data);
                    //Return appropriate result.
                    return - 1;
                    
                }
                
                else if(q->finished == 1 && checker > 0){
                    break ;
                }
            
                 //Set start to be the current offset associated with the current session.
                 uint64_t start = q -> current_offset;
                 //Set read_length to be appropriate amount of bytes.
                 uint64_t read_length = READ_LENGTH;
                 
                //If read_length goes beyond file, update to appropriate length.
                 if(start + read_length > q -> final_offset){
                     read_length = q -> final_offset - start ;
                 }
                 
                 //Update session data to count the currently selected byte range as read.
                 q -> current_offset += read_length;
                 
                 // If this is the last lot of data to be read, update session to be finished.
                 if(q -> current_offset >= q -> final_offset){
                    q -> finished = 1;
                 }
                
                 //Generate absolute path to file.
                 char* absolute_path = malloc(sizeof(char) * (strlen((char*)data->target_directory) + strlen((char*)payload)+ 2));
                 memcpy(absolute_path, data->target_directory, strlen(data->target_directory));
                 char slash = '/';
                 int string_size = strlen(data->target_directory);
                 memcpy(&absolute_path[string_size], &slash, 1);
                 memcpy(&absolute_path[string_size + 1], payload, strlen((char*)payload) + 1);
 
                 //Open and validate file.
                 FILE* readptr = fopen(absolute_path, "rb");
                 if(readptr == NULL){
                     free(absolute_path);
                     free(payload);
                     free(session_id);
                     free(starting_offset);
                     free(retrieve_length);
                     error_call(data);
                     return -1  ;
                 }
                
                 //Check the range to read is valid.
                fseek(readptr, 0, SEEK_END);
                uint64_t file_size = ftell(readptr);
                uint64_t true_start = convert_to_long(starting_offset);
                uint64_t true_read = convert_to_long(retrieve_length);
                 if(true_start + true_read > file_size){
                     free(absolute_path);
                     free(payload);
                     free(session_id);
                     free(starting_offset);
                     free(retrieve_length);
                     error_call(data);
                     return -1  ;
                 }
                 fseek(readptr,start, SEEK_SET);
                 
                 //Read in appropriate byte range from file.
                 uint64_t real_length = read_length;
                 unsigned char* bytes = malloc(real_length);
                 fread(bytes,real_length, 1, readptr);
                 
                 //Set up appropriate arrays for payload size, read length and staring offset.
                 uint64_t total_size = real_length + 20 ;
                 uint8_t t_size[8] = { total_size >>56, total_size >>48, total_size >>40, total_size >>32, total_size >>24, total_size >>16, total_size >>8, total_size};
                 uint8_t r_size[8] = { real_length >>56, real_length >>48, real_length >>40, real_length >>32, real_length >>24, real_length >>16, real_length >>8, real_length};
                 uint8_t s_off[8] = { start >>56, start >>48, start >>40, start >>32, start >>24, start >>16, start >>8, start};

                
                
                if(to_compress == 0){
                //If data need not be compressed, simply send to client.
                       uint8_t res_type = 0x70;
                       write(data->socketfd, &res_type, 1);
                       write(data->socketfd, t_size, 8);
                       write(data->socketfd, session_id, 4);
                       write(data->socketfd, s_off, 8);
                       write(data->socketfd, r_size, 8);
                       write(data->socketfd, bytes, real_length);
                }
                
                else{
                //Compress data using compression function and send to client.
                      uint8_t* full_payload = malloc(total_size);
                      memcpy(&full_payload[0], session_id, 4);
                      memcpy(&full_payload[4], s_off, 8);
                      memcpy(&full_payload[12], r_size, 8);
                      memcpy(&full_payload[20], bytes, real_length);
                      
                      uint8_t res_type = 0x78;
                      unsigned char** return_payload = malloc(sizeof(char*));
                      uint8_t*  size_holder = malloc(sizeof(uint8_t)*8);
                      uint64_t* current_offset  = malloc(sizeof(uint64_t));
                      uint8_t padding = compress_payload(data,full_payload,total_size, return_payload, size_holder, current_offset);
                      
                      write(data->socketfd, &res_type, 1);
                      write(data->socketfd, size_holder, 8);
                      write(data->socketfd, (*return_payload), (*current_offset) -1);
                      write(data->socketfd, &padding, 1);
                      
                      //Free allocated memory.
                      free(size_holder);
                      free(current_offset);
                      free((*return_payload));
                      free(return_payload);
                      free(full_payload);
                }
                
                //Free memory allocated within current loop.
                free(absolute_path);
                free(bytes);
                
                if(q->finished == 1){
                    //If the current session is finished sending data, break out of the while loop.
                    break ;
                }
                
                checker ++ ;
                
            }
                 //Free memory allocated prior to while loop.
                 free(payload);
                 free(session_id);
                 free(starting_offset);
                 free(retrieve_length);
                
            }
            
            else{
            //Need to decompress.
                                  
                unsigned char* pl = malloc(true_size);
                if((r = recv(data->socketfd, pl, true_size, MSG_WAITALL)) < 1){
                    free(pl);
                    error_call(data);
                    return -1  ;
                }
                
               //Obtain padding length.
               uint8_t padding = pl[true_size - 1];
               //Obtain length without padding.
               uint64_t nobit = ((true_size -1)*8);
               //Create array to hold all bits, excluding padding bits.
               unsigned char * bits = malloc(nobit);
                                                        
               //Copy bits from payload to bit array.
               for (int b =0; b < nobit; b++){
                   bits[b] = ((1 << (b % 8)) & (pl[b/8])) >> (b % 8);
               }
                
               //Account for endianess and reverse bits.
               unsigned char* ordered_bits = malloc(nobit);
               int reverse = -1;
               for(int u = 0 ; u < nobit - 8; u++){
                   if(u%8 == 0){
                       reverse = u*2 + 7 ;
                   }
                   ordered_bits[u] = bits[reverse - u];
               }
                
                
                unsigned char* decompressed_pl = NULL;
                uint64_t pl_size = 0 ;
                struct node* this_node = *(data->huffman_tree);
                
                
                //Utilise huffman tree to decompress bits into usable payload.
                for(int k = 0 ; k < nobit - padding; k++){
                    
                    if(this_node == NULL){
                        printf("errored 1 :-(\n");
                        exit(0);
                    }
                    
                    if(ordered_bits[k] == 1){
                        //If bit == 1, iterate to the right child.
                        this_node = this_node -> right_node;
                    }
                    
                    else{
                        //If bit == 0, iterate to the left child.
                        this_node = this_node -> left_node;
                    }
                    
                    if(this_node == NULL){
                        printf("errored 2 :-(\n");
                        exit(0);
                    }
                    
                    if(this_node -> indicator == 1){
                    //If this node holds a byte, complete the decoding sequence and update the decompressed payload.
                        if(decompressed_pl == NULL){
                            decompressed_pl = malloc(sizeof(this_node -> byte));
                        }
                        else{
                            decompressed_pl = realloc(decompressed_pl, (pl_size + 1)*sizeof(this_node -> byte));
                        }
                        memcpy(&decompressed_pl[pl_size], &this_node -> byte, sizeof(this_node -> byte));
                        pl_size ++ ;
                        
                        //Restard the sequence for the next set of bits.
                        this_node = *(data->huffman_tree);
                    }
                    
                }

                //Allocate and retrieve appropriate fields from the decompressed payload.
                unsigned char* payload = malloc(pl_size - 20);
                unsigned char* session_id = malloc(sizeof(unsigned char)* 4 );
                unsigned char* starting_offset = malloc(sizeof(unsigned char)*8);
                unsigned char* retrieve_length = malloc(sizeof(unsigned char)*8);
                
                memcpy(session_id, decompressed_pl, sizeof(unsigned char)* 4 );
                memcpy(starting_offset, &decompressed_pl[4], sizeof(unsigned char)* 8);
                memcpy(retrieve_length, &decompressed_pl[12],  sizeof(unsigned char)* 8);
                memcpy(payload, &decompressed_pl[20], sizeof(unsigned char)* (pl_size - 20));
                
                int checker = 0 ;
                
                while(1){
                    //This loop is a slightly altered version of the previous.
                    struct session_data* q = check_session(data, session_id, starting_offset, retrieve_length, payload, to_compress);
                    
                    if(q -> finished == 1 && checker == 0){
                        uint8_t res_type = 0;
                        if(to_compress == 0){
                             res_type = 0x70;
                        }
                        else{
                              res_type = 0x78;
                        }
                        uint64_t total_size = 0;
                        uint8_t t_size[8] = { total_size >>56, total_size >>48, total_size >>40, total_size >>32, total_size >>24, total_size >>16, total_size >>8, total_size};
                        write(data->socketfd, &res_type, 1);
                        write(data->socketfd, t_size, 8);
                        free(payload);
                        free(session_id);
                        free(starting_offset);
                        free(retrieve_length);
                        free(data);
                        return - 1;
                    }
                    
                    else if(q->finished == 1 && checker > 0){
                        break ;
                    }
                    
                     uint64_t start = q -> current_offset;
                     uint64_t read_length = READ_LENGTH;
                     if(start + read_length > q -> final_offset){
                         read_length = q -> final_offset - start ;
                     }
                    q -> current_offset += read_length;
                    if(q -> current_offset >= q -> final_offset){
                        q -> finished = 1;
                    }
                    unsigned char n_byte = '\0' ;
                    memcpy(&payload[pl_size -21], &n_byte, 1);
                    char* absolute_path = malloc(sizeof(char) * (strlen((char*)data->target_directory) + strlen((char*)payload)+ 2));
                    memcpy(absolute_path, data->target_directory, strlen(data->target_directory));
                    char slash = '/';
                    int string_size = strlen(data->target_directory);
                      
                     memcpy(&absolute_path[string_size], &slash, 1);
                     memcpy(&absolute_path[string_size + 1], payload, strlen((char*)payload) + 1);
                     FILE* readptr = fopen(absolute_path, "rb");
                     if(readptr == NULL){
                         free(absolute_path);
                         free(payload);
                         free(session_id);
                         free(starting_offset);
                         free(retrieve_length);
                         free(ordered_bits);
                         free(bits);
                         free(pl);
                         free(decompressed_pl);
                         error_call(data);
                         return -1;
                     }
                     fseek(readptr, 0, SEEK_END);
                     uint64_t file_size = ftell(readptr);
                     
                     if(start + read_length > file_size){
                         free(absolute_path);
                         free(payload);
                         free(session_id);
                         free(starting_offset);
                         free(retrieve_length);
                         free(ordered_bits);
                         free(bits);
                         free(pl);
                         free(decompressed_pl);
                         error_call(data);
                         return -1;
                     }
                     
                     fseek(readptr,start, SEEK_SET);
                     uint64_t real_length = read_length;
                     unsigned char* bytes = malloc(real_length);
                     fread(bytes,real_length, 1, readptr);
                     uint64_t total_size = real_length + 20;
                     uint8_t t_size[8] = { total_size >>56, total_size >>48, total_size >>40, total_size >>32, total_size >>24, total_size >>16, total_size >>8, total_size};
                     uint8_t r_size[8] = { real_length >>56, real_length >>48, real_length >>40, real_length >>32, real_length >>24, real_length >>16, real_length >>8, real_length};
                     uint8_t s_off[8] = { start >>56, start >>48, start >>40, start >>32, start >>24, start >>16, start >>8, start};
                  
                    if(to_compress == 0){
                               uint8_t res_type = 0x70;
                               write(data->socketfd, &res_type, 1);
                               write(data->socketfd, t_size, 8);
                               write(data->socketfd, session_id, 4);
                               write(data->socketfd, s_off, 8);
                               write(data->socketfd, r_size, 8);
                               write(data->socketfd, bytes, real_length);
                    }
                    
                    else{
                              uint8_t* full_payload = malloc(total_size);
                              memcpy(&full_payload[0], session_id, 4);
                              memcpy(&full_payload[4], s_off, 8);
                              memcpy(&full_payload[12], r_size, 8);
                              memcpy(&full_payload[20], bytes, real_length);
                              uint8_t res_type = 0x78;
                              unsigned char** return_payload = malloc(sizeof(char*));
                              uint8_t*  size_holder = malloc(sizeof(uint8_t)*8);
                              uint64_t* current_offset  = malloc(sizeof(uint64_t));
                              uint8_t padding = compress_payload(data,full_payload,total_size, return_payload, size_holder, current_offset);
                              write(data->socketfd, &res_type, 1);
                              write(data->socketfd, size_holder, 8);
                              write(data->socketfd, (*return_payload), (*current_offset) -1);
                              write(data->socketfd, &padding, 1);
                              free(size_holder);
                              free(current_offset);
                              free((*return_payload));
                              free(return_payload);
                              free(full_payload);
                        }
                    
                        free(absolute_path);
                        free(bytes);
                        if(q -> finished == 1){
                            break ;
                        }
                    
                    checker ++ ;
                }
                     free(payload);
                     free(session_id);
                     free(starting_offset);
                     free(retrieve_length);
                     free(bits);
                     free(ordered_bits);
                     free(pl);
                     free(decompressed_pl);
            }
        return 1 ;
}



void* thread_handler(void* arg) {
//Functions to handle all threads created by main().
    
    int looping = 0;
    struct con_data* data = (struct con_data*) arg;
    
    while((looping = recv(data->socketfd, NULL, 1, MSG_PEEK)) != 0){
    //Loop through all data sent by client until client ceases sending
          uint8_t header;
          int r ;
        
          //Read in header byte
          if((r = recv(data->socketfd, &header, 1, MSG_WAITALL)) < 1){
              error_call(data);
              return NULL ;
          }
          
          //Extract type and compression bits
          unsigned char type_bits = ((header) >> 4) & 0x0F;
          int to_compress = (header >> 2) & 0x01;
          int compressed = (header >> 3) & 0x01;
          
          //Extract padding bits.
          int pad_1 = (header >> 1) & 0x01;
          int pad_2 = (header >> 0) & 0x01;
        
          //Assert valid padding
          if(pad_1 != 0 || pad_2 != 0){
              error_call(data);
              return NULL;
          }
          
          //Read in payload size
          uint8_t payload_size[8];
          if((r = recv(data->socketfd, payload_size, 8, MSG_WAITALL)) < 1){
              error_call(data);
              return NULL;
          }
         uint64_t true_size = convert_to_long(payload_size);
                    
        
          if(type_bits == 0){
              //Echo command
              echo(data, to_compress, compressed, true_size, payload_size);
          }
        
          else if(type_bits == 0x8){
              //Shutdown Command
              close(data->socketfd);
              shutdown(data->serverfd, SHUT_RD);
              recursive_destroy((*(data->huffman_tree)));
              session_destroy(data);
              free(data->session_data);
              free(data->huffman_tree);
              free(data);
              exit(0);
          }
        
          else if(type_bits == 0x2){
              //List directories Command
              list_directories(data, to_compress, compressed, true_size, payload_size);
              
          }
        
          else if(type_bits == 0x4){
              //Find file_size command
              int success = find_filesize(data, to_compress, compressed, true_size, payload_size);
              if(success == -1){
                  return NULL;
              }
          }
        
          else if(type_bits == 0x6){
              //Retrieve file command
              int success = retrieve_file(data, to_compress, compressed, true_size, payload_size);
              if(success == -1){
                  return NULL;
              }
          }
        
          else{
              //Type not recognised, error call.
              error_call(data);
              return NULL;
          }
    }
    
    //Free thread handler data
    free(data);
    return NULL;
}


int main(int argc, char** argv) {
    
    //Validate arguments
    if(argc != 2){
        puts("Invalid arguments\n");
        return 1;
        
    }
    
    //Check file path to dictionary
    FILE* fp = fopen(argv[1], "rb");
    if(fp == NULL){
        puts("Invalid file path");
        return 1;
        
    }
    FILE* dfp = fopen("compression.dict", "rb");
    if(dfp == NULL){
        puts("Can't access compression dictionary");
    }

    
    unsigned char listen_address[4];
    char converted[4*2 + 1];
    memset(listen_address,0,4);
    unsigned char port[2];
    memset(port,0,2);
    
    //Read in listen address and port.
    fseek(fp, 0, SEEK_SET);
    fread(listen_address,1,4,fp);
    fread(port,1,2,fp);
    fseek(fp, 0, SEEK_END);
    
    //Read in dict size.
    fseek(dfp, 0, SEEK_END);
    int dict_size = ftell(dfp);
    fseek(dfp, 0, SEEK_SET);
    
    //Read in dict.
    unsigned char * dict = malloc(dict_size);
    fread(dict, 1, dict_size, dfp);
    unsigned char * bits = malloc(dict_size * 8);
    int reverse = -1;
    unsigned char* ordered_bits = malloc(dict_size * 8);
    
    for (int b =0; b <dict_size*8; b++){
        bits[b] = ((1 << (b % 8)) & (dict[b/8])) >> (b % 8);
    }
    
    //Acconting for endianess.
    for(int u = 0 ; u < dict_size*8 ; u++){
        if(u%8 == 0){
            reverse = u*2 + 7;
        }
        ordered_bits[u] = bits[reverse - u];
    }
    
    unsigned char** codes = NULL ;
    int * code_sizes = malloc(sizeof(int)*256);
    int pos = 0;
    int offset = 0;
    
    //Convert dict to char array where each offset corresponds to key and the data therein corresponds to its bitcode.
    while(pos<256){
        if(codes == NULL){
            codes = malloc(sizeof(char*));
        }
        else{
            codes = realloc(codes, sizeof(char*) * (pos+1));
        }

        int next_size = 0 ;
        for(int h = 0 ; h < 8 ; h ++){
            next_size += ordered_bits[h + offset] * pow(2,(7-h));
        }
        offset += 8;
        code_sizes[pos] = next_size;
        codes[pos] = malloc(sizeof(char) * next_size);
        memcpy(codes[pos], &ordered_bits[offset], next_size);
        offset += next_size;
        pos++;
    }
    
    //Free allocated data that is no longer needed.
    free(bits);
    free(dict);
    free(ordered_bits);
    
    //Generate huffman tree for given dict.
    struct node** huff_tree = malloc(sizeof(struct node*));
    *huff_tree = malloc(sizeof(struct node));
    (*(huff_tree)) -> left_node = NULL;
    (*(huff_tree)) -> right_node = NULL;
    (*(huff_tree)) -> indicator = 0;
    (*(huff_tree)) -> byte = 0;
    construct_tree(codes, code_sizes, huff_tree);
   
    //Convert listen address to IP address.
    int i;
    for(i=0;i<4;i++) {
       sprintf(&converted[i*2], "%02X", listen_address[i]);
    }
    char *output = (char*)malloc(sizeof(char) * 16);
    unsigned int a, b, c, d;
    sscanf(converted, "%2x%2x%2x%2x", &a, &b, &c, &d);
    sprintf(output, "%u.%u.%u.%u", a, b, c, d);
    
    
    int fsize = ftell(fp);
    int path_len = fsize - 6;
    fseek(fp, 6, SEEK_SET);
    char *path = malloc(path_len + 1);
    fread(path, 1, (path_len), fp);
    fclose(fp);
    
    path[path_len] = '\0';
    
    //Set up server socket.
    int client_fd = -1;
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    
    if(server_fd < 0) {
        puts("Failed to create server socket");
        return 1;
    }

    int option = 1;
    
    //Convert port to unsigned short and set to socket address in network order.
    //Set up sock address struct
    unsigned short p = (port[0] << 8) | port[1];
    struct sockaddr_in sock_add;
    sock_add.sin_family = AF_INET;
    sock_add.sin_port = htons(p);
    inet_aton(output, &sock_add.sin_addr);
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &option, sizeof(int));

    //Bind server with sock address.
    if(bind(server_fd, (struct sockaddr*) &sock_add, sizeof(struct sockaddr_in)) == -1) {
        perror("Failed to bind server socket");
        return 1;
    }

    //Start server listening
    if(listen(server_fd, 300) == -1){
        puts("Failed to start listening to socket");
        return 1;
    }
    
    //Initialise session data linked list to be shared by all threads.
    struct session_data** sd = malloc(sizeof(struct session_data*));
    *sd = NULL ;
    
    while(1) {
        uint32_t addrlen = sizeof(struct sockaddr_in);
        
        //Accept client connection and fill thread handler data.
        client_fd = accept(server_fd, (struct sockaddr*) &sock_add, &addrlen);
        struct con_data* data = malloc(sizeof(struct con_data));
        data -> socketfd = client_fd;
        data -> serverfd = server_fd;
        data -> target_directory = path;
        data -> dict = codes;
        data -> sizes = code_sizes;
        data -> huffman_tree = huff_tree;
        data -> session_data = sd;
        pthread_t thread;
        
        //Create thread and execute thread handler.
        pthread_create(&thread, NULL, thread_handler, data);
    }
    
    
    //After all threads have finished executing, close the server and return.
    close(server_fd);
    return 0;



}
