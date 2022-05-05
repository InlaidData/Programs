package edu.nmsu.cs.circles;

public class Circle1 extends Circle
{

	public Circle1(double x, double y, double radius)
	{
		super(x, y, radius);
	}

	public boolean intersects(Circle other)
	{
		double biggerRadius;
		double smallerRadius;
		double d;

		//Grab the bigger and smaller radius. 
		if (radius >= other.radius){ 
			biggerRadius = radius;
			smallerRadius = other.radius;
		}
		else{
			biggerRadius = other.radius;
			smallerRadius = radius;
		}

		d = Math.sqrt(Math.pow(center.x - other.center.x, 2) +
				Math.pow(center.y - other.center.y, 2));

		if (d <= radius + other.radius){ //At this point, one circle is in another, or touching.
			if (d + smallerRadius < biggerRadius)
				return false;
			else
				return true;
		}//end if
		
		//Original Code
		//if (Math.abs(center.x - other.center.x) < radius &&
				//Math.abs(center.y - other.center.y) < radius)
			//return true;
		return false;
	}

}
