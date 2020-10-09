#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "office.h"
#include "office.c"



typedef struct command command_t;

struct command {
	char* str;
	int (*exe)();
};


int test_office_place_1() {
  int pass = 0;
  const int p_match = 2;
  struct office* off = malloc(sizeof(struct office));
  off->department_head = NULL;

  struct employee emp = {
    .name = "Employee1",
    .supervisor = NULL,
    .subordinates = NULL,
    .n_subordinates = 0
  };

  office_employee_place(off, NULL, &emp);
  if(off->department_head != NULL) {
    pass++;
    if(strcmp("Employee1", off->department_head->name) == 0) {
    	pass++;
    } else {
      fputs(off->department_head->name, stdout);
    	fputs("Name does not match expected",
       stdout);
    }
  } else {
  	fputs("Department Head is NULL",
       stdout);
  }

  free(off->department_head->name);
  free(off->department_head);
  free(off);
  return pass == p_match;
}

int test_office_place_2() {
  int pass = 0;
  const int p_match = 3;
  struct office* off = malloc(sizeof(struct office));
  off->department_head = NULL;

  struct employee emp1 = {
    .name = "Employee1",
    .supervisor = NULL,
    .subordinates = NULL,
    .n_subordinates = 0
  };


  struct employee emp2 = {
    .name = "Employee2",
    .supervisor = NULL,
    .subordinates = NULL,
    .n_subordinates = 0
  };

	struct employee emp3 = {
		.name = "Employee3",
		.supervisor = NULL,
		.subordinates = NULL,
		.n_subordinates = 0
	};

	struct employee emp4 = {
		.name = "Employee4",
		.supervisor = NULL,
		.subordinates = NULL,
		.n_subordinates = 0
	};

	struct employee emp5 = {
		.name = "Employee5",
		.supervisor = NULL,
		.subordinates = NULL,
		.n_subordinates = 0
	};

	struct employee emp6 = {
		.name = "Employee6",
		.supervisor = NULL,
		.subordinates = NULL,
		.n_subordinates = 0
	};
    
    struct employee emp7 = {
        .name = "Employee7",
        .supervisor = NULL,
        .subordinates = NULL,
        .n_subordinates = 0
    };
    
    struct employee emp8 = {
        .name = "Employee8",
        .supervisor = NULL,
        .subordinates = NULL,
        .n_subordinates = 0
    };


    office_employee_place(off, NULL, &emp1);
    office_employee_place(off, off -> department_head , &emp2); //5th block
    office_employee_place(off, off -> department_head, &emp3); // 2nd block
    office_employee_place(off, off -> department_head, &emp4); // 3rd block
    office_employee_place(off, off -> department_head, &emp5); // 3rd block
    
    printf("%zu\n",off->department_head->subordinates[1].n_subordinates);

    
    
    office_fire_employee(&off->department_head->subordinates[1]);

    
    
    
    


	/*


	size_t l = 1;
	struct employee** emplys;
	struct employee* eArray = malloc(sizeof(struct employee));

	emplys = &eArray ;

	size_t* n_employees = malloc(sizeof(size_t));

	const char * name = "Employee3";

	//office_get_employees_at_level(off,l,emplys,n_employees);

	office_get_employees_postorder(off,emplys,n_employees);

	printf("\n ==number of employees : %zu == \n", *n_employees);
	for(int i = 0 ; i < *n_employees ; i ++){
		printf("emp[%d] : %s\n", i, (*emplys)[i].name);
		printf("num of subs : %zu\n", (*emplys)[i].n_subordinates);
		for (int j = 0 ; j < (*emplys)[i].n_subordinates; j++){
			printf("suboords[%d] : %s\n", j, (*emplys)[j].subordinates[j].name);
		}

	}

	*/


//	printf("REAL ADDRESS: %p\n", off -> department_head);
//	printf("\n %s \n", off -> department_head-> subordinates[1].name);

//  office_disband(off);

  return pass == p_match;
}


command_t tests[] = {
  { "test_office_place_1", test_office_place_1 },
  { "test_office_place_2", test_office_place_2 }
};

int main(int argc, char** argv) {
  int test_n = sizeof(tests) / sizeof(command_t);
  if(argc >= 2) {
    printf("%s",argv[1]);
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
