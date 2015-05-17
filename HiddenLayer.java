import org.jblas.*;
import java.util.*;
/**
Layers
    
To have:   
    get output vector
    compute output vector
 
*/

class HiddenLayer
{
    private int numberOfInputs;
    private List<Neuron> neurons;
    private FloatMatrix output;

    HiddenLayer(int numberOfNeurons, int numberOfInputs)
    {
        this.numberOfInputs = numberOfInputs;
        neurons = new ArrayList<Neuron>();
        for(int i=1; i<=numberOfNeurons; ++i) {
            neurons.add(new Neuron(numberOfInputs));
        }
    }
    
    FloatMatrix getOutput(FloatMatrix input) throws Exception
    {
        if(input.length!=numberOfInputs) {
            throw new Exception("input size doesnt match layer");
        }
        FloatMatrix inputPlusBiasConstant = addConstantTermToInputVector(input);
        
        FloatMatrix outputVector = new FloatMatrix(numberOfInputs);
        int i=0;
        for(Neuron n: neurons) {
            outputVector.put(i,0,n.getWeightVector().dot(inputPlusBiasConstant));
            ++i;
        }
        return outputVector;
    }
    
    private FloatMatrix addConstantTermToInputVector(FloatMatrix input)
    {
        FloatMatrix inputPlusBiasConstant = new FloatMatrix(numberOfInputs+1);
        inputPlusBiasConstant.put(0,0,-1);
        for(int i=1; i<=numberOfInputs; ++i) {
            inputPlusBiasConstant.put(i,0,input.get(i,0));
        }
        return inputPlusBiasConstant;
    }
    
    
    static void tests()
    {

    }

    public static void main(String[] args)
    {
      HiddenLayer test = new HiddenLayer(3,2);
    }
}
