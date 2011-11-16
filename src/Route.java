

/*	pseudo-code for a* 
 * 
 * 
 * create the open list of nodes, initially containing only our starting node
   create the closed list of nodes, initially empty
   while (we have not reached our goal) {
       consider the best node in the open list (the node with the lowest f value)
       if (this node is the goal) {
           then we're done
       }
       else {
           move the current node to the closed list and consider all of its neighbors
           for (each neighbor) {
               if (this neighbor is in the closed list and our current g value is lower) {
                   update the neighbor with the new, lower, g value 
                   change the neighbor's parent to our current node
               }
               else if (this neighbor is in the open list and our current g value is lower) {
                   update the neighbor with the new, lower, g value 
                   change the neighbor's parent to our current node
               }
               else this neighbor is not in either the open or closed list {
                   add the neighbor to the open list and set its g value
               }
           }
       }
   }
*/

public class Route implements Comparable<Route>
{
	private final Tile start, end;
	private final int distance;
	
	public Route(Tile start, Tile end, int distance)
	{
		this.start =  start;
		this.end = end;
		this.distance = distance;
	}
	
	public Tile getStart()
	{
		return start;
	}
	
	public Tile getEnd()
	{
		return end;
	}
	
	public int getDistance()
	{
		return distance;
	}
	
	public int compareTo(Route route)
	{
		return (distance - route.distance);
	}
	
	public int hashCode()
	{
		return start.hashCode() * Ants.MAX_MAP_SIZE * Ants.MAX_MAP_SIZE + end.hashCode();
	}
	
	public boolean equals(Object o)
	{
		boolean result = false;
		
		if(o instanceof Route)
		{
			Route route = (Route)o;
			result = start.equals(route.start) && end.equals(route.end);
		}
		
		return result;
	}
}
