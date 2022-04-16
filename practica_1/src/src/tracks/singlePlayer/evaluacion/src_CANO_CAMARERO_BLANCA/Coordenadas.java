package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

public class Coordenadas implements Comparable<Coordenadas>{
    int x,y; 

    public Coordenadas(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
	public int compareTo(Coordenadas c ) {
        int diferencia = x - c.x;
        if(diferencia == 0)
            diferencia = y - c.y;
        return (int)Math.signum(diferencia);
    }
    /**
	 * Método equals para ver si dos nodos son iguales
	 */
	@Override
	public boolean equals(Object o) {
		return this.compareTo((Coordenadas)o) == 0; 
	}
    @Override
    public int hashCode() {
        return 1000000*x+y;
    }
    /**
     * Sobrecarga del método para mostrarlo en pantalla 
     */
    public String toString() {
        return "(" + Integer.toString(x) 
                + "," + Integer.toString(y)
                + ")" 
                ;
    }
	
    
}
