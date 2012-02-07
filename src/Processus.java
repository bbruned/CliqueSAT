import java.util.Vector;

public class Processus {
	public int Num;
	public String Sorte;
	public Vector<Processus> Voisins;
	public Processus(String sorte,int num) {
		super();
		Num=num;
		Voisins=new Vector<Processus>();
		Sorte = sorte;
	}
	
}
