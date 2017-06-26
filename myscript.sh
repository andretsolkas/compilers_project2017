 #!/bin/bash

java -jar ./myParser/sablecc.jar ./myParser/parser.sable -d .

make

echo -e "\n\n\nCompiled\nExecute the compiler example:\n	./cgrace -o a.out ./examples/hello.grace\n"
