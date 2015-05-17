
import java.util.*;

class Driver
{
    static Random randomNumberGen;
    
    Driver()
    {
        randomNumberGen = new Random();
    }
    
    static void is(Object x, Object y)
    {
        System.out.print("testing " + x.toString() + " = " + y.toString() );

        if (x==y || (x != null && x.equals(y)) ) {
            System.out.println("...pass");
            return;
        }
        System.out.println("...fail");
    }
    
    static void tests()
    {
        //sin regression
        Network net =  new Network(2,5,2);
    }

    public static void main(String[] args)
    {
        Driver program = new Driver();
        program.tests();
    }
}
