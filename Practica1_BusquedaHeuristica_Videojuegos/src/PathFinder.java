package src_gvgai;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.sun.net.httpserver.Authenticator;
import tools.Vector2d;

import core.game.StateObservation;

import javax.sound.midi.SysexMessage;

public class PathFinder
{

	public StateObservation state;
    public ArrayList<core.game.Observation> grid[][];

    /*			Neighbours generation constants
	 * 				(x,y+1)
	 * 		(x-1,y)	 (x,y)	x+1,y)
	 * 				(x,y-1)
	 */
    private static final int[] x_neighs = new int[]{0, 0, -1, 1};
    private static final int[] y_neighs = new int[]{-1, 1, 0, 0};

    public PathFinder(){}

    public void initialize(StateObservation obs)
    {
    	this.state = obs;
    	this.grid = this.state.getObservationGrid();
    }

    public void updateState(StateObservation obs)
    {
    	this.state = obs;
    	this.grid = this.state.getObservationGrid();
    }

	public ArrayList<Node> a_star(Node start, Node goal)
	{
		ArrayList<Node> Path = new ArrayList<Node>();
		PriorityQueue<Node> openList = new PriorityQueue<Node>();
		PriorityQueue<Node> closedList = new PriorityQueue<Node>();
		Node current = null;

		start.g = 0;
		start.h += h(start, goal);
		openList.add(start);

		while(openList.size() != 0)
		{
			current = openList.poll();
			closedList.add(current);

			if(current.x == goal.x && current.y == goal.y)
			{
				return calculatePath(current);
			}

			ArrayList<Node> neighbours = new ArrayList<Node>();
			neighbours = getNeighbours(current);

			for(int i=0; i < neighbours.size(); i++)
			{
				Node actual_neighbour = neighbours.get(i);

				int current_distance = actual_neighbour.g;
				if(!openList.contains(actual_neighbour) && !closedList.contains(actual_neighbour))
				{
					actual_neighbour.g = current_distance + current.g;
					actual_neighbour.h += h(actual_neighbour, goal);
					actual_neighbour.parent = current;
					openList.add(actual_neighbour);
				}else if(current_distance+current.g < actual_neighbour.g)
				{
					actual_neighbour.g = current_distance+current.g;
					actual_neighbour.parent = current;

					if(openList.contains(actual_neighbour))
						openList.remove(actual_neighbour);
					if(closedList.contains(actual_neighbour))
						closedList.remove(actual_neighbour);

					openList.add(actual_neighbour);
				}
			}
		}

		// If failure -> Path = Empty array; If Success -> Path = optimal path found
		return Path;
	}


	/**
	 * Heurística
	 * @param node
	 * @param goal
	 * @return cost: current node to goal distance
	 */
	private int h(Node node, Node goal)
	{
		return (int) (Math.abs(node.x - goal.x) + Math.abs(node.y - goal.y));
	}

	private ArrayList<Node> calculatePath(Node node)
	{
		ArrayList<Node> path = new ArrayList<Node>();

		while(node != null)
		{
			if(node.parent != null)
				path.add(0,node);
			//System.out.println("node: "+node.x+", "+node.y);
			node = node.parent;
		}
		return path;
	}

	private ArrayList<Node> getNeighbours(Node actual_node)
	{
		ArrayList<Node> neighbours = new ArrayList<Node>();
		int x = (int) actual_node.x;
		int y = (int) actual_node.y;

		for(int i=0; i < x_neighs.length; i++)
		{
			if(grid[x+x_neighs[i]][y+y_neighs[i]].isEmpty())
			{
				neighbours.add(new Node(new Vector2d(x+x_neighs[i],y+y_neighs[i])));
			}else
			{
				// check if not outofbounds
				// not a wall
				if(grid[x+x_neighs[i]][y+y_neighs[i]].get(0).itype != 0 && grid[x+x_neighs[i]][y+y_neighs[i]].get(0).itype != 7)
					neighbours.add(new Node(new Vector2d(x+x_neighs[i],y+y_neighs[i])));
			}


		}

		return neighbours;
	}

}
