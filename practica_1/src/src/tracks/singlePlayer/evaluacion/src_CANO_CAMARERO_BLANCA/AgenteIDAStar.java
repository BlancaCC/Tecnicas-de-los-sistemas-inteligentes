package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteIDAStar extends AbstractPlayer {
 
    // variables relevantes al cálculo de la ruta
    Vector<ACTIONS> plan = new Vector<>(); // Ruta definitiva 
    int g = 0; // coste del nodo actual 
    int f = 0; // coste estimado de la ruta más barata 
    // valores por defecto 
    int ENCONTRADO = -1; // se usan negativos ya que son distancias que no se darán nunca
    int  INFINITO = 99999999; // suponemos que esta distancia nunca se dará

    // Variables para Act y métricas
    Boolean planCalculado = false;
    int nodos_expandidos = 0;
    int nodos_en_memoria = 0;
    int maximo_nodos_en_memoria = 0; 

    // Variables auxiliares relacionadas con coordenadas 
    Boolean [][] visitable; // almacenará obstáculos 
    int portal_x, portal_y; // posiciones del objetivo 
    int avatar_x, avatar_y; // coordenadas avatar 

    // Otras variables auxiliares 
    // Creamos vectores auxiliares para simplificar proceso  de cálculo de sucesores 
	ArrayList<ArrayList<Integer>> desplazamiento = new ArrayList<>();
	ArrayList<Types.ACTIONS> acciones = new ArrayList<>(
			List.of(
				Types.ACTIONS.ACTION_UP,
				Types.ACTIONS.ACTION_DOWN,
				Types.ACTIONS.ACTION_LEFT,
				Types.ACTIONS.ACTION_RIGHT
			)
		);
    /**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public AgenteIDAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
       
        // Cálculos iniciales relacionados con el cáculo de coordenadas del mapa 

        //Calculamos el factor de escala entre mundos (pixeles -> grid)
        Vector2d fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos coordenadas del Portal
        Vector2d portal_coordenadas= posiciones[0].get(0).position;
        portal_x = (int) Math.floor(portal_coordenadas.x / fescala.x);
		portal_y = (int) Math.floor(portal_coordenadas.y / fescala.y);

        //Posición del avatar en coordenadas
        avatar_x = (int) Math.floor(
			stateObs.getAvatarPosition().x / fescala.x);
        avatar_y = (int) Math.floor(
      		stateObs.getAvatarPosition().y / fescala.y);
		// Límites del mapa 
        int limite_mapa_x = stateObs.getObservationGrid().length;
		int limite_mapa_y = stateObs.getObservationGrid()[0].length;

        // Lectura de los obstáculos del mapa
		// a priori todo los sitios son visitables, lo indicamos
		visitable = new Boolean[limite_mapa_x][limite_mapa_y];
		for(int x = 0; x < limite_mapa_x; x++){
			Arrays.fill(visitable[x], true );
		}
		// Marcamos lo muros y trampas 

        //Obtenemos las posiciones de los muros y pinchos e indicamos que no sean visitados
        ArrayList<Observation>[] obstaculos = stateObs.getImmovablePositions();
		int x,y; 
        for (int j=0; j <=1; j++) // muros y pinchos
        for (int i = 0; i < obstaculos[j].size(); i++){
            //Obtenemos la posición de cada uno obstáculos 
 			x = (int)Math.floor(obstaculos[j].get(i).position.x / fescala.x);
            y = (int)Math.floor(obstaculos[j].get(i).position.y / fescala.y);
 			visitable[x][y]=false;
        }
        visitable[avatar_x][avatar_y] = false; // parte de ahí  
         
        // Inicializamos desplazamientos a calcular 
        desplazamiento.add(new ArrayList<>(List.of(0,-1))); // arriba 
        desplazamiento.add(new ArrayList<>(List.of(0,1))); // abajo 
        desplazamiento.add(new ArrayList<>(List.of(-1,0))); // izquierda 
        desplazamiento.add(new ArrayList<>(List.of(1,0))); // derecha	

    }

    /**
     * Los métodos del AgenteIDAStart van encaminados a obtener una ruta a partir del algoritmo IDA*, para ello se han definido los siguientes métodos: 
     * 1. act: irá devolviendo las acciones 
     * 2. generarPlanIDAStar: calcula el plan 
     * . buscar: método auxiliar par el cálculo de IDAStar
     * . distanciaManhattan: heurística 
     * . esObjetivo: si se ha alcanzado el objetivo
     * . calcularSucesores: calcula los sucesores 
     * . esVisitable: si un nodo es visitable 
     *  
     * 
     * Realizaremos su implementación en orden de la quinta a la primera. 
    */
   
    /**
	 * Devuelve si la casilla de coordenadas x e y es visitable
	 * @param coordenadas 
	 * @return si es visitable o no
     * 
     * No es necesario calcular si está dentro o no de los límites del mapa puesto los bordes están recubiertos de muros y la única forma de que se saliera, es que se generara un sucesor en el borde, pero nunca se generarán nodos en un muro. 
	 */
	public Boolean esVisitable(int x, int y){
		return  visitable[x][y]==true; 	
	} 
    /**
     * Devuelve si un nodo de coordenadas indicadas es objetivo o no
     * @param x
     * @param y
     * @return
     */
    boolean esObjetivo( int x,  int y){
        return (x == portal_x && y == portal_y);
    }
    /**
     *  Calcula la distancia Manhattan de dos puntos de coordenas, será utilizado como heurística
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return distancia Manhattan
     */
    private int distanciaManhattan(int x1, int y1){
        return Math.abs(x1-portal_x) + Math.abs(y1-portal_y); 
    }  

    /**
     * Función auxiliar para el algoritmo de IDA Star
     * Es la que genera los sucerores y va explorando el algoritmo 
     * @param g
     * @param cota
     * @param x
     * @param y
     * @return
     */
    private int buscar(int _g, int cota, int x, int y){
        int f = _g + distanciaManhattan(x, y);
        if(f > cota ) return f;
        if( esObjetivo(x, y) ) return ENCONTRADO;
        int minimo = INFINITO;
        // calculamos sucesores 
        for(int i=0; i<4; i++) {
			int coordenada_x_sucesor = x + desplazamiento.get(i).get(0);
			int coordenada_y_sucesor = y + desplazamiento.get(i).get(1);
            if(esVisitable(coordenada_x_sucesor, coordenada_y_sucesor)){
                plan.add(acciones.get(i)); // añadimos movimiento a la ruta
                visitable[coordenada_x_sucesor][coordenada_y_sucesor] = false; // para que no se pueda volver a repetir 
                // buscamos 
                int t = buscar(_g+1, cota, x, y);
                if(t == ENCONTRADO) return ENCONTRADO;
                if(t < minimo) minimo = t;
                plan.remove(_g); // el último elemento coincide con el coste
                visitable[coordenada_x_sucesor][coordenada_y_sucesor] = true;
            }
        }
        return minimo;
    }
    /**
     * Generamos plan en base al algoritmo IDAStar
     * @return
     */
    boolean generarPlanIDAStar(){
        int cota = distanciaManhattan(avatar_x, avatar_y);
        while(true){
            int t = buscar(0, cota, avatar_x, avatar_y);
            if(t == ENCONTRADO) return true;
            else if(t == INFINITO) return false;
            cota = t; // aumentamos cota
        }
    }
    /**
	 * Return the best action to arrive faster to the closest portal
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 * @return best	ACTION to arrive faster to the closest portal
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        if(planCalculado  == false) {
        	long tiempo_inicio = System.nanoTime();
        	planCalculado = generarPlanIDAStar();
        	long tiempo_fin = System.nanoTime();
			//calculamos tiempo de ejecución
    		double runtime = (double)((tiempo_fin - tiempo_inicio))/1000000;
    		//Calculamos el tamaño del plan
    		int tam_plan = plan.size();

			//Mostramos los valores para rellenar la tabla
        	System.out.println("Runtime: "+runtime);
        	System.out.println("Tamaño ruta calculada: "+tam_plan);
        	System.out.println("Número de nodos expandidos: "+nodos_expandidos);
        	System.out.println("Máximo número de nodos en memoria: "+maximo_nodos_en_memoria);
            // leemos el primero y enviamos
            ACTIONS accion = plan.firstElement(); 
            plan.remove(0);
    		return accion;
        }else {
    		// leemos el primero y enviamos
            ACTIONS accion = plan.firstElement(); 
            plan.remove(0);
    		return accion;
        }   		  	
	}
}
