import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MyBot extends Bot 
{
	private Set<Tile> map = new HashSet<Tile>();

	/**
	 * Main method executed by the game engine for starting the bot.
	 * 
	 * @param args command line arguments
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException 
	{
		new MyBot().readSystemInput();
	}

	//initiate some variables
	private Map<Tile,Tile> orders = new HashMap<Tile, Tile>();//set of orders given to prevent ants moving to same tile
	//private Map<Tile,Tile> pastOrders = new HashMap<Tile, Tile>();//list of last turns orders to prevent oscillating 
	private Set<Tile> unseenTiles;//map of all unseen tiles
	private Set<Tile> enemyHills = new HashSet<Tile>();//map of all found enemy hills
	private Set<Tile> destinations = new HashSet<Tile>();
	int clear = 0;

	private boolean doMoveDirection(Tile antLoc, Aim direction)
	{
		Ants ants = getAnts();
		//Track all movements, prevent collisions
		Tile newLoc = ants.getTile(antLoc, direction);
		if(ants.getIlk(newLoc).isPassable() && !orders.containsKey(newLoc))// && !pastOrders.containsKey(newLoc) && map.contains(newLoc))
		{
			ants.issueOrder(antLoc, direction);
			orders.put(newLoc, antLoc);
			return true;
		}
		else
		{
			return false;
		}
	}

	//find efficient path using A*
	//create data structure of tiles to be checked... maybe use unseen tiles and manipulate this data 
	//get list of neighbors using ants.getIlk(new Tile(interestedTile.getRow/Col +/- )) aka do math to get tiles surrounding interestedTile


	private boolean doMoveLocation(Tile antLoc, Tile destLoc)
	{
		Ants ants = getAnts();
		//track targets to prevent friendly ant on ant violence
		List<Aim> directions = ants.getDirections(antLoc, destLoc);

		for(Aim direction : directions)
		{
			if(doMoveDirection(antLoc, direction))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void doTurn() 
	{
		Ants ants = getAnts();

		createOverViewMap(ants);

		/*switch(clear)
		{
		case 0:
			clear++;
			break;
		case 1:
			clear++;
			break;
		case 2:
			pastOrders.clear();
			clear = 0;
			break;
		default:
			clear = 0;
			break;
		}*/

		//pastOrders.putAll(orders);
		orders.clear();

		if((unseenTiles != null)&&(!unseenTiles.isEmpty()))
		{
			// remove any tiles that can be seen, run each turn
			for (Iterator<Tile> locIter = unseenTiles.iterator(); locIter.hasNext(); ) 
			{
				Tile next = locIter.next();
				if (ants.isVisible(next)) 
				{
					locIter.remove();
				}
			}
		}

		// prevent stepping on own hill
		for (Tile myHill : ants.getMyHills()) 
		{
			orders.put(myHill, null);
		}

		// find close food
		TreeSet<Tile> sortedFood = new TreeSet<Tile>(ants.getFoodTiles());
		TreeSet<Tile> sortedAnts = new TreeSet<Tile>(ants.getMyAnts());

		gatherFood(sortedFood, sortedAnts, ants);

		unblockFriendlyHills(ants);

		exploreNewTerritory(sortedAnts, ants);
		
		attackEnemyHills(sortedAnts, ants);

		huntEnemyAnts(ants);


		//pastOrders.clear();
	}

	private void gatherFood(TreeSet<Tile> sortedFood, TreeSet<Tile> sortedAnts, Ants ants) 
	{
		//Map<Tile, Tile> foodTargets = new HashMap<Tile, Tile>();
		Set<Node> foodTargets = new HashSet<Node>();
		List<Route> foodRoutes = new ArrayList<Route>();

		for (Tile foodLoc : sortedFood) 
		{
			for (Tile antLoc : sortedAnts) 
			{
				int distance = ants.getDistance(antLoc, foodLoc);
				
				//Node foodNode = new Node(foodLoc);
				//Node antNode = new Node(antLoc);
				AstarSearch path = new AstarSearch(antLoc, foodLoc);
				Tile nextMove = path.assessRoute(new Node(antLoc));
				Route route = new Route(antLoc, nextMove, distance);
				foodRoutes.add(route);
			}
		}
		
		Collections.sort(foodRoutes);
		for (Route route : foodRoutes) 
		{
			if (/*!foodTargets.contains(route.getEnd()) && !foodTargets.contains(route.getStart()) && */doMoveLocation(route.getStart(), route.getEnd())) 
			{
				//foodTargets.put(route.getEnd(), route.getStart());
				doMoveLocation(route.getStart(), route.getEnd());
			}
		}		
	}

	private void unblockFriendlyHills(Ants ants) 
	{
		// unblock hills
		for (Tile myHill : ants.getMyHills()) 
		{
			if (ants.getMyAnts().contains(myHill) && !orders.containsValue(myHill)) 
			{
				for (Aim direction : Aim.values()) 
				{
					if (doMoveDirection(myHill, direction)) 
					{
						break;
					}
				}
			}
		}
	}

	private void attackEnemyHills(TreeSet<Tile> sortedAnts, Ants ants) 
	{
		// add new hills to set
		for (Tile enemyHill : ants.getEnemyHills()) 
		{
			if (!enemyHills.contains(enemyHill)) 
			{
				enemyHills.add(enemyHill);
			}
		}

		// attack hills
		List<Route> hillRoutes = new ArrayList<Route>();
		for (Tile hillLoc : enemyHills) 
		{
			for (Tile antLoc : sortedAnts) 
			{
				if (!orders.containsValue(antLoc)) 
				{
					int distance = ants.getDistance(antLoc, hillLoc);
					Route route = new Route(antLoc, hillLoc, distance);
					hillRoutes.add(route);
				}
			}
		}

		Collections.sort(hillRoutes);
		for (Route route : hillRoutes) 
		{
			doMoveLocation(route.getStart(), route.getEnd());
		}
	}

	private void huntEnemyAnts(Ants ants)
	{
		//hunt enemy ants

		Set<Tile> targets = new HashSet<Tile>(ants.getEnemyAnts());

		for (Tile location : ants.getMyAnts()) 
		{
			boolean issued = false;
			Tile closestTarget = null;
			int closestDistance = 999999;
			for (Tile target : targets) 
			{
				int distance = ants.getDistance(location, target);
				if (distance < closestDistance) 
				{
					closestDistance = distance;
					closestTarget = target;
				}
			}

			if (closestTarget != null) 
			{
				List<Aim> directions = ants.getDirections(location, closestTarget);
				Collections.shuffle(directions);
				for (Aim direction : directions) 
				{
					Tile destination = ants.getTile(location, direction);
					if (ants.getIlk(destination).isUnoccupied() && !destinations.contains(destination)) 
					{
						ants.issueOrder(location, direction);
						destinations.add(destination);
						issued = true;
						break;
					}
				}
			}

			if (!issued) 
			{
				destinations.add(location);
			}
		}
	}

	private void exploreNewTerritory(TreeSet<Tile> sortedAnts, Ants ants) 
	{
		// explore unseen areas
		for (Tile antLoc : sortedAnts) 
		{
			if (!orders.containsValue(antLoc)) 
			{
				List<Route> unseenRoutes = new ArrayList<Route>();
				for (Tile unseenLoc : unseenTiles) 
				{
					int distance = ants.getDistance(antLoc, unseenLoc);
					Route route = new Route(antLoc, unseenLoc, distance);
					unseenRoutes.add(route);
				}

				Collections.sort(unseenRoutes);
				for (Route route : unseenRoutes) 
				{
					if (doMoveLocation(route.getStart(), route.getEnd())) 
					{
						break;
					}
				}
			}
		}
	}

	//this code is only run once at the beginning of the match
	private void createOverViewMap(Ants ants)
	{
		//create map of entire playing field land(valid) vs water(null)
		if (map == null)
		{
			map = new HashSet<Tile>();
		}
		else if((map != null) && (map.isEmpty()))
		{
			for(int c = 0 ; c < ants.getCols(); c++)
			{
				for(int r = 0; r < ants.getRows(); r++)
				{
					if(ants.getIlk(new Tile(r,c)).isPassable())
					{
						map.add(new Tile(r, c));
					}
					else
					{
						map.add(null);
					}
				}
			}
		}

		// add all locations to unseen tiles set, run once
		if (unseenTiles == null) 
		{
			unseenTiles = new HashSet<Tile>(map);
		}

	}
}
