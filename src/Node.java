
public class Node 
{
	private final int xCoord, yCoord;
	private int F, G, H;
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

	public Node(Tile loc)
	{
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
		neighbors[0] = new Tile(xCoord+1,yCoord);
		neighbors[1] = new Tile(xCoord-1,yCoord);
		neighbors[2] = new Tile(xCoord,yCoord+1);
		neighbors[3] = new Tile(xCoord,yCoord-1);
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
