#include <stdlib.h>
#include <sys/types.h>
#include <stdio.h>
#include <pwd.h>
#include <unistd.h>
#include <errno.h>

uid_t
getuseridfromname(const char *argv)
{
	struct passwd pwd;
	struct passwd *result;
	char *buf;
	size_t bufsize;
	int s;

   bufsize = sysconf(_SC_GETPW_R_SIZE_MAX);
	if (bufsize == -1)          /* Value was indeterminate */
		bufsize = 16384;        /* Should be more than enough */

   buf = malloc(bufsize);
	if (buf == NULL) {
			perror("malloc");
			exit(EXIT_FAILURE);
		}

   s = getpwnam_r(argv, &pwd, buf, bufsize, &result);
	if (result == NULL) {
			if (s == 0)
				printf("Not found\n");
			else {
						errno = s;
						perror("getpwnam_r");
				}
			exit(EXIT_FAILURE);
		}

   printf("Name: %s; UID: %ld\n", pwd.pw_gecos, (long) pwd.pw_uid);
   return  pwd.pw_uid;
}
char *getCurrentUserName(){
        uid_t uid = getuid();
        struct passwd *pw = getpwuid(uid);
        if(pw){
                return pw->pw_name;
        }
        return "";
}
int
main(int argc,const char *argv[]){
	if(argc != 3){
		printf("args number expect 3 but found %d\n",argc);
		return 1;
	}
	char *userName = getCurrentUserName();
    printf("user is %s \n",userName);
    if(strcmp(userName,"yarn") != 0 && strcmp(userName,"chorus") != 0){
        printf("only user yarn and chorus can use this program \n");
        return 1;
    }
	if (strcmp(argv[1], "root") == 0) {
        printf("root is not allowed \n");
        return 1;
    }
	if(setuid(getuseridfromname(argv[1])) != 0){
		printf("setuid fail");
		return 1;
	}
	printf( "Real: %d:%d; Effective: %d:%d\n",
	getuid(), getgid(), geteuid(), getegid());

	system(argv[2]);
	return 0;
}
