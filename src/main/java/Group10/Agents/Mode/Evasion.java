package Group10.Agents.Mode;

import Group10.Agents.Container.IntruderContainer;
import Group10.Engine.Game;
import Interop.Action.Sprint;
import Interop.Geometry.Distance;

public class Evasion {

	private IntruderContainer intruder;
	private Game game;
	
	private Distance maxSprint;
	private double maxRotate;
	
	public Evasion(Game game, IntruderContainer intruder) {
		this.setGame(game);
		this.setIntruder(intruder);
		
		maxSprint = game.settings.getIntruderMaxSprintDistance();
		maxRotate = game.settings.getScenarioPercepts().getMaxRotationAngle().getRadians();
	}
	
	//Method that makes the intruder rotate randomly and start sprinting
	public void evade() {
		//Variable that holds random angle (<maxRotate)
		double randomAngle;
		//Method to determine if angle is negative or positive
		double randomNum = Math.random();
		
		//If randomNum<0.5 then angle is negative else positive
		if(randomNum<0.5) {
			randomAngle = -(Math.random()*maxRotate);
		}
		else {
			randomAngle = (Math.random()*maxRotate);
		}
		
		//Rotating intruder
		intruder.rotate(randomAngle);
		//Letting intruder sprint
		game.executeAction(intruder, new Sprint(maxSprint));
	}

	public IntruderContainer getIntruder() {
		return intruder;
	}

	public void setIntruder(IntruderContainer intruder) {
		this.intruder = intruder;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
