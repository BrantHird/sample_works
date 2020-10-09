#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "office.h"

struct stack{
  struct employee* contents[40];
  int top;
  int bottom;
};

struct stack* createStack(){
  struct stack* s = malloc(sizeof(struct stack));
  s -> top = -1 ;
  s -> bottom = - 1 ;
  return s ;

}

int stackEmpty(struct stack* s){
  if(s -> bottom == -1){
    return 1;
  }
  else{
    return 0 ;
  }
}

void addToStack(struct stack* s , struct employee* employee){

  if(s->top == 40 - 1){
    //stack too big
  }

  else{
    if(s -> bottom == -1){
      s -> bottom = 0 ;
    }
    s -> top ++ ;
    s -> contents[s-> top] = employee ;
  }

}
struct employee* popStack(struct stack* s){

	struct employee* employee;

	if(stackEmpty(s)){
		employee = NULL ;
	}

	else{
		employee = s->contents[s->top];
    s -> contents[s->top] = NULL ;
		s -> top -- ;

		if(s->top < s -> bottom){
			s -> top = s -> bottom = -1 ;
		}

	}
	return employee;
}


struct queue{

	struct employee* contents[40];
	int head;
	int tail;

};

struct queue* createQueue() {

	struct queue* q = malloc(sizeof(struct queue));
	q -> head = -1 ;
	q -> tail = -1 ;

	return q ;
}

int isEmpty(struct queue* q){
	if(q->tail == -1){
		return 1 ;
	}
	else{
		return 0;
	}
}

void addToQueue(struct queue* q, struct employee* employee){

	if((q -> tail) == 40 - 1){
    //printf("Queue too fat");
	}

	else{
		if(q -> head == -1){
			q -> head = 0 ;
		}

		q -> tail ++ ;
		q -> contents[q-> tail] = employee ;
	}
}

struct employee* removeFromQueue(struct queue* q){

	struct employee* employee;

	if(isEmpty(q)){
		employee = NULL ;
	}

	else{
		employee = q->contents[q->head];
    q -> contents[q->head] = NULL ;
		q -> head ++ ;

		if(q->head > q -> tail){
			q -> head = q->tail= -1 ;
		}

	}
	return employee;
}

struct employee* bfs(struct office* off){

	if(off -> department_head == NULL){
		return NULL ;
	}

	struct queue* q = createQueue();

	addToQueue(q, off -> department_head);

	struct employee* currentEmployee;

	while(!isEmpty(q)){

		  currentEmployee = removeFromQueue(q);

			for(int i = 0 ; i < currentEmployee -> n_subordinates ; i++){
				addToQueue(q, &(currentEmployee -> subordinates[i]));
			}

      if(currentEmployee -> n_subordinates == 0){
        break ;
      }

	}

  free(q);

	return currentEmployee;
}

struct employee* find(struct office* off, struct employee* toFind){

  if(off -> department_head == NULL){
    return NULL ;
  }

  struct queue* q = createQueue();

  addToQueue(q, off -> department_head);

  struct employee* currentEmployee;

  int found = 0 ;

  while(!isEmpty(q)){

    currentEmployee = removeFromQueue(q);

    if(0 == memcmp(currentEmployee, toFind, sizeof(struct employee))){
      found = 1;
      break;
    }

    for(int i = 0 ; i < currentEmployee -> n_subordinates ; i++){
      addToQueue(q, &(currentEmployee -> subordinates[i]));
    }

  }

  free(q);

  if(found == 0){
    return NULL ;
  }

  else{
    return currentEmployee ;
  }
}


struct employee* findName(struct office* off, const char* toFind){

  if(off -> department_head == NULL){
    return NULL ;
  }

  struct queue* q = createQueue();

  addToQueue(q, off -> department_head);

  struct employee* currentEmployee;

  int found = 0 ;

  while(!isEmpty(q)){

    currentEmployee = removeFromQueue(q);

    if(0 == strcmp(currentEmployee -> name, toFind)){
      found = 1;
      break;
    }

    for(int i = 0 ; i < currentEmployee -> n_subordinates ; i++){
      addToQueue(q, &(currentEmployee -> subordinates[i]));
    }
  }

  free(q);

  if(found == 0){
    return NULL ;
  }

  else{
    return currentEmployee ;
  }
}


struct employee* findLastName(struct office* off, const char* toFind){

  if(off -> department_head == NULL){
    return NULL ;
  }

  struct queue* q = createQueue();

  addToQueue(q, off -> department_head);

  struct employee* currentEmployee;

  struct employee* foundEmployee;

  int found = 0 ;

  while(!isEmpty(q)){

    currentEmployee = removeFromQueue(q);

    if(0 == strcmp(currentEmployee -> name, toFind)){
      found = 1;
      foundEmployee = currentEmployee ;
    }

    for(int i = 0 ; i < currentEmployee -> n_subordinates ; i++){
      addToQueue(q, &(currentEmployee -> subordinates[i]));
    }
  }

  free(q);

  if(found == 0){
    return NULL ;
  }

  else{
    return foundEmployee ;
  }
}


size_t findParentLevel(struct stack* s, struct employee* e, size_t* levels){

  for(int i = 0 ; i < 40 ; i ++){

    size_t suboords = s->contents[i] -> n_subordinates;

    for(int j = 0 ; j < suboords ; j ++){

      if( memcmp(&s -> contents[i] -> subordinates[j], e, sizeof(struct employee) )== 0){
        return levels[i];
      }
    }
  }

  return - 1;


}

int getOffset(struct employee* suboordinate, struct employee* supervisor){

  for(int i = 0 ; i < supervisor -> n_subordinates ; i ++){
    if(0 == memcmp(suboordinate, &supervisor -> subordinates[i], sizeof(struct employee))){
      return i ;
    }
  }
  return - 1;





}






void postOrder(struct employee* currentEmployee, struct employee** emplys,
  size_t* n_employees){



    for(int i = 0 ; i < currentEmployee -> n_subordinates ;  i++){
      postOrder(&currentEmployee->subordinates[i], emplys, n_employees);
    }

    *(n_employees) = *(n_employees) + 1 ;

    if(*n_employees == 1){
      *emplys = malloc(sizeof(struct employee));
    }

    else{
      *emplys = realloc(*emplys, sizeof(struct employee)*(*n_employees));
    }

    (*emplys)[*n_employees - 1].name = malloc(sizeof(char) * (strlen(currentEmployee -> name) + 1));
    memcpy((*emplys)[*n_employees - 1].name, currentEmployee -> name, sizeof(char) *(strlen(currentEmployee -> name) + 1));
    (*emplys)[*n_employees - 1].subordinates = currentEmployee -> subordinates;
    (*emplys)[*n_employees - 1].supervisor =  currentEmployee -> supervisor;
    (*emplys)[*n_employees - 1].n_subordinates = currentEmployee -> n_subordinates ;

  }


void allocateSupervisors(struct employee* emp, struct employee* sup){

  if(emp == NULL){
    return ;
  }

  for(int i = 0 ; i < emp -> n_subordinates ; i ++){
    allocateSupervisors(&emp->subordinates[i],emp);
  }

  if(sup == NULL){
    return ;
  }

  emp->supervisor = sup ;

  return ;

}

/**
 * Places an employee within the office, if the supervisor field is NULL
 *  it is assumed the employee will be placed under the next employee that is
 * not superivising any other employee (top-down, left-to-right).
 *
 * If the supervisor is not NULL, it will be added to the supervisor's subordinates list
 *  of employees (make sure the supervisor exists in hierarchy).
 * if the office or employee are null, the function not do anything.
 */
void office_employee_place(struct office* off, struct employee* supervisor,
  struct employee* emp) {

    if(off == NULL || emp == NULL){
      return ;
    }

    if(supervisor == NULL){

			struct employee* firstEmployee = bfs(off);

			if(firstEmployee == NULL){
				off -> department_head = (struct employee*) malloc(sizeof(struct employee));
				(off->department_head) -> name = malloc(sizeof(char) * (strlen(emp->name) + 1));
				off -> department_head -> n_subordinates = 0 ;
				off -> department_head -> subordinates = NULL ;
				off -> department_head -> supervisor = NULL ;
				memcpy(off->department_head->name, emp->name, sizeof(char)*10);

			}

			else{
          int numberOfSubs = firstEmployee -> n_subordinates ;

          if(numberOfSubs == 0){
    				firstEmployee -> subordinates = malloc(sizeof(struct employee));
            firstEmployee -> n_subordinates = 1 ;
    				firstEmployee -> subordinates -> name = malloc(sizeof(char)* (strlen(emp->name) + 1));
    				memcpy(firstEmployee -> subordinates -> name, emp -> name, sizeof(char) * (strlen(emp->name) + 1));
            firstEmployee -> subordinates -> supervisor = firstEmployee;
            firstEmployee -> subordinates -> n_subordinates = 0 ;


        }

        else{
          firstEmployee -> subordinates = realloc(firstEmployee -> subordinates, sizeof(struct employee) * (numberOfSubs + 1));
          firstEmployee -> n_subordinates ++ ;
          firstEmployee -> subordinates[numberOfSubs].name = malloc(sizeof(char)*(strlen(emp -> name) + 1));
          memcpy(firstEmployee -> subordinates[numberOfSubs].name, emp-> name, sizeof(char)*(strlen(emp -> name) + 1));
          firstEmployee -> subordinates[numberOfSubs].supervisor = firstEmployee;
          firstEmployee -> subordinates[numberOfSubs].n_subordinates = 0;
        }

			}
  }

  else {

    struct employee * sup = find(off, supervisor);

    //printf("number of suboordinates for %s is %zu\n", sup -> name, sup -> n_subordinates);

    int numberOfSubs = sup -> n_subordinates;

    if(numberOfSubs != 0){
      sup -> subordinates = realloc(sup -> subordinates, sizeof(struct employee)*(numberOfSubs +1));
      sup -> subordinates[numberOfSubs].name = malloc(sizeof(char)*(strlen(emp->name)+1));
      memcpy(sup -> subordinates[numberOfSubs].name, emp -> name, sizeof(char) * (strlen(emp->name) + 1));
      sup -> subordinates[numberOfSubs].n_subordinates = 0;
      sup -> n_subordinates ++ ;
      sup -> subordinates[numberOfSubs].supervisor = sup;

    }

    else{
      sup -> subordinates = malloc(sizeof(struct employee));
      sup -> subordinates -> name = malloc(sizeof(char)*(strlen(emp->name) + 1));
      memcpy(sup -> subordinates -> name, emp -> name, sizeof(char)*(strlen(emp->name) + 1)) ;
      sup -> subordinates -> n_subordinates = 0 ;
      sup -> n_subordinates = 1 ;
      sup -> subordinates -> supervisor = sup;

    }

 }

 allocateSupervisors(off -> department_head, NULL);

}
/**
 * Fires an employee, removing from the office
 * If employee is null, nothing should occur
 * If the employee does not supervise anyone, they will just be removed
 * If the employee is supervising other employees, the first member of that
 *  team will replace him.
 */
void office_fire_employee(struct employee* emp) {

    if(emp == NULL){
    return ;
    }
    
    
    if(emp->n_subordinates > 0){
        
        
           if(emp->subordinates[0].n_subordinates == 0){
                       
               struct employee * save = malloc(sizeof(struct employee) * (emp->n_subordinates - 1) );
               
               for(int i = 0 ; i < emp-> n_subordinates - 1 ; i ++){
                   save[i] = emp ->subordinates[i+1];
               }
               
               free(emp -> name);

               emp -> name = malloc(sizeof(char)* (strlen(emp->subordinates[0].name) + 1));

               memcpy(emp->name, emp->subordinates[0].name, sizeof(char)* (strlen(emp->subordinates[0].name) + 1));
               
               emp -> n_subordinates -- ;
               
               if(emp-> n_subordinates == 0){
                   free(save);
                   free(emp->subordinates[0].name);
                   free(emp->subordinates);
                   return ;
               }
               
               
               emp->subordinates = malloc(sizeof(struct employee)*(emp->n_subordinates));
               
               memcpy(emp->subordinates, save, sizeof(struct employee)*(emp->n_subordinates));
               
                       
               free(save);
               
               
               
               return ;
               
           }
           
           else{
               
               free(emp -> name);

               emp -> name = malloc(sizeof(char)* (strlen(emp->subordinates[0].name) + 1));
               
               memcpy(emp->name, emp->subordinates[0].name, sizeof(char)* (strlen(emp->subordinates[0].name) + 1));
               
               office_fire_employee(&emp->subordinates[0]);
               
               
           }
        
        
    }
    
    
    else{
            
        
        
        int supPlace = getOffset(emp, emp->supervisor);
                
        if(supPlace == emp->supervisor->n_subordinates -1){
            
            struct employee * save = malloc(sizeof(struct employee) * (emp->supervisor->n_subordinates - 1) );

            emp->supervisor->n_subordinates -- ;
            free(emp->name);
            emp->supervisor->subordinates = realloc(emp->supervisor->subordinates,sizeof(struct employee)* emp->supervisor->n_subordinates);
            free(save);
            
            return ;
        }
        
        else{
            
            
            struct employee * save = malloc(sizeof(struct employee) * (emp->supervisor->n_subordinates - 1 - supPlace));
            
            int h = 0 ;
            
            
            for(size_t o = supPlace + 1 ; o < emp ->supervisor->n_subordinates ; o ++){
                
                save[h] = emp->supervisor->subordinates[o];
                h++ ;
                
                
            }
            
            free(emp->name);
            
            emp->supervisor->subordinates = realloc(emp->supervisor->subordinates, sizeof(struct employee)*(emp->supervisor->n_subordinates - 1));
            
            emp->supervisor->n_subordinates -- ;
            int g = 0;
            
            for(int p = supPlace ; p < emp->supervisor->n_subordinates; p++){
                
                memcpy(emp->supervisor->subordinates[p].name, save[g].name, sizeof(char)* (strlen(save[g].name + 1)));
                g++;
            }
            
            free(save);
            
            
        }

        return ;
        
        
        
        
        
    }
    
   

    
    
    
    
        



}


/**
 * Retrieves the first encounter where the employee's name is matched to one in the office
 * If the employee does not exist, it must return NULL
 * if office or name are NULL, your function must do nothing
 */
struct employee* office_get_first_employee_with_name(struct office* office,
  const char* name) {

    if(office == NULL || name == NULL){
      return NULL ;
    }

    struct employee* find = findName(office,name);

    return find ;

  }

/**
 * Retrieves the last encounter where the employee's name is matched to one in the office
 * If the employee does not exist, it must return NULL
 * if office or name are NULL, your function must do nothing
 */
struct employee* office_get_last_employee_with_name(struct office* office,
  const char* name) {

    if(office == NULL || name == NULL){
      return NULL ;
    }

    struct employee* find = findLastName(office,name);

    return find ;


	return NULL;
}


/**
 * This function will need to retrieve all employees at a level.
 * A level is defined as distance away from the boss. For example, all
 * subordinates of the boss are 1 level away, subordinates of the boss's subordinates
 * are 2 levels away.
 *
 * if office, n_employees or emplys are NULL, your function must do nothing
 * You will need to provide an allocation for emplys and specify the
 * correct number of employees found in your query.
 */
void office_get_employees_at_level(struct office* office, size_t level,
  struct employee** emplys, size_t* n_employees) {

    if(office == NULL || n_employees == NULL || emplys == NULL){
      return ;
    }

    struct queue* q = createQueue();
    struct stack* s = createStack();
    size_t levels[40];

    memset(&levels, 0, sizeof(levels));

    size_t currLevel = 0 ;
    int stackIndex = 0 ;

    addToQueue(q, office -> department_head);

    struct employee* currentEmployee;

    while(!isEmpty(q)){

        currentEmployee = removeFromQueue(q);

        addToStack(s,currentEmployee);

        if(currentEmployee -> supervisor != NULL){

          currLevel = findParentLevel(s,currentEmployee, levels);

          if(currLevel == -1){
            printf("something went wrong with stack level find function\n");
          }
          currLevel ++ ;
          levels[stackIndex] = currLevel;
          stackIndex ++;
        }

        else{
          levels[stackIndex] = currLevel;
          stackIndex ++ ;
        }

        for(int i = 0 ; i < currentEmployee -> n_subordinates ; i++){
          addToQueue(q, &(currentEmployee -> subordinates[i]));
        }

    }

    int eIndex = 0;
    size_t numEmps = 0 ;

    for(int j = 0 ; j < stackIndex ; j++){

      if(levels[j] == level){

        numEmps ++ ;
        if(numEmps == 1){
          *emplys = malloc(sizeof(struct employee));
        }

        else{
          *emplys = realloc(*emplys, sizeof(struct employee)* numEmps);
        }
        (*emplys)[eIndex].name = malloc(sizeof(char) * (strlen(s -> contents[j] -> name) + 1));
        memcpy((*emplys)[eIndex].name, s -> contents[j] -> name, sizeof(char) *(strlen(s -> contents[j] -> name) + 1));
        (*emplys)[eIndex].subordinates = s -> contents[j] -> subordinates;
        (*emplys)[eIndex].supervisor = s -> contents[j] -> supervisor;
        (*emplys)[eIndex].n_subordinates = s -> contents[j] -> n_subordinates ;
        eIndex ++ ;
      }

    }
    memcpy(n_employees, &numEmps, sizeof(size_t));
    free(s);
    free(q);

}

/**
 * Will retrieve a list of employees that match the name given
 * If office, name, emplys or n_employees is NULL, this function should do
 * nothing
 * if office, n_employees, name or emplys are NULL, your function must do
 * nothing.
 * You will need to provide an allocation to emplys and specify the
 * correct number of employees found in your query.
 */
void office_get_employees_by_name(struct office* office, const char* name,
  struct employee** emplys, size_t* n_employees) {

    if(office == NULL || name == NULL || emplys ==NULL || n_employees == NULL){
      return ;
    }

    size_t found = 0 ;

    if(office -> department_head == NULL){
      memcpy(n_employees, &found, sizeof(size_t));
      return ;
    }

    struct queue* q = createQueue();

    addToQueue(q, office -> department_head);

    struct employee* currentEmployee;

    while(!isEmpty(q)){

      currentEmployee = removeFromQueue(q);

      if(0 == strcmp(currentEmployee -> name, name)){
        found ++;

        if(found == 1){
          *emplys = malloc(sizeof(struct employee));
        }

        else{
          *emplys = realloc(*emplys, sizeof(struct employee)*found);
        }

        (*emplys)[found - 1].name = malloc(sizeof(char) * (strlen(currentEmployee -> name) + 1));
        memcpy((*emplys)[found - 1].name, currentEmployee -> name, sizeof(char) *(strlen(currentEmployee -> name) + 1));
        (*emplys)[found - 1].subordinates = currentEmployee -> subordinates;
        (*emplys)[found - 1].supervisor =  currentEmployee -> supervisor;
        (*emplys)[found - 1].n_subordinates = currentEmployee -> n_subordinates ;
      }

      for(int i = 0 ; i < currentEmployee -> n_subordinates ; i++){
        addToQueue(q, &(currentEmployee -> subordinates[i]));
      }
    }

    free(q);

    memcpy(n_employees, &found, sizeof(size_t));

    return ;

}

/**
 * You will traverse the office and retrieve employees using a postorder traversal
 * If off, emplys or n_employees is NULL, this function should do nothing
 *
 * You will need to provide an allocation to emplys and specify the
 * correct number of employees found in your query.
 */
void office_get_employees_postorder(struct office* off,
  struct employee** emplys,
  size_t* n_employees) {

    if(off == NULL || emplys == NULL || n_employees == NULL){
      return ;
    }

    size_t nEmps = 0 ;

    size_t * eptr = &nEmps ;

    postOrder(off->department_head, emplys, eptr);

    memcpy(n_employees, eptr, sizeof(size_t));

    return ;

}

/**
 * The employee will be promoted to the same level as their supervisor and will
 *  join their supervisor's team.
 * If the employee has members on their team, the first employee from that team
 *   will be promoted to manage that team.
 * if emp is NULL, this function will do nothing
 * if the employee is at level 0 or level 1, they cannot be promoted
 */
void office_promote_employee(struct employee* emp) {

}

/**
 * Demotes an employee, placing them under the supervision of another employee.
 * If supervisor or emp is null, nothing should occur
 * If the employee does not supervise anyone, they will not be demoted as they
 *  are already at the lowest position
 * If an employee is to be demoted but their new distance from the boss is less
 *  than the previous position, nothing will happen.
 * Otherwise, the employee should be assigned at the end the supervisor's team
 *  and the first employee from the previously managed team will be promoted.
 *
 * Edge case:
 * if the supervisor use to be an subordinate to the demoted employee
 *   (they will get promoted)
 * the demoted employee will be attached to subordinate's new subordinate's
 *   list not their previous list.
 */
void office_demote_employee(struct employee* supervisor, struct employee* emp){

}

/**
 * The office disbands
 * (You will need to free all memory associated with employees attached to
 *   the office and the office itself)
 */

void office_disband(struct office* office) {

    if(office == NULL || office -> department_head == NULL){
      free(office);
      return ;
    }

    struct queue* q = createQueue();

    struct stack* s = createStack();

    addToQueue(q, office -> department_head);

    struct employee* currentEmployee;

    while(!isEmpty(q)){
        currentEmployee = removeFromQueue(q);
        addToStack(s,currentEmployee);

        for(int i = 0 ; i < currentEmployee -> n_subordinates ; i++){
          addToQueue(q, &(currentEmployee -> subordinates[i]));
        }
    }

    while(!stackEmpty(s)){
        currentEmployee = popStack(s);
        free(currentEmployee -> name);
        
        if(currentEmployee -> n_subordinates != 0){
          free(currentEmployee -> subordinates);
        }

    }

    free(office -> department_head);
    free(office);
    free(q);
    free(s);


}
