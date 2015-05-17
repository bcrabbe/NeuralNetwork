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
        float randomX = Driver.randomNumberGen.nextFloat()*(float)Math.PI;
        float randomY = (float)Math.sin(randomX);
        /*FloatMatrix(int newRows, int newColumns, float... newData)
          Create a new matrix with newRows rows, newColumns columns using newData> as the data.
        */
        return new FloatMatrix(2,1,randomX,randomY);
    }
    
    public static void main(String[] args)
    {
        
    }
}
