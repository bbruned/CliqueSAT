import java.io.IOException;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		Clique c=new Clique();
		c.LoadData("egfr20_flat");
		//c.LoadData("tcrsig40_flat");
		c.Solve();
		c.Save("cliques");
	}

}
