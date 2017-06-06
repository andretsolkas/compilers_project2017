.intel_syntax noprefix
.text
    .globl main

main:

    push ebp
    mov ebp, esp


	mov eax, 4

	push eax					# pass parameter

	call _puti_0
	add esp, 4

	mov esp, ebp
	pop ebp
	ret



_puti_0:

	push ebp
	mov ebp, esp


	mov ebx, [ebp+12]			# puti parameter - skip EPC and RV address
	push ebx

	mov eax, OFFSET FLAT:fmt	# Put the argument of printf() on the stack
	push eax

	call printf					# Has two parameters
	add esp, 8					# Clean parameters


	mov esp, ebp
	pop ebp
	ret

.data
    fmt: .asciz  "Output: %d\n"
