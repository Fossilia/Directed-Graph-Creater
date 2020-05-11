/**
 * @author Faisal Bagalagel
 * ID: 105049481
 * @since Feb 23, 2020
 * Assignment5.java
 * Allows user to create a directed graph with nodes and edges, with the ability to save and load them
 */

package com.fossilia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectedGraphCreator extends JFrame implements java.io.Serializable{

    //final values for distinguishing the current mode
    public final int ADDNODE = 0;
    public final int ADDEDGESTART = 1;
    public final int MOVENODESTART = 2;
    public final int DELETENODE = 3;
    public final int DELETEEDGE = 4;
    public final int ADDEDGEEND = 5;
    public final int MOVENODEEND = 6;

    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    public int mode;
    JLabel text;
    Node tempStartNode;
    JPanel drawPanel, radioPanel, textPanel;
    JPopupMenu nodePop, edgePop, pop;
    JMenuItem addN, addE, moveN, deleteN, deleteE;
    Edge tempEdge;
    int tempX, tempY;
    int OGmode;
    String OGtext;

    public DirectedGraphCreator(){
        setTitle("Graph Draw");
        setSize(700, 750);

        try {
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {} //sets the look to be more modern

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mode = ADDNODE;
        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        drawPanel = new DrawPanel(); //where nodes and edges are drawn
        add(drawPanel);

        radioPanel = new RadioPanel(); //where user picks options
        add(radioPanel);

        textPanel = new TextPanel(); //where suggestion text is displayed
        add(textPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
	    new DirectedGraphCreator();
    }

    public class DrawPanel extends JPanel implements MouseListener, ActionListener{
        public DrawPanel(){
            setMaximumSize(new Dimension(700, 600));
            addMouseListener(this);

            pop = new JPopupMenu("Pop-up"); //menu to hold menu items for right clicking empty space
            nodePop = new JPopupMenu("Node Pop-up"); //menu to hold menu items for right clicking nodes
            edgePop = new JPopupMenu("Edge Pop-up"); //menu to hold menu items for right clicking nodes edges

            addN = new JMenuItem("Add Node"); //menu item
            addN.addActionListener(this);

            addE = new JMenuItem("Start Edge");
            addE.addActionListener(this);
            moveN = new JMenuItem("Move");
            moveN.addActionListener(this);
            deleteN = new JMenuItem("Delete");
            deleteN.addActionListener(this);

            deleteE = new JMenuItem("Delete");
            deleteE.addActionListener(this);

            pop.add(addN);

            nodePop.add(addE);
            nodePop.add(moveN);
            nodePop.add(deleteN);

            edgePop.add(deleteE);

            add(pop);
            add(nodePop);
            add(edgePop);

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            for(Node node: nodes){ //draw nodes
                node.draw(g);
            }
            for(Edge edge: edges){ //draw edges
                edge.draw(g2);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(e.getModifiers() == MouseEvent.BUTTON1_MASK) { //if left mouse button is clicked
                if (mode == ADDNODE) {
                    boolean hit = false;
                    for (Node node : nodes) { //checking if not clicking a node
                        if (node.contains(e.getX(), e.getY())) {
                            hit = true;
                        }
                    }
                    if (!hit) {
                        Node node = new Node(e.getX(), e.getY()); //adding a node
                        nodes.add(node);
                    }
                } else if (mode == MOVENODESTART) { //first click when moving a node
                    for (Node node : nodes) {
                        if (node.contains(e.getX(), e.getY())) {
                            tempStartNode = node; //save node
                            text.setText("Click where you want to move that node");
                            OGmode = MOVENODESTART;
                            mode = MOVENODEEND;
                            break;
                        }
                    }
                } else if (mode == MOVENODEEND) { //clicking where you want to place node saved
                    boolean hit = false;
                    for (Node node : nodes) {
                        if (node.contains(e.getX(), e.getY())) {
                            hit = true;
                        }
                    }
                    if (!hit) {
                        tempStartNode.move(e.getX(), e.getY()); //moving node
                        mode = OGmode;
                        text.setText("Click a node that you want to move.");
                    }
                } else if (mode == DELETENODE) { //deleting node
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).contains(e.getX(), e.getY())) {
                            for (int k = 0; k < edges.size(); k++) {
                                if (nodes.get(i).containsEdge(edges.get(k))) {
                                    edges.remove(k);
                                    k -= 1;
                                }
                            }
                            nodes.remove(i);
                            break;
                        }
                    }
                } else if (mode == ADDEDGESTART) { //starting an edge
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).contains(e.getX(), e.getY())) {
                            tempStartNode = nodes.get(i); //save node that starts edge
                            OGmode = ADDEDGESTART;
                            mode = ADDEDGEEND;
                            text.setText("Click another node to complete the edge.");
                            break;
                        }
                    }
                } else if (mode == ADDEDGEEND) { //ending an edge
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).contains(e.getX(), e.getY())) {
                            Edge edge = new Edge(tempStartNode, nodes.get(i)); //create an edge with previous saved edge and current edge clicked
                            edges.add(edge);
                            tempStartNode.addSendingEdge(edge);
                            nodes.get(i).addReceivingEdge(edge);
                            mode = OGmode;
                            text.setText("Click a node to add the start of the edge (sending).");
                            break;
                        }
                    }
                } else if (mode == DELETEEDGE) { //deleting an edge
                    for (int i = 0; i < edges.size(); i++) {
                        if (edges.get(i).contains(e.getX(), e.getY())) {
                            edges.remove(i);
                            break;
                        }
                    }
                }
                repaint();
            }
            if(e.getModifiers() == MouseEvent.BUTTON3_MASK){ //when right clicking (pop-ups)
                boolean clicked = false;

                for(Node node: nodes){
                    if(node.contains(e.getX(), e.getY())){ //if right clikcing a node
                        tempStartNode = node;
                        nodePop.show(this, e.getX(), e.getY());
                        clicked = true;
                        break;
                    }
                }
                for(Edge edge: edges){
                    if(edge.contains(e.getX(), e.getY()) && !clicked){ //if right clicking an edge
                        tempEdge = edge;
                        edgePop.show(this, e.getX(), e.getY());
                        clicked = true;
                        break;
                    }
                }

                if(!clicked){ //if right clicking screen
                    pop.show(this, e.getX(), e.getY());
                    tempX = e.getX();
                    tempY = e.getY();
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == addN){ //adding node through pop up
                Node node = new Node(tempX, tempY);
                nodes.add(node);
            }
            if(e.getSource() == deleteE){ //deleting edge through pop-up
                for(int i=0; i<edges.size(); i++){
                    if(edges.get(i).equals(tempEdge)){
                        edges.remove(i);
                        break;
                    }
                }
            }
            if(e.getSource() == deleteN){ //deleting node through pop-up
                for(int i=0; i<nodes.size(); i++){
                    if(nodes.get(i).equals(tempStartNode)){
                        for (int k = 0; k < edges.size(); k++) {
                            if (nodes.get(i).containsEdge(edges.get(k))) {
                                edges.remove(k);
                                k -= 1;
                            }
                        }
                        nodes.remove(i);
                        break;
                    }
                }
            }
            if(e.getSource() == moveN){ //moving node through pop-up
                OGmode = mode;
                mode = MOVENODEEND;
                text.setText("Click where you want to move that node");
            }
            if(e.getSource() == addE){ //adding edge through pop-up
                OGmode = mode;
                mode = ADDEDGEEND;
                text.setText("Click another node to complete the edge.");
            }
            drawPanel.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }
    }

    public class RadioPanel extends JPanel implements ActionListener{
        JRadioButton nAdd, nMove, nDelete, eAdd, eDelete;
        ButtonGroup menu;
        Button save, load;

        public RadioPanel(){
            setMaximumSize(new Dimension(700, 100));
            setBackground(new Color(140, 136, 136));
            setLayout(new GridLayout(2, 5));
            JLabel nodeLabel = new JLabel("Nodes:");
            add(nodeLabel);

            //radio buttons
            nAdd = new JRadioButton("Add", true);
            nAdd.setOpaque(false);
            nAdd.setSelected(true);
            nMove = new JRadioButton("Move", false);
            nMove.setOpaque(false);
            nDelete = new JRadioButton("Delete", false);
            nDelete.setOpaque(false);

            nAdd.addActionListener(this);
            nMove.addActionListener(this);
            nDelete.addActionListener(this);

            menu = new ButtonGroup();
            menu.add(nAdd);
            menu.add(nMove);
            menu.add(nDelete);

            add(nAdd);
            add(nMove);
            add(nDelete);

            save = new Button("save");
            save.addActionListener(this);
            add(save);

            JLabel edgeLabel = new JLabel("Edges:");
            add(edgeLabel);

            eAdd = new JRadioButton("Add", true);
            eAdd.setOpaque(false);
            eDelete = new JRadioButton("Delete", false);
            eDelete.setOpaque(false);

            eAdd.addActionListener(this);
            eDelete.addActionListener(this);

            menu.add(eAdd);
            menu.add(eDelete);

            add(eAdd);
            add(eDelete);
            add(new Label());

            load = new Button("Load");
            load.addActionListener(this);
            add(load);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //activating modes through radio buttons
            if(e.getSource() == nAdd){
                mode = ADDNODE;
                text.setText("Click anywhere on the panel to add a node.");
            }
            if(e.getSource() == eAdd){
                mode = ADDEDGESTART;
                text.setText("Click a node to add the start of the edge (sending).");
            }
            if(e.getSource() == nMove){
                mode = MOVENODESTART;
                text.setText("Click a node that you want to move.");
            }
            if(e.getSource() == nDelete){
                mode = DELETENODE;
                text.setText("Click a node to delete it.");
            }
            if(e.getSource() == eDelete){
                mode = DELETEEDGE;
                text.setText("Click an edge to delete it.");
            }
            //if clicking save button
            if(e.getSource() == save){
                //save arrays to map
                HashMap<String, ArrayList> saved = new HashMap<String, ArrayList>();
                saved.put("nodes", nodes);
                saved.put("edges", edges);
                try {
                    //save map to file (save.graph)
                    FileOutputStream fos = new FileOutputStream("save.graph");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(saved);
                    oos.flush();
                    oos.close();
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                text.setText("graph saved to save.graph");
            }

            if(e.getSource() == load){
                try{
                    //loading map from file
                    FileInputStream fis = new FileInputStream("save.graph");
                    ObjectInputStream ois = new ObjectInputStream(fis);

                    HashMap<String,ArrayList> retreived = (HashMap<String,ArrayList>)ois.readObject();
                    fis.close();

                    nodes = retreived.get("nodes");
                    edges = retreived.get("edges");
                    drawPanel.repaint();
                    text.setText("graph loaded from save.graph");
                }
                catch(ClassNotFoundException | IOException e1){
                    e1.printStackTrace();
                }
            }
        }
    }

    public class TextPanel extends JPanel{

        public TextPanel(){
            setMaximumSize(new Dimension(700, 50));
            setLayout(new FlowLayout());
            text = new JLabel("Testing text");
            text.setText("Click anywhere on the panel to add a node.");
            text.setHorizontalAlignment(SwingConstants.CENTER);
            text.setVerticalAlignment(SwingConstants.CENTER);
            add(text);
        }
    }

}

/*
// create an AffineTransform
// and a triangle centered on (0,0) and pointing downward
// somewhere outside Swing's paint loop
AffineTransform tx = new AffineTransform();
Line2D.Double line = new Line2D.Double(0,0,100,100);

Polygon arrowHead = new Polygon();
arrowHead.addPoint( 0,5);
arrowHead.addPoint( -5, -5);
arrowHead.addPoint( 5,-5);

// [...]
private void drawArrowHead(Graphics2D g2d) {
    tx.setToIdentity();
    double angle = Math.atan2(line.y2-line.y1, line.x2-line.x1);
    tx.translate(line.x2, line.y2);
    tx.rotate((angle-Math.PI/2d));

    Graphics2D g = (Graphics2D) g2d.create();
    g.setTransform(tx);
    g.fill(arrowHead);
    g.dispose();
}
 */

/*
String first = "first";
    String second = "second";
    HashMap<String, Object> saved = new HashMap<String, Object>();
    saved.put("A", first);
    saved.put("B", second);

    try {
        FileOutputStream fos = new FileOutputStream("test.obj");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(saved);
        oos.flush();
        oos.close();
        fos.close();


        FileInputStream fis = new FileInputStream("test.obj");
        ObjectInputStream ois = new ObjectInputStream(fis);

        HashMap<String,Object> retreived = (HashMap<String,Object>)ois.readObject();
        fis.close();

        System.out.println(retreived.get("A"));
        System.out.println(retreived.get("B"));
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}
 */

/*
public float pDistance(float x, float y, float x1, float y1, float x2, float y2) {

      float A = x - x1; // position of point rel one end of line
      float B = y - y1;
      float C = x2 - x1; // vector along line
      float D = y2 - y1;
      float E = -D; // orthogonal vector
      float F = C;

      float dot = A * E + B * F;
      float len_sq = E * E + F * F;

      return (float) Math.abs(dot) / Math.sqrt(len_sq);
    }
 */