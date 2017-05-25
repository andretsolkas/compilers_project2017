#include <stdio.h>
#include <string.h>

#define putc(x) myputc(x)
#define puti(x) myputi(x)
#define puts(x) myputs(x)

#define geti() mygeti()
#define getc() mygetc()
#define gets(i,string) mygets(i,string)

#define abs(x) myabs(x)

void myputc(char c){
	printf("%c\n", c);
	return;
}

void myputi(int n){
	printf("%d\n", n);
	return;
}

void myputs(char * string[]){
	printf("%s\n", string);
	return;
}

int mygeti(){
	int i;
	scanf("%d", &i);
	return i;
}

char mygetc(){
	char c;
	scanf("%c", &c);
	return c;
}

void mygets(int i, char* string[]){
	fgets(string, i, stdin);
	return;
}

int myabs(int n){
	if (n>=0) return n;
	else return -n;
}

int ord(char c){
	return (int)c;
}

char chr(int i){
	return (char)i;
}