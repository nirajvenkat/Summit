package entities;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlatformEntityTest {
	PlatformEntity p1, p2;

	@Before
	public void setUp() throws Exception {
		p1 = new PlatformEntity(20,20);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetX() {
		assertEquals(20,p1.getX(),0.01);
	}

	@Test
	public void testGetY() {
		assertEquals(20,p1.getY(),0.01);
	}

	@Test
	public void testGetWidth() {
		assertEquals(200,p1.getWidth(),0.01);
	}

	@Test
	public void testGetHeight() {
		assertEquals(10,p1.getHeight(),0.01);
	}

	@Test
	public void testSetX() {
		p1.setX(30);
		assertEquals(30,p1.getX(),0.01);
	}

	@Test
	public void testSetY() {
		p1.setY(30);
		assertEquals(30,p1.getY(),0.01);
	}

	@Test
	public void testSetWidth() {
		p1.setWidth(10);
		assertEquals(10,p1.getWidth(),0.01);
	}

	@Test
	public void testSetHeight() {
		p1.setHeight(10);
		assertEquals(10,p1.getHeight(),0.01);
	}

	@Test
	public void testSetLocation() {
		p1.setLocation(30,30);
		if(p1.getX() == 30){
			assertEquals(30,p1.getY(),0.01);
		}
	}
	
	@Test
	public void testIntersects() {
		p2 = new PlatformEntity(22,22);
		assertTrue(p1.intersects(p2));
	}
	
	@Test
	public void testIntersectsTop() {
		p2 = new PlatformEntity(20,10);
		assertFalse(p1.intersects(p2));
	}
	
	@Test
	public void testIntersectsRight() {
		p2 = new PlatformEntity(220,20);
		assertFalse(p1.intersects(p2));
	}
	
	@Test
	public void testIntersectsBottom() {
		p2 = new PlatformEntity(20,30);
		assertFalse(p1.intersects(p2));
	}
	
	@Test
	public void testIntersectsLeft() {
		p2 = new PlatformEntity(-180,20);
		assertFalse(p1.intersects(p2));
	}

}
