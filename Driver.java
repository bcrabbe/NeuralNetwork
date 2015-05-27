import org.jblas.*;
import java.util.*;
/**
Driver

Initialisation class
 
*/
class Driver
{
    static Random randomNumberGen;
    static int numberOfFails;
    
    Driver()
    {
        Driver.randomNumberGen = new Random();
        numberOfFails=0;
    }
    //add each matrix in mList2 element wise to each in mList1 result is stored in mList1
    static List<FloatMatrix> addFloatMatrixListsi(List<FloatMatrix> mList1, List<FloatMatrix> mList2)
    {
        for(int matrix=0; matrix<mList1.size(); ++matrix) {
            mList1.get(matrix).addi(mList2.get(matrix));
        }
        return mList1;
    }
    
    // multiples each element of each matrix in mList by scalar, result is stored in mList
    static List<FloatMatrix> scalarMultiplyFloatMatrixListsi(List<FloatMatrix> mList, float scalar)
    {
        for(int matrix=0; matrix<mList.size(); ++matrix) {
            mList.get(matrix).muli(scalar);
        }
        return mList;
    }
    
    //initialises the list matricies of weight updates to the correct size and all elements 0
    //from the first one that is returned from the networks backpropagate
    static List<FloatMatrix> copySizingInMatrixList(List<FloatMatrix> mList)
    {
        List<FloatMatrix> mListCopy = new ArrayList<FloatMatrix>();
        
        for(FloatMatrix matrix: mList) {
            int rows = matrix.rows;
            int cols = matrix.columns;
            mListCopy.add(FloatMatrix.zeros(rows, cols));
        }
        return mListCopy;
    }
    
    static void printMatrixDetails(String name, FloatMatrix m)
    {
        System.out.println("FloatMatrix " + name + " has " + m.rows + " rows and " + m.columns + " columns. Contains: ");
        System.out.println(m.toString()+ "\n");
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
        Network net = new Network(1,5,10,15,10,1);
        Trainer trainer = new Trainer(net, 1);
        trainer.trainNetwork();
    }
}
