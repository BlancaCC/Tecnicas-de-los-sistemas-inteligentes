package tracks.singlePlayer;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {

    public static void main(String[] args) {
		// Mis track
		String BFSController = "tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA.AgenteBFS";
		String DFSController = "tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA.AgenteDFS";
		String AStarController = "tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA.AgenteAStar";
		String IDAStarController = "tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA.AgenteIDAStar";
		String RTAStarController = "tracks.singlePlayer.evaluacion.src_CANO_CAMARERO_BLANCA.AgenteRTAStar";

		// Available tracks:
		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = false;
		int seed = new Random().nextInt();

		// Game and level to play
		int gameIdx = 58;
		int levelIdx = 4; // level names from 0 to 4 (game_lvlN.txt).
		// a partir 100 son los mío 
		// dle 5 al 8 son los del examen 
		// se añaden en la carpeta gridphysics
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.

		// 1. This starts a game, in a level, played by a human.
// 		// ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

		// CAMBIAR AQUÍ LO DE CAMBIAR EL NIVEL 
		// 2. This plays a game in a level by the controller.
		//ArcadeMachine.runOneGame(game, level1, visuals, RTAStarController , recordActionsFile, seed, 0);
		//ArcadeMachine.runOneGame(game, level1, visuals, IDAStarController , recordActionsFile, seed, 0);
		//ArcadeMachine.runOneGame(game, level1, visuals, AStarController, recordActionsFile, seed, 0);
		ArcadeMachine.runOneGame(game, level1, visuals, DFSController, recordActionsFile, seed, 0);
		//ArcadeMachine.runOneGame(game, level1, visuals, BFSController, recordActionsFile, seed, 0);
		//ArcadeMachine.runOneGame(game, level1, visuals, sampleRandomController, recordActionsFile, seed, 0);

		// MI CÓDIGO PARA RECORRER TODOS LOS NIVELES
	String []controladores = new String[]{BFSController, DFSController, AStarController, IDAStarController, RTAStarController};
	controladores = new String[]{DFSController};
		for(String controladora : controladores){
			System.out.println("====================================================================");
			for(int level_id = 5; level_id <= 8; level_id++){
				if(controladora != IDAStarController || level_id <=7){
					System.out.println("Controladora : "+ controladora);
					String level = game.replace(gameName, gameName + "_lvl" + level_id);
					System.out.println("------------------------------");
					System.out.println("Level: "+ level);
					ArcadeMachine.runOneGame(game, level, false, controladora, recordActionsFile, seed, 0);
					System.out.println("------------------------------");
				}
			}
		}
		
		// 3. This replays a game from an action file previously recorded
	//	 String readActionsFile = recordActionsFile;
	//	 ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

		// 4. This plays a single game, in N levels, M times :
//		String level2 = new String(game).replace(gameName, gameName + "_lvl" + 1);
//		int M = 10;
//		for(int i=0; i<games.length; i++){
//			game = games[i][0];
//			gameName = games[i][1];
//			level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
//			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleMCTSController, null);
//		}

		//5. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
//		int N = games.length, L = 2, M = 1;
//		boolean saveActions = false;
//		String[] levels = new String[L];
//		String[] actionFiles = new String[L*M];
//		for(int i = 0; i < N; ++i)
//		{
//			int actionIdx = 0;
//			game = games[i][0];
//			gameName = games[i][1];
//			for(int j = 0; j < L; ++j){
//				levels[j] = game.replace(gameName, gameName + "_lvl" + j);
//				if(saveActions) for(int k = 0; k < M; ++k)
//				actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//			}
//			ArcadeMachine.runGames(game, levels, M, sampleRHEAController, saveActions? actionFiles:null);
//		}


    }
}
