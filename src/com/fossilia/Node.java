/**
 * @author Faisal Bagalagel
 * ID: 105049481
 * @since Feb 23, 2020
 * Node.java
 * NOde class with all node properties
 */


package com.fossilia;

import java.awt.*;
import java.util.ArrayList;

public class Node implements java.io.Serializable {

    Point pos;
    Rectangle hitbox;
    ArrayList<Edge> sendingEdges;
    ArrayList<Edge> recievingEdges;

    public Node(int x, int y){
        sendingEdges = new ArrayList<>();
        recievingEdges = new ArrayList<>();
        pos = new Point((int)x, (int)y);
        hitbox = new Rectangle((int)pos.getX(), (int)pos.getY(), 14, 14);
    }

    public void draw(Graphics g){
        g.fillOval((int)pos.getX()-7, (int)pos.getY()-7, 14, 14);
        hitbox = new Rectangle((int)pos.getX()-7, (int)pos.getY()-7, 14, 14);
    }

    public boolean contains(int x, int y){
        if(hitbox.contains(x, y)){
            return true;
        }
        else{
            return false;
        }
    }

    public void addSendingEdge(Edge send){
        sendingEdges.add(send);
    }

    public void addReceivingEdge(Edge receive){
        recievingEdges.add(receive);
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public void move(int x, int y){
        pos = new Point(x,y);
        for(Edge edge: recievingEdges){
            edge.setEnd(pos);
        }
        for(Edge edge: sendingEdges){
            edge.setStart(pos);
        }
    }

    public void deleteReceivingEdges(){
        for(Edge edge: recievingEdges){
            edge = null;
        }
    }

    public void deleteSendingEdges(){
        for(Edge edge: sendingEdges){
            edge = null;
        }
    }

    public boolean containsEdge(Edge edge){
        for(Edge edge1: recievingEdges){
            if(edge1.equals(edge)){
                return true;
            }
        }
        for(Edge edge1: sendingEdges){
            if(edge1.equals(edge)){
                return true;
            }
        }
        return false;
    }
}
