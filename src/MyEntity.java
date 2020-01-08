public class MyEntity extends Entity {
	/**
	 * @param myId
	 * 			Node ID.
	 * @param myNeighbors
	 * 			myNeighbors[i] is the distance from {@code this} node to the
	 * 			i-th node.
	 */
	public MyEntity(int myId, int[] myNeighbors) {
		this.myId=myId;
		this.myNeighbors=new int[myNeighbors.length];
		for(int i=0; i<this.myNeighbors.length; i++)
		{
			if(i!=this.myId)
				this.myNeighbors[i]=myNeighbors[i];
		}
		this.myNeighbors[myId]=Project.INF;
	}//MyEntity

	/**
	 * Initialization (first broadcast).
	 */
	public final void rtinit() {
		/*
		 * Initilize the distance table with the direct destinations
		 * Now, just the main diagonal of the table is not INFINTE
		 */
		for(int i=0; i<NetworkSimulator.NUMENTITIES; i++)
		{
			for(int j=0; j<NetworkSimulator.NUMENTITIES; j++)
			{
				if(i==j)
					this.distanceTable[j][i]=this.myNeighbors[i];
				else this.distanceTable[j][i]=Project.INF;
			}
		}
		System.out.println(this); //print the matrix after init
		/*
		 * Tell the neighbors that I update my table
		 * Pass on my direct neighbors, 
		 * For each neighbor I send the new distance vector via all the entities
		 */
		for(int i=0; i<NetworkSimulator.NUMENTITIES; i++)
		{
			if(this.myNeighbors[i]==Project.INF)
				continue;
			for(int j=0; j<NetworkSimulator.NUMENTITIES; j++)
			{	
				if(this.myNeighbors[j]==Project.INF || i==j)
					continue;
				Packet p=new Packet(this.myId, i, j, this.distanceTable[j]);
				NetworkSimulator.toLayer2(p);
			}
		}
	}//start

	/**
	 *  Handles the received packet, this action includes an update of the
	 *  distance table if any.
	 *  @param p
	 *  		Packet that have been received.
	 */
	public final void rtupdate(Packet p) {
		/*
		 * Check if it is worth to update my table due to the packet
		 * If yes, update my table.
		 * Then, pass on my direct neighbors an send them the new distance vector
		 */
		if(this.myNeighbors[p.getSource()]+p.getMinCost() < 
				this.distanceTable[p.getDvIndex()][p.getSource()])
		{
			this.distanceTable[p.getDvIndex()][p.getSource()]=
					this.myNeighbors[p.getSource()]+p.getMinCost();

			for(int i=0; i<NetworkSimulator.NUMENTITIES; i++)
			{
				if(this.myNeighbors[i]!=Project.INF && p.getDvIndex()!=i)
				{
					Packet pkt=new Packet(this.myId, i, p.getDvIndex(), this.distanceTable[p.getDvIndex()]);
					NetworkSimulator.toLayer2(pkt);
				}
			}//for
		}//if
	}//rtupdate
}//class