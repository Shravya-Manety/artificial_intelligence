Problem definition details

You will write a program that will take an input file that describes the terrain map, landing site,
target sites, and characteristics of the robot. For each target site, you should find the optimal
(shortest) safe path from the landing site to that target. A path is composed of a sequence of
elementary moves. Each elementary move consists of moving the rover to one of its 8 neighbors.
To find the solution you will use the following algorithms:
- Breadth-first search (BFS)
- Uniform-cost search (UCS)
- A* search (A*).

Your algorithm should return an optimal path, that is, with shortest possible operational path
length. Operational path length is further described below and is not equal to geometric path
length. If an optimal path cannot be found, your algorithm should return “FAIL” as further
described below.
Terrain map
We assume a terrain map that is specified as follows:
A matrix with H rows (where H is a strictly positive integer) and W columns (W is also a strictly
positive integer) will be given, with a Z elevation value (an integer number, to avoid rounding
problems) specified in every cell of the WxH map. For example:
10 20 30
12 13 14
is a map with W=3 columns and H=2 rows, and each cell contains a Z value (in arbitrary units). By
convention, we will use North (N), East (E), South (S), West (W) as shown above to describe
motions from one cell to another. In the above example, Z elevation in the North West corner of
the map is 10, and Z elevation in the South East corner is 14.
To help us distinguish between your three algorithm implementations, you must follow the
following conventions for computing operational path length:

- Breadth-first search (BFS)
In BFS, each move from one cell to any of its 8 neighbors counts for a unit path cost of 1. You do
not need to worry about elevation differences (except that you still need to ensure that they are
allowable and not too steep for your rover), or about the fact that moving diagonally (e.g., North-
East) actually is a bit longer

- Uniform-cost search (UCS)
When running UCS, you should compute unit path costs in 2D. Assume that cells’ center
coordinates projected to the 2D ground plane are spaced by a 2D distance of 10 North-South and
East-West. That is, a North or South or East or West move from a cell to one of its 4-connected
neighbors incurs a unit path cost of 10, while a diagonal move to a neighbor incurs a unit path
cost of 14 as an approximation to 10√𝟐 when running UCS.

- A* search (A*).
When running A*, you should compute an approximate integer unit path cost of each move in
3D, by summing the horizontal move distance as in the UCS case (unit cost of 10 when moving
North to South or East to West, and unit cost of 14 when moving diagonally), plus the absolute
difference in elevation between the two cells. For example, moving diagonally from one cell with
Z=20 to adjacent North-East cell with elevation Z=18 would cost 14+|20-18|=16. Moving from a
cell with Z=-23 to adjacent cell to the West with Z=-30 would cost 10+|-23+30|=17. You need to
design an admissible heuristic for A* for this problem.

INPUT:

A*

8 6

4 4

7

2

1 1

6 3

0 0 0 0 0 0 0 0

0 60 64 57 45 66 68 0

0 63 64 57 45 67 68 0

0 58 64 57 45 68 67 0

0 60 61 67 65 66 69 0

0 0 0 0 0 0 0 0


OUTPUT: 

4,4 3,4 2,3 2,2 1,1

4,4 5,4 6,3
