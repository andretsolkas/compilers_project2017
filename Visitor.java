import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.util.*;

	class Visitor extends DepthFirstAdapter
	{
		
		SymbolTable symtable;
		
		int dataTypeMode;								//0 if parameter, 1 if return_value
		int headerMode;									//0 if fun definition, 1 if fun declaration
		
		LinkedList<TypeCheck> typeCheck = new LinkedList<>();				//Functions as a stack
		
		Boolean reference;
		String datatype;
		Key name;
		Key funname;
		LinkedList <Param> params;
		LinkedList <Integer> arraylist;					//In case of array- list's size depicts array's dimensions
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
	        
			TypeCheck opRight = typeCheck.removeLast();
			TypeCheck opLeft = typeCheck.removeLast();

			if(opLeft.type.equals("int") && opRight.type.equals("int")){
				if(opLeft.declarraylist == null && opRight.declarraylist == null){				//Not arrays
					typeCheck.addLast(new TypeCheck(opLeft.type, null, null, null, null));
				}
	        }
			
	        else{
	        	System.out.println("Error: Expr " + str + " has operands of different types\n");
	        	System.exit(1);
	        }
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
	    }		

        @Override
	    public void inAFuncdeclLocalDef(AFuncdeclLocalDef node)
	    {
	        headerMode = 1;
        }

        @Override
	    public void outAFuncdeclLocalDef(AFuncdeclLocalDef node)
	    {
	        defaultOut(node);
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
	    public void inAWithparsHeader(AWithparsHeader node)
	    {
	    	initialize();
	    }
	    
        @Override
	    public void outAWithparsHeader(AWithparsHeader node)
	    {	//
	    }

	    @Override
	    public void caseAWithparsHeader(AWithparsHeader node)
	    {
	        inAWithparsHeader(node);
	        
	        funname = new Key(node.getId().getText());											//Functions' id name	        
	        if(node.getId() != null){
	            node.getId().apply(this);
	        }
	        
	        dataTypeMode = 1;																				//Functions' return type
	        if(node.getRetType() != null){
	            node.getRetType().apply(this);
	        }

            Key key;
            Param param;
            
	        dataTypeMode = 0;
            List<PFparDef> copy = new ArrayList<>(node.getFparDef());
            for(PFparDef e : copy)
            {
                e.apply(this);
	                
            	for(int i=0; i<idlist.size(); i++){														//Insert all parameter-variables into the symbol table

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
            	
                idlist = new LinkedList<>(); 															//Initialize id list 
            }

    		symtable.decrease_scope();																		//Function's prototype belongs to the previous scope
    		
    		if(headerMode == 0)																				//If function definition
    			symtable.insert(funname, null, null, null, params, true, retvalue);
    		
    		else symtable.insert(funname, null, null, null, params, false, retvalue);						//If function declaration
    		
			symtable.increase_scope();
    		
	        outAWithparsHeader(node);
	    }

        @Override
	    public void inAWithoutparsHeader(AWithoutparsHeader node)
	    {	
	    	initialize();
	    }

        @Override
	    public void outAWithoutparsHeader(AWithoutparsHeader node)
	    {
	        funname = new Key(node.getId().getText());						//Functions' id name	        
	        if(node.getId() != null){
	            node.getId().apply(this);
	        }

	        dataTypeMode = 1;															//Functions' return type
	        if(node.getRetType() != null){
	            node.getRetType().apply(this);
	        }
	        
	        symtable.decrease_scope();
	        
	        if(headerMode == 0)
	        	symtable.insert(funname, null, null, null, null, true, retvalue);							//If function definition
	    
	        else symtable.insert(funname, null, null, null, null, false, retvalue);							//If function declaration

        	symtable.increase_scope();
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
	    public void outAWithrefFparDef(AWithrefFparDef node)
	    {
	        defaultOut(node);
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
	    public void outAWithoutrefFparDef(AWithoutrefFparDef node)
	    {
	        defaultOut(node);
	    }
	    
        @Override
	    public void inANoarrayFparType(ANoarrayFparType node)
	    {		//array list is empty
	    }

        @Override
	    public void outANoarrayFparType(ANoarrayFparType node)
	    {
	        defaultOut(node);
	    }
	    
        @Override
	    public void inAArrayFirstFparType(AArrayFirstFparType node)
	    {
	    	arraylist.addFirst(0);
	    }

        @Override
	    public void outAArrayFirstFparType(AArrayFirstFparType node)
	    {
	        defaultOut(node);
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
	    public void outAArrayFirstsecFparType(AArrayFirstsecFparType node)
	    {
	        defaultOut(node);
	    }
	    
        @Override
	    public void inAArraySecFparType(AArraySecFparType node)
	    {
	    	for(int i=0; i<node.getNumber().size(); i++){
	    		arraylist.add(Integer.parseInt(node.getNumber().get(i).getText()));
	    	}
	    }

        @Override
	    public void outAArraySecFparType(AArraySecFparType node)
	    {
	        defaultOut(node);
	    }

        @Override
	    public void inADataRetType(ADataRetType node)
	    {
	        defaultOut(node);
	    }

        @Override
	    public void outADataRetType(ADataRetType node)
	    {
	        defaultOut(node);
	    }
	    
        @Override
	    public void inANoneRetType(ANoneRetType node)
	    {
	        retvalue = "nothing";
	    }

        @Override
	    public void outANoneRetType(ANoneRetType node)
	    {
	        defaultOut(node);
	    }
	    
        @Override
	    public void inAIntDataType(AIntDataType node)
	    {
	    	if(dataTypeMode == 0)
	    		datatype = "int";
	    	
	    	else retvalue = "int";
        }

        @Override
	    public void outAIntDataType(AIntDataType node)
	    {
	        defaultOut(node);
	    }
	    
        @Override
	    public void inACharDataType(ACharDataType node)
	    {
	    	if(dataTypeMode == 0)
	    		datatype = "char";
	    	
	    	else retvalue = "char";
	    }

        @Override
	    public void outACharDataType(ACharDataType node)
	    {
	        defaultOut(node);
	    }
	    
	    //////////////////////////////////////////////////////////////////////////////////////////////////
		
        @Override
	    public void inAArrayType(AArrayType node)
	    {
	    	for(int i=0; i<node.getNumber().size(); i++){
	    		arraylist.add(Integer.parseInt(node.getNumber().get(i).getText()));
	    	}
	    }

        @Override
	    public void outAArrayType(AArrayType node)
	    {
	        defaultOut(node);
	    }

        @Override
	    public void inAPrimitiveType(APrimitiveType node)
	    {
	        defaultIn(node);
	    }

        @Override
	    public void outAPrimitiveType(APrimitiveType node)
	    {
	        defaultOut(node);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    

	    
	    /////////////////////////////////////
	    
        @Override
	    public void inANoneStmtexpr(ANoneStmtexpr node)
	    {
	        defaultIn(node);
	    }

        @Override
	    public void outANoneStmtexpr(ANoneStmtexpr node)
	    {
	        defaultOut(node);
	    }

        @Override
	    public void outAAssignmentStmtexpr(AAssignmentStmtexpr node)
	    {
        	TypeCheck opRight = typeCheck.removeLast();
        	TypeCheck opLeft = typeCheck.removeLast();
	        
        	if(!opLeft.type.equals(opRight.type)){
        		System.out.println("Error: Assignment: Left Value and Right Value are of different types\n");
	        	System.exit(1);
        	}
        	
	    }
        
        @Override
        public void inAFblockStmtexpr(AFblockStmtexpr node)
        {
            defaultIn(node);
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
	    public void inAReturnexprStmtexpr(AReturnexprStmtexpr node)
	    {
	        defaultIn(node);
	    }

        @Override
	    public void outAReturnexprStmtexpr(AReturnexprStmtexpr node)
	    {
	        defaultOut(node);
	    }

        @Override
	    public void inAReturnStmtexpr(AReturnStmtexpr node)
	    {
	        defaultIn(node);
	    }

        @Override
	    public void outAReturnStmtexpr(AReturnStmtexpr node)
	    {
	        defaultOut(node);
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

        public void outAMultStmtexpr(AMultStmtexpr node)
        {
        	typeCheckerInt("Mult");
        }

        public void outADivStmtexpr(ADivStmtexpr node)
        {
        	typeCheckerInt("Div");
        }

        public void outAModStmtexpr(AModStmtexpr node)
        {
        	typeCheckerInt("Mod");
        }
  
        

        public void inAPosStmtexpr(APosStmtexpr node)
        {
            defaultIn(node);
        }

        public void outAPosStmtexpr(APosStmtexpr node)
        {
            defaultOut(node);
        }
	    	    
        public void inANegStmtexpr(ANegStmtexpr node)
        {
            defaultIn(node);
        }

        public void outANegStmtexpr(ANegStmtexpr node)
        {
            defaultOut(node);
        }

        
        
        
        public void outALValueStmtexpr(ALValueStmtexpr node)
        {
        	TypeCheck value = typeCheck.getLast();
        	
        	if(value.declarraylist != null){ 
        		
        		if(value.arraylist.size() != value.declarraylist.size()){					//If is an array
	        		System.out.println("Error: Variable " + value.idname + " : different number of ARRAY arguments \n");
	        		System.exit(1);
        		}
        		
	        	for(int i=0; i<arraylist.size(); i++){
	        		if(Integer.parseInt(value.arraylist.get(i)) <= Integer.parseInt(value.arraylist.get(i))){
	 
	        			System.out.println("Error: Variable " + value.idname + " : out of bound\n");
	            		System.exit(1);
	        		}
	        	}
        	}
        }
        
        public void outAConcharStmtexpr(AConcharStmtexpr node)
        {
        	typeCheck.addLast(new TypeCheck("char", null, null, null, null));
        	print_typeCheck();
        }
	    
        public void outANumStmtexpr(ANumStmtexpr node)
        {
        	typeCheck.addLast(new TypeCheck("int", null, node.getNumber().getText(), null, null));
        	print_typeCheck();
        }

        public void outAIdStmtexpr(AIdStmtexpr node)
        {
        	Key key = new Key(node.getId().getText());
        	
        	Node n = symtable.lookup(key);

        	if(n != null)
        		typeCheck.addLast(new TypeCheck(n.type, new LinkedList <String>(), null, node.getId().getText(), n.arraylist));
        	
        	else {
        		System.out.println("Error: Variable " + key.name + " has not been declared before\n");
        		System.exit(1);
        	}
        	print_typeCheck();
        }

        public void outAStrStmtexpr(AStrStmtexpr node)
        {
        	typeCheck.addLast(new TypeCheck(node.getString().getText(), null, null, null, null));						//MAYBE NEEDS FIX IN CASE IT CAN BE AN ARRAY
        	print_typeCheck();
        }

        public void outAArrayStmtexpr(AArrayStmtexpr node)
        {
        	TypeCheck rightExpr = typeCheck.removeLast();
			TypeCheck leftId = typeCheck.removeLast();

			if(!leftId.type.equals("int")){
				
				String str = rightExpr.num;
				
				if(str == null)
					str = leftId.type;
				
				
				LinkedList <String> arlist = leftId.arraylist;
				arlist.addLast(str);
				
				typeCheck.addLast(new TypeCheck(leftId.type, arlist, null, leftId.idname, leftId.declarraylist));

			}
			
			else{
        		System.out.println("Error: Variable " + leftId.idname + " .. Array index is not int\n");
        		System.exit(1);
			}
			print_typeCheck();	
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
        		System.out.println("Error: Function " + key.name + " has not been declared/defined before\n");
        		System.exit(1);
        	}
        	
        	if(n.retvalue == null){
        		System.out.println("Error: " + key.name + " is not a function\n");
        		System.exit(1);
        	}
        	
            {
            	TypeCheck tpc;
            	LinkedList<Param> ps = new LinkedList<>();
            	
                List<PStmtexpr> copy = new ArrayList<PStmtexpr>(node.getStmtexpr());
                for(PStmtexpr e : copy)
                {
                    e.apply(this);
                
                    tpc = typeCheck.removeLast();
                    ps.addLast(new Param(tpc.type, new Key(tpc.idname), tpc.declarraylist));
                
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
                
                for(int i=0; i<ps.size(); i++){								//Check the parameters of the function
                	if(n.params.get(i).type.equals(ps.get(i).type)){
                		
                		if(n.params.get(i).arraylist != null && ps.get(i).arraylist != null){									//In case of an array -- check the indexes of each array
                			if(n.params.get(i).arraylist.size() != ps.get(i).arraylist.size()){
                				System.out.println("Error: Function " + n.name.name + ": array argument with wrong dimensions\n");
                        		System.exit(1);
                			}
                		
                			else{
                				for(int j=0; j<ps.get(i).arraylist.size(); j++){
                						
                					if(ps.get(i).arraylist.get(j).intValue() >= n.params.get(i).arraylist.get(j).intValue()){							//WATCH IN CASE OF [] 0!!!!!!!!!
                	                	System.out.println("Error: Function " + n.name.name + ": argument: index out of bounds\n");
                	            		System.exit(1);
                					}
                				}
                			}
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