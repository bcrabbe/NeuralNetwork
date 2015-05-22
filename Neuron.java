import org.jblas.*;
import java.util.*;

/** Each neuron, i, in a layer, l, with l-1 having n neurons has a
    connection vector 
        W_i^l = [ b, W_1i, W_2i, ..., W_ni]
    where W_ni is the connection to unit n in l-1
    and b is a bias term.
*/
class Neuron
{
    private int numberOfConnections;
    private FloatMatrix weights;//numberOfConnections+1 for bias
    
    
    Neuron(int numberOfInputs)
    {
        //there are n+1 connections. to n units in prev layer and a bias
        numberOfConnections=numberOfInputs+1;
         //weights = new FloatMatrix
         //        weights.rand(numberOfConnections);
        weights = FloatMatrix.rand(numberOfConnections);
    }
    
    
    
    FloatMatrix getWeightVector()
    {
        return weights.dup();
    }
    
    void tests()
    {
        System.out.println(weights.toString());

    }

    public static void main(String[] args)
    {
        Neuron n = new Neuron(3);
        n.tests();
    }
}
