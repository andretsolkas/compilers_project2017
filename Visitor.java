import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.util.*;

	class Visitor extends DepthFirstAdapter
	{
		
		SymbolTable symtable;
		
		int dataTypeMode;														//0 if parameter, 1 if return_value
		int headerMode;															//0 if fun definition, 1 if fun declaration
		
		LinkedList<TypeCheck> typeCheck = new LinkedList<>();					//Using it as a stack
		LinkedList<Node> funcDefinition = new LinkedList<>();					//Using it as a stack
		boolean returned = false;
		
		//Members used for the symbol table construction
		Boolean reference;
		String datatype;
		Key name;
		Key funname;
		LinkedList <Param> params;
		LinkedList <Integer> arraylist;											//In case of array- list's size depicts array's dimensions
		LinkedList <Key> idlist;
		
		String retvalue;
		
		public Visitor(){
			
			symtable = new SymbolTable();
			initialize();
		}

		private void initialize(){

			reference = null;
			datatype = null;
			name = null;
			funname = null;
			params = new LinkedList<>();
			arraylist = new LinkedList<>();
			idlist = new LinkedList<>();
		}
		
		
		private void typeCheckerInt(String str){
	        
			TypeCheck opRight = typeCheck.removeLast();				//opLeft and opRight must be primitive integers
			TypeCheck opLeft = typeCheck.removeLast();

			if(!(opLeft.type.equals("int") && opRight.type.equals("int")) || 											//Not Both Integers OR
				(opLeft.dimensions > 0 && (opLeft.indices == null || (opLeft.indices.size() != opLeft.dimensions) ||	//opLeft not primitive OR
				(opRight.dimensions > 0 && (opRight.indices == null || (opRight.indices.size() != opRight.dimensions))	//opRight not primitive
			{
				System.out.println("Error: Expr " + str + " 's operands must both be primitive integers\n");
	        	System.exit(1);
			}

			typeCheck.addLast(new TypeCheck(opLeft.type, null, null, null, 0));
		}
		

		private void print_typeCheck(){
			System.out.println("Type Check Stack:\n");
			for(int i=0; i<typeCheck.size(); i++){
				
				System.out.println(i);
				
				typeCheck.get(i).print();
			}
			System.out.println("\n");
		}

		//////////////////////////////////////
		
        @Override
		public void outAProgram(AProgram node){
           symtable.exit();
		}

        @Override
		public void inAFuncdefLocalDef(AFuncdefLocalDef node)
	    {
	        headerMode = 0;
	        symtable.enter();   
	    }

        @Override
		public void outAFuncdefLocalDef(AFuncdefLocalDef node)
	    {
	        Node nd = funcDefinition.removeLast();

	        if(!nd.retvalue.equals("nothing") && returned == false){									//if function is not supposed to return 'nothing', yet returned value is false
        		System.out.println("Error: Function must have a return statement\n");
	        	System.exit(1);
	        }
	        
	        returned = false;
	    }
        
        @Override
	    public void inAFuncdeclLocalDef(AFuncdeclLocalDef node)
	    {
	        headerMode = 1;
        }


        @Override
	    public void inAVardefLocalDef(AVardefLocalDef node)
	    {
	        initialize();
	        dataTypeMode = 0;
	    }

        @Override
	    public void outAVardefLocalDef(AVardefLocalDef node)
	    {
	    	Key key;
	    	for(int i=0; i<node.getId().size(); i++){
            	key = new Key(node.getId().get(i).getText());

                if(1 == symtable.SearchKey(key)){
                    System.out.println("Error: variable " + key.name + " has been declared before in this scope\n");
                    System.exit(1);
                }
                
                
	    		symtable.insert(key, datatype, false, arraylist, null, null, null);
	    	}
	    }
        
	    ////////////////////////////////////////////////////////////////////////////////////////////////////

	    @Override
	    public void inAHeader(AHeader node)
	    {
	    	initialize();
	    }

	    @Override
	    public void caseAHeader(AHeader node)
	    {
	        inAHeader(node);
	        
	        funname = new Key(node.getId().getText());														//Functions' id name	        
	        if(node.getId() != null){
	            node.getId().apply(this);
	        }
	        
	        dataTypeMode = 1;																				//Functions' return type
	        if(node.getRetType() != null){
	            node.getRetType().apply(this);
	        }
	        dataTypeMode = 0;

            Key key;
            Param param;
            
            if(node.getFparDef().isEmpty())
            	params = null;

            List<PFparDef> copy = new ArrayList<>(node.getFparDef());
            for(PFparDef e : copy)
            {
                e.apply(this);
	                
            	for(int i=0; i<idlist.size(); i++){															//Insert all parameter-variables into the symbol table

                    key = idlist.get(i);

                    if(headerMode == 0){
                        if(1 == symtable.SearchKey(key)){
                            System.out.println("Error: variable " + key.name + " has been declared before in this scope\n");
                            System.exit(0);
                        }
                        symtable.insert(key, datatype, reference, arraylist, null, null, null);
                    }
                    
                    param = new Param(datatype, key, arraylist);												//Add a parameter into the parameter list 
                    params.add(param);
            	
            	}
                
            	arraylist = new LinkedList<>();
                idlist = new LinkedList<>();			 																//Initialize id list 
            }

            /************************************/
    		symtable.decrease_scope();																			//Function's prototype belongs to the previous scope
    		
    		if(headerMode == 0){																				//If function definition
    			symtable.insert(funname, null, null, null, params, true, retvalue);
    			funcDefinition.addLast(symtable.lookup(funname));
    		}
    		else symtable.insert(funname, null, null, null, params, false, retvalue);							//If function declaration
    		
			symtable.increase_scope();
			
			outAHeader(node);
	    }

        @Override
	    public void inAWithrefFparDef(AWithrefFparDef node)
	    {
	    	reference = true;

	    	for(int i=0; i<node.getId().size(); i++){
	    		idlist.add(new Key(node.getId().get(i).getText()));
	    	}
	    }

        @Override
	    public void inAWithoutrefFparDef(AWithoutrefFparDef node)
	    {
	        reference = false;
	        
	    	for(int i=0; i<node.getId().size(); i++){
	    		idlist.add(new Key(node.getId().get(i).getText()));
	    	}
	    }

        @Override
	    public void inANoarrayFparType(ANoarrayFparType node)
	    {		arraylist = null;
	    }

        @Override
	    public void inAArrayFirstFparType(AArrayFirstFparType node)
	    {
	    	arraylist.addFirst(0);
	    }

        @Override
	    public void inAArrayFirstsecFparType(AArrayFirstsecFparType node)
	    {
	    	arraylist.addFirst(0);
	    	for(int i=0; i<node.getNumber().size(); i++){
	    		arraylist.add(Integer.parseInt(node.getNumber().get(i).getText()));
	    	}
	    }

        @Override
	    public void inAArraySecFparType(AArraySecFparType node)
	    {
	    	for(int i=0; i<node.getNumber().size(); i++){
	    		arraylist.add(Integer.parseInt(node.getNumber().get(i).getText()));
	    	}
	    }

	    
        @Override
	    public void inANoneRetType(ANoneRetType node)
	    {
	        retvalue = "nothing";
	    }
	    
        @Override
	    public void inAIntDataType(AIntDataType node)
	    {
	    	if(dataTypeMode == 0)
	    		datatype = "int";
	    	
	    	else retvalue = "int";
        }

        @Override
	    public void inACharDataType(ACharDataType node)
	    {
	    	if(dataTypeMode == 0)
	    		datatype = "char";
	    	
	    	else retvalue = "char";
	    }
	    
	    //////////////////////////////////////////////////////////////////////////////////////////////////
		
        @Override
	    public void inAArrayType(AArrayType node)
	    {
	    	for(int i=0; i<node.getNumber().size(); i++){
	    		arraylist.add(Integer.parseInt(node.getNumber().get(i).getText()));
	    	}
	    }


	    
	    /////////////////////////////////////
	    

        @Override
	    public void outAAssignmentStmtexpr(AAssignmentStmtexpr node)
	    {
        	TypeCheck opRight = typeCheck.removeLast();
        	TypeCheck opLeft = typeCheck.removeLast();
	        
        	if(opLeft.dimensions > 0 && (opLeft.indices == null || (opLeft.indices.size() != opLeft.dimensions)){
        		System.out.println("Error: Assignment: Left Value must be of type t where t can't be an Array\n");
	        	System.exit(1);
        	}
        	
        	if(!opLeft.type.equals(opRight.type) || (opRight.dimensions > 0 && (opRight.indices == null || (opRight.indices.size() != opRight.dimensions)){
        		System.out.println("Error: Assignment: Left Value and Right Value are of different types\n");
	        	System.exit(1);
        	}	
	    }

        
        @Override
        public void outAFblockStmtexpr(AFblockStmtexpr node)
        {
        	symtable.exit();
        }
        
        @Override
	    public void inABlockStmtexpr(ABlockStmtexpr node)
	    {
	        symtable.enter();
	    }

        @Override
	    public void outABlockStmtexpr(ABlockStmtexpr node)
	    {	
	        symtable.exit();
	    }


        @Override
	    public void outAReturnexprStmtexpr(AReturnexprStmtexpr node)
	    {
	        TypeCheck tp = typeCheck.removeLast();
	        Node nd = funcDefinition.getLast();

	        if(nd.retvalue.equals("nothing")){
        		System.out.println("Error: Function must not return an expression\n");
	        	System.exit(1);
	        }
	        
	        if(!tp.type.equals(nd.retvalue)){
        		System.out.println("Error: Function must return " + nd.retvalue + ", not " + tp.type);
	        	System.exit(1);
	        }
	        
	        returned = true;
	    }

        @Override
	    public void outAReturnNoneStmtexpr(AReturnNoneStmtexpr node)
	    {
	        Node nd = funcDefinition.getLast();

	        if(!nd.retvalue.equals("nothing")){
        		System.out.println("Error: Function must return an expression\n");
	        	System.exit(1);
	        }
	        
	        returned = true;
	    }

        @Override
	    public void inAIfStmtexpr(AIfStmtexpr node)
	    {
        	symtable.enter();
	    }

        @Override
	    public void outAIfStmtexpr(AIfStmtexpr node)
	    {
        	symtable.exit();
	    }
	    
        @Override
	    public void inAIfelseStmtexpr(AIfelseStmtexpr node)
	    {
	        symtable.enter();
	    }

        @Override
	    public void outAIfelseStmtexpr(AIfelseStmtexpr node)
	    {
	        symtable.exit();
	    }

        @Override
	    public void inAWhileStmtexpr(AWhileStmtexpr node)
	    {
	        symtable.enter();
	    }

        @Override
	    public void outAWhileStmtexpr(AWhileStmtexpr node)
	    {
	        symtable.exit();
	    }

        @Override
	    public void outAPlusStmtexpr(APlusStmtexpr node)
	    {
        	typeCheckerInt("Plus");
	    }

        @Override
	    public void outAMinusStmtexpr(AMinusStmtexpr node)
	    {
        	typeCheckerInt("Minus");
	    }

        @Override
        public void outAMultStmtexpr(AMultStmtexpr node)
        {
        	typeCheckerInt("Mult");
        }

        @Override
        public void outADivStmtexpr(ADivStmtexpr node)
        {
        	typeCheckerInt("Div");
        }

        @Override
        public void outAModStmtexpr(AModStmtexpr node)
        {
        	typeCheckerInt("Mod");
        }

        @Override
        public void outAPosStmtexpr(APosStmtexpr node)
        {
         //
        }

        @Override
        public void outANegStmtexpr(ANegStmtexpr node)
        {
        	//
        }

        
        
        
        @Override
        public void outALValueStmtexpr(ALValueStmtexpr node)
        {
        	TypeCheck value = typeCheck.getLast();
        	
        	if(value.indices != null){																		//In case the given input is array	 											
        		
        		if(value.dimensions == 0){																	//In case the corresponding variable is declared as a primitive type
           			System.out.println("Error: Variable " + value.idname + " : is primitive, yet, it's been treated as an array\n");
            		System.exit(1);
        		}        	
        		
        		else{																						//In case the corresponding variable is declared as an array			
        		
        			if(value.indices.size() > value.dimensions){
		        		System.out.println("Error: Variable " + value.idname + " : Array is being given too many indices\n");
		        		System.exit(1);
	        		}
        		
        			if(value.idname != null){											//Not a string -- therefore, it can be searched in the symbol table
        				Node myNode = symtable.lookup(new Key(value.idname));			//No need to check if returned value is null, as i am sure at this point that variable with such idname exists
        			
        				String index;
        				int declindex;

        				for(int i=0; i<value.indices.size(); i++){												//Check for index boundaries
        					
        					index = value.indices.get(i);
        					declindex = myNode.arraylist.get(i);
        					
			        		if(!index.equals("int") && !index.equals("char") && declindex != 0){				//Then index is been given a number -- so i can perform an index-bound check
			        			
				        		if(declindex <= Integer.parseInt(index)){
				        			System.out.println("Error: Variable " + value.idname + " : index out of bound\n");
				            		System.exit(1);
				        		}
				        	}
			        	}
		        	}
		        }
        	}
        }
        
        
        
        
        @Override
        public void outAConcharStmtexpr(AConcharStmtexpr node)
        {
        	typeCheck.addLast(new TypeCheck("char", null, null, null, 0));
        }
	    
        @Override
        public void outANumStmtexpr(ANumStmtexpr node)
        {
        	typeCheck.addLast(new TypeCheck("int", null, node.getNumber().getText(), null, 0));
        }

        
        @Override
        public void outAIdStmtexpr(AIdStmtexpr node)
        {
        	Key key = new Key(node.getId().getText());        	
        	Node n = symtable.lookup(key);

        	if(n == null){
        		System.out.println("Error: Variable " + key.name + " has not been declared before\n");
        		System.exit(1);
        	}
        	
        	int dimensions;
        	if(n.arraylist == null)		//Variable has not been defined as an array
        		dimensions = 0;
        	else dimensions = n.arraylist.size();
        	
        	typeCheck.addLast(new TypeCheck(n.type, null, null, node.getId().getText(), dimensions));
        }
        	
        @Override
        public void outAStrStmtexpr(AStrStmtexpr node)
        {      	
        	typeCheck.addLast(new TypeCheck("char", null, null, null, 1));		//String's type is char[]
        }

        
        
        
        @Override
        public void outAArrayStmtexpr(AArrayStmtexpr node)
        {
        	TypeCheck rightExpr = typeCheck.removeLast();				//rightExpr is the Index of the array
			TypeCheck leftId = typeCheck.removeLast();					//leftId is the array's id -- it could be also a string

			if(!rightExpr.type.equals("int")){ 							//Must check if right value is an integer
        		System.out.println("Error: Variable " + leftId.idname + " Type " +   rightExpr.type + " .. Array index is not int\n");
        		System.exit(1);
			}
																		//Right expr is integer but it may not be primitive - it could be an array
			
			if(rightExpr.dimensions != rightExpr.indices.size()){		//It is primitive only when the number of given indices equals to the number of indices with which it was declared
        		System.out.println("Error: Variable " + leftId.idname + " Type " +   rightExpr.type + " .. Array index is not a primitive integer type\n");
        		System.exit(1);
			}
			
			String str = rightExpr.num;									//In case its value is known it's been passed for later index boundary checking
			if(str == null)
				str = "int";
            
			if(leftId.indices == null){
				leftId.indices = new LinkedList<>();
			}
			leftId.indices.addLast(str);
			
			typeCheck.addLast(new TypeCheck(leftId.type, leftId.indices, null, leftId.idname, leftId.dimensions));

        }

        
        
        
        
        @Override
        public void caseAFuncallStmtexpr(AFuncallStmtexpr node)
        {
            inAFuncallStmtexpr(node);
            if(node.getId() != null)
            {
                node.getId().apply(this);
            }
            
            Key key = new Key(node.getId().getText());
        	Node n = symtable.lookup(key);

        	if(n == null){
        		System.out.println("Error: Function " + key.name + " has not been declared before\n");
        		System.exit(1);
        	}
        	
        	if(n.retvalue == null){
        		System.out.println("Error: " + key.name + " is not a function\n");
        		System.exit(1);
        	}
        	
        	if(n.defined == false){
        		System.out.println("Error: Function " + key.name + " is declared but not defined\n");
        		System.exit(1);
        	}
        	
        	/**********************************/
            {															//Get the parameters one by one, add them to ps list
            	TypeCheck tpc;
            	LinkedList<Param> ps = new LinkedList<>();
            	
                List<PStmtexpr> copy = new ArrayList<PStmtexpr>(node.getStmtexpr());
                for(PStmtexpr e : copy)
                {
                    e.apply(this);
                
                    tpc = typeCheck.removeLast();														//Remove from stack the parameter
                    
                    ps.addLast(new Param(tpc.type, null, tpc.declarraylist));							//Add it to the ps list
                
                }


                if(n.params != null){
	                if(ps.size() != n.params.size()){
	                	System.out.println("Error: Function " + n.name.name + ": different amount of arguments\n");
	            		System.exit(1);
	                }

                }
                
                else if(!ps.isEmpty()){
                	System.out.println("Error: Function " + n.name.name + ": has no parameters, yet it is given arguments\n");
            		System.exit(1);
                }
                
                for(int i=0; i<ps.size(); i++){													//Check the arguments of the function
                	if(n.params.get(i).type.equals(ps.get(i).type)){
                		
                		if(n.params.get(i).arraylist != null){									//In case of an array -- check the indexes of each array
                			
                			if(ps.get(i).arraylist == null){
                				System.out.println("Error: Function " + n.name.name + ": argument: given primitive but expects array\n");
        	            		System.exit(1);
                			}
                			
                			
                			if(n.params.get(i).arraylist.size() != ps.get(i).arraylist.size()){
                				System.out.println("Error: Function " + n.name.name + ": array argument with wrong dimensions\n");
                        		System.exit(1);
                			}
                		
                				for(int j=0; j<ps.get(i).arraylist.size(); j++){
                						
                					if(ps.get(i).arraylist.get(j) >= n.params.get(i).arraylist.get(j)){							//WATCH IN CASE OF [] 0!!!!!!!!!
                	                	
                				System.out.println(ps.get(i).arraylist.get(j));
                				System.out.println(n.params.get(i).arraylist.get(j));
                						System.out.println("Error: Function " + n.name.name + ": array argument: index out of bounds\n");
                	            		System.exit(1);
                					}
                				}
                		}
                        
                        else if(ps.get(i).arraylist != null && !ps.get(i).arraylist.isEmpty()){
                            System.out.println("Error: Function " + n.name.name + ": argument: given array but expects primitive argument\n");
                            System.exit(1);
                        }

                	}
                	
                	else{
                		System.out.println("Error: Function " + n.name.name + ": argument of different type than expected\n");
                		System.exit(1);
                	}
                }
                

                typeCheck.addLast(new TypeCheck(n.retvalue, null, null, null, null));				//Add the return type on stack

            }
            
            outAFuncallStmtexpr(node);
        }
        
        
        
        
        
        
        
        
        
	}