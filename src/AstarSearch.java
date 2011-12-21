import java.util.ArrayList;
import java.util.List;


public class AstarSearch 
{
	Node startingTile, goalTile, nextLoc;
	List<Node> tobeSearched, fullySearched;
	Ants ants;

	public AstarSearch(Tile start, Tile end, Ants ants)
	{
		this.ants = ants;
		startingTile = new Node(start, ants);
		goalTile = new Node(end, ants);

		tobeSearched = new ArrayList<Node>();
		tobeSearched.add(startingTile);
	}
	
	private boolean isGoal(Node testTile)//true if goal else false
	{//TODO change to tertiary operator 
		if(testTile == goalTile)//if the next tile is the goal return this tile and go no further
			{
				return true;
			}
		
		return false;
	}

	public Tile assessRoute(Node interestedNode)
	{
		while(!interestedNode.getTile().equals(goalTile.getTile())&&interestedNode != null)
		{
			interestedNode.setH(Math.abs(startingTile.getX()-goalTile.getX()) + Math.abs(startingTile.getY()-goalTile.getY()));//the estimated(heuristic) cost to reach the destination from here.
			interestedNode.setG(interestedNode.getG()+1);//the exact cost to reach this node from the starting node.
			interestedNode.setF(interestedNode.getG()+interestedNode.getH());//As the algorithm runs the F value of a node tells us how expensive we think it will be to reach our goal by way of that node.

			for(Node test : tobeSearched)//check for node with lowest F value;
			{
				if(test.getF() < interestedNode.getF())
				{
					nextLoc = test;
				}
			}

			if(isGoal(nextLoc))
			{
				return nextLoc.getTile();
			}

			if(fullySearched == null)//prevent null list
			{
				fullySearched = new ArrayList<Node>();
			}

			List<Node> toBeDeleted = new ArrayList<Node>();//temp node to prevent ConcurrentMod error

			fullySearched.add(nextLoc);//add nextLoc to list of already searched tiles

			if(tobeSearched.contains(nextLoc))//remove tile from list to be searched
			{
				toBeDeleted.add(nextLoc);
			}
			
			for(Node delete : toBeDeleted)
			{
				tobeSearched.remove(delete);
			}
			
			toBeDeleted.clear();

			for(Tile neighbor : nextLoc.getNeighbors())
			{
				Node temp = new Node(neighbor, ants);
				temp.setPreviousNode(nextLoc);
				tobeSearched.add(temp);
			}

			for(Node test : tobeSearched)
			{
				if(test.getF() < nextLoc.getF())
				{
					return test.getTile();
				}
				else
				{
					fullySearched.add(test);
					toBeDeleted.add(test);
				}
			}

			for(Node delete : toBeDeleted)
			{
				tobeSearched.remove(delete);
			}

			toBeDeleted.clear();
		}
		return interestedNode.getTile();
	}

	public Node getStart()
	{
		return startingTile;
	}
}
