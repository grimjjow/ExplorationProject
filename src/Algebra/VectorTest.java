package Algebra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class VectorTest {

	private Vector x = new Vector(0,0);
	private Vector y = new Vector(0,Double.MIN_VALUE);
	private Vector z = new Vector(0, Math.sqrt(Double.MAX_VALUE));
	
	private Vector a = new Vector(Double.MIN_VALUE,0);
	private Vector b = new Vector(Double.MIN_VALUE,Double.MIN_VALUE);
	private Vector c = new Vector(Double.MIN_VALUE,Math.sqrt(Double.MAX_VALUE));
	
	private Vector d = new Vector(Math.sqrt(Double.MAX_VALUE),0);
	private Vector e = new Vector(Math.sqrt(Double.MAX_VALUE),Double.MIN_VALUE);
	private Vector f = new Vector(1000,1000);
	
	private Vector h = new Vector(-Double.MIN_VALUE,0);
	private Vector i = new Vector(-Double.MIN_VALUE,Double.MIN_VALUE);
	private Vector j = new Vector(-Double.MIN_VALUE,Math.sqrt(Double.MAX_VALUE));
	
	private Vector k = new Vector(-Math.sqrt(Double.MAX_VALUE),0);
	private Vector l = new Vector(-Math.sqrt(Double.MAX_VALUE),Double.MIN_VALUE);
	private Vector m = new Vector(-1000,1000);
	
	private Vector n = new Vector(-Double.MIN_VALUE,-Double.MIN_VALUE);
	private Vector o = new Vector(-Double.MIN_VALUE,-Math.sqrt(Double.MAX_VALUE));
	private Vector p = new Vector(-1000,-1000);
	
	@Test
	void test() {
		Assertions.assertEquals(x.length(),0,1E-10);
		Assertions.assertEquals(y.length(),Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(z.length(),Math.sqrt(Double.MAX_VALUE),1E-10);
		
		Assertions.assertEquals(a.length(),Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(b.length(),2*Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(c.length(),Math.sqrt(Double.MAX_VALUE)+Double.MIN_VALUE,1E-10);
		
		Assertions.assertEquals(d.length(),Math.sqrt(Double.MAX_VALUE),1E-10);
		Assertions.assertEquals(e.length(),Math.sqrt(Double.MAX_VALUE)+Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(f.length(),1414.2135623730950488016,1E-10);
		
		Assertions.assertEquals(h.length(),Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(i.length(),2*Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(j.length(),Math.sqrt(Double.MAX_VALUE)+Double.MIN_VALUE,1E-10);
		
		Assertions.assertEquals(k.length(),Math.sqrt(Double.MAX_VALUE),1E-10);
		Assertions.assertEquals(l.length(),Math.sqrt(Double.MAX_VALUE)+Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(m.length(),1414.2135623730950488016,1E-10);
		
		Assertions.assertEquals(n.length(),2*Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(o.length(),Math.sqrt(Double.MAX_VALUE)+Double.MIN_VALUE,1E-10);
		Assertions.assertEquals(p.length(),1414.2135623730950488016,1E-10);
	}

}
