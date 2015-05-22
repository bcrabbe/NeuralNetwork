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
    private int layerNumber;

    HiddenLayer(int layerNumber, int numberOfNeurons, int numberOfInputs)
    {
        this.layerNumber = layerNumber;
        this.numberOfInputs = numberOfInputs;
        neurons = new ArrayList<Neuron>();
        for(int i=1; i<=numberOfNeurons; ++i) {
            neurons.add(new Neuron(numberOfInputs));
        }
    }
    
    FloatMatrix getOutput(FloatMatrix input)
    {
        if(input.length!=numberOfInputs) {
            System.out.println("ERROR: input vector to layer" + layerNumber +
             " is not correct size. expected: " +
            numberOfInputs + " got: " + input.length);
            throw new Error();
        }
        FloatMatrix inputPlusBiasConstant = addConstantTermToInputVector(input);
        
        FloatMatrix outputVector = new FloatMatrix(neurons.size());
        int i=0;
        for(Neuron n: neurons) {
            outputVector.put(i, 0,
            activationFunction(n.getWeightVector().dot(inputPlusBiasConstant)));
            ++i;
        }
        return outputVector;
    }
    
    //rectified linear unit (ReLU) non linear activation applied on each neuron
    private float activationFunction(float z)
    {
        if(z>0) return z;
        else return 0;
    }
    
    private FloatMatrix addConstantTermToInputVector(FloatMatrix input)
    {
        FloatMatrix inputPlusBiasConstant = new FloatMatrix(numberOfInputs+1);
        inputPlusBiasConstant.put(0,0,-1);
        for(int i=1; i<=numberOfInputs; ++i) {
            inputPlusBiasConstant.put(i,0,input.get(i-1,0));
        }
        return inputPlusBiasConstant;
    }
    
    int getWidth()
    {
        return neurons.size();
    }
    
    int getNumberOfInputs()
    {
        return numberOfInputs;
    }
    
    int getLayerNumber()
    {
        return layerNumber;
    }
    
    static void tests()
    {

    }

    public static void main(String[] args)
    {
      HiddenLayer test = new HiddenLayer(1,3,2);
    }
}
