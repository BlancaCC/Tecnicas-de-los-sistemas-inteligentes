package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * Resuelve el mapa implementando el método de búsqueda en produndidad
 */
public class AgenteDFS extends AbstractPlayer  {

	// Situación del agente y meta
	Coordenadas avatar;
	Coordenadas portal;
	Boolean planCalculado = false; // Si la ruta se conoce ya
	//Pila con el plan a seguir
	private Stack<ACTIONS> plan = new Stack<>();

	// Si un par de coordenadas pertenecen aquí no serán visitables
	// ya sea por ser un obstáculo o por haberse visitado con anterioridad
	Set<Coordenadas> noVisitable= new HashSet<>();

	// Métricas que mostrar en pantalla 
	int nodos_expandidos = 0;

	//Creamos vectores auxiliares para simplificar proceso  de cálculo de sucesores 
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
	public AgenteDFS(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
        Vector2d fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos coordenadas del Portal
        Vector2d portal_coordenadas= posiciones[0].get(0).position;
		portal = new Coordenadas(
			(int) Math.floor(portal_coordenadas.x / fescala.x),
			(int) Math.floor(portal_coordenadas.y / fescala.y)
		);
		//Posición del avatar en coordenadas
        avatar = new Coordenadas( 
			(int) Math.floor(stateObs.getAvatarPosition().x / fescala.x),
			(int) Math.floor(stateObs.getAvatarPosition().y / fescala.y)
		);
		// Marcamos lo muros y trampas 
         //Obtenemos las posiciones de los muros y pinchos e indicamos que no sean visitados
         ArrayList<Observation>[] obstaculos = stateObs.getImmovablePositions();

         for (int j=0; j <=1; j++) // muros y pinchos
         for (int i = 0; i < obstaculos[j].size(); i++){
            //Obtenemos la posición de cada uno
			noVisitable.add(
				new Coordenadas(
					(int)Math.floor(obstaculos[j].get(i).position.x / fescala.x),
					(int)Math.floor(obstaculos[j].get(i).position.y / fescala.y)
			 	)
			);
         } 
		// añadimos desplazamientos a calcular 
		desplazamiento.add(new ArrayList<>(List.of(0,-1))); // arriba 
		desplazamiento.add(new ArrayList<>(List.of(0,1))); // abajo 
		desplazamiento.add(new ArrayList<>(List.of(-1,0))); // izquierda 
		desplazamiento.add(new ArrayList<>(List.of(1,0))); // derecha		
	}

	/**
	 * Devuelve si la casilla de coordenadas x e y es visitable
	 * @param coordenadas 
	 * @return si es visitable o no, ya sea por ser un obstáculo o porque se visitó antes
	 * No vamos a comprobar si se sale del límite del mapa ya que ese caso nunca se dará
	 * por las circunstancias
	 */
	public Boolean esVisitable(Coordenadas c){
		return !noVisitable.contains(c);
	}
	/**
	 * Algoritmo que para encontrar la ruta en profundidad
	 * @param inicial
	 * @return
	 */
	Boolean DFS(Coordenadas inicial){
		nodos_expandidos++;
		if(inicial.equals(portal)) return true;
		// Se expande nodo 
		noVisitable.add(inicial);
		return DFS_search(inicial);
	}
	/**
	 * Algoritmo auxiliar de DFS 
	 * que genera sucesores y añade el vector al plan si se encuentra
	 * @param padre
	 * @return
	 */
	Boolean DFS_search(Coordenadas padre){
		// Generamos sucesores 
		for(int i= 0; i<4; i++) {
			Coordenadas sucesor = new Coordenadas(
				padre.x + desplazamiento.get(i).get(0),
				padre.y + desplazamiento.get(i).get(1)
			);
			if( esVisitable(sucesor)) {
				if(DFS(sucesor)){ // si se ha encontrado 
					plan.push(acciones.get(i)); // añadimos el movimiento que siguió al plan
					return true;
				}
			}
		}
		return false;		
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
        	planCalculado =DFS(avatar);
        	long tiempo_fin = System.nanoTime();
			//calculamos tiempo de ejecución
    		double runtime = (double)((tiempo_fin - tiempo_inicio))/1000000;
    		//Calculamos el tamaño del plan
    		int tam_plan = plan.size();

			//Mostramos los valores para rellenar la tabla
        	System.out.println("Runtime: "+runtime);
        	System.out.println("Tamaño ruta calculada: "+tam_plan);
        	System.out.println("Número de nodos expandidos: "+nodos_expandidos);
        	System.out.println("Máximo número de nodos en memoria: "+nodos_expandidos);
    		return plan.pop();
        }else {
    		return plan.pop();
        }   
		  	
	}
}
