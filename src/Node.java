
public class Node 
{
	private final int xCoord, yCoord, MAX_X, MAX_Y;
	private final int MIN_X = 0, MIN_Y = 0;
	private int F, G, H;
	private Ants ants;
	private Tile location;
	Node previousNode;
	private Tile [] neighbors;

	/*	
	G 
	the exact cost to reach this node from the starting node.
	H 
	the estimated(heuristic) cost to reach the destination from here.
	F = G + H 
	As the algorithm runs the F value of a node tells us how expensive we think it will be to reach our goal by way of that node.
	*/

	public Node(Tile loc, Ants ants)
	{
		this.ants = ants;
		MAX_X = ants.getCols();
		MAX_Y = ants.getRows();
		location = loc;
		xCoord = location.getCol();
		yCoord = location.getRow();
		F=G=H=0;
		setNeighbors();
	}
	
	private void setNeighbors()
	{
		if(neighbors == null)
		{
			neighbors = new Tile[4];
		}
		//TODO fix for wrap around!! ternary... (check ? true : false)
		neighbors[0] = (xCoord >= MAX_X)? new Tile(MIN_X, yCoord): new Tile(xCoord+1,yCoord);//North
		neighbors[1] = (xCoord <= MIN_X)? new Tile(MAX_X, yCoord): new Tile(xCoord-1,yCoord);//South
		neighbors[2] = (yCoord >= MAX_Y)? new Tile(xCoord, MIN_Y): new Tile(xCoord, yCoord +1);//East
		neighbors[3] = (yCoord <= MIN_Y)? new Tile(xCoord, MAX_Y): new Tile(xCoord,yCoord-1);//West
	}
	
	public int getH()
	{
		return H; 
	}
	
	public void setH(int h)
	{
		this.H = h;
	}
	
	public int getF()
	{
		return F;
	}
	
	public void setF(int f)
	{
		this.F = f;
	}
	
	public int getG()
	{
		return G;
	}
	
	public void setG(int g)
	{
		this.G = g;
	}
	
	public int getX()
	{
		return xCoord;
	}
	
	public int getY()
	{
		return yCoord;
	}
	
	public void setPreviousNode(Node from)
	{
		this.previousNode = from;
	}
	
	public Tile[] getNeighbors()
	{
		return neighbors;
	}
	
	public Tile getTile()
	{
		return location;
	}
	
	public Node getPreviousTile()
	{
		return previousNode;
	}
}
