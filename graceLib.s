.intel_syntax noprefix

.text

.globl	_puti_0

_puti_0:

        push ebp
        mov ebp, esp


        mov ebx, [ebp+12]                       # puti parameter - skip EPC and RV address
        push ebx

        mov eax, OFFSET FLAT:fmt_pi             # Put the argument of printf() on the stack
        push eax

        call printf                             # Has two 'parameters'
        add esp, 8                              # Clean parameters


        mov esp, ebp
        pop ebp
        ret

###################################

.globl	_putc_0

_putc_0:

    push ebp
    mov ebp, esp


    mov ebx, [ebp+12]                           # putc parameter - skip EPC and RV address
    push ebx

    mov eax, OFFSET FLAT:fmt_pc                 # Put the argument of printf() on the stack
    push eax

    call printf                                 # Has two 'parameters'
    add esp, 8                                  # Clean parameters


    mov esp, ebp
    pop ebp
    ret


    ###################################

.globl	_puts_0

_puts_0:

    push ebp
    mov ebp, esp


    mov ebx, [ebp+12]                           # puts parameter - skip EPC and RV address
    push ebx

    mov eax, OFFSET FLAT:fmt_ps                 # Put the argument of printf() on the stack
    push eax

    call printf                                 # Has two 'parameters'
    add esp, 8                                  # Clean parameters


    mov esp, ebp
    pop ebp
    ret


###################################

.globl	_geti_0

_geti_0:

    push ebp
    mov ebp, esp
    sub esp, 4

    lea esi, DWORD PTR [ebp-4]                  #Load Local Variable's Address
    push esi

    # Pass the format string literal to scanf
    mov eax, OFFSET FLAT:scanf_fmt_gi
    push eax
    call scanf
    add esp, 8

    mov esi, DWORD PTR [ebp+8]                  #Load geti's return value address
    mov eax, DWORD PTR [ebp-4]
    mov DWORD PTR [esi], eax

    push eax

    mov eax, OFFSET FLAT:fmt_gi
    push eax

    call printf
    add esp, 8


    mov esp, ebp
    pop ebp
    ret


###################################

.globl	_getc_0

_getc_0:

    push ebp
    mov ebp, esp
    sub esp, 4

    lea esi, DWORD PTR [ebp-1]                  #Load Local Variable's Address
    push esi

    # Pass the format string literal to scanf
    mov eax, OFFSET FLAT:scanf_fmt_gc
    push eax
    call scanf
    add esp, 8

    mov esi, DWORD PTR [ebp+8]                  #Load getc's return value address
    mov al, BYTE PTR [ebp-1]
    mov BYTE PTR [esi], al


    movzx eax, BYTE PTR [ebp-1]
    push eax

    mov eax, OFFSET FLAT:fmt_gc
    push eax

    call printf
    add esp, 8


    mov esp, ebp
    pop ebp
    ret
###################################

.globl	_gets_0

_gets_0:
                push ebp
                mov ebp, esp
                sub esp, 32

                mov DWORD PTR [ebp-4], 0

                mov eax, DWORD PTR [ebp+12] 
                sub eax, 1

                mov DWORD PTR [ebp+12], eax 

gets_5:         mov eax, DWORD PTR [ebp-4] 
                mov edx, DWORD PTR [ebp+12]
                cmp eax, edx
                jl gets_7

gets_6:         jmp gets_21

gets_7:         lea esi, DWORD PTR [ebp-13]  
                push esi             

gets_8:         call _getc_0
                add esp, 4               

gets_9:         movzx eax, BYTE PTR [ebp-13]
                mov BYTE PTR [ebp-5], al    

gets_10:        movzx eax, BYTE PTR [ebp-5]
                mov edx, 10
                cmp eax, edx
                jne gets_12

gets_11:        jmp gets_17

gets_12:        mov eax, DWORD PTR [ebp-4] 
                mov ecx, 1
                imul ecx
                mov ecx, DWORD PTR [ebp+16]  
                add eax, ecx
                mov DWORD PTR [ebp-20], eax 

gets_13:        movzx eax, BYTE PTR [ebp-5]
                mov edi, DWORD PTR [ebp-20]
                mov BYTE PTR [edi], al

gets_14:        mov eax, DWORD PTR [ebp-4] 
                mov ecx, 1 
                add eax, ecx
                mov DWORD PTR [ebp-24], eax 


gets_15:        mov eax, DWORD PTR [ebp-24]
                mov DWORD PTR [ebp-4], eax  


gets_16:        jmp gets_5

gets_17:        mov eax, DWORD PTR [ebp-4] 
                mov ecx, 1
                imul ecx
                mov ecx, DWORD PTR [ebp+16]  
                add eax, ecx
                mov DWORD PTR [ebp-28], eax 

gets_18:        mov eax, 0 
                mov edi, DWORD PTR [ebp-28]
                mov BYTE PTR [edi], al

gets_19:        jmp end_gets_0

gets_20:        jmp gets_5

gets_21:        mov eax, DWORD PTR [ebp+12]
                mov ecx, DWORD PTR [ebp+16]  
                add eax, ecx
                mov DWORD PTR [ebp-32], eax 

gets_22:        mov eax, 0 
                mov edi, DWORD PTR [ebp-32]
                mov BYTE PTR [edi], al

gets_23:
end_gets_0:
                mov esp, ebp
                pop ebp
                ret


###################################

.globl	_strlen_0

_strlen_0:
		push ebp
		mov ebp, esp
		sub esp, 8

		mov DWORD PTR [ebp-4], 0

_len_3:	mov eax, DWORD PTR [ebp-4]
		mov ecx, DWORD PTR [ebp+12]
		add eax, ecx
		mov DWORD PTR [ebp-8], eax

		mov edi, DWORD PTR [ebp-8]
		movzx eax, BYTE PTR [edi]
		mov edx, 0
		cmp eax, edx
		jne _len_6

		jmp _len_9

_len_6:	mov eax, DWORD PTR [ebp-4]
		add eax, 1
		mov DWORD PTR [ebp-4], eax

		jmp _len_3

_len_9:	mov eax, DWORD PTR [ebp-4]
		mov esi, DWORD PTR [ebp+8]
		mov DWORD PTR [esi], eax

		mov esp, ebp
		pop ebp
		ret

###################################

.globl	_strcmp_0

_strcmp_0:

		push ebp
		mov ebp, esp
		sub esp, 44

		mov DWORD PTR [ebp-4], 0

		mov esi, DWORD PTR [ebp+12]
		push esi

		lea esi, DWORD PTR [ebp-24]
		push esi

		call _strlen_0
		add esp, 8

		mov eax, DWORD PTR [ebp-24]
		mov DWORD PTR [ebp-8], eax

		mov esi, DWORD PTR [ebp+16]
		push esi

		lea esi, DWORD PTR [ebp-28]
		push esi

		call _strlen_0
		add esp, 8

		mov eax, DWORD PTR [ebp-28]
		mov DWORD PTR [ebp-12], eax

		mov eax, DWORD PTR [ebp-8]
		mov edx, DWORD PTR [ebp-12]
		cmp eax, edx
		jle cmp_13

		jmp cmp_15

cmp_13:		mov eax, DWORD PTR [ebp-8]
		mov DWORD PTR [ebp-16], eax

		jmp cmp_16

cmp_15:		mov eax, DWORD PTR [ebp-12]
		mov DWORD PTR [ebp-16], eax

cmp_16:		mov eax, DWORD PTR [ebp-4]
		mov edx, DWORD PTR [ebp-16]
		cmp eax, edx
		jle cmp_18

		jmp cmp_35

cmp_18:		mov eax, DWORD PTR [ebp-4]
		mov ecx, DWORD PTR [ebp+12]
		add eax, ecx
		mov DWORD PTR [ebp-32], eax

		mov edi, DWORD PTR [ebp-32]
		movzx eax, BYTE PTR [edi]
		mov BYTE PTR [ebp-17], al

		mov eax, DWORD PTR [ebp-4]
		mov ecx, DWORD PTR [ebp+16]
		add eax, ecx
		mov DWORD PTR [ebp-36], eax

		mov edi, DWORD PTR [ebp-36]
		movzx eax, BYTE PTR [edi]
		mov BYTE PTR [ebp-18], al

		movzx eax, BYTE PTR [ebp-17]
		movzx edx, BYTE PTR [ebp-18]
		cmp eax, edx
		jg cmp_24

		jmp cmp_27

cmp_24:		mov eax, 1
		mov esi, DWORD PTR [ebp+8]
		mov DWORD PTR [esi], eax

		jmp end_strcmp_0

		jmp cmp_32

cmp_27:		movzx eax, BYTE PTR [ebp-17]
		movzx edx, BYTE PTR [ebp-18]
		cmp eax, edx
		jl cmp_29

		jmp cmp_32

cmp_29:		mov eax, 0
		mov ecx, 1
		sub eax, ecx
		mov DWORD PTR [ebp-40], eax

		mov eax, DWORD PTR [ebp-40]
		mov esi, DWORD PTR [ebp+8]
		mov DWORD PTR [esi], eax

		jmp end_strcmp_0

cmp_32:		mov eax, DWORD PTR [ebp-4]
		mov ecx, 1
		add eax, ecx
		mov DWORD PTR [ebp-44], eax

		mov eax, DWORD PTR [ebp-44]
		mov DWORD PTR [ebp-4], eax

		jmp cmp_16

cmp_35:		mov eax, 0
		mov esi, DWORD PTR [ebp+8]
		mov DWORD PTR [esi], eax

		jmp end_strcmp_0

end_strcmp_0:

		mov esp, ebp
		pop ebp
		ret


###################################

.globl	_strcpy_0

_strcpy_0:
		push ebp
		mov ebp, esp
		sub esp, 24

		mov DWORD PTR [ebp-8], 0

		mov esi, DWORD PTR [ebp+16]
		push esi

		lea esi, DWORD PTR [ebp-12]
		push esi

		call near ptr _strlen_0
		add esp, 8

		mov eax, DWORD PTR [ebp-12]
		mov DWORD PTR [ebp-4], eax

cpy_7:		mov eax, DWORD PTR [ebp-8]
		mov edx, DWORD PTR [ebp-4]
		cmp eax, edx
		jle cpy_9

		jmp cpy_15

cpy_9:		mov eax, DWORD PTR [ebp-8]
		mov ecx, DWORD PTR [ebp+12]
		add eax, ecx
		mov DWORD PTR [ebp-16], eax

		mov eax, DWORD PTR [ebp-8]
		mov ecx, DWORD PTR [ebp+16]
		add eax, ecx
		mov DWORD PTR [ebp-20], eax

		mov edi, DWORD PTR [ebp-20]
		movzx eax, BYTE PTR [edi]
		mov edi, DWORD PTR [ebp-16]
		mov BYTE PTR [edi], al

		mov eax, DWORD PTR [ebp-8]
		mov ecx, 1
		add eax, ecx
		mov DWORD PTR [ebp-24], eax

		mov eax, DWORD PTR [ebp-24]
		mov DWORD PTR [ebp-8], eax

		jmp cpy_7


cpy_15:
end_strcpy_0:
		mov esp, ebp
		pop ebp
		ret

###################################

.globl	_strcat_0

_strcat_0:
		push ebp
		mov ebp, esp
		sub esp, 36

		mov DWORD PTR [ebp-12], 0

		mov esi, DWORD PTR [ebp+16]
		push esi

		lea esi, DWORD PTR [ebp-16]
		push esi

		call near ptr _strlen_0
		add esp, 8

		mov eax, DWORD PTR [ebp-16]
		mov DWORD PTR [ebp-4], eax

		mov esi, DWORD PTR [ebp+12]
		push esi

		lea esi, DWORD PTR [ebp-20]
		push esi

		call near ptr _strlen_0
		add esp, 8

		mov eax, DWORD PTR [ebp-20]
		mov DWORD PTR [ebp-8], eax

cat_11:	mov eax, DWORD PTR [ebp-12]
		mov edx, DWORD PTR [ebp-4]
		cmp eax, edx
		jle cat_13

		jmp cat_21

cat_13:	mov eax, DWORD PTR [ebp-8]
		mov ecx, DWORD PTR [ebp+12]
		add eax, ecx
		mov DWORD PTR [ebp-24], eax

		mov eax, DWORD PTR [ebp-12]
		mov ecx, DWORD PTR [ebp+16]
		add eax, ecx
		mov DWORD PTR [ebp-28], eax

		mov edi, DWORD PTR [ebp-28]
		movzx eax, BYTE PTR [edi]
		mov edi, DWORD PTR [ebp-24]
		mov BYTE PTR [edi], al

		mov eax, DWORD PTR [ebp-12]
		mov ecx, 1
		add eax, ecx
		mov DWORD PTR [ebp-32], eax

		mov eax, DWORD PTR [ebp-32]
		mov DWORD PTR [ebp-12], eax

		mov eax, DWORD PTR [ebp-8]
		mov ecx, 1
		add eax, ecx
		mov DWORD PTR [ebp-36], eax

		mov eax, DWORD PTR [ebp-36]
		mov DWORD PTR [ebp-8], eax

		jmp cat_11

cat_21:
end_strcat_0:
		mov esp, ebp
		pop ebp
		ret

###################################

.globl	_abs_0

_abs_0:
		push ebp
		mov ebp, esp
		sub esp, 4

		mov eax, DWORD PTR [ebp+12]
		mov edx, 0
		cmp eax, edx
		jl abs_4

		jmp abs_7

abs_4:	mov eax, 0
		mov ecx, DWORD PTR [ebp+12]
		sub eax, ecx
		mov DWORD PTR [ebp-4], eax

		mov eax, DWORD PTR [ebp-4]
		mov esi, DWORD PTR [ebp+8]
		mov DWORD PTR [esi], eax

		jmp end_abs_0

abs_7:		mov eax, DWORD PTR [ebp+12]
		mov esi, DWORD PTR [ebp+8]
		mov DWORD PTR [esi], eax

		jmp end_abs_0

end_abs_0:
		mov esp, ebp
		pop ebp
		ret


###################################

.globl	_ord_0

_ord_0:
	push ebp
    mov ebp, esp

 	movzx eax, BYTE PTR [ebp+12]
    mov esi, DWORD PTR [ebp+8]
    mov DWORD PTR [esi], eax

    mov esp, ebp
    pop ebp
    ret

###################################

.globl	_chr_0

_chr_0:
        push ebp
        mov ebp, esp

        mov eax, DWORD PTR [ebp+12]
        mov esi, DWORD PTR [ebp+8]
        mov BYTE PTR [esi], al


        mov esp, ebp
        pop ebp
        ret

###################################
###################################

.data
        fmt_pi: .asciz  "%d\n"
        fmt_pc: .asciz  "%c\n"
        fmt_ps: .asciz	"%s"
        fmt_gi: .asciz  "%d\n"
        fmt_gc: .asciz  "%c\n"
        
        scanf_fmt_gi: .asciz  "%d"
        scanf_fmt_gc: .asciz  "%c"
     
