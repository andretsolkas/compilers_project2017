 #!/bin/bash

java -jar sablecc.jar ./myParser/parser.sable

make

echo -e "\n\n\nCompiled\nExecute the compiler example:\n	./cgrace -o a.out ./examples/hello.grace\n"
