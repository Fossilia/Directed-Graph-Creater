/**
 * @author Faisal Bagalagel
 * ID: 105049481
 * @since Feb 23, 2020
 * Edge.java
 * Edge class with all edge properties
 */

package com.fossilia;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class Edge implements java.io.Serializable {
    Node send;
    Node receive;
    Line2D line;
    AffineTransform tx;
    Polygon arrowHead;

    public Edge(Node send, Node recieve){
        this.send = send;
        this.receive = recieve;

        line = new Line2D.Float(send.getPos(), recieve.getPos());
        tx = new AffineTransform();
        arrowHead = new Polygon();
        arrowHead.addPoint( 0,7);
        arrowHead.addPoint( -7, -7);
        arrowHead.addPoint( 7,-7);
        //line = new Line2D.Double(send.getPos().getX()+7, send.getPos().getY()+7, recieve.getPos().getX()+7, recieve.getPos().getY()+7);
    }

    public void drawLine(Graphics2D g){
        g.setStroke(new BasicStroke(3));
        g.draw(line);
    }

    public void drawArrowHead(Graphics2D g){
        //drawing arrowhead
        tx.setToIdentity();
        double angle = Math.atan2(line.getY2()-line.getY1(), line.getX2()-line.getX1());
        int differenceX = 7;
        int differenceY = 7;
        if(line.getX1()<line.getX2()){
            differenceX = -7;
        }
        if(line.getY1()<line.getY2()){
            differenceY = -7;
        }
        tx.translate(line.getX2()+differenceX, line.getY2()+differenceY);
        tx.rotate((angle-Math.PI/2d));
        g.setTransform(tx);
        //g.setColor(Color.GRAY);
        g.fill(arrowHead);
        g.setTransform(new AffineTransform());
    }

    public void draw(Graphics2D g){
        //g.setColor(Color.BLACK);
        drawLine(g);
        drawArrowHead(g);
    }

    public void setLine(Line2D.Float line){
        this.line = line;
    }

    public void setStart(Point pos){
        line = new Line2D.Float(pos, receive.getPos());
    }

    public void setEnd(Point pos){
        line = new Line2D.Float(send.getPos(), pos);
    }

    public boolean contains(int x, int y){
        if(pDistance(x, y, (float)line.getX1(), (float)line.getY1(), (float)line.getX2(), (float)line.getY2())<5){
            return true;
        }
        return false;
    }

    public float pDistance(float x, float y, float x1, float y1, float x2, float y2) {

        float A = x - x1; // position of point rel one end of line
        float B = y - y1;
        float C = x2 - x1; // vector along line
        float D = y2 - y1;
        float E = -D; // orthogonal vector
        float F = C;

        float dot = A * E + B * F;
        float len_sq = E * E + F * F;

        return (float) ((float) Math.abs(dot) / Math.sqrt(len_sq));
    }
}
