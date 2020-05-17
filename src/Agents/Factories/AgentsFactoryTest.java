/**
 * 
 */
package Agents.Factories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class AgentsFactoryTest {

	@Test
	void testCreateIntruders() {
		Assertions.assertEquals(AgentsFactory.createIntruders(1000).size(),1000);
		Assertions.assertEquals(AgentsFactory.createIntruders(0).size(),0);
	}
	
	@Test
	void testCreateGuards() {
		Assertions.assertEquals(AgentsFactory.createGuards(1000).size(),1000);
		Assertions.assertEquals(AgentsFactory.createGuards(0).size(),0);
	}

}
