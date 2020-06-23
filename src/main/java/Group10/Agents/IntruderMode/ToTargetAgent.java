package Group10.Agents;

import Group10.Agents.AStar.AFrame;
import Group10.Agents.AStar.AStarAlgo;
import Group10.Agents.AStar.Grid;
import Group10.Agents.AStar.Node;
import Group10.Agents.Container.IntruderContainer;
import Group10.Agents.Mode.Evasion;
import Group10.Algebra.Bias;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.NoAction;
import Interop.Action.Rotate;
import Interop.Action.Sprint;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;
import javafx.scene.paint.Color;
import Group10.Algebra.Line;
import Group10.Algebra.Vector;
import Group10.Engine.Game;
import Group10.GUI.GuiSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

public class ToTargetAgent implements Intruder {

    private State state;
    private ArrayList<IntruderAction> actionsQueue;

    private ORIENTATION ORIENTATION;
    
    private boolean yellHeard;
    private Direction guardDir;
    private Angle oldDirection;
    private boolean spotted; //If spotted a guard
    private boolean hitWall; //If wall hit/seen
    
    private boolean queueEmpty;
    
    private ArrayList<Node> AL;
    private int counter = 0;
    private int phase = 0; //0 is start, 1 is hitwall, 2 = normal
    private boolean returning;
    private double randomX = Math.random()*Grid.GRIDWIDTH * Grid.SQUARESIZE;
	private double randomY = Math.random()*Grid.GRIDHEIGHT * Grid.SQUARESIZE;
	private Node targetNode;
	private Node shadedNode;
	private double oldAngle;
	private boolean first = true;
	private boolean firstTeleport = true;
	private boolean firstSpotted = true;
	private boolean firstSpotted2;
    
    Grid grid;
    AFrame AF;
    
    //EXPERIMENTS
    public Grid gridCheck;
    public AFrame AFCheck;
    
    private int turn;

    public ToTargetAgent() {
        state = State.totarget;
        actionsQueue = new ArrayList<>();
        if(Math.random() > 0.5) {
            ORIENTATION = ORIENTATION.LEFT;
        }else{
            ORIENTATION = ORIENTATION.RIGHT;
        }
        
        grid = new Grid(new Point((Grid.GRIDWIDTH * Grid.SQUARESIZE)/2,(Grid.GRIDHEIGHT * Grid.SQUARESIZE)/2));
        
        AF = new AFrame(grid.getNodeGrid(), grid.SQUARESIZE);
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
    	if(percepts.getAreaPercepts().isJustTeleported() && firstTeleport) {
    		Grid oldGrid = grid;
    		grid = new Grid(new Point((Grid.GRIDWIDTH * Grid.SQUARESIZE)/2,(Grid.GRIDHEIGHT * Grid.SQUARESIZE)/2));
    		AFrame AF2 = AF;
    		AF = new AFrame(grid.getNodeGrid(), Grid.SQUARESIZE);
    		first = true;
    		firstTeleport = false;
    	}
    	else if(!percepts.getAreaPercepts().isJustTeleported() && !firstTeleport) {
    		firstTeleport = true;
    	}
    	if(first) {
    		if(oldAngle == 0) {
    			oldAngle = percepts.getTargetDirection().getRadians();
    		}
    		else {
    			double newAngle;
    			if(oldAngle >= percepts.getTargetDirection().getRadians()) {
    				newAngle = Math.PI*2 - oldAngle;
    			}
    			else {
    				newAngle = oldAngle;
    			}
    			//System.out.println(oldAngle);
    			//System.out.println(percepts.getTargetDirection().getRadians());
    			while(randomX < 0 || randomY < 0){
	    			double randomNum = ((double)Grid.GRIDWIDTH*(double)Grid.SQUARESIZE)/2;
	    	        if(newAngle >= 0 && newAngle < Math.PI/2) { //1
	    	        	randomX = (grid.getMyLocation().getX() - (Math.sin(newAngle)*randomNum));
	    	        	randomY = (grid.getMyLocation().getY() - (Math.cos(newAngle)*randomNum));
	    	        }
	    			else if(newAngle >= Math.PI/2 && newAngle < Math.PI) { //2
	    				randomX = (grid.getMyLocation().getX() - (Math.sin(Math.PI-newAngle)*randomNum));
	    	        	randomY = (grid.getMyLocation().getY() + (Math.cos(Math.PI-newAngle)*randomNum));
	    			}
	    			else if(newAngle >= Math.PI && newAngle < Math.PI*(3.0/2.0)) { //3
	    				randomX = (grid.getMyLocation().getX() + (Math.sin(newAngle-Math.PI)*randomNum));
	    	        	randomY = (grid.getMyLocation().getY() + (Math.cos(newAngle-Math.PI)*randomNum));
	    			}
	    			else if(newAngle >= Math.PI*(3.0/2.0) && newAngle <= Math.PI*2){ //4
	    				randomX = (grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-newAngle)*randomNum));
	    	        	randomY = (grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-newAngle)*randomNum));
	    			}
    			}
    	    	AF.color(AF.getGraphics(), 1, grid.getNodeC((int)randomX, (int)randomY).getX() ,grid.getNodeC((int)randomX, (int)randomY).getY());
    	        first = false;
    	        phase = 0;
    	        oldAngle = 0;
    		}
    	}
    	
    	//Getting all objects that intruder sees
    	VisionPrecepts VP;
		VP = percepts.getVision();
		ObjectPercepts OPS;
		OPS = VP.getObjects();
		Set<ObjectPercept> SOP;
		SOP = OPS.getAll();
		
		//Check each object if it is a guard
		hitWall=false;
		for(ObjectPercept OP : SOP) {
			firstSpotted2 = false;
			
			//Seeing empty space
			if(OP.getType() == ObjectPerceptType.EmptySpace) {
				double emptyAngle = Math.atan(OP.getPoint().getX()/OP.getPoint().getY());
				double emptyDistance = OP.getPoint().getDistanceFromOrigin().getValue();
				double checkAngle = grid.getMyDirection().getRadians() + emptyAngle;
				if(checkAngle<0) {
					checkAngle = checkAngle + Math.PI*2;
				}
				else if(checkAngle>=Math.PI*2) {
					checkAngle = checkAngle - Math.PI*2;
				}
				Node checkNode;
				if(checkAngle >= 0 && checkAngle < Math.PI/2) { //1
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(checkAngle)*emptyDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos(checkAngle)*emptyDistance)));
				}
				else if(checkAngle >= Math.PI/2 && checkAngle < Math.PI) { //2
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(Math.PI-checkAngle)*emptyDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(Math.PI-checkAngle)*emptyDistance)));
				}
				else if(checkAngle >= Math.PI && checkAngle < Math.PI*(3.0/2.0)) { //3
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin(checkAngle-Math.PI)*emptyDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(checkAngle-Math.PI)*emptyDistance)));
				}
				else if(checkAngle >= Math.PI*(3.0/2.0) && checkAngle <= Math.PI*2){ //4
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-checkAngle)*emptyDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-checkAngle)*emptyDistance)));
				}
				else {
					System.out.println(checkAngle);
					checkNode = null;
				}
				double tempX = Math.abs(grid.getMyLocation().getX() - (Math.sin(checkAngle)*emptyDistance));
				double tempY = Math.abs(grid.getMyLocation().getY() - (Math.cos(checkAngle)*emptyDistance));
				double tempX2 = Math.abs(checkNode.getX() - tempX);
				double tempY2 = Math.abs(checkNode.getY() - tempY);
				double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
				if(checkNode.getType() != 0 && distance<checkNode.getDistance()) {
					checkNode.setDistance(distance);
					checkNode.setType(-1);
					AF.color(AF.getGraphics(), -1, checkNode.getX(), checkNode.getY());
				}
			}
			//Check if seen object is door
			else if(OP.getType() == ObjectPerceptType.Door) {
				double doorAngle = Math.atan(OP.getPoint().getX()/OP.getPoint().getY());
				double doorDistance = OP.getPoint().getDistanceFromOrigin().getValue();
				double checkAngle = grid.getMyDirection().getRadians() + doorAngle;
				if(checkAngle<0) {
					checkAngle = checkAngle + Math.PI*2;
				}
				else if(checkAngle>=Math.PI*2) {
					checkAngle = checkAngle - Math.PI*2;
				}
				Node checkNode;
				if(checkAngle >= 0 && checkAngle < Math.PI/2) { //1
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(checkAngle)*doorDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos(checkAngle)*doorDistance)));
				}
				else if(checkAngle >= Math.PI/2 && checkAngle < Math.PI) { //2
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(Math.PI-checkAngle)*doorDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(Math.PI-checkAngle)*doorDistance)));
				}
				else if(checkAngle >= Math.PI && checkAngle < Math.PI*(3.0/2.0)) { //3
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin(checkAngle-Math.PI)*doorDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(checkAngle-Math.PI)*doorDistance)));
				}
				else if(checkAngle >= Math.PI*(3.0/2.0) && checkAngle <= Math.PI*2){ //4
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-checkAngle)*doorDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-checkAngle)*doorDistance)));
				}
				else {
					System.out.println(checkAngle);
					checkNode = null;
				}
				double tempX = Math.abs(grid.getMyLocation().getX() - (Math.sin(checkAngle)*doorDistance));
				double tempY = Math.abs(grid.getMyLocation().getY() - (Math.cos(checkAngle)*doorDistance));
				double tempX2 = Math.abs(checkNode.getX() - tempX);
				double tempY2 = Math.abs(checkNode.getY() - tempY);
				double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
				if(checkNode.getType() != 3 && distance<checkNode.getDistance()) {
					checkNode.setDistance(distance);
					checkNode.setType(3);
					AF.color(AF.getGraphics(), 3, checkNode.getX(), checkNode.getY());
				}
			}
			//If intruder hits wall
			else if(OP.getType() == ObjectPerceptType.Wall) {
				
				double wallAngle = Math.atan(OP.getPoint().getX()/OP.getPoint().getY());
				double wallDistance = OP.getPoint().getDistanceFromOrigin().getValue();
				double checkAngle = grid.getMyDirection().getRadians() + wallAngle;
				if(checkAngle<0) {
					checkAngle = checkAngle + Math.PI*2;
				}
				else if(checkAngle>=Math.PI*2) {
					checkAngle = checkAngle - Math.PI*2;
				}
				Node checkNode;
				if(checkAngle >= 0 && checkAngle < Math.PI/2) { //1
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(checkAngle)*wallDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos(checkAngle)*wallDistance)));
				}
				else if(checkAngle >= Math.PI/2 && checkAngle < Math.PI) { //2
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(Math.PI-checkAngle)*wallDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(Math.PI-checkAngle)*wallDistance)));
				}
				else if(checkAngle >= Math.PI && checkAngle < Math.PI*(3.0/2.0)) { //3
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin(checkAngle-Math.PI)*wallDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(checkAngle-Math.PI)*wallDistance)));
				}
				else if(checkAngle >= Math.PI*(3.0/2.0) && checkAngle <= Math.PI*2){ //4
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-checkAngle)*wallDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-checkAngle)*wallDistance)));
				}
				else {
					System.out.println(checkAngle);
					checkNode = null;
				}
				double tempX = Math.abs(grid.getMyLocation().getX() - (Math.sin(checkAngle)*wallDistance));
				double tempY = Math.abs(grid.getMyLocation().getY() - (Math.cos(checkAngle)*wallDistance));
				double tempX2 = Math.abs(checkNode.getX() - tempX);
				double tempY2 = Math.abs(checkNode.getY() - tempY);
				double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
				if(checkNode.getType() != 2  && distance<checkNode.getDistance()) {
					checkNode.setDistance(distance);
					checkNode.setType(2);
					AF.color(AF.getGraphics(), 2, checkNode.getX(), checkNode.getY());
					hitWall = true;
					spotted = false;
				}
			}
			//Check if seen object is window
			else if(OP.getType() == ObjectPerceptType.Window) {
				double windowAngle = Math.atan(OP.getPoint().getX()/OP.getPoint().getY());
				double windowDistance = OP.getPoint().getDistanceFromOrigin().getValue();
				double checkAngle = grid.getMyDirection().getRadians() + windowAngle;
				if(checkAngle<0) {
					checkAngle = checkAngle + Math.PI*2;
				}
				else if(checkAngle>=Math.PI*2) {
					checkAngle = checkAngle - Math.PI*2;
				}
				Node checkNode;
				if(checkAngle >= 0 && checkAngle < Math.PI/2) { //1
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(checkAngle)*windowDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos(checkAngle)*windowDistance)));
				}
				else if(checkAngle >= Math.PI/2 && checkAngle < Math.PI) { //2
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(Math.PI-checkAngle)*windowDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(Math.PI-checkAngle)*windowDistance)));
				}
				else if(checkAngle >= Math.PI && checkAngle < Math.PI*(3.0/2.0)) { //3
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin(checkAngle-Math.PI)*windowDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(checkAngle-Math.PI)*windowDistance)));
				}
				else if(checkAngle >= Math.PI*(3.0/2.0) && checkAngle <= Math.PI*2){ //4
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-checkAngle)*windowDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-checkAngle)*windowDistance)));
				}
				else {
					System.out.println(checkAngle);
					checkNode = null;
				}
				double tempX = Math.abs(grid.getMyLocation().getX() - (Math.sin(checkAngle)*windowDistance));
				double tempY = Math.abs(grid.getMyLocation().getY() - (Math.cos(checkAngle)*windowDistance));
				double tempX2 = Math.abs(checkNode.getX() - tempX);
				double tempY2 = Math.abs(checkNode.getY() - tempY);
				double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
				if(checkNode.getType() != 4 && distance<checkNode.getDistance()) {
					checkNode.setDistance(distance);
					checkNode.setType(4);
					AF.color(AF.getGraphics(), 4, checkNode.getX(), checkNode.getY());
				}
			}
			//Check if seen object is sentry tower
			else if(OP.getType() == ObjectPerceptType.SentryTower) {
				double SentryTowerAngle = Math.atan(OP.getPoint().getX()/OP.getPoint().getY());
				double SentryTowerDistance = OP.getPoint().getDistanceFromOrigin().getValue();
				double checkAngle = grid.getMyDirection().getRadians() + SentryTowerAngle;
				if(checkAngle<0) {
					checkAngle = checkAngle + Math.PI*2;
				}
				else if(checkAngle>=Math.PI*2) {
					checkAngle = checkAngle - Math.PI*2;
				}
				Node checkNode;
				if(checkAngle >= 0 && checkAngle < Math.PI/2) { //1
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(checkAngle)*SentryTowerDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos(checkAngle)*SentryTowerDistance)));
				}
				else if(checkAngle >= Math.PI/2 && checkAngle < Math.PI) { //2
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(Math.PI-checkAngle)*SentryTowerDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(Math.PI-checkAngle)*SentryTowerDistance)));
				}
				else if(checkAngle >= Math.PI && checkAngle < Math.PI*(3.0/2.0)) { //3
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin(checkAngle-Math.PI)*SentryTowerDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(checkAngle-Math.PI)*SentryTowerDistance)));
				}
				else if(checkAngle >= Math.PI*(3.0/2.0) && checkAngle <= Math.PI*2){ //4
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-checkAngle)*SentryTowerDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-checkAngle)*SentryTowerDistance)));
				}
				else {
					System.out.println(checkAngle);
					checkNode = null;
				}
				double tempX = Math.abs(grid.getMyLocation().getX() - (Math.sin(checkAngle)*SentryTowerDistance));
				double tempY = Math.abs(grid.getMyLocation().getY() - (Math.cos(checkAngle)*SentryTowerDistance));
				double tempX2 = Math.abs(checkNode.getX() - tempX);
				double tempY2 = Math.abs(checkNode.getY() - tempY);
				double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
				if(checkNode.getType() != 5 && distance<checkNode.getDistance()) {
					checkNode.setDistance(distance);
					checkNode.setType(5);
					AF.color(AF.getGraphics(), 5, checkNode.getX(), checkNode.getY());
					hitWall = true;
				}
			}
			//Check if seen object is shaded area
			else if(OP.getType() == ObjectPerceptType.ShadedArea) {
				double ShadedAreaAngle = Math.atan(OP.getPoint().getX()/OP.getPoint().getY());
				double ShadedAreaDistance = OP.getPoint().getDistanceFromOrigin().getValue();
				double checkAngle = grid.getMyDirection().getRadians() + ShadedAreaAngle;
				if(checkAngle<0) {
					checkAngle = checkAngle + Math.PI*2;
				}
				else if(checkAngle>=Math.PI*2) {
					checkAngle = checkAngle - Math.PI*2;
				}
				Node checkNode;
				if(checkAngle >= 0 && checkAngle < Math.PI/2) { //1
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(checkAngle)*ShadedAreaDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos(checkAngle)*ShadedAreaDistance)));
				}
				else if(checkAngle >= Math.PI/2 && checkAngle < Math.PI) { //2
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(Math.PI-checkAngle)*ShadedAreaDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(Math.PI-checkAngle)*ShadedAreaDistance)));
				}
				else if(checkAngle >= Math.PI && checkAngle < Math.PI*(3.0/2.0)) { //3
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin(checkAngle-Math.PI)*ShadedAreaDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(checkAngle-Math.PI)*ShadedAreaDistance)));
				}
				else if(checkAngle >= Math.PI*(3.0/2.0) && checkAngle <= Math.PI*2){ //4
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-checkAngle)*ShadedAreaDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-checkAngle)*ShadedAreaDistance)));
				}
				else {
					System.out.println(checkAngle);
					checkNode = null;
				}
				double tempX = Math.abs(grid.getMyLocation().getX() - (Math.sin(checkAngle)*ShadedAreaDistance));
				double tempY = Math.abs(grid.getMyLocation().getY() - (Math.cos(checkAngle)*ShadedAreaDistance));
				double tempX2 = Math.abs(checkNode.getX() - tempX);
				double tempY2 = Math.abs(checkNode.getY() - tempY);
				double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
				if(checkNode.getType() != 6 && distance<checkNode.getDistance()) {
					checkNode.setDistance(distance);
					checkNode.setType(6);
					AF.color(AF.getGraphics(), 6, checkNode.getX(), checkNode.getY());
				}
			}
			//Check if seen object is teleport
			else if(OP.getType() == ObjectPerceptType.Teleport) {
				double TeleportAngle = Math.atan(OP.getPoint().getX()/OP.getPoint().getY());
				double TeleportDistance = OP.getPoint().getDistanceFromOrigin().getValue();
				double checkAngle = grid.getMyDirection().getRadians() + TeleportAngle;
				if(checkAngle<0) {
					checkAngle = checkAngle + Math.PI*2;
				}
				else if(checkAngle>=Math.PI*2) {
					checkAngle = checkAngle - Math.PI*2;
				}
				Node checkNode;
				if(checkAngle >= 0 && checkAngle < Math.PI/2) { //1
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(checkAngle)*TeleportDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos(checkAngle)*TeleportDistance)));
				}
				else if(checkAngle >= Math.PI/2 && checkAngle < Math.PI) { //2
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(Math.PI-checkAngle)*TeleportDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(Math.PI-checkAngle)*TeleportDistance)));
				}
				else if(checkAngle >= Math.PI && checkAngle < Math.PI*(3.0/2.0)) { //3
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin(checkAngle-Math.PI)*TeleportDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(checkAngle-Math.PI)*TeleportDistance)));
				}
				else if(checkAngle >= Math.PI*(3.0/2.0) && checkAngle <= Math.PI*2){ //4
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-checkAngle)*TeleportDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-checkAngle)*TeleportDistance)));
				}
				else {
					System.out.println(checkAngle);
					checkNode = null;
				}
				double tempX = Math.abs(grid.getMyLocation().getX() - (Math.sin(checkAngle)*TeleportDistance));
				double tempY = Math.abs(grid.getMyLocation().getY() - (Math.cos(checkAngle)*TeleportDistance));
				double tempX2 = Math.abs(checkNode.getX() - tempX);
				double tempY2 = Math.abs(checkNode.getY() - tempY);
				double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
				if(checkNode.getType() != 7 && distance<checkNode.getDistance()) {
					checkNode.setDistance(distance);
					checkNode.setType(7);
					AF.color(AF.getGraphics(), 7, checkNode.getX(), checkNode.getY());
				}
			}
			//Check if seen object is target
			else if(OP.getType() == ObjectPerceptType.TargetArea) {
				double TargetAreaAngle = Math.atan(OP.getPoint().getX()/OP.getPoint().getY());
				double TargetAreaDistance = OP.getPoint().getDistanceFromOrigin().getValue();
				double checkAngle = grid.getMyDirection().getRadians() + TargetAreaAngle;
				if(checkAngle<0) {
					checkAngle = checkAngle + Math.PI*2;
				}
				else if(checkAngle>=Math.PI*2) {
					checkAngle = checkAngle - Math.PI*2;
				}
				Node checkNode;
				if(checkAngle >= 0 && checkAngle < Math.PI/2) { //1
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(checkAngle)*TargetAreaDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos(checkAngle)*TargetAreaDistance)));
				}
				else if(checkAngle >= Math.PI/2 && checkAngle < Math.PI) { //2
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() - (Math.sin(Math.PI-checkAngle)*TargetAreaDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(Math.PI-checkAngle)*TargetAreaDistance)));
				}
				else if(checkAngle >= Math.PI && checkAngle < Math.PI*(3.0/2.0)) { //3
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin(checkAngle-Math.PI)*TargetAreaDistance)),
							(int)Math.round(grid.getMyLocation().getY() + (Math.cos(checkAngle-Math.PI)*TargetAreaDistance)));
				}
				else if(checkAngle >= Math.PI*(3.0/2.0) && checkAngle <= Math.PI*2){ //4
					checkNode = grid.getNodeC((int)Math.round(grid.getMyLocation().getX() + (Math.sin((2*Math.PI)-checkAngle)*TargetAreaDistance)),
							(int)Math.round(grid.getMyLocation().getY() - (Math.cos((2*Math.PI)-checkAngle)*TargetAreaDistance)));
				}
				else {
					System.out.println(checkAngle);
					checkNode = null;
				}
				double tempX = Math.abs(grid.getMyLocation().getX() - (Math.sin(checkAngle)*TargetAreaDistance));
				double tempY = Math.abs(grid.getMyLocation().getY() - (Math.cos(checkAngle)*TargetAreaDistance));
				double tempX2 = Math.abs(checkNode.getX() - tempX);
				double tempY2 = Math.abs(checkNode.getY() - tempY);
				double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
				if(checkNode.getType() != 8 && distance<checkNode.getDistance()) {
					checkNode.setDistance(distance);
					checkNode.setType(8);
					AF.color(AF.getGraphics(), 8, checkNode.getX(), checkNode.getY());
					phase = 3;
					targetNode = checkNode;
				}
			}
			//If object is guard then intruder spotted a guard
			if(OP.getType() == ObjectPerceptType.Guard) {
				firstSpotted2 = true;
				//If he wasn't already spotted
				if(!spotted && firstSpotted) {
					spotted = true;
					clearQueue();
					guardDir = OP.getPoint().getClockDirection();
					oldDirection = grid.getMyDirection();
				}
				else {
					guardDir = OP.getPoint().getClockDirection();
					oldDirection = grid.getMyDirection();
				}
			}
		}
		if(!firstSpotted2) {
			firstSpotted = true;
		}
		else {
			firstSpotted = false;
		}
		//Checking for sounds the intruder hears
		SoundPercepts SP;
		SP = percepts.getSounds();
		Set<SoundPercept> SSP;
		SSP = SP.getAll();
		
		if(!spotted) {
			yellHeard = false;
		}
		//For each sound check if it is a yell
		for(SoundPercept sound : SSP) {
			if(sound.getType() == SoundPerceptType.Yell) {
				//If he wasn't already spotted
				if(!spotted) {
					spotted = true;
					yellHeard = true;
					guardDir = sound.getDirection();
				}
				else {
					yellHeard = true;
					guardDir = sound.getDirection();
				}
			}
		}
		//Marking empties the intruder is currently standing on
		double tempX = grid.getMyLocation().getX();
		double tempY = grid.getMyLocation().getY();
		Node tempNode = grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY());
		double tempX2 = Math.abs(tempNode.getX() - tempX);
		double tempY2 = Math.abs(tempNode.getY() - tempY);
		double distance = Math.sqrt(Math.pow(tempX2, 2)+Math.pow(tempY2, 2));
		if(distance < grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()).getDistance()) {
			if(!percepts.getAreaPercepts().isInDoor() && !percepts.getAreaPercepts().isInSentryTower() && !percepts.getAreaPercepts().isInWindow()) {
				tempNode.setDistance(distance);
				tempNode.setType(-1);
				AF.color(AF.getGraphics(), -1, tempNode.getX(), tempNode.getY());
			}
			if(percepts.getAreaPercepts().isInDoor() && !percepts.getAreaPercepts().isInSentryTower() && !percepts.getAreaPercepts().isInWindow()) {
				tempNode.setDistance(distance);
				tempNode.setType(1);
				AF.color(AF.getGraphics(), 1, tempNode.getX(), tempNode.getY());
			}
			if(!percepts.getAreaPercepts().isInDoor() && percepts.getAreaPercepts().isInSentryTower() && !percepts.getAreaPercepts().isInWindow()) {
				tempNode.setDistance(distance);
				tempNode.setType(5);
				AF.color(AF.getGraphics(), 5, tempNode.getX(), tempNode.getY());
			}
			if(!percepts.getAreaPercepts().isInDoor() && !percepts.getAreaPercepts().isInSentryTower() && percepts.getAreaPercepts().isInWindow()) {
				tempNode.setDistance(distance);
				tempNode.setType(4);
				AF.color(AF.getGraphics(), 4, tempNode.getX(), tempNode.getY());
			}
		}
		
		if(phase != 0 && hitWall) {
			phase = 1;
		}
		else if(phase != 0){
			phase = 2;
		}
		if(spotted) {
			phase = 4;
		}
		
    	switch(phase) {
    	case 0: //Start A* phase
    		AStarAlgo A = new AStarAlgo();
    		AL = A.findPath(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()), grid.getNodeC((int)randomX, (int)randomY), grid);
    		
    		if(AL != null) {
    			phase = 2;
        		returning = false;
        		counter = 0;
    			return moveTo(AL.get(counter).getX(),AL.get(counter).getY(),percepts);
    		}
    		else {
    			Node[] neighborList = grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()).getNeighbors();
    			for(int i=0;i<1000;i++) {
    				int j = (int)(Math.random()*8.0);
    				AL = A.findPath(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()), neighborList[j], grid);
    				if(AL != null) {
    					break;
    				}
    			}
    			phase = 2;
        		returning = false;
        		counter = 0;
    			return moveTo(AL.get(counter).getX(),AL.get(counter).getY(),percepts);
    		}
    	case 1: //Hit a wall phase
    		//AStarAlgo B = new AStarAlgo();
    		//AL = B.findPath(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()), grid.getNodeC((int)grid.getSpawnLocation().getX(), (int)grid.getSpawnLocation().getY()), grid);

    		clearQueue();
    		//counter = 0;
    		if(counter>0) {
    			counter--;
    		}
    		else {
    			counter = 0;
    		}
    		phase = 2;
    		returning = true;
    		//System.out.println(AL.size());
    		
    		return moveTo(AL.get(counter).getX(),AL.get(counter).getY(),percepts);
    	case 2: //Normal walking phase
    		if (actionsQueue.size() > 0){
	            return queueActions();
	        }
    		
    		if(returning) {
    			counter--;
    			if(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()).getType() != 2) {
    				phase = 0;
        			returning = false;
        			return new NoAction();
    			}
    		}
    		else {
    			counter++;
    		}
    		
    		if(counter >= AL.size() || counter < 0) {
    			phase = 0;
    			returning = false;
    			return new NoAction();
    		}
    		
    		return moveTo(AL.get(counter).getX(),AL.get(counter).getY(),percepts);
    	case 3: //Teleport phase
    		AStarAlgo C = new AStarAlgo();
    		AL = C.findPath(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()), targetNode, grid);
    		
    		clearQueue();
    		counter = 0;
    		
    		phase = 2;
    		
    		return moveTo(AL.get(counter).getX(),AL.get(counter).getY(),percepts);
    	case 4: //Spotted phase
    		if(shadedNode != null) {
    			AStarAlgo B = new AStarAlgo();
	    		AL = B.findPath(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()), shadedNode, grid);
    		
	    		clearQueue();
	    		counter = 0;
	    		
	    		phase = 2;
	    		
	    		spotted = false;
	    		
	    		return SprintTo(AL.get(counter).getX(),AL.get(counter).getY(),percepts);
    		} else {
	    		AStarAlgo B = new AStarAlgo();
	    		AL = B.findPath(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()), grid.getNodeC((int)grid.getSpawnLocation().getX(), (int)grid.getSpawnLocation().getY()), grid);
	
	    		clearQueue();
	    		counter = 0;
	    		
	    		phase = 2;
	    		
	    		spotted = false;
	    		
	    		if(percepts.getScenarioIntruderPercepts().getSprintCooldown() != 0) {
	    			return SprintTo(AL.get(counter).getX(),AL.get(counter).getY(),percepts);
	    		}
	    		else {
	    			return moveTo(AL.get(counter).getX(),AL.get(counter).getY(),percepts);
	    		}
    		}
    	}
    	return new NoAction();
    	/*
    	//System.out.println(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue());
        Angle orientation = percepts.getTargetDirection().getDistance(Angle.fromDegrees(new Point(0,1).getClockDirection().getDegrees()));
        
        //Getting all objects that intruder sees
    	VisionPrecepts VP;
		VP = percepts.getVision();
		ObjectPercepts OPS;
		OPS = VP.getObjects();
		Set<ObjectPercept> SOP;
		SOP = OPS.getAll();
		
		//Check each object if it is a guard
		hitWall=false;
		for(ObjectPercept OP : SOP) {
			//If object is guard then intruder spotted a guard
			if(OP.getType() == ObjectPerceptType.Guard) {
				//If he wasn't already spotted
				if(!spotted) {
					spotted = true;
					guardDir = OP.getPoint().getClockDirection();
					oldDirection = grid.getMyDirection();
				}
				else {
					guardDir = OP.getPoint().getClockDirection();
					oldDirection = grid.getMyDirection();
				}
			}
			//If intruder hits wall
			if(OP.getType() == ObjectPerceptType.Wall) {
				if(!hitWall) {
					hitWall = true;
				}	
			}
		}
		
		//Checking for sounds the intruder hears
		SoundPercepts SP;
		SP = percepts.getSounds();
		Set<SoundPercept> SSP;
		SSP = SP.getAll();
		
		if(!spotted) {
			yellHeard = false;
		}
		//For each sound check if it is a yell
		for(SoundPercept sound : SSP) {
			if(sound.getType() == SoundPerceptType.Yell) {
				//If he wasn't already spotted
				if(!spotted) {
					spotted = true;
					yellHeard = true;
					guardDir = sound.getDirection();
				}
				else {
					yellHeard = true;
					guardDir = sound.getDirection();
				}
			}
		}
		
		if (actionsQueue.size() > 0){
            return queueActions();
        }
		
		//If a guard has been spotted by the intruder
		if(spotted && !hitWall) {
			return moveTo(50,-50,percepts);
			//return evade(oldDirection,guardDir,percepts);
		}
		else {
	        if (!percepts.wasLastActionExecuted()) {
	            return ifStuck(percepts);
	        }
	        
	        if (orientation.getDegrees() < 90) {
	            if (ORIENTATION ==  ORIENTATION.LEFT){
	                ORIENTATION = ORIENTATION.RIGHT;
	            }
	        }
	        else if (orientation.getDegrees() > 270 ){
	            if (ORIENTATION == ORIENTATION.RIGHT)
	            ORIENTATION = ORIENTATION.LEFT;
	        }

	        if (state == State.totarget) {
	            return updateToTargetState(percepts);
	        }

	        if (state == State.navigate) {
	            return updateNavigationState(percepts);
	        }

	        if (state == State.explore) {
	            return updateExplorationState(percepts);
	        }
		}
        return new NoAction();
        */
    }

    //Evasion method
    private IntruderAction evade(Angle oldDirection, Direction guardDirection, IntruderPercepts percepts) {
    	//If guard direction is on the left side
    	if(guardDirection.getRadians() > Math.PI) {
    		//Set guard direction to be close to 0 like the right side 
    		Direction newGuardDirection = Direction.fromRadians(Math.abs(guardDirection.getRadians()-(Math.PI*2)));
    		
    		//If the angle between the orientation and the guard direction is less than 0.5 PI
    		if(Math.abs((oldDirection.getRadians()+newGuardDirection.getRadians()-grid.getMyDirection().getRadians())) < Math.PI/2) {
        		//System.out.println(Angle.fromRadians(newGuardDirection.getRadians() + (Math.PI/2)).getDegrees());
        		
        		double angleTracker = Angle.fromRadians(newGuardDirection.getRadians() + (Math.PI/2)).getRadians();
        		
        		while(angleTracker > Math.PI/4) {
        			actionsQueue.add(new Rotate(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle()));
        			angleTracker -= (Math.PI/4);
        		}
        		actionsQueue.add(new Rotate(Angle.fromRadians(angleTracker)));
    			//actionsQueue.add(new Rotate(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle()));
    			//actionsQueue.add(new Rotate(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle()));
        	}
    		//If the intruder heard a yell
        	/*else if(yellHeard){
        		actionsQueue.add(new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder()));
        	}
        	else {
        		actionsQueue.add(new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder()));
        	}*/
    		else {
        		actionsQueue.add(new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder()));
        	}
    		//If the sprint cooldown is 0 then set spotted to false
        	if(percepts.getScenarioIntruderPercepts().getSprintCooldown() == 0) {
        		//spotted = false;
        	}
    	}
    	else {
    		if(Math.abs((oldDirection.getRadians()+guardDirection.getRadians()-grid.getMyDirection().getRadians())) < Math.PI/2) {
    			double angleTracker = Angle.fromRadians(guardDirection.getRadians() - (Math.PI/2)).getRadians();
        		
        		while(angleTracker < -(Math.PI/4)) {
        			actionsQueue.add(new Rotate(Angle.fromRadians(-percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians())));
        			angleTracker += (Math.PI/4);
        		}
        		actionsQueue.add(new Rotate(Angle.fromRadians(angleTracker)));
    			//actionsQueue.add(new Rotate(Angle.fromRadians(-percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians())));
    			//actionsQueue.add(new Rotate(Angle.fromRadians(-percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians())));
        	}
        	/*else if(yellHeard){
        		actionsQueue.add(new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder()));
        	}
    		else {
        		actionsQueue.add(new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder()));
        	}*/
    		else {
        		actionsQueue.add(new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder()));
        	}
        	if(percepts.getScenarioIntruderPercepts().getSprintCooldown() == 0) {
        		spotted = false;
        	}
    	}
    	return queueActions();
    }
    
    private IntruderAction moveTo(double X, double Y, IntruderPercepts percepts) {
    	Point myLocation = grid.getMyLocation();
    	Direction myDirection = grid.getMyDirection();
    	Point targetLocation = new Point(X,Y);
    	Direction directionToTarget;
    	
    	if(X==myLocation.getX() && Y<myLocation.getY()) { //Bottom
    		directionToTarget = Direction.fromRadians(0);
    	}
    	else if(X<myLocation.getX() && Y<myLocation.getY()) { //Bottom Left quadrant (1)
    		directionToTarget = Direction.fromRadians(Math.atan(Math.abs(myLocation.getX()-X)/Math.abs(myLocation.getY()-Y)));
    	}
    	else if(X<myLocation.getX() && Y==myLocation.getY()) { //Left
    		directionToTarget = Direction.fromRadians(Math.PI/2);
    	}
    	else if(X<myLocation.getX() && Y>myLocation.getY()) { //Top Left quadrant (2)
    		directionToTarget = Direction.fromRadians(Math.atan(Math.abs(myLocation.getY()-Y)/Math.abs(myLocation.getX()-X)) + (Math.PI/2));
    	}
    	else if(X==myLocation.getX() && Y>myLocation.getY()) { //Top
    		directionToTarget = Direction.fromRadians(Math.PI);
    	}
    	else if(X>myLocation.getX() && Y>myLocation.getY()) { //Top Right quadrant (3)
    		directionToTarget = Direction.fromRadians(Math.atan(Math.abs(myLocation.getX()-X)/Math.abs(myLocation.getY()-Y)) + (Math.PI));
    	}
    	else if(X>myLocation.getX() && Y==myLocation.getY()) { //Right
    		directionToTarget = Direction.fromRadians(Math.PI*(3.0/2.0));
    	}
    	else if(X>myLocation.getX() && Y<myLocation.getY()) { //Bottom Right quadrant (4)
    		directionToTarget = Direction.fromRadians(Math.atan(Math.abs(myLocation.getY()-Y)/Math.abs(myLocation.getX()-X)) + (Math.PI*(3.0/2.0)));
    	}
    	else if(X==myLocation.getX() && Y==myLocation.getY()) { //Center
    		directionToTarget = null;
    		//spotted=false;
    		//System.out.println("no action (CENTER)");
    		if(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()) == grid.getNodeC((int)randomX, (int)randomY)) {
    			first = true;
    		}
    		return new NoAction();
    	}
    	else {
    		directionToTarget = null;
    		spotted=false;
    		return new NoAction();
    	}
    	
    	double angleToRotate;
    	if(myDirection.getRadians() > directionToTarget.getRadians()) {
    		if(Math.PI*2 - myDirection.getRadians() + directionToTarget.getRadians() > myDirection.getRadians() - directionToTarget.getRadians()) {
    			angleToRotate = -(myDirection.getRadians() - directionToTarget.getRadians());
    		}
    		else {
    			angleToRotate = Math.PI*2 - myDirection.getRadians() + directionToTarget.getRadians();
    		}
    	}
    	else {
    		if(Math.PI*2 - directionToTarget.getRadians() + myDirection.getRadians() > directionToTarget.getRadians() - myDirection.getRadians()) {
    			angleToRotate = (directionToTarget.getRadians() - myDirection.getRadians());
    		}
    		else {
    			angleToRotate = -(Math.PI*2 - directionToTarget.getRadians() + myDirection.getRadians());
    		}
    	}
    	
    	if(angleToRotate < 0) {
    		while(angleToRotate < -Math.PI/4) {
    			actionsQueue.add(new Rotate(Angle.fromRadians(-percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians())));
    			angleToRotate += (Math.PI/4);
    		}
    		actionsQueue.add(new Rotate(Angle.fromRadians(angleToRotate)));
    	}
    	else if(angleToRotate > 0){
    		while(angleToRotate > Math.PI/4) {
    			actionsQueue.add(new Rotate(Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians())));
    			angleToRotate -= (Math.PI/4);
    		}
    		actionsQueue.add(new Rotate(Angle.fromRadians(angleToRotate)));
    	}
    	
    	double distanceToTarget = Math.sqrt(Math.pow(Math.abs(myLocation.getX() - targetLocation.getX()), 2) + Math.pow(Math.abs(myLocation.getY() - targetLocation.getY()), 2));
    	
    	while(distanceToTarget>percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue()) {
    		actionsQueue.add(new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder()));
    		distanceToTarget -= percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue();
    	}
    	actionsQueue.add(new Move(new Distance(distanceToTarget)));
    	
    	//spotted = false;
    	return queueActions();
    }
    
    private IntruderAction SprintTo(double X, double Y, IntruderPercepts percepts) {
    	Point myLocation = grid.getMyLocation();
    	Direction myDirection = grid.getMyDirection();
    	Point targetLocation = new Point(X,Y);
    	Direction directionToTarget;
    	
    	if(X==myLocation.getX() && Y<myLocation.getY()) { //Bottom
    		directionToTarget = Direction.fromRadians(0);
    	}
    	else if(X<myLocation.getX() && Y<myLocation.getY()) { //Bottom Left quadrant (1)
    		directionToTarget = Direction.fromRadians(Math.atan(Math.abs(myLocation.getX()-X)/Math.abs(myLocation.getY()-Y)));
    	}
    	else if(X<myLocation.getX() && Y==myLocation.getY()) { //Left
    		directionToTarget = Direction.fromRadians(Math.PI/2);
    	}
    	else if(X<myLocation.getX() && Y>myLocation.getY()) { //Top Left quadrant (2)
    		directionToTarget = Direction.fromRadians(Math.atan(Math.abs(myLocation.getY()-Y)/Math.abs(myLocation.getX()-X)) + (Math.PI/2));
    	}
    	else if(X==myLocation.getX() && Y>myLocation.getY()) { //Top
    		directionToTarget = Direction.fromRadians(Math.PI);
    	}
    	else if(X>myLocation.getX() && Y>myLocation.getY()) { //Top Right quadrant (3)
    		directionToTarget = Direction.fromRadians(Math.atan(Math.abs(myLocation.getX()-X)/Math.abs(myLocation.getY()-Y)) + (Math.PI));
    	}
    	else if(X>myLocation.getX() && Y==myLocation.getY()) { //Right
    		directionToTarget = Direction.fromRadians(Math.PI*(3.0/2.0));
    	}
    	else if(X>myLocation.getX() && Y<myLocation.getY()) { //Bottom Right quadrant (4)
    		directionToTarget = Direction.fromRadians(Math.atan(Math.abs(myLocation.getY()-Y)/Math.abs(myLocation.getX()-X)) + (Math.PI*(3.0/2.0)));
    	}
    	else if(X==myLocation.getX() && Y==myLocation.getY()) { //Center
    		directionToTarget = null;
    		//spotted=false;
    		//System.out.println("no action (CENTER)");
    		if(grid.getNodeC((int)grid.getMyLocationRounded().getX(), (int)grid.getMyLocationRounded().getY()) == grid.getNodeC((int)randomX, (int)randomY)) {
    			first = true;
    		}
    		return new NoAction();
    	}
    	else {
    		directionToTarget = null;
    		spotted=false;
    		return new NoAction();
    	}
    	
    	double angleToRotate;
    	if(myDirection.getRadians() > directionToTarget.getRadians()) {
    		if(Math.PI*2 - myDirection.getRadians() + directionToTarget.getRadians() > myDirection.getRadians() - directionToTarget.getRadians()) {
    			angleToRotate = -(myDirection.getRadians() - directionToTarget.getRadians());
    		}
    		else {
    			angleToRotate = Math.PI*2 - myDirection.getRadians() + directionToTarget.getRadians();
    		}
    	}
    	else {
    		if(Math.PI*2 - directionToTarget.getRadians() + myDirection.getRadians() > directionToTarget.getRadians() - myDirection.getRadians()) {
    			angleToRotate = (directionToTarget.getRadians() - myDirection.getRadians());
    		}
    		else {
    			angleToRotate = -(Math.PI*2 - directionToTarget.getRadians() + myDirection.getRadians());
    		}
    	}
    	
    	if(angleToRotate < 0) {
    		while(angleToRotate < -Math.PI/4) {
    			actionsQueue.add(new Rotate(Angle.fromRadians(-percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians())));
    			angleToRotate += (Math.PI/4);
    		}
    		actionsQueue.add(new Rotate(Angle.fromRadians(angleToRotate)));
    	}
    	else if(angleToRotate > 0){
    		while(angleToRotate > Math.PI/4) {
    			actionsQueue.add(new Rotate(Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians())));
    			angleToRotate -= (Math.PI/4);
    		}
    		actionsQueue.add(new Rotate(Angle.fromRadians(angleToRotate)));
    	}
    	
    	double distanceToTarget = Math.sqrt(Math.pow(Math.abs(myLocation.getX() - targetLocation.getX()), 2) + Math.pow(Math.abs(myLocation.getY() - targetLocation.getY()), 2));
    	
    	while(distanceToTarget>percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder().getValue()) {
    		actionsQueue.add(new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder()));
    		distanceToTarget -= percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder().getValue();
    	}
    	actionsQueue.add(new Sprint(new Distance(distanceToTarget)));
    	
    	//spotted = false;
    	return queueActions();
    }
    
    //Make an Agent Bounce of the Wall
    private IntruderAction updateNavigationState(IntruderPercepts percepts){
        ObjectPercepts visionObjects = percepts.getVision().getObjects();
        ObjectPercepts walls = visionObjects.filter(percept -> percept.getType().equals(ObjectPerceptType.Wall));

        if (walls.getAll().size() <= 2) {
        	grid.updateMyLocation(calculateWallDistance(walls,
                    new Distance(percepts.getVision().getFieldOfView().getRange().getValue() * 0.5),
                    percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder()).getValue());
            return new Move(calculateWallDistance(walls,
                    new Distance(percepts.getVision().getFieldOfView().getRange().getValue() * 0.5),
                    percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder()));
        }

        Line wall;
        try {
            wall = findLine(walls);
        } catch (Exception e) {
            return ifStuck(percepts);
        }
        state = State.totarget;



        Angle angle = calculateAngleToLine(wall);
        Angle rotation = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();


        if (Math.abs(angle.getRadians()) < Bias.bias) {
            state = State.explore;
            return new NoAction();
        }

        double angleToRotate = angle.getRadians() * (1);
        if (Math.abs(angleToRotate) >= rotation.getRadians()){
            double rotate = ((int) Math.signum(angleToRotate) * rotation.getRadians());
            grid.updateMyDirection(rotate);
            return new Rotate(Angle.fromRadians(rotate));
        }
        else {
            return new NoAction();
        }

    }


    private Distance calculateWallDistance(ObjectPercepts objectPercepts, Distance distanceFromWall, Distance maxMove){
        OptionalDouble minDistance = objectPercepts.getAll().stream().mapToDouble(p -> p.getPoint().getDistanceFromOrigin().getValue()).min();
        double feasibleDistance = minDistance.orElse(distanceFromWall.getValue() * 2) - distanceFromWall.getValue();

        if (maxMove.getValue() <= feasibleDistance) {
            return maxMove;
        }
        return new Distance(feasibleDistance);
    }

    private Line findLine(ObjectPercepts objects) throws Exception {
        Point firstPoint;
        Point lastPoint = null;
        List<Point> points = new ArrayList<Point>();
        for (ObjectPercept percept: objects.getAll()){
            points.add(percept.getPoint());
        }
        points.sort(Comparator.comparing(Point::getX));


        for (int i = 0; i < points.size() - 2; i++) {
            firstPoint = points.get(i);
            Line lineCut = new Line(firstPoint, points.get(i+1));

            for (Point point: points.subList(i+2,points.size())){
                if (lineCut.isPointOnExtendedLine(point)) {
                    lastPoint = point;
                }
            }
            if (lastPoint != null) {
                return new  Line(firstPoint, lastPoint);
            }
        }

        throw new Exception("error");
    }



    private Angle calculateAngleToLine(Line l) {
        double x = l.getLe().getX() - l.getLs().getX();
        double y = l.getLe().getY() - l.getLs().getY();
        double theta = Math.atan2(y, x);
        return Angle.fromRadians(theta);
    }



    private IntruderAction ifStuck(IntruderPercepts percepts) {

        double randomRotate = (int) (180 * Math.random());

        if (randomRotate < 90) {
            queueRotation(Angle.fromDegrees(90 + randomRotate), percepts);
        }
        else {
            queueRotation(Angle.fromDegrees(-randomRotate), percepts);
        }

        queueMovement(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder(), percepts);

        state = State.navigate;
        return queueActions();
    }


    private Point wallsInView(ObjectPercepts walls){
        List<ObjectPercept> sortedPoints = walls
                .getAll()
                .stream()
                .filter(p -> p.getPoint().getClockDirection().getDistance(Angle.fromDegrees(0)).getRadians() < 0.2)
                .sorted(Comparator.comparing(p -> p.getPoint().getClockDirection().getDistance(Angle.fromDegrees(0)).getRadians()))
                .collect(Collectors.toList());
        try{
            return sortedPoints.get(0).getPoint();
        } catch (Exception e){
            return null;
        }
    }

    private IntruderAction updateExplorationState(IntruderPercepts percepts) {

        ObjectPercepts visionObjects = percepts.getVision().getObjects();
        ObjectPercepts walls = visionObjects.filter(percept -> percept.getType().equals(ObjectPerceptType.Wall));

        if (walls.getAll().size() == 0) {

            queueMovement(new Distance(percepts.getVision().getFieldOfView().getRange().getValue() * 1), percepts);
            queueRotation(Angle.fromDegrees(90 * ORIENTATION.update * -1), percepts);

            return queueActions();
        }

        Point wallPointStraightAhead = wallsInView(walls);
        if (wallPointStraightAhead == null) {
        	grid.updateMyLocation(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue());
            return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        }

        double distanceToWall = wallPointStraightAhead.getDistanceFromOrigin().getValue();
        double minDistance = percepts.getVision().getFieldOfView().getRange().getValue() * 0.5;
        if (distanceToWall > minDistance) {
        	grid.updateMyLocation(new Distance(Math.min(distanceToWall - minDistance,
                    percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue())).getValue());
            return new Move(new Distance(Math.min(distanceToWall - minDistance,
                    percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue())));
        }
        Angle orientation = wallPointStraightAhead.getClockDirection().getDistance(percepts.getTargetDirection());

        if (orientation.getDegrees() < 90) {
            queueRotation(Angle.fromDegrees(90 * (-1)), percepts);
        }
        else if (orientation.getDegrees() > 270 ){
            queueRotation(Angle.fromDegrees(90 * (1)), percepts);
        }
        else queueRotation(Angle.fromDegrees(90 * ORIENTATION.update), percepts);

        return queueActions();
    }

    private IntruderAction updateToTargetState(IntruderPercepts percepts) {
        Direction direction = percepts.getTargetDirection();
        double movement = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue();

        if (direction.getDegrees() > 1) {
            double maxRotation = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians();

            if (direction.getRadians() > maxRotation) {
            	grid.updateMyDirection(maxRotation);
                return new Rotate(Angle.fromRadians(maxRotation));
            }

            grid.updateMyDirection(direction.getRadians());
            return new Rotate(Angle.fromRadians(direction.getRadians()));
        } else {
        	grid.updateMyLocation(movement);
            return new Move(new Distance(movement));
        }
    }

    private IntruderAction updateToTeleportState(IntruderPercepts percepts) {
        // todo if the agent cannot get to target it should go to the teleport
        return null;
    }

    private void queueMovement(Distance distance, IntruderPercepts percepts) {
        double distanceValue = distance.getValue();
        double maxMove = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue();

        while(distanceValue > 0) {
            actionsQueue.add(new Move(new Distance(Math.min(distanceValue, maxMove))));
            distanceValue = distanceValue - maxMove;
        }
    }


    private void queueRotation(Angle angle, IntruderPercepts percepts) {
        double remainingAngle = angle.getDegrees();
        int sign = (int) Math.signum(remainingAngle);
        double maxRotate = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees();
        double rotateStep;

        while(Math.abs(remainingAngle) > Bias.bias) {

            if (Math.abs(remainingAngle) < maxRotate){
                rotateStep = remainingAngle;
            } else {
                rotateStep = maxRotate * sign;
            }
            actionsQueue.add(new Rotate(Angle.fromDegrees(rotateStep)));
            remainingAngle = (Math.abs(remainingAngle) - Math.abs(rotateStep)) * sign;

        }
    }

    private IntruderAction queueActions(){
        IntruderAction nextAction = actionsQueue.get(0);
        if(nextAction.getClass().toString().equals("class Interop.Action.Rotate")) {
        	Rotate nextActionR = (Rotate) nextAction;
        	grid.updateMyDirection(nextActionR.getAngle().getRadians());
        	//System.out.println(nextActionR.getAngle().getDegrees());
        	//System.out.println(nextAction.getClass().toString());
        }
        if(nextAction.getClass().toString().equals("class Interop.Action.Move")) {
        	Move nextActionM = (Move) nextAction;
        	grid.updateMyLocation(nextActionM.getDistance().getValue());
        	//System.out.println(nextActionR.getDistance().getValue());
        	//System.out.println(nextAction.getClass().toString());
        }
        actionsQueue.remove(0);
        return nextAction;
    }
    
    private void clearQueue() {
    	actionsQueue.clear();
    }

    private enum State {
        navigate, explore, totarget, toteleport
    }

    private enum ORIENTATION {

        LEFT(1), RIGHT(-1);

        private final int update;

        ORIENTATION(final int newU){
            update = newU;
        }
    }

}
