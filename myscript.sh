 #!/bin/bash

java -jar sablecc.jar ./parser.sable

make

echo -e "\n\n\nCompiled\n\n\nExecute example:\n	java Main ./examples/hello.grace\n"
