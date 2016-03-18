package Utilities;

import java.awt.*;

public class PathNode {
    public Point pos;
    public PathNode parent;
    public float g;
    public double h;
    public double f;

    public PathNode(Point pos, PathNode parent){
        this.pos = pos;
        this.parent = parent;
    }

    public PathNode(Point pos){
        this.pos = pos;
    }

    @Override
    public String toString(){
        return ("(x:"+pos.x+", y:"+pos.y+")");
    }
}
