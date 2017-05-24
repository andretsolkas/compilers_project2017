import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.util.*;

	class Visitor extends DepthFirstAdapter
	{
		
		SymbolTable symtable;
		QuadManager quadManager;
		
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
			quadManager = new QuadManager();
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
		
		
		private void typeCheckerExpr(String str){
	        
			TypeCheck opRight = typeCheck.removeLast();				//opLeft and opRight must be primitive integers
			TypeCheck opLeft = typeCheck.removeLast();

			if(!(opLeft.type.equals("int") && opRight.type.equals("int")) || 											//Not Both Integers OR
				(opLeft.dimensions > 0 && (opLeft.indices == null || (opLeft.indices.size() != opLeft.dimensions))) ||	//opLeft not primitive OR
				(opRight.dimensions > 0 && (opRight.indices == null || (opRight.indices.size() != opRight.dimensions))))	//opRight not primitive
			{
				System.out.println("Error: Expr " + str + " 's operands must both be primitive integers\n");
	        	System.exit(1);
			}

			typeCheck.addLast(new TypeCheck(opLeft.type, null, null, null, 0));
		}
		
		private void typeCheckerCond(String str){
	        
			TypeCheck opRight = typeCheck.removeLast();				//opLeft and opRight must be primitive integers
			TypeCheck opLeft = typeCheck.removeLast();

			if(!(opLeft.type.equals("int") && opRight.type.equals("int")) || 											//Not Both Integers OR
				(opLeft.dimensions > 0 && (opLeft.indices == null || (opLeft.indices.size() != opLeft.dimensions))) ||	//opLeft not primitive OR
				(opRight.dimensions > 0 && (opRight.indices == null || (opRight.indices.size() != opRight.dimensions))))	//opRight not primitive
			{
				System.out.println("Error: Cond " + str + " 's operands must both be primitive integers\n");
	        	System.exit(1);
			}
		}

		
		//////////////////////////////////////
		
        @Override
		public void outAProgram(AProgram node){
           symtable.exit();
           
           //quadManager.printQuads();
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

	        IRelement irel = quadManager.stack.removeLast();
	        quadManager.backpatch(irel.next, quadManager.nextQuad());
	        
	        quadManager.genQuad("endu", nd.name.name, "_", "_");	        
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


	    @Override
	    public void caseAHeader(AHeader node)
	    {
	    	initialize();
	        
	        funname = new Key(node.getId().getText());														//Functions' id name	        
	        if(node.getId() != null){
	            node.getId().apply(this);
	        }

	        /*************************************************/
	        
	    	if(headerMode == 0){																			//If function definition
	    		
    			symtable.decrease_scope();
	    	
    			if(1 == symtable.SearchKey(funname)){
    	    		
    				Node nd = symtable.lookup(funname);
    				
    	    		if(nd.defined == true){
                        System.out.println("Error: Function " + funname.name + " is being  redefined\n");
                        System.exit(1);
    				}
    	    		
    	    		else nd.defined = true;
    	    	}
    			
    			symtable.increase_scope();
    			
    			quadManager.genQuad("unit", funname.name, "_", "_");
    			
    		}
    		
    		else {																							//If function declaration
    			if(1 == symtable.SearchKey(funname)){

    				Node nd = symtable.lookup(funname);
    				
    	    		if(nd.defined == true){
	    				System.out.println("Error: Function " + funname.name + " has been defined before, yet it is being redeclared\n");
	                    System.exit(1);
    	    		}
    	    		
    	    		else{
	    				System.out.println("Error: Function " + funname.name + " has been declared again before\n");
	                    System.exit(1);
    	    		}
    	    	}
    		}
	    	
	    	
	    	/*************************************************/
	    	
	    	symtable.decrease_scope();
	    	
	        dataTypeMode = 1;																				//Functions' return type
	        if(node.getRetType() != null){
	            node.getRetType().apply(this);
	        }
	        dataTypeMode = 0;

            if(headerMode == 0 && symtable.scope == 0 && !retvalue.equals("nothing")){
				System.out.println("Error: Main Function " + funname.name + " must only return \"nothing\"\n");
                System.exit(1);
            }
	        
            Key key;
            Param param;
            
            if(node.getFparDef().isEmpty())
            	params = null;
            
            else if(headerMode == 0 && symtable.scope == 0){
				System.out.println("Error: Main Function " + funname.name + " can not have parameters\n");
                System.exit(1);
            }
            
            symtable.increase_scope();
            
            /************************************/
            List<PFparDef> copy = new ArrayList<>(node.getFparDef());
            for(PFparDef e : copy)
            {
                e.apply(this);

                if(reference == false && arraylist != null){
                	System.out.println("Error: Function's " + funname.name + " array parameter must be passed by reference\n");
                    System.exit(0);
                }
                
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
    		
    		if(headerMode == 0){																				//If function definition
    		
    			symtable.decrease_scope();																		//Function's prototype belongs to the previous scope
    			
    			symtable.insert(funname, null, null, null, params, true, retvalue);
    			funcDefinition.addLast(symtable.lookup(funname));

    			
    			symtable.increase_scope();
    		}
    		
    		else symtable.insert(funname, null, null, null, params, false, retvalue);							//If function declaration
    		
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
        public void outANoneStmtexpr(ANoneStmtexpr node)
        {
        	quadManager.stack.addLast(new IRelement(null, null, new LinkedList<>(), null, null));
        }

        @Override
	    public void outAAssignmentStmtexpr(AAssignmentStmtexpr node)
	    {
        	TypeCheck opRight = typeCheck.removeLast();
        	TypeCheck opLeft = typeCheck.removeLast();
	        
        	if(opLeft.dimensions > 0 && (opLeft.indices == null || (opLeft.indices.size() != opLeft.dimensions))){
        		System.out.println("Error: Assignment: Left Value must be of type t where t can't be an Array\n");
	        	System.exit(1);
        	}
        	
        	if(!opLeft.type.equals(opRight.type) || (opRight.dimensions > 0 && (opRight.indices == null || (opRight.indices.size() != opRight.dimensions)))){
        		System.out.println("Error: Assignment: Left Value and Right Value are of different types\n");
	        	System.exit(1);
        	}	
	    
        	IRelement right = quadManager.stack.removeLast();
        	IRelement left = quadManager.stack.removeLast();
	    
        	quadManager.genQuad("<-", right.place, "_", left.place);
        	
        	quadManager.stack.addLast(new IRelement(null, null, new LinkedList<>(), null, null));

	    }

        @Override
        public void caseAFblockStmtexpr(AFblockStmtexpr node)
        {
        	IRelement irel;
        	int i;
        	
            if(node.getStmtexpr().size() > 0){
	            	
	        	for(i=0; i<node.getStmtexpr().size()-1; i++){
	                
	            	node.getStmtexpr().get(i).apply(this);
	            	
	            	irel = quadManager.stack.removeLast();
	                quadManager.backpatch(irel.next, quadManager.nextQuad());
	            }
	          
	            
	            node.getStmtexpr().get(i).apply(this);
	        
	            irel = quadManager.stack.removeLast();
	            
	            quadManager.stack.addLast(new IRelement(null, null, irel.next, null, null));
            }
            
            else quadManager.stack.addLast(new IRelement(null, null, new LinkedList<>(), null, null));
            
            symtable.alteredExit();
        }

        @Override
        public void caseABlockStmtexpr(ABlockStmtexpr node)
        {
        	symtable.enter();
            
        	IRelement irel;
        	int i;
        	
        	if(node.getStmtexpr().size() > 0){
	        		
	            for(i=0; i<node.getStmtexpr().size()-1; i++){
	                
	            	node.getStmtexpr().get(i).apply(this);
	            	
	            	irel = quadManager.stack.removeLast();
	                quadManager.backpatch(irel.next, quadManager.nextQuad());
	            }
	          
	            node.getStmtexpr().get(i).apply(this);
	        
	            irel = quadManager.stack.removeLast();
	            
	            quadManager.stack.addLast(new IRelement(null, null, irel.next, null, null));
        	}
        	
        	else quadManager.stack.addLast(new IRelement(null, null, new LinkedList<>(), null, null));
            
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
	        
			if((tp.dimensions > 0 && (tp.indices == null || (tp.indices.size() != tp.dimensions)))){
		        System.out.println("Error: Function " + nd.name.name + " must not retrurn an array\n");
	        	System.exit(1);
		}
	        
	        returned = true;


	        IRelement expr = quadManager.stack.removeLast();
	        
	        quadManager.genQuad("ret", "_", "_", "_");

	        quadManager.stack.addLast(new IRelement(expr.type, expr.place, new LinkedList<>(), null, null));
	        
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
	        
	        quadManager.genQuad("ret", "_", "_", "_");

	        quadManager.stack.addLast(new IRelement(null, null, new LinkedList<>(), null, null));
	    }
	    
        @Override
        public void caseAIfStmtexpr(AIfStmtexpr node)
        {
        	symtable.enter();

        	if(node.getCond() != null)
            {
                node.getCond().apply(this);
            }
            
        	IRelement irel = quadManager.stack.removeLast();
        	
        	quadManager.backpatch(irel.True, quadManager.nextQuad());
        	
        	if(node.getStmtexpr() != null)
            {
                node.getStmtexpr().apply(this);
            }
            
        	quadManager.backpatch(irel.False, quadManager.nextQuad());
            
        	
        	IRelement stmt_right = quadManager.stack.removeLast();
        	quadManager.stack.addLast(new IRelement(null, null, stmt_right.next, null, null));
        	
            symtable.exit();
        }

        @Override
        public void caseAIfelseStmtexpr(AIfelseStmtexpr node)
        {
        	symtable.enter();

            if(node.getCond() != null)
            {
                node.getCond().apply(this);
            }

            IRelement irel = quadManager.stack.removeLast();
        	
        	quadManager.backpatch(irel.True, quadManager.nextQuad());
        	
            if(node.getLeft() != null)
            {
                node.getLeft().apply(this);
            }
            
            Quad quad = quadManager.genQuad("jump", "_", "_", "*");
            LinkedList<Quad> L1 = new LinkedList<>();
            L1.addLast(quad);
            
            quadManager.backpatch(irel.False, quadManager.nextQuad());
            
            
            IRelement stmt_left = quadManager.stack.removeLast();
            LinkedList<Quad> nextlist = quadManager.merge(L1, stmt_left.next);
            
            if(node.getRight() != null)
            {
                node.getRight().apply(this);
            }
            
            IRelement stmt_right = quadManager.stack.removeLast();
            
            nextlist = quadManager.merge(nextlist, stmt_right.next);
            
            quadManager.stack.addLast(new IRelement(null, null, nextlist, null, null));
            
            symtable.exit();
            
            
        }
        
        @Override
        public void caseAWhileStmtexpr(AWhileStmtexpr node)
        {
        	symtable.enter();

        	Integer Q = quadManager.nextQuad();
        	
        	if(node.getCond() != null)
            {
                node.getCond().apply(this);
            }
        	
        	IRelement cond = quadManager.stack.removeLast();
        	quadManager.backpatch(cond.True, quadManager.nextQuad());
        	
            if(node.getStmtexpr() != null)
            {
                node.getStmtexpr().apply(this);
            }
            
            IRelement stmt = quadManager.stack.removeLast();
            quadManager.backpatch(stmt.next, Q);
            
            quadManager.genQuad("jump", "_", "_", Q.toString());
            
            quadManager.stack.addLast(new IRelement(null, null, cond.False, null, null));

            symtable.exit();
        }

        /*******************************************************/
        @Override
	    public void outAPlusStmtexpr(APlusStmtexpr node)
	    {
        	typeCheckerExpr("Plus");
        	quadGenExpr("+");
	    }

        @Override
	    public void outAMinusStmtexpr(AMinusStmtexpr node)
	    {
        	typeCheckerExpr("Minus");
        	quadGenExpr("-");
	    }

        @Override
        public void outAMultStmtexpr(AMultStmtexpr node)
        {
        	typeCheckerExpr("Mult");
        	quadGenExpr("*");
        }

        @Override
        public void outADivStmtexpr(ADivStmtexpr node)
        {
        	typeCheckerExpr("Div");
        	quadGenExpr("/");
        }

        @Override
        public void outAModStmtexpr(AModStmtexpr node)
        {
        	typeCheckerExpr("Mod");
        	quadGenExpr("mod");
        }
        /*******************************************************/
        
        
        @Override
        public void outANegStmtexpr(ANegStmtexpr node)
        {
 
        	TypeCheck value = typeCheck.getLast();
        	if(value.num != null){
        		String newstr = "-";
        		value.num = new String(newstr.concat(value.num));
        	}
        	
        	IRelement irel = quadManager.stack.removeLast();
        	
        	String newTemp = quadManager.newtemp(irel.type);
        	quadManager.genQuad("-", "0", irel.place, newTemp);
        	
        	quadManager.stack.addLast(new IRelement(irel.type, newTemp, null, null, null));
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

			        		if(!index.equals("int") && !index.equals("char")){				//Then index is been given a number -- so i can perform an index-bound check

			        			if(Integer.parseInt(index) < 0 || (declindex != 0 && declindex <= Integer.parseInt(index))){
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
        	
        	quadManager.stack.addLast(new IRelement("char", node.getConchar().getText(), null, null, null));
        }
	    
        @Override
        public void outANumStmtexpr(ANumStmtexpr node)
        {
        	typeCheck.addLast(new TypeCheck("int", null, node.getNumber().getText(), null, 0));
        	
        	quadManager.stack.addLast(new IRelement("int", node.getNumber().getText(), null, null, null));
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
        	
        	quadManager.stack.addLast(new IRelement(n.type, node.getId().getText(), null, null, null));
        	
        }
        
        	
        @Override
        public void outAStrStmtexpr(AStrStmtexpr node)
        {      	
        	typeCheck.addLast(new TypeCheck("char", null, null, null, 1));		//String's type is char[]
        
        	quadManager.stack.addLast(new IRelement("char", node.getString().getText(), null, null, null));					//FIXFIXFIXFIXI
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
			
			int indices = 0;
			if(rightExpr.indices != null)
				indices = rightExpr.indices.size();
			
			if(rightExpr.dimensions != indices){						//It is primitive only when the number of given indices equals the number of indices with which it was declared
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

        	{	/**********************************/
        		//Get the parameters one by one, add them to list
        		
        		TypeCheck tpc;
            	LinkedList<TypeCheck> args = new LinkedList<>();
            	
                List<PStmtexpr> copy = new ArrayList<PStmtexpr>(node.getStmtexpr());
                for(PStmtexpr e : copy)
                {
                    e.apply(this);
                
                    tpc = typeCheck.removeLast();														//Remove from stack the argument
                    args.addLast(tpc);																	//Pass by reference - it won't cause problems  
                }

                /**********************************/
                if(n.params != null){
	                if(args.size() != n.params.size()){
	                	System.out.println("Error: Function " + n.name.name + ": different amount of arguments than expected\n");
	            		System.exit(1);
	                }
                }
                
                else if(!args.isEmpty()){			//Function has no parameters, yet it is given arguments 
                	System.out.println("Error: Function " + n.name.name + ": has no parameters, yet it is given arguments\n");
            		System.exit(1);
                }
                
                /**********************************/		//Check the arguments given to the function
                
                for(int i=0; i<args.size(); i++){
                	
                	if(!(n.params.get(i).type.equals(args.get(i).type))){
                		System.out.println("Error: Function " + n.name.name + ": argument of different type than expected");
                		System.exit(1);
                	}
                	
                	int argIndices = 0;
                	if(args.get(i).indices != null)
                		argIndices = args.get(i).indices.size(); 
                
                	int argDimension = args.get(i).dimensions - argIndices;			//Find out arg's type i.e. boo[0][2] is of type char[4] if boo is declared as: var boo : char[0][2][4] 
                	
                	
                	int paramDimension = 0;
                	if(n.params.get(i).arraylist != null)
                		paramDimension = n.params.get(i).arraylist.size();
                	
                	if(paramDimension != argDimension){
                		System.out.println("Error: Function " + n.name.name + ": argument of different type than expected");
                		System.exit(1);
                	}
                	
	                if(argDimension > 0){				//Array Case - Further Checking
	                	
	                	if(args.get(i).idname != null){												//Not a string -- it is an id
	                		
	                		Node myNode = symtable.lookup(new Key(args.get(i).idname));				//At this point myNode won't be null

	                		int j = myNode.arraylist.size() - argDimension;

	                		for(int x=0; x<paramDimension; j++, x++){
								                		
	                			if(!myNode.arraylist.get(j).equals(n.params.get(i).arraylist.get(x)) && myNode.arraylist.get(j) != 0){
					        		System.out.println("Error: Function " + n.name.name + ": expected an array argument of size " + n.params.get(i).arraylist.get(x) + " instead of " + myNode.arraylist.get(j));
					        		System.exit(1);
	                			}
	                		}
	                	}
	                	
	                	//STRING CASE
                	}
                }

                typeCheck.addLast(new TypeCheck(n.retvalue, null, null, null, 0));				//Add the return type on stack
                quadManager.stack.addLast(new IRelement(null, null, new LinkedList<>(), null, null));		//NOT READY
        	}
            
            outAFuncallStmtexpr(node);
        }


/**********************************************************/
        


        public void outANotCond(ANotCond node)
        {
        	IRelement irel = quadManager.stack.getLast();
        	
        	LinkedList<Quad> temp;
        	temp = irel.True;
        	irel.True = irel.False;
        	irel.False = temp;
        }
        
        
        @Override
        public void caseAAndCond(AAndCond node)
        {
            
            if(node.getLeft() != null)
            {
                node.getLeft().apply(this);
            }
            
            IRelement left = quadManager.stack.removeLast();
            
            quadManager.backpatch(left.True, quadManager.nextQuad());

            if(node.getRight() != null)
            {
                node.getRight().apply(this);
            }

            IRelement right = quadManager.stack.removeLast();
            
            LinkedList<Quad> trueStack = right.True;
            
            LinkedList<Quad> falseStack = quadManager.merge(left.False, right.False);
            		
            quadManager.stack.addLast(new IRelement(null, null, null, trueStack, falseStack));	
        }

        
        @Override
        public void caseAOrCond(AOrCond node)
        {
        	
            if(node.getLeft() != null)
            {
                node.getLeft().apply(this);
            }
            
            IRelement left = quadManager.stack.removeLast();
            
            quadManager.backpatch(left.False, quadManager.nextQuad());
            
            if(node.getRight() != null)
            {
                node.getRight().apply(this);
            }
           
            IRelement right = quadManager.stack.removeLast();
            
            LinkedList<Quad> trueStack = quadManager.merge(left.True, right.True);
            
            LinkedList<Quad> falseStack = right.False;
            		
            quadManager.stack.addLast(new IRelement(null, null, null, trueStack, falseStack));
        }


        
/**************************************************************/
        public void outAEqualCond(AEqualCond node)
        {
        	typeCheckerCond("=");
        	genQuadCond("=");
        }

        public void outANequalCond(ANequalCond node)
        {
        	typeCheckerCond("#");
        	genQuadCond("#");
        }

        public void outALessCond(ALessCond node)
        {
        	typeCheckerCond("<");
        	genQuadCond("<");
        }   

        public void outAGreaterCond(AGreaterCond node)
        {
        	typeCheckerCond(">");
        	genQuadCond(">");
        }

        public void outALesseqCond(ALesseqCond node)
        {
            typeCheckerCond("<=");
            genQuadCond("<=");
        }

        public void outAGreatereqCond(AGreatereqCond node)
        {
        	typeCheckerCond(">=");
        	genQuadCond(">=");
        }
/**************************************************************/
        
        
        
        
    	
        public void quadGenExpr(String opcode){
        	
    		IRelement right = quadManager.stack.removeLast();
        	IRelement left = quadManager.stack.removeLast();
        	
        	String newTemp = quadManager.newtemp(left.type);
        	
        	quadManager.genQuad(opcode, left.place, right.place, newTemp);
        	
        	quadManager.stack.addLast(new IRelement(left.type, newTemp, null, null, null));
    	}
                
        public void genQuadCond(String relop){
        	
    		IRelement right = quadManager.stack.removeLast();
        	IRelement left = quadManager.stack.removeLast();
        	
        	Quad quad1 = quadManager.genQuad(relop, left.place, right.place, "*");
        	
        	LinkedList<Quad> trueStack = new LinkedList<>();
        	trueStack.addLast(quad1);
        	
        	Quad quad2 = quadManager.genQuad("jump", "_", "_", "*");
        	
        	LinkedList<Quad> falseStack = new LinkedList<>();
        	falseStack.addLast(quad2);
        
        	quadManager.stack.addLast(new IRelement(null, null, null, trueStack, falseStack));
        }
        
        
        
        
        
        
        
        
        
        
        
        
        

}