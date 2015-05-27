import org.jblas.*;
import java.util.*;
/**
Layers
    
 
*/

class HiddenLayer
{
    private int numberOfInputs;
    private List<Neuron> neurons;
    private int layerNumber;
    protected int numberOfNeurons;
    private FloatMatrix activationZ;
    protected FloatMatrix fDashOfActivationZ;
    protected FloatMatrix deltas;
    private FloatMatrix inputs;
    private FloatMatrix outputs;

    
    HiddenLayer(int layerNumber, int numberOfNeurons, int numberOfInputs)
    {
        this.numberOfNeurons = numberOfNeurons;
        this.layerNumber = layerNumber;
        this.numberOfInputs = numberOfInputs;
        neurons = new ArrayList<Neuron>();
        for(int i=1; i<=numberOfNeurons; ++i) {
            neurons.add(new Neuron(numberOfInputs));
        }
        activationZ = new FloatMatrix(numberOfNeurons);
        fDashOfActivationZ = new FloatMatrix(numberOfNeurons);
        deltas = new FloatMatrix(numberOfNeurons, numberOfInputs);
    }
    
    //System.out.println("ERROR: input vector to layer" + layerNumber +
    //       " is not correct size. expected: " +
    //    numberOfInputs + " got: " + input.length);
    FloatMatrix getOutput(FloatMatrix input)
    {
        if(input.length!=numberOfInputs) {
            throw new Error("ERROR: input vector to layer" + layerNumber +
             " is not correct size. expected: " +
            numberOfInputs + " got: " + input.length);
        }
        //save the input for backwards pass
        this.inputs = input.dup();
        FloatMatrix inputPlusBiasConstant = addConstantTermToInputVector(input);
        FloatMatrix outputVector = new FloatMatrix(numberOfNeurons);
        int i=0;
        for(Neuron n: neurons) {
            float neuronActivation = n.getWeightVector().dot(inputPlusBiasConstant);
            activationZ.put(i, 0, neuronActivation);
            fDashOfActivationZ.put(i, 0, activationFunctionDash(neuronActivation));
            outputVector.put(i, 0, activationFunction(neuronActivation));
            ++i;
        }
        outputs = outputVector.dup();
        return outputVector;
    }
    
    // non linear activation applied on each neuron
    private float activationFunction(float z)
    {
        //return z>0 ? z : 0; //ReLU
        //return 1/(float)(1+Math.exp(-z));//logistic function
        return (float)1.7159*(float)Math.tanh(0.666*z);//tanh
    }
    
    //f'(z) for back propagate
    private float activationFunctionDash(float z)
    {
       // return z>0 ? 1 : 0;
       //return activationFunction(z)*(1-activationFunction(z));//logistic
        return 1-(float)Math.pow(activationFunction(z),2);//tanh
    }
    
    private FloatMatrix addConstantTermToInputVector(FloatMatrix input)
    {
        FloatMatrix inputPlusBiasConstant = new FloatMatrix(numberOfInputs+1);
        inputPlusBiasConstant.put(0,0,-1);
        for(int i=1; i<=numberOfInputs; ++i) {
            inputPlusBiasConstant.put(i, 0, input.get(i-1,0));
        }
        return inputPlusBiasConstant;
    }
    
    FloatMatrix computeDeltas(FloatMatrix weightMatrixLplus1, FloatMatrix deltasLplus1)
    {
        if(weightMatrixLplus1.columns!=numberOfNeurons) {
            System.out.println("computeDeltas in layer" + layerNumber + " recieved: ");
            Driver.printMatrixDetails("weightMatrixLplus1", weightMatrixLplus1);
            Driver.printMatrixDetails("deltasLplus1", deltasLplus1);
            throw new Error("weightMatrixLplus1 should have " + numberOfNeurons +
                            "columns."  );
        }
        //deltas = W^(l+1) * d^(l+1)
        deltas = weightMatrixLplus1.transpose().mmul(deltasLplus1);
        //d = d bullet f'(z)
        deltas.muli(fDashOfActivationZ);
        return deltas.dup();
    }
    
    FloatMatrix getInputs()
    {
        return inputs.dup();
    }
    FloatMatrix getOutputs()
    {
        return outputs.dup();
    }
    
    FloatMatrix getDeltas()
    {
        return deltas.dup();
    }

    FloatMatrix getWeightMatrix()
    {
        FloatMatrix weightMatrix = new FloatMatrix(numberOfNeurons, numberOfInputs);
        int i=0;
        for(Neuron n: neurons) {
            weightMatrix.putRow(i, n.getWeightVectorNoBias());
            ++i;
        }
        return weightMatrix;
    }
    
    void setWeightMatrix(FloatMatrix updatedWeights)
    {
        int i=0;
        for(Neuron n: neurons) {
            n.setWeights(updatedWeights.getRow(i));
            ++i;
        }
    }
    
    //gets the bias weight from each neuron and puts them in a column vector
    FloatMatrix getBiasVector()
    {
        FloatMatrix biasVector = new FloatMatrix(numberOfNeurons, 1);
        int i=0;
        for(Neuron n: neurons) {
            biasVector.put(i, n.getBiasWeight());
            ++i;
        }
        return biasVector;
    }
    
    //takes a column vector of new bias' and sets the value in each neuron
    //to the corresponding element
    void setBiasVector(FloatMatrix updatedBiasVector)
    {
        int i=0;
        for(Neuron n: neurons) {
            n.setBias(updatedBiasVector.get(i));
            ++i;
        }
    }
    
    int getWidth()
    {
        return neurons.size();
    }
    
    int getNumberOfInputs()
    {
        return numberOfInputs;
    }
    
    int getNumberOfNeurons()
    {
        return numberOfNeurons;
    }
    
    int getLayerNumber()
    {
        return layerNumber;
    }
    
    void printLayerDetails()
    {
        System.out.println("\nLAYER " + layerNumber);
        System.out.println("number of inputs: " + numberOfInputs);
        System.out.println("number of neurons: " + numberOfNeurons);
        Driver.printMatrixDetails("activationZ", activationZ);
        Driver.printMatrixDetails("fDashOfActivationZ", fDashOfActivationZ);
        Driver.printMatrixDetails("deltas", deltas);
        Driver.printMatrixDetails("inputs", inputs);
        Driver.printMatrixDetails("outputs", outputs);
        FloatMatrix weightMatrix = getWeightMatrix();
        Driver.printMatrixDetails("weightMatrix", weightMatrix);
        FloatMatrix biasVector = getBiasVector();
        Driver.printMatrixDetails("biasVector", biasVector);
        System.out.println("\n");
    }

    void testComputeDeltas()
    {
        fDashOfActivationZ = FloatMatrix.rand(numberOfNeurons);
        FloatMatrix weightMatrixLplus1 = FloatMatrix.rand(3, numberOfNeurons);
        FloatMatrix deltasLplus1 = FloatMatrix.rand(3);

        FloatMatrix deltas = computeDeltas(weightMatrixLplus1,deltasLplus1);
        Driver.printMatrixDetails("deltas", deltas);
        Driver.is(deltas.rows, numberOfNeurons, "does delta Matrix have correct rows (number of neurons)");
        Driver.is(deltas.columns, 1, "does delta Matrix have correct columns");
        
        weightMatrixLplus1 = FloatMatrix.rand(20, numberOfNeurons);
        deltasLplus1 = FloatMatrix.rand(20);

        deltas = computeDeltas(weightMatrixLplus1,deltasLplus1);
        Driver.printMatrixDetails("deltas", deltas);
        Driver.is(deltas.rows, numberOfNeurons, "does delta Matrix have correct rows (number of neurons)");
        Driver.is(deltas.columns, 1, "does delta Matrix have correct columns");
    }
    
    void testGetWeightMatrix()
    {
        FloatMatrix weightMatrix = getWeightMatrix();
        int unit=0;
        for(Neuron n: neurons) {
            FloatMatrix weightsN = n.getWeightVectorNoBias();
            for(int i=0; i<numberOfInputs; ++i) {
                Driver.is( weightsN.get(i,0), weightMatrix.get(unit, i),
                "checking that weight matrix equals each element of the weights");
            }
            ++unit;
        }
    }
    
    void tests()
    {
        testGetWeightMatrix();
        testComputeDeltas();
    }

    public static void main(String[] args)
    {
      HiddenLayer hl = new HiddenLayer(1,9,18);
      hl.tests();
    }
}
