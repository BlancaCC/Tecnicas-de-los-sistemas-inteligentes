package tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * Resuelve el mapa implementando el método de búsqueda en anchura
 */
public class AgenteAStar extends AbstractPlayer {
	public int num_actions;
	Vector2d fescala;
	Vector2d avatar_coordenadas;
	Boolean planCalculado = false;
	int portal_x;
	int portal_y;
	int avatar_coordenadas_x, avatar_coordenadas_y;

	// ArrayList con el plan a seguir
	private Queue<Types.ACTIONS> plan = new LinkedList<>();

	// True si el nodo se puede explorar, false si no,
	// si no está en el diccionario también se podrá visitar
	private Boolean [][] visitable;
	private Boolean [][] esta_abierto;
	private Boolean[][] esta_cerrado;
	private int [][] coste_g;
	// TODO arreglar esto

	// límites superiores del mapa
	Vector2d limite_mapa;

	// Creamos vectores auxiliares para simplificar proceso de cálculo de sucesores
	ArrayList<ArrayList<Integer>> desplazamiento = new ArrayList<>();
	ArrayList<Types.ACTIONS> acciones = new ArrayList<>(
			List.of(
					Types.ACTIONS.ACTION_UP,
					Types.ACTIONS.ACTION_DOWN,
					Types.ACTIONS.ACTION_LEFT,
					Types.ACTIONS.ACTION_RIGHT));

	/**
	 * initialize all variables for the agent
	 * 
	 * @param stateObs     Observation of the current state.
	 * @param elapsedTimer Timer when the action returned is due.
	 */
	public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		// Calculamos el factor de escala entre mundos (pixeles -> grid)
		fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
				stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);

		// Se crea una lista de observaciones de portales, ordenada por cercania al
		// avatar
		ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
		// Seleccionamos coordenadas del Portal
		Vector2d portal_coordenadas = posiciones[0].get(0).position;
		portal_x = (int) Math.floor(portal_coordenadas.x / fescala.x);
		portal_y = (int) Math.floor(portal_coordenadas.y / fescala.y);

		// Posición del avatar en coordenadas
		avatar_coordenadas_x = (int) Math.floor(stateObs.getAvatarPosition().x / fescala.x);
		avatar_coordenadas_y = (int) Math.floor(stateObs.getAvatarPosition().y / fescala.y);
		// Límites del mapa
		int limite_mapa_x = stateObs.getObservationGrid().length;
		int limite_mapa_y = stateObs.getObservationGrid()[0].length;

		// A priori todo los sitios son visitables, lo indicamos
		esta_cerrado = new Boolean[limite_mapa_x][limite_mapa_y];
		esta_abierto = new Boolean[limite_mapa_x][limite_mapa_y];
		visitable = new Boolean[limite_mapa_x][limite_mapa_y];
		coste_g = new int [limite_mapa_x][limite_mapa_y];
		for (int x = 0; x <= limite_mapa_x; x++) {
			Arrays.fill(esta_cerrado[x], false);
			Arrays.fill(esta_abierto[x], false);
			Arrays.fill(visitable[x], true);
			//suponemos que se incia a infinito
			Arrays.fill(coste_g[x], 999999999);
		}
		// Marcamos lo muros y trampas

		// Obtenemos las posiciones de los muros y pinchos e indicamos que no sean
		// visitados
		ArrayList<Observation>[] obstaculos = stateObs.getImmovablePositions();
		int x, y;
		for (int j = 0; j <= 1; j++) // muros y pinchos
			for (int i = 0; i < obstaculos[j].size(); i++) {
				// Obtenemos la posición de cada uno
				x = (int) Math.floor(obstaculos[j].get(i).position.x / fescala.x);
				y = (int) Math.floor(obstaculos[j].get(i).position.y / fescala.y);

				visitable[x][y] = false;
			}
		// Añadimos desplazamientos a calcular (notemos que el orden de lectura será el
		// de abajo arriba)
		// añadimos desplazamientos a calcular 
		desplazamiento.add(new ArrayList<>(List.of(0,-1))); // arriba 
		desplazamiento.add(new ArrayList<>(List.of(0,1))); // abajo 
		desplazamiento.add(new ArrayList<>(List.of(-1,0))); // izquierda 
		desplazamiento.add(new ArrayList<>(List.of(1,0))); // derecha	
	}

	/*
	 * A continuación construiremos funciones básicas que se encarguen de
	 * 1. Llamar a siguiente acción.
	 * 2. General plan.
	 * 
	 * Que cuentan con las funciones auxiliares que se encargarán de
	 * 3 Indicar si cierta casilla es visitable por no tener un objeto
	 * 
	 * Dicho esto escribiremos las respectivas implementación en orden ascendente al
	 * expuesto
	 */

	/**
	 * Devuelve si la casilla de coordenadas x e y es visitable
	 * 
	 * @param coordenadas
	 * @return si es visitable o no
	 */
	public Boolean esVisitable(int x, int y) {
		Boolean dentro_mapa = (x >= 0
				&& y >= 0
				&& x <= limite_mapa.x
				&& y <= limite_mapa.y);
		return dentro_mapa && ( // está dentro del mapa
		visitable[x][y] == true //
		);
	}

	/** 
	 * Genera el plan a seguir 
	 * lo que hace es añadir el plan a la variable  privada plan 
	 * El plan se genera mediante el algoritmo A estrella,
	 *  es por ello que la estructura que almacena 
	 * el plan sea una cola con prioridad  
	 * devuelve true si se encuentra 
	 */
	private  boolean generarPlanAEstrella(){
		
		PriorityQueue<NodoEstrella> abiertos = new PriorityQueue<>();
        NodoEstrella inicial = new NodoEstrella(avatar_coordenadas_x, avatar_coordenadas_y,
		portal_x, portal_y);
		abiertos.add(inicial);
		esta_abierto[avatar_coordenadas_x][avatar_coordenadas_y] = true;
		coste_g[avatar_coordenadas_x][avatar_coordenadas_y] = 0;
		NodoEstrella candidato; 

		while(true){
			// como no borramos las actualizaciones, solo tomamos como bueno si 
			// están abierto 
			do{
				if(abiertos.isEmpty()){
					return false;
				}
				candidato = abiertos.poll();
			}while(esta_abierto[candidato.x][candidato.y] == false);
			// puede producir a error si abierto está vacío 
			//actualizamos estado 
			esta_abierto[candidato.x][candidato.y] = false;
			esta_cerrado[candidato.x][candidato.y] = true;
			// se ha encontrado un nodo que conduce al objetivo
			if(candidato.x == portal_x && candidato.y == portal_y) {
				plan = new LinkedList<ACTIONS>(candidato.historialPasos);
				return true;
			}

			// Para ahorrar coste solo calcularemos las coordenadas del sucesor y su g, en caso de querer añadirse se calculará el nodo 
			for (int i = 0; i < 4; i++) {	
				int sucesor_x = candidato.x + desplazamiento.get(i).get(0);
				int sucesor_y = candidato.y + desplazamiento.get(i).get(1);
				// heurística el nodo no ha sido visitado hace dos generaciones
				boolean no_abuelo = !candidato.coordenadasIguales(candidato.coordenada_padre_x, candidato.coordenada_padre_y);
				// si se trata de un obstáculo no se considera
				boolean no_obstaculo = esVisitable(sucesor_x,sucesor_y);
				// se comprueban condiciones 
				if(no_abuelo && no_obstaculo){
					if (!(esta_cerrado[sucesor_x][sucesor_y]
						||
						esta_abierto[sucesor_x][sucesor_y])){

							// creamos nodo 
							NodoEstrella sucesor = new NodoEstrella(sucesor_x, sucesor_y, 
							portal_x, portal_y, candidato, 
							acciones.get(i));
							// añadimos a abiertos
							abiertos.add(sucesor);
							// actualizamos coste y estado
							coste_g[sucesor_x][sucesor_y] = sucesor.g;
							esta_abierto[sucesor_x][sucesor_y] = true;
					}else{ // está creado o abierto 
						// Se avanza un paso 
						int sucesor_g = candidato.g + 1;
						// si mejora el coste g se añade nuevo
						if( coste_g[sucesor_x][sucesor_y] > sucesor_g ){
							esta_cerrado[sucesor_x][sucesor_y] = false;
							coste_g[sucesor_x][sucesor_y] = sucesor_g;
							esta_abierto[sucesor_x][sucesor_y] = true;
							abiertos.add(
								new NodoEstrella(sucesor_x, sucesor_y, 
								portal_x, portal_y, candidato, 
								acciones.get(i))
							);
						}
					
					}
				}
			}
		}
	}

	/**
	 * Return the best action to arrive faster to the closest portal
	 * 
	 * @param stateObs     Observation of the current state.
	 * @param elapsedTimer Timer when the action returned is due.
	 * @return best ACTION to arrive faster to the closest portal
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

		if (planCalculado == false) {
			// Llamamos al plan con la información del lugar dónde se encuentran los muros
			planCalculado = generarPlanAEstrella();
			System.out.print(plan);
			return plan.poll();
		} else {
			return plan.poll();
		}

	}
}
