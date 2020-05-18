package Group10.Algebra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class MathsTest {

	@Test
	void testGetDistanceBetweenAngles() {
		Assertions.assertEquals(Maths.getDistanceBetweenAngles(0, 0), 0, 0);
		Assertions.assertEquals(Maths.getDistanceBetweenAngles(Double.MAX_VALUE, Double.MAX_VALUE), 0, 0);
	}

	@Test
	void testClamp() {
		Assertions.assertEquals(Maths.clamp(0,0,0),0,0);
		
		Assertions.assertEquals(Maths.clamp(0,0,Double.MAX_VALUE),0,0);
		Assertions.assertEquals(Maths.clamp(0,Double.MIN_VALUE,Double.MAX_VALUE),Double.MIN_VALUE,0);
		Assertions.assertEquals(Maths.clamp(0,Double.MAX_VALUE,Double.MAX_VALUE),Double.MAX_VALUE,0);
		
		Assertions.assertEquals(Maths.clamp(Double.MIN_VALUE,0,Double.MAX_VALUE),Double.MIN_VALUE,0);
		Assertions.assertEquals(Maths.clamp(Double.MIN_VALUE,Double.MIN_VALUE,Double.MAX_VALUE),Double.MIN_VALUE,0);
		Assertions.assertEquals(Maths.clamp(Double.MIN_VALUE,Double.MAX_VALUE,Double.MAX_VALUE),Double.MAX_VALUE,0);
		
		Assertions.assertEquals(Maths.clamp(Double.MAX_VALUE,0,Double.MAX_VALUE),Double.MAX_VALUE,0);
		Assertions.assertEquals(Maths.clamp(Double.MAX_VALUE,Double.MIN_VALUE,Double.MAX_VALUE),Double.MAX_VALUE,0);
		Assertions.assertEquals(Maths.clamp(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE),Double.MAX_VALUE,0);
		
		Assertions.assertEquals(Maths.clamp(-Double.MAX_VALUE,-Double.MAX_VALUE,0),-Double.MAX_VALUE,0);
		Assertions.assertEquals(Maths.clamp(-Double.MAX_VALUE,-Double.MIN_VALUE,0),-Double.MIN_VALUE,0);
		Assertions.assertEquals(Maths.clamp(-Double.MAX_VALUE,0,0),0,0);
		
		Assertions.assertEquals(Maths.clamp(-Double.MIN_VALUE,-Double.MAX_VALUE,0),-Double.MIN_VALUE,0);
		Assertions.assertEquals(Maths.clamp(-Double.MIN_VALUE,-Double.MIN_VALUE,0),-Double.MIN_VALUE,0);
		Assertions.assertEquals(Maths.clamp(-Double.MIN_VALUE,0,0),0,0);
		
		Assertions.assertEquals(Maths.clamp(0,-Double.MAX_VALUE,0),0,0);
		Assertions.assertEquals(Maths.clamp(0,-Double.MIN_VALUE,0),0,0);
		
		Assertions.assertEquals(Maths.clamp(0,-Double.MAX_VALUE,-Double.MAX_VALUE),-Double.MAX_VALUE,0);
		Assertions.assertEquals(Maths.clamp(0,-Double.MAX_VALUE,-Double.MIN_VALUE),-Double.MIN_VALUE,0);
		Assertions.assertEquals(Maths.clamp(0,-Double.MIN_VALUE,-Double.MIN_VALUE),-Double.MIN_VALUE,0);
	}

	// DO NOT EXISTS ANYMORE
//	@Test
//	void testGeq() {
//		Assertions.assertTrue(Maths.geq(0, 0));
//
//		Assertions.assertTrue(Maths.geq(Double.MIN_VALUE, 0));
//		Assertions.assertTrue(Maths.geq(-Double.MIN_VALUE, 0)); //(allows 1E-10 delta.)
//		Assertions.assertTrue(Maths.geq(Double.MAX_VALUE, 0));
//		Assertions.assertFalse(Maths.geq(-Double.MAX_VALUE, 0));
//
//		Assertions.assertTrue(Maths.geq(0, Double.MIN_VALUE)); //(allows 1E-10 delta.)
//		Assertions.assertTrue(Maths.geq(0, -Double.MIN_VALUE));
//		Assertions.assertFalse(Maths.geq(0, Double.MAX_VALUE));
//		Assertions.assertTrue(Maths.geq(0, -Double.MIN_VALUE));
//
//		Assertions.assertTrue(Maths.geq(Double.MIN_VALUE, Double.MIN_VALUE));
//		Assertions.assertFalse(Maths.geq(Double.MIN_VALUE, Double.MAX_VALUE));
//		Assertions.assertTrue(Maths.geq(Double.MAX_VALUE, Double.MIN_VALUE));
//		Assertions.assertTrue(Maths.geq(Double.MAX_VALUE, Double.MAX_VALUE));
//
//		Assertions.assertTrue(Maths.geq(-Double.MIN_VALUE, Double.MIN_VALUE)); //(allows 1E-10 delta.)
//		Assertions.assertTrue(Maths.geq(Double.MIN_VALUE, -Double.MIN_VALUE));
//		Assertions.assertFalse(Maths.geq(-Double.MAX_VALUE, Double.MAX_VALUE));
//		Assertions.assertTrue(Maths.geq(Double.MAX_VALUE, -Double.MAX_VALUE));
//
//		Assertions.assertTrue(Maths.geq(-Double.MIN_VALUE, -Double.MIN_VALUE));
//		Assertions.assertTrue(Maths.geq(-Double.MIN_VALUE, -Double.MAX_VALUE));
//		Assertions.assertFalse(Maths.geq(-Double.MAX_VALUE, -Double.MIN_VALUE));
//		Assertions.assertTrue(Maths.geq(-Double.MAX_VALUE, -Double.MAX_VALUE));
//	}
//
//	@Test
//	void testLeq() {
//		Assertions.assertTrue(Maths.leq(0, 0));
//
//		Assertions.assertTrue(Maths.leq(Double.MIN_VALUE, 0)); //(allows 1E-10 delta.)
//		Assertions.assertTrue(Maths.leq(-Double.MIN_VALUE, 0));
//		Assertions.assertFalse(Maths.leq(Double.MAX_VALUE, 0));
//		Assertions.assertTrue(Maths.leq(-Double.MAX_VALUE, 0));
//
//		Assertions.assertTrue(Maths.leq(0, Double.MIN_VALUE));
//		Assertions.assertTrue(Maths.leq(0, -Double.MIN_VALUE)); //(allows 1E-10 delta.)
//		Assertions.assertTrue(Maths.leq(0, Double.MAX_VALUE));
//		Assertions.assertTrue(Maths.leq(0, -Double.MIN_VALUE)); //(allows 1E-10 delta.)
//
//		Assertions.assertTrue(Maths.leq(Double.MIN_VALUE, Double.MIN_VALUE));
//		Assertions.assertTrue(Maths.leq(Double.MIN_VALUE, Double.MAX_VALUE));
//		Assertions.assertFalse(Maths.leq(Double.MAX_VALUE, Double.MIN_VALUE));
//		Assertions.assertTrue(Maths.leq(Double.MAX_VALUE, Double.MAX_VALUE));
//
//		Assertions.assertTrue(Maths.leq(-Double.MIN_VALUE, Double.MIN_VALUE));
//		Assertions.assertTrue(Maths.leq(Double.MIN_VALUE, -Double.MIN_VALUE)); //(allows 1E-10 delta.)
//		Assertions.assertTrue(Maths.leq(-Double.MAX_VALUE, Double.MAX_VALUE));
//		Assertions.assertFalse(Maths.leq(Double.MAX_VALUE, -Double.MAX_VALUE));
//
//		Assertions.assertTrue(Maths.leq(-Double.MIN_VALUE, -Double.MIN_VALUE));
//		Assertions.assertFalse(Maths.leq(-Double.MIN_VALUE, -Double.MAX_VALUE));
//		Assertions.assertTrue(Maths.leq(-Double.MAX_VALUE, -Double.MIN_VALUE));
//		Assertions.assertTrue(Maths.leq(-Double.MAX_VALUE, -Double.MAX_VALUE));
//	}
}
