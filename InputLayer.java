import org.jblas.*;
import java.util.*;


class InputLayer
{
    private int width;

    InputLayer(int width)
    {
        this.width = width;
    }
    
    FloatMatrix getInput()
    {
        return generateRandomSinSample();
    }
    
    private FloatMatrix generateRandomSinSample()
    {
        float randomX = Driver.randomNumberGen.nextFloat()*(float)Math.PI-((float)Math.PI/2);
        //float randomY = (float)Math.sin(randomX);
        /*FloatMatrix(int newRows, int newColumns, float... newData)
          Create a new matrix with newRows rows, newColumns columns using newData> as the data.
        */
        return new FloatMatrix(1,1,randomX);
    }
    
    int getWidth()
    {
        return width;
    }
    
    private void tests()
    {
        float randomX = Driver.randomNumberGen.nextFloat()*(float)Math.PI-((float)Math.PI/2);
        System.out.println("x = " + randomX);
        System.out.println("y = " + (float)Math.sin(randomX));

    }
    
    public static void main(String[] args)
    {
        InputLayer il = new InputLayer(3);
        il.tests();
        
    }
}
