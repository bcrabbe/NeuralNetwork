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
        weights = FloatMatrix.rand(numberOfConnections);
    }
    
    //returns the bias term in the weightVector
    float getBiasWeight()
    {
        return weights.get(0,0);
    }
    
    //returns a row vector of the weights not including the first which is the bias
    FloatMatrix getWeightVectorNoBias()
    {
        FloatMatrix weightsNoBias = new FloatMatrix(1, numberOfConnections-1);
        for(int i=1; i<numberOfConnections; ++i) {
            weightsNoBias.put(0, i-1, weights.get(i,0));
        }
        return weightsNoBias;
    }
    
    FloatMatrix getWeightVector()
    {
        return weights.dup();
    }
    
    //sets the column vector of connection weights
    //to the values in the row vector newWeights
    // **** NOT INCLUDING THE BIAS ****
    void setWeights(FloatMatrix newWeights)
    {
         for(int i=1; i<numberOfConnections; ++i) {
            weights.put(i, 0, newWeights.get(0, i-1));
        }
    }
    
    void setBias(float value)
    {
        weights.put(0,0, value);
    }
    
    void testGetWeightVectorNoBias()
    {
        FloatMatrix weightsNoBias = getWeightVectorNoBias();
        System.out.println("weights: " + weights.toString());
        System.out.println("weightsNoBias: " + weightsNoBias.toString());
        for(int i=1; i<numberOfConnections; ++i) {
            Driver.is(weights.get(i,0), weightsNoBias.get(0, i-1),
            "testing getWeightVectorNoBias, checking elements are equal to the correct weight element");
        }

    }
    
    void tests()
    {
        testGetWeightVectorNoBias();
    }

    public static void main(String[] args)
    {
        Neuron n = new Neuron(3);
        n.tests();
    }
}
