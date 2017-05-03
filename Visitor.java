import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;
import java.util.*;

	class Visitor extends DepthFirstAdapter
	{
		
		SymbolTable symtable;
		
		int dataTypeMode;								//0 if parameter, 1 if return value
		
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

		public void initialize(){
			
			dataTypeMode = 0;
			reference = null;
			datatype = null;
			name = null;
			funname = null;
			params = new LinkedList<Param>();
			arraylist  = new LinkedList<Integer>();
			idlist = new LinkedList <Key>();
			
		}
		

		//////////////////////////////////////
	    @Override 
		public void inAFuncdefLocalDef(AFuncdefLocalDef node)
	    {
	        defaultIn(node);
	    }

	    public void outAFuncdefLocalDef(AFuncdefLocalDef node)
	    {
	        defaultIn(node);
	    }		

	    /////////////////////////////////////
		
	    public void inAFuncdeclLocalDef(AFuncdeclLocalDef node)
	    {
	        defaultIn(node);
	    }

	    public void outAFuncdeclLocalDef(AFuncdeclLocalDef node)
	    {
	        defaultOut(node);
	    }
		
	    /////////////////////////////////////
	    
	    public void inAVardefLocalDef(AVardefLocalDef node)
	    {
	        defaultIn(node);
	    }

	    public void outAVardefLocalDef(AVardefLocalDef node)
	    {
	        defaultOut(node);
	    }
	    /////////////////////////////////////

	    @Override
	    public void inAWithparsHeader(AWithparsHeader node)
	    {
	    	initialize();
	    }
	    
	    public void outAWithparsHeader(AWithparsHeader node)
	    {
	        defaultOut(node);
	    }

	    @Override
	    public void caseAWithparsHeader(AWithparsHeader node)
	    {
	        inAWithparsHeader(node);
	        
	        funname = new Key(new String(node.getId().getText()));						//Functions' id name	        
	        if(node.getId() != null){
	            node.getId().apply(this);
	        }
	        
	        dataTypeMode = 1;															//Functions' return type
	        if(node.getRetType() != null){
	            node.getRetType().apply(this);
	        }

	        
	        symtable.enter();
	        
	        dataTypeMode = 0;
            List<PFparDef> copy = new ArrayList<PFparDef>(node.getFparDef());
            for(PFparDef e : copy)
            {
                e.apply(this);

                for(int i=0; i<idlist.size(); i++){										// Insert all parameter-variables into the symbol table
                	symtable.insert(idlist.get(i), datatype, reference, null, null);
                }
                idlist = new LinkedList<Key>();											//Initialize id list 

                Param param = new Param(datatype, arraylist);							//Add a parameter into the parameter list 
                params.add(param);

            }
            
    		symtable.decrease_scope();													//Function's prototype belongs to the previous scope
    		
    		symtable.insert(funname, null, null, params, retvalue);
    		
    		symtable.increase_scope();

	        outAWithparsHeader(node);
	    }

	    /////////////////////////////////////
	    
	    public void inAWithoutparsHeader(AWithoutparsHeader node)
	    {
	        defaultIn(node);
	    }

	    public void outAWithoutparsHeader(AWithoutparsHeader node)
	    {
	        defaultOut(node);
	    }

	    /////////////////////////////////////
	    
	    public void inAWithrefFparDef(AWithrefFparDef node)
	    {
	    	reference = true;

	    	for(int i=0; i<node.getId().size(); i++){
	    		idlist.add(new Key(node.getId().get(i).getText()));
	    	}
	    }

/*
	    public void outAWithrefFparDef(AWithrefFparDef node)
	    {
	        defaultOut(node);
	    }
*/
	    /////////////////////////////////////
	    
	    public void inAWithoutrefFparDef(AWithoutrefFparDef node)
	    {
	        reference = false;
	        
	    	for(int i=0; i<node.getId().size(); i++){
	    		idlist.add(new Key(node.getId().get(i).getText()));
	    	}
	    }
/*
	    public void outAWithoutrefFparDef(AWithoutrefFparDef node)
	    {
	        defaultOut(node);
	    }
*/
	    /////////////////////////////////////
	    
	    public void inANoarrayFparType(ANoarrayFparType node)
	    {		//array list is empty
	    }

	    public void outANoarrayFparType(ANoarrayFparType node)
	    {
	        defaultOut(node);
	    }
	    
	    /////////////////////////////////////
	    
	    public void inAArrayFirstFparType(AArrayFirstFparType node)
	    {
	    	arraylist.addFirst(0);
	    }
/*
	    public void outAArrayFirstFparType(AArrayFirstFparType node)
	    {
	        defaultOut(node);
	    }
*/
	    /////////////////////////////////////
	    
	    public void inAArrayFirstsecFparType(AArrayFirstsecFparType node)
	    {
	    	arraylist.addFirst(0);
	    	for(int i=0; i<node.getNumber().size(); i++){
	    		arraylist.add(Integer.parseInt(node.getNumber().get(i).getText()));
	    	}
	    }

	    public void outAArrayFirstsecFparType(AArrayFirstsecFparType node)
	    {
	        defaultOut(node);
	    }


	    /////////////////////////////////////
	    
	    public void inAArraySecFparType(AArraySecFparType node)
	    {
	    	for(int i=0; i<node.getNumber().size(); i++){
	    		arraylist.add(Integer.parseInt(node.getNumber().get(i).getText()));
	    	}
	    }

	    public void outAArraySecFparType(AArraySecFparType node)
	    {
	        defaultOut(node);
	    }

/*
	    /////////////////////////////////////
	    
	    public void inAHelper(AHelper node)
	    {
	        defaultIn(node);
	    }

	    public void outAHelper(AHelper node)
	    {
	        defaultOut(node);
	    }
*/
	    /////////////////////////////////////
	    
	    public void inADataRetType(ADataRetType node)
	    {
	        defaultOut(node);
	    }

	    public void outADataRetType(ADataRetType node)
	    {
	        defaultOut(node);
	    }

	    /////////////////////////////////////
	    
	    public void inANoneRetType(ANoneRetType node)
	    {
	        retvalue = new String("nothing");
	    }

	    public void outANoneRetType(ANoneRetType node)
	    {
	        defaultOut(node);
	    }
	    /////////////////////////////////////
	    
	    public void inAIntDataType(AIntDataType node)
	    {
	    	if(dataTypeMode == 0)
	    		datatype = new String("int");
	    	
	    	else retvalue = new String("int");
	    	
	    }

	    public void outAIntDataType(AIntDataType node)
	    {
	        defaultOut(node);
	    }

	    /////////////////////////////////////
	    
	    public void inACharDataType(ACharDataType node)
	    {
	    	if(dataTypeMode == 0)
	    		datatype = new String("char");
	    	
	    	else retvalue = new String("char");
	    }

	    public void outACharDataType(ACharDataType node)
	    {
	        defaultOut(node);
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    /////////////////////////////////////
		
	    public void inAArrayType(AArrayType node)
	    {
	        defaultIn(node);
	    }

	    public void outAArrayType(AArrayType node)
	    {
	        defaultOut(node);
	    }

	    
	    /////////////////////////////////////
	    
	    public void inAPrimitiveType(APrimitiveType node)
	    {
	        defaultIn(node);
	    }

	    public void outAPrimitiveType(APrimitiveType node)
	    {
	        defaultOut(node);
	    }

	    
	    /////////////////////////////////////
	    
	    public void inANoneStmtexpr(ANoneStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outANoneStmtexpr(ANoneStmtexpr node)
	    {
	        defaultOut(node);
	    }

	    /////////////////////////////////////
	    
	    public void inAAssignmentStmtexpr(AAssignmentStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outAAssignmentStmtexpr(AAssignmentStmtexpr node)
	    {
	        defaultOut(node);
	    }
	    
	    /////////////////////////////////////
	    
	    public void inABlockStmtexpr(ABlockStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outABlockStmtexpr(ABlockStmtexpr node)
	    {
	        defaultOut(node);
	    }
	    
	    /////////////////////////////////////
	    
	    public void inAReturnexprStmtexpr(AReturnexprStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outAReturnexprStmtexpr(AReturnexprStmtexpr node)
	    {
	        defaultOut(node);
	    }

	    /////////////////////////////////////
	    

	    public void inAReturnStmtexpr(AReturnStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outAReturnStmtexpr(AReturnStmtexpr node)
	    {
	        defaultOut(node);
	    }

	    /////////////////////////////////////
	    
	    public void inAIfStmtexpr(AIfStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outAIfStmtexpr(AIfStmtexpr node)
	    {
	        defaultOut(node);
	    }
	    /////////////////////////////////////
	    
	    public void inAIfelseStmtexpr(AIfelseStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outAIfelseStmtexpr(AIfelseStmtexpr node)
	    {
	        defaultOut(node);
	    }

	    /////////////////////////////////////
	    
	    public void inAWhileStmtexpr(AWhileStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outAWhileStmtexpr(AWhileStmtexpr node)
	    {
	        defaultOut(node);
	    }
	    /////////////////////////////////////
	    
	    public void inAPlusStmtexpr(APlusStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outAPlusStmtexpr(APlusStmtexpr node)
	    {
	        defaultOut(node);
	    }
	    /////////////////////////////////////
	    
	    public void inAMinusStmtexpr(AMinusStmtexpr node)
	    {
	        defaultIn(node);
	    }

	    public void outAMinusStmtexpr(AMinusStmtexpr node)
	    {
	        defaultOut(node);
	    }

	    /////////////////////////////////////
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
/*		

if(node == null){
	System.out.println("Error: variable " + name + "has not been declared before\n");
	exit(1);
}
*/
	

		
	}