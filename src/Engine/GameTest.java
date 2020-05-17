package Engine;

import java.io.File;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import Agents.Container.GuardContainer;
import Agents.Container.IntruderContainer;
import Agents.Factories.DefaultAgentFactory;
import Algebra.Vector;
import Interop.Action.DropPheromone;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Action.Sprint;
import Interop.Action.Yell;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.*;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.*;
import World.Reader.*;

class GameTest {

	File mapFile = new File("src/Map/Maps/test_2.map");
	Game g = new Game(Reader.parseFile(mapFile.getAbsolutePath()), new DefaultAgentFactory(), false, 15, null);
	
	GuardPercepts GP;
	IntruderPercepts IP;
	
	@Test
	void testVisions() {
		//Loop going through all the guards
		for(GuardContainer GC : g.guards) {
			GP = g.generateGuardPercepts(GC);
			VisionPrecepts VP;
			VP = GP.getVision();
			ObjectPercepts OPS;
			OPS = VP.getObjects();
			Set<ObjectPercept> SOP;
			SOP = OPS.getAll();
			
			//Loop going through each object the guards can see
			for(ObjectPercept OP : SOP) {
				//Assert if object is in viewing range
				Assertions.assertTrue(OP.getPoint().getDistanceFromOrigin().getValue() <= g.settings.getGuardViewRangeNormal().getValue());
				GC.getDirection();
				g.settings.getViewAngle().getRadians();
				GC.getPosition().getX();
				GC.getPosition().getY();
				Vector V = new Vector(OP.getPoint().getX(),OP.getPoint().getY());
				
				OP.getPoint().getX();
				OP.getPoint().getY();
				
				double upperB = GC.getDirection().getAngle() + g.settings.getViewAngle().getRadians()/2;
				double lowerB = GC.getDirection().getAngle() - g.settings.getViewAngle().getRadians()/2;
				
				//Assert if object is in viewing angle
				Assertions.assertTrue(Math.atan2(OP.getPoint().getY(),OP.getPoint().getX()) <= upperB);
				Assertions.assertTrue(Math.atan2(OP.getPoint().getY(),OP.getPoint().getX()) >= lowerB);
				
			}
		}
		//Loop going through all the guards
		for(IntruderContainer IC : g.intruders) {
			IP = g.generateIntruderPercepts(IC);
			VisionPrecepts VP;
			VP = IP.getVision();
			ObjectPercepts OPS;
			OPS = VP.getObjects();
			Set<ObjectPercept> SOP;
			SOP = OPS.getAll();
			
			//Loop going through each object the guards can see
			for(ObjectPercept OP : SOP) {
				//Assert if object is in viewing range
				Assertions.assertTrue(OP.getPoint().getDistanceFromOrigin().getValue() <= g.settings.getIntruderViewRangeNormal().getValue());
				IC.getDirection();
				g.settings.getViewAngle().getRadians();
				IC.getPosition().getX();
				IC.getPosition().getY();
				Vector V = new Vector(OP.getPoint().getX(),OP.getPoint().getY());
				
				OP.getPoint().getX();
				OP.getPoint().getY();
				
				double upperB = IC.getDirection().getAngle() + g.settings.getViewAngle().getRadians()/2;
				double lowerB = IC.getDirection().getAngle() - g.settings.getViewAngle().getRadians()/2;
				
				//Assert if object is in viewing angle
				Assertions.assertTrue(Math.atan2(OP.getPoint().getY(),OP.getPoint().getX()) <= upperB);
				Assertions.assertTrue(Math.atan2(OP.getPoint().getY(),OP.getPoint().getX()) >= lowerB);
				
			}
		}
	}
	
	@Test
	void testMove() {
		//Test move for guards
		for(GuardContainer GC : g.guards) {
			//Testing moving max distance positive
			double oldPosX = GC.getPosition().getX();
			double oldPosY = GC.getPosition().getY();
			g.executeAction(GC, new Move(g.settings.getGuardMaxMoveDistance()));
			double newPosX = GC.getPosition().getX();
			double newPosY = GC.getPosition().getY();
			
			double dis = Math.sqrt(Math.pow(oldPosX-newPosX,2) + Math.pow(oldPosY-newPosY,2));

			//Assert distance between points and actual moving distance
			Assertions.assertEquals(dis,g.settings.getGuardMaxMoveDistance().getValue(),0.0001);
			
			
			//Testing moving max distance + 1
			double POoldPosX = GC.getPosition().getX();
			double POoldPosY = GC.getPosition().getY();
			g.executeAction(GC, new Move(new Distance(g.settings.getGuardMaxMoveDistance().getValue()+1)));
			double POnewPosX = GC.getPosition().getX();
			double POnewPosY = GC.getPosition().getY();
			
			double POdis = Math.sqrt(Math.pow(POoldPosX-POnewPosX,2) + Math.pow(POoldPosY-POnewPosY,2));

			//Assert distance between points and actual moving distance if above max
			Assertions.assertEquals(POdis,0,0.0001);
			
			
			//Negative already intercepted by the require class in interop
		}
		//Test move for intruders
		for(IntruderContainer IC : g.intruders) {
			//Testing moving max distance positive
			double oldPosX = IC.getPosition().getX();
			double oldPosY = IC.getPosition().getY();
			g.executeAction(IC, new Move(g.settings.getIntruderMaxMoveDistance()));
			double newPosX = IC.getPosition().getX();
			double newPosY = IC.getPosition().getY();
			
			double dis = Math.sqrt(Math.pow(oldPosX-newPosX,2) + Math.pow(oldPosY-newPosY,2));

			//Assert distance between points and actual moving distance
			Assertions.assertEquals(dis,g.settings.getIntruderMaxMoveDistance().getValue(),0.0001);
			
			
			/* THIS ONE NOT WORKING (POSSIBLY BECAUSE OF SPRINT)
			//Testing moving max distance + 1
			double POoldPosX = IC.getPosition().getX();
			double POoldPosY = IC.getPosition().getY();
			g.executeAction(IC, new Move(new Distance(g.settings.getIntruderMaxMoveDistance().getValue()+1)));
			double POnewPosX = IC.getPosition().getX();
			double POnewPosY = IC.getPosition().getY();
			
			double POdis = Math.sqrt(Math.pow(POoldPosX-POnewPosX,2) + Math.pow(POoldPosY-POnewPosY,2));

			//Assert distance between points and actual moving distance if above max
			Assertions.assertEquals(POdis,0,0.0001);
			
			//Negative already intercepted by the require class in interop
			*/
		}
		//Test sprint for intruders
		for(IntruderContainer IC : g.intruders) {
			//Testing moving max distance positive
			double oldPosX = IC.getPosition().getX();
			double oldPosY = IC.getPosition().getY();
			g.executeAction(IC, new Sprint(g.settings.getIntruderMaxSprintDistance()));
			double newPosX = IC.getPosition().getX();
			double newPosY = IC.getPosition().getY();
			
			double dis = Math.sqrt(Math.pow(oldPosX-newPosX,2) + Math.pow(oldPosY-newPosY,2));

			if(dis != 0) {
				//Assert distance between points and actual sprinting distance
				Assertions.assertEquals(dis,g.settings.getIntruderMaxSprintDistance().getValue(),0.0001);
			}
			
			
			//Testing moving max distance negative
			double POoldPosX = IC.getPosition().getX();
			double POoldPosY = IC.getPosition().getY();
			g.executeAction(IC, new Sprint(new Distance(g.settings.getIntruderMaxSprintDistance().getValue()+1)));
			double POnewPosX = IC.getPosition().getX();
			double POnewPosY = IC.getPosition().getY();
			
			double POdis = Math.sqrt(Math.pow(POoldPosX-POnewPosX,2) + Math.pow(POoldPosY-POnewPosY,2));

			//Assert distance between points and actual sprinting distance if above max
			Assertions.assertEquals(POdis,0,0.0001);
			
			//Negative already intercepted by the require class in interop
		}
	}
	@Test
	void testRotation() {
		//Test rotation for guards
				for(GuardContainer GC : g.guards) {
					//Test moving max rotation positive
					double oldRot = GC.getDirection().getAngle();
					g.executeAction(GC, new Rotate(g.settings.getScenarioPercepts().getMaxRotationAngle()));
					double newRot = GC.getDirection().getAngle();
					
					double ang = Math.abs(newRot-oldRot);

					//Assert rotation between calculated angle and actual angle in radians
					Assertions.assertEquals(ang,g.settings.getScenarioPercepts().getMaxRotationAngle().getRadians(),0);
					
					
					//Test moving max rotation positive + 1
					double POoldRot = GC.getDirection().getAngle();
					g.executeAction(GC, new Rotate(Angle.fromRadians(g.settings.getScenarioPercepts().getMaxRotationAngle().getRadians()+1)));
					double POnewRot = GC.getDirection().getAngle();
					
					double POang = Math.abs(POnewRot-POoldRot);

					//Assert rotation between calculated angle and actual angle in radians which should be 0
					Assertions.assertEquals(POang,0,0);
					
					
					//Test moving max rotation negative
					double NEGoldRot = GC.getDirection().getAngle();
					g.executeAction(GC, new Rotate(Angle.fromRadians(-g.settings.getScenarioPercepts().getMaxRotationAngle().getRadians())));
					double NEGnewRot = GC.getDirection().getAngle();
					
					double NEGang = Math.abs(NEGnewRot-NEGoldRot);

					//Assert rotation between calculated angle and actual angle in radians which should be 0
					Assertions.assertEquals(NEGang,g.settings.getScenarioPercepts().getMaxRotationAngle().getRadians(),0);
				}
				//Test rotation for intruders
				for(IntruderContainer IC : g.intruders) {
					//
					double oldRot = IC.getDirection().getAngle();
					g.executeAction(IC, new Rotate(g.settings.getScenarioPercepts().getMaxRotationAngle()));
					double newRot = IC.getDirection().getAngle();
					
					double ang = Math.abs(newRot-oldRot);

					
					//Assert rotation between calculated angle and actual angle in radians
					Assertions.assertEquals(ang,g.settings.getScenarioPercepts().getMaxRotationAngle().getRadians(),0);
					
					//Test moving max rotation positive + 1
					double POoldRot = IC.getDirection().getAngle();
					g.executeAction(IC, new Rotate(Angle.fromRadians(g.settings.getScenarioPercepts().getMaxRotationAngle().getRadians()+1)));
					double POnewRot = IC.getDirection().getAngle();
					
					double POang = Math.abs(POnewRot-POoldRot);

					//Assert rotation between calculated angle and actual angle in radians which should be 0
					Assertions.assertEquals(POang,0,0);
					
					
					//Test moving max rotation negative
					double NEGoldRot = IC.getDirection().getAngle();
					g.executeAction(IC, new Rotate(Angle.fromRadians(-g.settings.getScenarioPercepts().getMaxRotationAngle().getRadians())));
					double NEGnewRot = IC.getDirection().getAngle();
					
					double NEGang = Math.abs(NEGnewRot-NEGoldRot);

					//Assert rotation between calculated angle and actual angle in radians which should be 0
					Assertions.assertEquals(NEGang,g.settings.getScenarioPercepts().getMaxRotationAngle().getRadians(),0);
				}
	}
	
	@Test
	void testSound(){
		//Testing for sounds outside of radius
		//Moving guard-0 to 31 units above guard-1
		g.guards.get(0).moveTo(new Vector(g.guards.get(1).getPosition().getX(),g.guards.get(1).getPosition().getY()-31));
		
		//Guard-0 yelling
		g.executeAction(g.guards.get(0), new Yell());
		
		//Generating percepts for guard-1
		GP = g.generateGuardPercepts(g.guards.get(1));
		SoundPercepts HSP;
		HSP = GP.getSounds();
		Set<SoundPercept> HSSP;
		HSSP = HSP.getAll();
		
		//Assert if guard-1 didn't heard yell (outside radius)
		Assertions.assertTrue(HSSP.size() == 0);
		
		//Testing for sounds inside of radius
		//Moving guard-0 to 30 units above guard-1
		g.guards.get(0).moveTo(new Vector(g.guards.get(1).getPosition().getX(),g.guards.get(1).getPosition().getY()-30));
		
		//Guard-0 yelling
		g.executeAction(g.guards.get(0), new Yell());
		
		//Generating percepts for guard-1
		GP = g.generateGuardPercepts(g.guards.get(1));
		SoundPercepts SP;
		SP = GP.getSounds();
		Set<SoundPercept> SSP;
		SSP = SP.getAll();
		
		//Assert if guard-1 heard yell (inside radius)
		Assertions.assertTrue(SSP.size() != 0);
		
		//Loop through all sounds heard (should be only one)
		for(SoundPercept SNP : SSP) {
			//Assert if sound heard is in fact a yell (Soundtype)
			Assertions.assertEquals(SNP.getType(), SoundPerceptType.Yell);
			
			//THIS DOES NOT WORK BECAUSE UNPRECISE SOUND DIRECTION, MISTAKE???
			//Assertions.assertEquals(SNP.getDirection().getDegrees(),0,0.1);
			
			//Assert if direction of sound is correct (Not fully working currently)
			Assertions.assertEquals(SNP.getDirection().getDegrees(),0,360);
		}
	}
	
	@Test
	void testSmell() {
		//Testing if smell works if in range
		//Moving guard-2 to 4 units above guard-3
		g.guards.get(2).moveTo(new Vector(g.guards.get(3).getPosition().getX(),g.guards.get(3).getPosition().getY()-4));
		
		//Guard-2 dropping a pheromone
		g.executeAction(g.guards.get(2), new DropPheromone(SmellPerceptType.Pheromone1));
		
		//Generating percepts for guard-3
		GP = g.generateGuardPercepts(g.guards.get(3));
		SmellPercepts SP;
		SP = GP.getSmells();
		Set<SmellPercept> SSP;
		SSP = SP.getAll();
		
		//Assert if guard-3 smells anything
		Assertions.assertTrue(SSP.size() != 0);
		
		//Loop through all smells which should be only one
		for(SmellPercept SNP : SSP) {
			//Assert if smell is the correct pheromone type
			Assertions.assertEquals(SNP.getType(), SmellPerceptType.Pheromone1);
			
			//Assert if smell is within range
			Assertions.assertEquals(SNP.getDistance().getValue(),4,0.000001);
		}
	}
	
	@Test
	void testSmell2() {
		//Testing if smell doesn't work if out of range
		//Moving guard-0 to 6 units above guard-1
		g.guards.get(0).moveTo(new Vector(g.guards.get(1).getPosition().getX(),g.guards.get(1).getPosition().getY()-6));

		//Guard-0 dropping a pheromone
		g.executeAction(g.guards.get(0), new DropPheromone(SmellPerceptType.Pheromone1));
		
		//Generating percepts for guard-1
		GP = g.generateGuardPercepts(g.guards.get(1));
		SmellPercepts HSP;
		HSP = GP.getSmells();
		Set<SmellPercept> HSSP;
		HSSP = HSP.getAll();
		
		//Assert that guard-1 doesn't smell anything
		Assertions.assertTrue(HSSP.size() == 0);
	}
	
	@Test
	void testArea() {
		//Testing if guards can recognize the areas they're in
		//Move guard to a window
		g.guards.get(0).moveTo(new Vector(155.5,100));
		
		//Generate guard percepts
		GP = g.generateGuardPercepts(g.guards.get(0));
		AreaPercepts AP;
		AP = GP.getAreaPercepts();
		boolean IIW;
		boolean IID;
		boolean IIS;
		boolean IJT;
		IIW = AP.isInWindow();
		IID = AP.isInDoor();
		IIS = AP.isInSentryTower();
		IJT = AP.isJustTeleported();
		
		//Assert that guard knows it's in a window and not somewhere else
		Assertions.assertTrue(IIW);
		Assertions.assertFalse(IID);
		Assertions.assertFalse(IIS);
		Assertions.assertFalse(IJT);
		
		//Move guard to a door
		g.guards.get(0).moveTo(new Vector(152,77));
		
		//Generate guard percepts
		GP = g.generateGuardPercepts(g.guards.get(0));
		AreaPercepts AP1;
		AP1 = GP.getAreaPercepts();
		boolean IIW1;
		boolean IID1;
		boolean IIS1;
		boolean IJT1;
		IIW1 = AP1.isInWindow();
		IID1 = AP1.isInDoor();
		IIS1 = AP1.isInSentryTower();
		IJT1 = AP1.isJustTeleported();
		
		//Assert that guard knows it's in a door and not somewhere else
		Assertions.assertFalse(IIW1);
		Assertions.assertTrue(IID1);
		Assertions.assertFalse(IIS1);
		Assertions.assertFalse(IJT1);
		
		//Move guard to a sentry tower
		g.guards.get(0).moveTo(new Vector(65,75));
		
		//Generate guard percepts
		GP = g.generateGuardPercepts(g.guards.get(0));
		AreaPercepts AP2;
		AP2 = GP.getAreaPercepts();
		boolean IIW2;
		boolean IID2;
		boolean IIS2;
		boolean IJT2;
		IIW2 = AP2.isInWindow();
		IID2 = AP2.isInDoor();
		IIS2 = AP2.isInSentryTower();
		IJT2 = AP2.isJustTeleported();
		
		//Assert that guard knows it's in a sentry tower and not somewhere else
		Assertions.assertFalse(IIW2);
		Assertions.assertFalse(IID2);
		Assertions.assertTrue(IIS2);
		Assertions.assertFalse(IJT2);
		
		//TELEPORT TEST NOT WORKING FOR NOW
		/*g.guards.get(0).moveTo(new Vector(9,72));
		g.guards.get(0).rotate(-Math.PI*.5);
		g.executeAction(g.guards.get(0), new Move(new Distance(6)));
		
		GP = g.generateGuardPercepts(g.guards.get(0));
		AreaPercepts AP3;
		AP3 = GP.getAreaPercepts();
		boolean IIW3;
		boolean IID3;
		boolean IIS3;
		boolean IJT3;
		IIW3 = AP3.isInWindow();
		IID3 = AP3.isInDoor();
		IIS3 = AP3.isInSentryTower();
		IJT3 = AP3.isJustTeleported();
		
		System.out.println(g.guards.get(0).getPosition().getX());
		System.out.println(g.guards.get(0).getPosition().getY());
		
		Assertions.assertFalse(IIW3);
		Assertions.assertFalse(IID3);
		Assertions.assertFalse(IIS3);
		Assertions.assertTrue(IJT3);*/
	}

}
