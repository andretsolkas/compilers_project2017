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

_gets_0:










###################################
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

.data
        fmt_pi: .asciz  "%d\n"
        fmt_pc: .asciz  "Output: %c\n"
        fmt_ps: .asciz	"%s"
        fmt_gi: .asciz  "Your integer input is %d\n"
        fmt_gc: .asciz  "Your char input is %c\n"
        #gets
        scanf_fmt_gi: .asciz  "%d"
        scanf_fmt_gc: .asciz  "%c"
