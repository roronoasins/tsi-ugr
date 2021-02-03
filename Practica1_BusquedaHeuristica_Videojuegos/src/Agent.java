package src_gvgai;

import java.util.ArrayList;
import java.util.Stack;

// Imports de la superclase

import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
//import tools.*;
import core.player.*;
import core.game.StateObservation;
import core.game.Observation;
import java.awt.Dimension;

/*
 * Agente creado para la práctica de videojuegos de TSI.
 * Mapa 11 niveles 5-10
 */
public class Agent extends AbstractPlayer
{
    public ArrayList<Types.ACTIONS> actions;
    public boolean change;
    Vector2d fescala;			// Factor de escala del mapa
    private Vector2d goal;
    PathFinder pf;
	ArrayList<Node> path;
	boolean change_path;
	public boolean goal_reached;
    Heatmap hp;
    int n_gems;

    // reactive vars
    ArrayList<Types.ACTIONS> reactive_actions;
    int actual_enemy;

	// comp deliberativ
	int total_gems;
	Vector2d actual_gem;
	boolean change_gem;
	boolean gem_reached;

	boolean endgame;

	Types.ACTIONS action;

	private enum state
	{
		SIMPLE_REACTIVE,
		SIMPLE_DELIBERATIVE,
		COMPOUND_DELIBERATIVE,
		REACTIVE_DELIBERATIVE;
	}

	private state actualState;

	public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//Scanner f = new Scanner(new File("../../../examples/gridphysics/");
		pf = new PathFinder();
		pf.initialize(stateObs);
    //for (int i = 0; i < actions.size(); ++i) {
      //  actions.add(act.get(i));
    //}
    //NUM_ACTIONS = actions.size();

    // Factor de escala (pixeles -> grid)
    this.fescala = new Vector2d(stateObs.getWorldDimension().getWidth()/stateObs.getObservationGrid().length,
    		stateObs.getWorldDimension().getHeight()/stateObs.getObservationGrid()[0].length);
    // Lista de observaciones de portales ordenadas por cercania al avatar
    ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
    if(posiciones != null)
		{
			this.goal = posiciones[0].get(0).position;
			this.goal.x /= (fescala.x);
			this.goal.y /= (fescala.y);
		}else
			goal = null;
    path = new ArrayList<Node>();
    change = true;
    change_gem = true;
    change_path = true;
    goal_reached = false;
    gem_reached = false;
		reactive_actions = new ArrayList<Types.ACTIONS>();
		actual_enemy = 0;
		this.total_gems = (containsGems(stateObs) == true) ? 10 : 0;
		endgame = false;
    }

	public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		n_gems = this.getRemainingGems(stateObs);
		hp = new Heatmap(stateObs);
		Vector2d avatar = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
		ArrayList<Observation>[] npcs = stateObs.getNPCPositions();

		if(n_gems == 0)
		{
			actualState = state.SIMPLE_DELIBERATIVE;
			// if avatar enter to heat area, reactive behaviour
			if(npcs != null)
			{
				hp.updateState(stateObs);
				hp.updateHeatmapMatrix();
				if(hp.heatmatrix[(int)avatar.x][(int)avatar.y].value > 0)
				{
					actualState = state.SIMPLE_REACTIVE;
					change_path = true;
				}
			}
			//else actualState = state.SIMPLE_REACTIVE;
		}else if(n_gems > 0)
		{
			actualState = state.COMPOUND_DELIBERATIVE;
			if(npcs != null)
			{
				hp.updateState(stateObs);
				hp.updateHeatmapMatrix();
				if(hp.heatmatrix[(int)avatar.x][(int)avatar.y].value > 0)
				{
					actualState = state.SIMPLE_REACTIVE;
					//change_gem = true;
					change = true;
				}
			}
			/*switch (actualState)
			{
				case SIMPLE_REACTIVE:
					System.out.println("simple-reactivo");
					break;
				case SIMPLE_DELIBERATIVE:
					System.out.println("deliberativo-simple");
					break;
				case COMPOUND_DELIBERATIVE:
					System.out.println("deliberativo-compuesto");
					break;
			}*/

		}


		if(actualState == state.SIMPLE_REACTIVE)
		{
  		if(reactive_actions.size() == 0 || (hp.heatmatrix[(int)avatar.x][(int)avatar.y].vector_index != actual_enemy))
  		{
  			hp.updateState(stateObs);
  			hp.updateHeatmapMatrix();
  			reactive_actions = hp.scapeFrom(avatar, getOrientation(stateObs));
  			actual_enemy = hp.heatmatrix[(int)avatar.x][(int)avatar.y].vector_index;
  		}

			if(reactive_actions.size() > 0)
			{
				//System.out.println("hi");
				action = reactive_actions.get(0);
				reactive_actions.remove(0);
				/*switch (getOrientation(stateObs))
				{
					case N:
						System.out.println("norte");
						break;
					case S:
						System.out.println("sur");
						break;
					case E:
						System.out.println("este");
						break;
					case W:
						System.out.println("oeste");
						break;
				}*/
			}else
			{
				action = Types.ACTIONS.ACTION_NIL; // if avatar is not in an enemy heat
			}

			change = true;
		}

		if(actualState == state.SIMPLE_DELIBERATIVE)
		{
			if(avatar.x == goal.x && avatar.y == goal.y && !endgame)
			{
				endgame = true;
				goal_reached = true;
			}else goal_reached = false;

			if(change_path == true && !endgame)
			{
				pf.updateState(stateObs);
				path = pf.a_star(new Node(avatar), new Node(goal));
				change_path = false;
			}

			// If there is a path and no moves calculated
			if(!path.isEmpty() && !goal_reached)
			{
				if(avatar.x == path.get(0).x && avatar.y == path.get(0).y)
					path.remove(0);
				if(!path.isEmpty())
					action = getNextAction(new Node(avatar), path.get(0));
			}else action = Types.ACTIONS.ACTION_NIL;
		}

		if(actualState == state.COMPOUND_DELIBERATIVE)
		{
			Vector2d gem;
			if(this.getRemainingGems(stateObs) > 0)
			{
				if(!path.isEmpty())
					if((int)avatar.x == (int)actual_gem.x && (int)avatar.y == (int)actual_gem.y)
					{
						//System.out.println("done");
						gem_reached = true;
						//change_gem = true;
						change = true;
					}else gem_reached = false;


				ArrayList<Observation>[] gem_positions = stateObs.getResourcesPositions(stateObs.getAvatarPosition());
				//gem = gem_positions[0].get(0).position;

				//gem.x = gem.x / fescala.x;
				//gem.y = gem.y / fescala.y;

				if(change == true)
				{
					int min_cost = 999;
					int min_index;
					ArrayList<Node> min_path;
					// for para todas las gemas, escoger menor coste
					pf.updateState(stateObs);
					//System.out.println("explore gems");
					path = getClosestGems(gem_positions[0], avatar);
					if(path.size()>0)
						actual_gem = new Vector2d(path.get(path.size()-1).x, path.get(path.size()-1).y);
					//pf.updateState(stateObs);
					//path = pf.a_star(new Node(avatar), new Node(min_index));
					change = false;
				}
			}

			// If there is a path and no moves calculated
			if(!path.isEmpty() && !gem_reached)
			{
				if(avatar.x == path.get(0).x && avatar.y == path.get(0).y)
					path.remove(0);
				if(!path.isEmpty())
					action = getNextAction(new Node(avatar), path.get(0));
			}else action = Types.ACTIONS.ACTION_NIL;
		}
        // return first action
        return action;
    }

    // Return actions required to  reach pf from pi
    private Types.ACTIONS getNextAction(Node pi, Node pf)
	{
		Vector2d dif = new Vector2d(pi.x - pf.x, pi.y - pf.y);
		//Types.ACTIONS move = new Types.ACTIONS();
		switch((int) dif.y)
		{
			// horizontal move
			case 0:
				switch((int) dif.x)
				{
					case -1:
						return Types.ACTIONS.ACTION_RIGHT;
					case 1:
						return Types.ACTIONS.ACTION_LEFT;
				}
				// vertical move
			case -1:
				return Types.ACTIONS.ACTION_DOWN;
			case 1:
				return Types.ACTIONS.ACTION_UP;
		}

		return Types.ACTIONS.ACTION_NIL;
	}

	public OrientationEnum getOrientation(StateObservation stateObs)
	{
		switch((int)stateObs.getAvatarOrientation().x)
		{
			case 0:
				switch((int)stateObs.getAvatarOrientation().y)
				{
					case -1:
						return OrientationEnum.N;
					case 1:
						return OrientationEnum.S;
				}
			case -1:
				return OrientationEnum.W;
			case 1:
				return OrientationEnum.E;
		}
		return OrientationEnum.N;
	}

	protected ArrayList<EnvironmentObservation>[][] getObservationGrid(StateObservation stateObs){
		ArrayList<core.game.Observation>[][] grid = stateObs.getObservationGrid();
		ArrayList<EnvironmentObservation>[][] finalGrid;

		Dimension worldDimension = stateObs.getWorldDimension();
		int blockSize = stateObs.getBlockSize(); // Tamaño de un sprite en píxeles

		int width = worldDimension.width / blockSize; // Cuadrículas de ancho
		int height = worldDimension.height / blockSize; // Cuadrículas de alto

		finalGrid = new ArrayList[width][height];

		EnvironmentObservation new_obs;

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++){
				finalGrid[x][y] = new ArrayList<EnvironmentObservation>();

				if (!grid[x][y].isEmpty()){

					for(core.game.Observation obs : grid[x][y]){
						new_obs = new EnvironmentObservation(obs, stateObs.getBlockSize());

						// <Solución bug forward model>
						if (new_obs.getType() != null) // Compruebo que sea una observación válida
							finalGrid[x][y].add(new_obs);
					}
				}
				else{
					finalGrid[x][y].add(new EnvironmentObservation(x, y, ObservationEnum.EMPTY));
				}

				if (finalGrid[x][y].isEmpty()) // Debido al bug puede que ahora esté vacío
					finalGrid[x][y].add(new EnvironmentObservation(x, y, ObservationEnum.EMPTY));
			}

		return finalGrid;
	}

	protected boolean containsGems(StateObservation stateObs)
	{
		return (stateObs.getResourcesPositions() != null) ? true : false;
	}

	protected int getNumGems(StateObservation stateObs)
	{
	int n_gems = stateObs.getResourcesPositions(stateObs.getAvatarPosition())[0].size();
	return n_gems;
	}

	protected int getGemsStorage(StateObservation stateObs)
	{
	 Integer gems = stateObs.getAvatarResources().get(6);

	 return (gems == null) ? 0 : gems;
	}

	protected int getRemainingGems(StateObservation stateObs)
	{
	 return total_gems - getGemsStorage(stateObs);
	}

	protected ArrayList<Node> getClosestGems(ArrayList<core.game.Observation> gem_positions, Vector2d avatar)
	{
		ArrayList<Node> min_path = new ArrayList<Node>();
		ArrayList<Node> tmp_path = new ArrayList<Node>();
		int min_cost = 999;

		for(int a=0; a < gem_positions.size(); a++)
		{
			tmp_path = pf.a_star(new Node(avatar), new Node(new Vector2d((int)(gem_positions.get(a).position.x/fescala.x), (int)(gem_positions.get(a).position.y/fescala.y))));
			if (tmp_path.size() < min_cost) {
				min_path = tmp_path;
				min_cost = tmp_path.size();
			}
		}
		return min_path;
	}
}
