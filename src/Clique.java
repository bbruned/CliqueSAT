import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;


public class Clique {
	public Vector<Sorte> Sortes;
	public Map<String,Integer> Numerotation;
	public Vector<Sorte> Cliques;
	public int Dim;
	
	public void LoadData(String fichier) throws IOException{
		try{
			Reader reader = new FileReader(fichier+".ph");
			// Prise en compte d'une ligne
			BufferedReader in = new BufferedReader(reader);
			String ligne = in.readLine();
			//lecture des sortes
			while ((ligne != null)&&(ligne.startsWith("process"))){
				// traitement de la ligne courante: decouper les mots separes par des delimiteurs
				String[] temp=ligne.split(" "); 
				Sorte s =new Sorte(temp[1]);
				int nb=Integer.parseInt(temp[2]);
		        for (int i=0; i<nb+1; i++){ 
		        	s.Processus.add(new Processus(temp[1],Dim));
		        	Dim++;
		        	//System.out.println(Dim);
		        }
		        Sortes.add(s);
		        Numerotation.put(temp[1], Sortes.size()-1);
		        // Lecture de la prochaine ligne 
		        ligne = in.readLine();
			}
			//lecture des arcs
			ligne=in.readLine();
			while ((ligne != null)&&(!ligne.equals(""))){
				// traitement de la ligne courante: decouper les mots separes par des delimiteurs
				String[] temp=ligne.split(" ");
				int n=Integer.parseInt(temp[1]);
				Processus p=Sortes.get(Numerotation.get(temp[0])).Processus.get(n);
				n=Integer.parseInt(temp[4]);
				Processus voisin=Sortes.get(Numerotation.get(temp[3])).Processus.get(n);
				p.Voisins.add(voisin);
				//voisin.Voisins.add(p);
				// Lecture de la prochaine ligne
				ligne = in.readLine();
		    }
			
		}
		catch (Exception e){
			System.out.println(e.toString());
		}		
	}
	public Clique() {
		Sortes=new Vector<Sorte>();
		Numerotation=new HashMap<String,Integer>();
		Cliques=new Vector<Sorte>();
		Dim=0;
	}
	public void Solve(){
		//1- Create the model
		CPModel m = new CPModel();
		//2- Create the variables
		//IntegerVariable[] graph = Choco. makeBooleanVarArray("clique",Dim);
		IntegerVariable[] graph = Choco.makeIntVarArray("clique",Dim);
		for(int i=0;i<Dim;i++)
			m.addConstraint(Choco.leq(graph[i],1));
		//3- Post constraints
		//un seul sommet par sortes
		IntegerExpressionVariable  expression;
		for (Sorte s:Sortes){
			expression=Choco.plus(graph[s.Processus.get(0).Num],0);
			for(int i=1;i<s.Processus.size();i++){
				expression=Choco.plus(graph[s.Processus.get(i).Num],expression);
			}
			m.addConstraint(Choco.eq(expression,1));
		}
		//exclusion d'une clique de deux sommets reliés
		for (Sorte s:Sortes) {
			for (Processus p:s.Processus) {
				for(Processus voisin:p.Voisins){
					m.addConstraint(Choco.leq(Choco.plus(graph[p.Num], graph[voisin.Num]),1));
				}
			}
		}
		
		//4- Create the solver
		CPSolver s = new CPSolver();
		s.read(m);
		s.solve();
		System.out.println(s.isFeasible()==Boolean.TRUE);
		if (s.isFeasible()==Boolean.TRUE)do {
	        // Exploitation de cette solution
			Sorte sol= new Sorte("clique"); 
			for(Sorte sorte:Sortes){
				for(Processus p:sorte.Processus){
					int n=s.getIntVar(p.Num).getVal();
					if (n==1){
						sol.Processus.add(p);
					}
					System.out.print(s.getIntVar(p.Num).getVal());
				}
				System.out.print("\n");
			}
			Cliques.add(sol);
			//vérification des contraintes d'exclusion de deux sommets d'une clique
			for(Processus proc:sol.Processus){
				for(Processus voisin:proc.Voisins){
					if (s.getIntVar(voisin.Num).getVal()+s.getIntVar(proc.Num).getVal()==2){
						System.out.println(proc.Num+" "+voisin.Num);						
					}
				}
			}
	     // On relance la recherche d'une solution 
	    } while (s.nextSolution() == Boolean.TRUE);
		System.out.println("Number of solutions found:"+s.getSolutionCount());
				
		//vérification des cliques
		if (!Cliques.isEmpty()){
			for(Sorte sol:Cliques){
				for(Processus p:sol.Processus){
					for(Processus voisin :p.Voisins){
						if(sol.Processus.contains(voisin)){
							System.out.println("c'est pas une clique");
							System.out.println(sol.Processus.indexOf(p)+" "+sol.Processus.indexOf(voisin));
						}
					}
				}
			}
		}
	}
	
	public void Save(){
		
	}
}
