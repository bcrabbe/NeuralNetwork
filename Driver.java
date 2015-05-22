
import java.util.*;

class Driver
{
    static Random randomNumberGen;
    static int numberOfFails;
    
    Driver()
    {
        this.randomNumberGen = new Random();
        numberOfFails=0;
    }
    
    static void is(Object x, Object y)
    {
        System.out.print("testing: " + x.toString() + " = " + y.toString() );

        if (x==y || (x != null && x.equals(y)) ) {
            System.out.println("...pass");
            return;
        }
        System.out.println("...fail");
    }
    
    static void is(Object x, Object y, String description)
    {
        System.out.println("test description: " + description );

        System.out.print("testing: " + x.toString() + " = " + y.toString() );

        if (x==y || (x != null && x.equals(y)) ) {
            System.out.println("...pass");
            return;
        }
        System.out.println("...fail");
        ++numberOfFails;
    }
    
    static void finishTesting(String suiteName)
    {
        System.out.println("\n\n" + suiteName + " finished with " + numberOfFails + " fails.");
        System.out.println("***************************************************************\n\n");

        numberOfFails=0;
    }
    
    static void tests()
    {
        float randomX = Driver.randomNumberGen.nextFloat()*(float)Math.PI;
        System.out.println("x = " + randomX);
        System.out.println("y = " + (float)Math.sin(randomX));

    }

    public static void main(String[] args)
    {
        Driver program = new Driver();
        Network net = new Network(1,5,4,1);
        //Trainer trainer = new Trainer(Network);
    }
}
