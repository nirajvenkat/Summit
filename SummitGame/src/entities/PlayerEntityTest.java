package entities;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlayerEntityTest {
	PlayerEntity p1, p2;

	@Before
	public void setUp() throws Exception {
		p1 = new PlayerEntity(20,20);
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
		assertEquals(5,p1.getWidth(),0.01);
	}

	@Test
	public void testGetHeight() {
		assertEquals(5,p1.getHeight(),0.01);
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
		p2 = new PlayerEntity(22,22);
		assertTrue(p1.intersects(p2));
	}
	
	@Test
	public void testIntersectsTop() {
		p2 = new PlayerEntity(20,0);
		assertTrue(p1.intersects(p2));
	}
	
	@Test
	public void testIntersectsRight() {
		p2 = new PlayerEntity(30,20);
		assertTrue(p1.intersects(p2));
	}
	
	@Test
	public void testIntersectsBottom() {
		p2 = new PlayerEntity(20,40);
		assertTrue(p1.intersects(p2));
	}
	
	@Test
	public void testIntersectsLeft() {
		p2 = new PlayerEntity(10,20);
		assertTrue(p1.intersects(p2));
	}

}
