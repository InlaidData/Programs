package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle1 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle1Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

import org.junit.*;

public class Circle1Test
{
	// Data you need for each test case
	private Circle1 circle1;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle1 = new Circle1(1, 2, 3);
	}

	//
	// Stuff you want to do after each test case
	//
	@After
	public void teardown()
	{
		System.out.println("\nTest finished.");
	}

	@Test
	//Sees the output when two circles do not Touch.
	public void intersectNoIntersection(){

			//One above another, not touching.
			System.out.println("Test intersectNoIntersection: Top of each other.\n");
			Circle1 baseCircle = new Circle1(0, 50, 10);
			Circle1 secondCircle = new Circle1(0, 0, 5);
			Assert.assertFalse(baseCircle.intersects(secondCircle));
			Assert.assertFalse(baseCircle.intersects(baseCircle));

			//Next to another. Not touching.
			System.out.println("Test intersectNoIntersection: Side by side not touching\n");
			baseCircle = new Circle1(0, 0, 10);
			secondCircle = new Circle1(50, 0, 5);
			Assert.assertFalse(baseCircle.intersects(secondCircle));
			Assert.assertFalse(baseCircle.intersects(baseCircle));

			//Next to another. barely missing each other.
			System.out.println("Test intersectNoIntersection: almost touching\n");
			baseCircle = new Circle1(0, 0, 10);
			secondCircle = new Circle1(0.00000000001, 0, 5);
			Assert.assertFalse(baseCircle.intersects(secondCircle));
			Assert.assertFalse(baseCircle.intersects(baseCircle));

			//One above another. barely missing each other.
			System.out.println("Test intersectNoIntersection: Side by side not touching\n");
			baseCircle = new Circle1(0, 10, 2.999999);
			secondCircle = new Circle1(0, 5, 2);
			Assert.assertFalse(baseCircle.intersects(secondCircle));
			Assert.assertFalse(baseCircle.intersects(baseCircle));

			//One above and to the right (diagonal, to make sure we arent testing bounding boxes.)
			System.out.println("Test intersectNoIntersection: Diagonal Not Touching\n");
			baseCircle = new Circle1(0, 0, 1);
			secondCircle = new Circle1(1.421, 1.421, 1);
			Assert.assertFalse(baseCircle.intersects(secondCircle));
			Assert.assertFalse(baseCircle.intersects(baseCircle));
	}

	@Test
	//Sees the output when two circles are the same size and overlap exactly.
	public void intersectCompleteOverlap(){
		System.out.println("Test intersectCompleteOverlap: Same Size\n");
		//Intersect Overlap
		Circle1 baseCircle = new Circle1(0, 0, 5);
		Circle1 secondCircle = new Circle1(0, 0, 5);
		Assert.assertTrue(baseCircle.intersects(secondCircle));
		Assert.assertTrue(baseCircle.intersects(baseCircle));
			
	}//end method

	@Test
	//Sees the output when two circles are the same size and overlap exactly.
	public void intersectInside(){
		//Base circle is smaller than second, they are inside one another.
		System.out.println("Test intersectInside: Base Circle inside second circle.\n");
		Circle1 baseCircle = new Circle1(0, 0, 5);
		Circle1 secondCircle = new Circle1(0, 0, 10);
		Assert.assertFalse(baseCircle.intersects(secondCircle));
		Assert.assertFalse(baseCircle.intersects(baseCircle));

		//Second circle is smaller than base circle, sees if there is an issue.
		System.out.println("Test intersectInside: second Circle inside base circle.\n");
		baseCircle = new Circle1(0, 0, 10);
		secondCircle = new Circle1(0, 0, 5);
		Assert.assertFalse(baseCircle.intersects(secondCircle));
		Assert.assertFalse(baseCircle.intersects(baseCircle));
	}

	//Test a simple positive scale

	@Test
	public void simplePositiveScale(){
		System.out.println("Test simplePositiveScale: scaling a unit circle by 0.8.\n");
		Circle1 baseCircle = new Circle1(0, 0, 1);
		baseCircle.scale(0.8);
		Assert.assertTrue(baseCircle.radius == 0.8);
	}//end method

	//Test a simple negative scale

	@Test
	public void simpleNegativeScale(){
		System.out.println("Test simpleNegativeScale: scaling a unit circle by -0.9.\n");
		Circle1 baseCircle = new Circle1(0, 0, 1);
		baseCircle.scale(-0.9);
		Assert.assertTrue(baseCircle.radius == 0.9);
	}//end method

	//
	// Test a simple positive move
	//
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running test simpleMove.");
		p = circle1.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	}

	//
	// Test a simple negative move
	//
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		System.out.println("Running test simpleMoveNeg.");
		p = circle1.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	}

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
