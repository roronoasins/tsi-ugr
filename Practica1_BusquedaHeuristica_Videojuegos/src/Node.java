package src_gvgai;

import tools.Vector2d;

public class Node implements Comparable<Node>{

    Node parent;
    public int x;
    public int y;
    public int g;
    public int h;

    public Node(Vector2d xy)
    {
        g = 1;
        h = 0;
        x = (int)xy.x;
        y = (int)xy.y;
        parent = null;
    }

    @Override
    public int compareTo(Node n) {
        if(this.h + this.g < n.h + n.g)
            return -1;
        if(this.h + this.g > n.h + n.g)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object anObject)
    {
        if (anObject instanceof Node)
        {
            Node anotherObj = (Node) anObject;
            if(this.x == anotherObj.x && this.y == anotherObj.y)
                return true;
        }
        return false;
    }
}
