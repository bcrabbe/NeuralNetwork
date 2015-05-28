import org.jblas.*;
import java.util.*;
/* Neural network class
    implements backpropagation 
    trained by Trainer
 
    computeFowardPass() will compute the output for a given input
*/
class Network
{
    //private InputLayer inputLayer;
    private List<HiddenLayer> hiddenLayers;
    private int[] layerWidths;
    private List<FloatMatrix> previousWeightUpdate;
    private List<FloatMatrix> previousBiasUpdate;
    private boolean verbose = false;
    
    Network(int...definition)
    {
        layerWidths = definition.clone();
        //inputLayer = new InputLayer(layerWidths[0]);
        hiddenLayers = new ArrayList<HiddenLayer>();
        for(int i=1; i<definition.length-1; ++i) {
            hiddenLayers.add(new HiddenLayer(i-1, layerWidths[i], layerWidths[i-1]));
        }
        hiddenLayers.add(new OutputLayer(definition.length-2, layerWidths[definition.length-1],
        layerWidths[definition.length-2]));
    }
    
    // given a training example this computes the errors for each node
    // returns the gradient of the loss wrt the parameters
    List<List<FloatMatrix>> backpropagate(FloatMatrix networkOutput, FloatMatrix expectedOutput)
    {
        //lists of matricies for each layer. These will hold the dE/dW^(l)_ij dE/db^(l)_i terms
        List<FloatMatrix> gradLoss_wrtW = new ArrayList<FloatMatrix>(hiddenLayers.size());
        List<FloatMatrix> gradLoss_wrtB = new ArrayList<FloatMatrix>(hiddenLayers.size());
        
        int lastLayer = hiddenLayers.size()-1;
        //add each layer to 0 position in the list, then they all shift down each time
        FloatMatrix deltasLplus1 = hiddenLayers.get(lastLayer).computeDeltas(networkOutput, expectedOutput);
        gradLoss_wrtB.add(0, deltasLplus1);
        gradLoss_wrtW.add(0, deltasLplus1.mmul( hiddenLayers.get(lastLayer).getInputs().transpose()));
        
        FloatMatrix deltasL;
        for(int layer=lastLayer-1; layer>=0; --layer) {
            deltasL = hiddenLayers.get(layer).computeDeltas( hiddenLayers.get(layer+1).getWeightMatrix(),
                                                             deltasLplus1);
            gradLoss_wrtB.add(0, deltasL);
            gradLoss_wrtW.add(0, deltasL.mmul( hiddenLayers.get(layer).getInputs().transpose()));
            deltasLplus1.copy(deltasL);
        }
        List<List<FloatMatrix>> gradWandB = new ArrayList<List<FloatMatrix>>();
        gradWandB.add(gradLoss_wrtW);
        gradWandB.add(gradLoss_wrtB);
        return gradWandB;
    }
    
    //computes output of network given input
    FloatMatrix computeFowardPass(FloatMatrix input)
    {
        if(input.length!=layerWidths[0]) {
            throw new Error("Network.computeFowardPass() recieved wrong sized input. Expected: " +
            layerWidths[0] + " recieved: " + input.length);
        }
        int i=1;
        for(HiddenLayer l: hiddenLayers) {
            input = l.getOutput(input);
            ++i;
        }
        return input;
    }
    
    void initialisePreviousUpdates(List<FloatMatrix> weightUpdateTemplate,
                                   List<FloatMatrix> biasUpdateTemplate)
    {
        previousWeightUpdate = Driver.copySizingInMatrixList(weightUpdateTemplate);
        previousBiasUpdate = Driver.copySizingInMatrixList(biasUpdateTemplate);
    }
    
    void updateParameters(List<FloatMatrix> deltaW_l, List<FloatMatrix> deltaB_l,
                          float weightDecay, float momementum, float learningRate, float layerLRmultiplier)
    {
        int layerNumber=0;
        for(HiddenLayer layer: hiddenLayers) {
            FloatMatrix currentWeights = layer.getWeightMatrix();
            FloatMatrix currentBiases = layer.getBiasVector();
            FloatMatrix weightDecayTerm = currentWeights.mul(weightDecay);
            FloatMatrix weightMomentumTerm = previousWeightUpdate.get(layerNumber).mul(momementum);
            FloatMatrix biasMomentumTerm = previousBiasUpdate.get(layerNumber).mul(momementum);
            float layerMultiplier = (hiddenLayers.size()-layer.getLayerNumber())*layerLRmultiplier;
            FloatMatrix weightUpdates = weightMomentumTerm.sub(
                deltaW_l.get(layerNumber).add(weightDecayTerm).mul(learningRate).mul(
                (float)Math.sqrt(layer.getNumberOfInputs())).mul(layerMultiplier));
            FloatMatrix biasUpdates = biasMomentumTerm.sub(
                deltaB_l.get(layerNumber).mul(learningRate).mul(
                (float)Math.sqrt(layer.getNumberOfInputs())).mul(layerMultiplier));
            layer.setWeightMatrix(currentWeights.addi(weightUpdates));
            layer.setBiasVector(currentBiases.addi(biasUpdates));
            previousWeightUpdate.get(layerNumber).copy(weightUpdates);
            previousBiasUpdate.get(layerNumber).copy(biasUpdates);
            ++layerNumber;
        
            if(verbose) Driver.printMatrixDetails("weight updates",weightUpdates );
        }
    }
    
    float getMaxPreviousUpdate()
    {
        float max=0;
        for(FloatMatrix layerUpdates: previousWeightUpdate) {
            if(Math.abs(layerUpdates.max())>max) {
                max = Math.abs(layerUpdates.max());
            }
        }
        return max;
    }
    
    int getNumberOfLayers()
    {
        return hiddenLayers.size();
    }
    
    void printNetworkWeights()
    {
        for(HiddenLayer layer: hiddenLayers) {
            String name = "\nLAYER " + layer.getLayerNumber() + ": \n";
            System.out.println(name + layer.getWeightMatrix());
        }
    }
    
    void printNetworkDetails()
    {
        for(HiddenLayer layer: hiddenLayers) {
            layer.printLayerDetails();
        }
    }
    
    private void testsNetworkDefinition(int... definition)
    {
        System.out.println("Network initialised with layers: " + Arrays.toString(layerWidths) );
        
        int i=1;
        for(HiddenLayer l: hiddenLayers) {
            System.out.println("\n\nLAYER " + i);

            Driver.is(l.getWidth(),definition[i],"has correct number of neurons");
            Driver.is(l.getNumberOfInputs(),definition[i-1],"has the correct number of connections");
            ++i;
        }

        Driver.is(hiddenLayers.get(0).getNumberOfInputs(), layerWidths[0],
        "is the size of the input vector the size expected by the first layer.");
        
        System.out.println("\n\nLAYER 1");
        FloatMatrix input = new FloatMatrix(1,1,(float)0.5);
        FloatMatrix firstLayerOutput = hiddenLayers.get(0).getOutput(input);
        Driver.is(firstLayerOutput.length, definition[1],
        "is the output of the first hidden layer the correct size");
        
        i=2;
        FloatMatrix input2forTest;
        input.copy(firstLayerOutput);
        while(i<definition.length) {
            System.out.println("\n\nLAYER " + hiddenLayers.get(i-1).getLayerNumber());
            Driver.is(input.length, hiddenLayers.get(i-1).getNumberOfInputs(),
            "is the output of the previous layer correctly sized");
            input = hiddenLayers.get(i-1).getOutput(input);
            ++i;
        }
    }
    
    private void testForwardPass()
    {
        FloatMatrix testInput = FloatMatrix.rand(layerWidths[0]);
        System.out.println("Forwards pass: " + testInput.toString() + "--->" +
        computeFowardPass(testInput).toString());
    }
    
    private List<List<FloatMatrix>> backpropagateWithChecks(FloatMatrix networkOutput, FloatMatrix expectedOutput)
    {
        Driver.is(networkOutput.length, layerWidths[layerWidths.length-1], "are the netOutputs right size");
        Driver.is(expectedOutput.length, layerWidths[layerWidths.length-1]);

        //lists of matricies for each layer. These will hold the dE/dW^(l)_ij dE/db^(l)_i terms
        List<FloatMatrix> gradLoss_wrtW = new ArrayList<FloatMatrix>(hiddenLayers.size());
        List<FloatMatrix> gradLoss_wrtB = new ArrayList<FloatMatrix>(hiddenLayers.size());
        
        //get each layer's position in hiddenLayers list also postion in gradLoss_wrtW/b
        int lastLayer = hiddenLayers.size()-1;
        
        //add each layer to 0 position in the list, then they all shift down each time
        //gradLoss_wrtB_l = that layers delta vector
        FloatMatrix deltasLplus1 = hiddenLayers.get(lastLayer).computeDeltas(networkOutput, expectedOutput);
        gradLoss_wrtB.add(0, deltasLplus1);
        gradLoss_wrtW.add(0, deltasLplus1.mmul( hiddenLayers.get(lastLayer).getInputs().transpose()));
        hiddenLayers.get(lastLayer).printLayerDetails();

        Driver.is(hiddenLayers.get(lastLayer).getNumberOfNeurons(), deltasLplus1.rows,
             " are output layer deltas the sma number of rows as number of neurons");
        Driver.is(1, deltasLplus1.columns,  " are output layer deltas have 1 col");
        Driver.printMatrixDetails("gradLoss_wrtW.get(0)", gradLoss_wrtW.get(0));
        
        FloatMatrix deltasL;
        for(int layer=lastLayer-1; layer>=0; --layer) {
            hiddenLayers.get(layer).printLayerDetails();
            deltasL = hiddenLayers.get(layer).computeDeltas( hiddenLayers.get(layer+1).getWeightMatrix(),
                                                             deltasLplus1);
            Driver.is(hiddenLayers.get(layer).getNumberOfNeurons(), deltasL.rows,
             " are layer deltas the sma number of rows as number of neurons");
            Driver.is(1, deltasL.columns,  " do layer deltas have 1 col");
            
            gradLoss_wrtB.add(0, deltasL);
            
            Driver.is(gradLoss_wrtB.get(0).rows,
             hiddenLayers.get(layer).getNumberOfNeurons(),
             " are each hiddenLayers gradLoss_wrtB the sma number of rows as its number of neurons");
            Driver.is(gradLoss_wrtB.get(0).columns,
             1,
             " are each hiddenLayers gradLoss_wrtB has 1 col");
            
            gradLoss_wrtW.add(0, deltasL.mmul( hiddenLayers.get(layer).getInputs().transpose()));
            
            Driver.printMatrixDetails("gradLoss_wrtW.get(0)", gradLoss_wrtW.get(0));

            Driver.is(gradLoss_wrtW.get(0).rows,
             hiddenLayers.get(layer).getWeightMatrix().rows,
             " are each hiddenLayers gradLoss_wrtW the sma number of rows as its weight matrix");
              Driver.is(gradLoss_wrtW.get(0).columns,
             hiddenLayers.get(layer).getWeightMatrix().columns,
             " are each hiddenLayers gradLoss_wrtW the sma number of cols as its weight matrix");
             deltasLplus1.copy(deltasL);
        }
        List<List<FloatMatrix>> gradWandB = new ArrayList<List<FloatMatrix>>();
        gradWandB.add(gradLoss_wrtW);
        gradWandB.add(gradLoss_wrtB);
        Driver.finishTesting("backpropagateWithChecks");
        return gradWandB;
    }
    
    private void testBackPropagate()
    {
        System.out.println("testBackPropagate");
        testForwardPass();
        List<List<FloatMatrix>> gradWandB = backpropagateWithChecks(
            FloatMatrix.rand(layerWidths[layerWidths.length-1]),
            FloatMatrix.rand(layerWidths[layerWidths.length-1])    );
        List<FloatMatrix> gradW = gradWandB.get(0);
        List<FloatMatrix> gradB = gradWandB.get(1);

        for(int layer=0; layer<hiddenLayers.size(); ++layer) {
            hiddenLayers.get(layer).printLayerDetails();
            Driver.printMatrixDetails("gradW.get(layer)", gradW.get(layer));
            Driver.is(gradW.get(layer).rows, hiddenLayers.get(layer).getNumberOfNeurons(),
            "does GradW rows equal number of nuerons");
            Driver.is(gradW.get(layer).columns, hiddenLayers.get(layer).getWeightMatrix().columns,
            "does GradW columns equal weightMatrix columns");
        }
        Driver.finishTesting("testBackPropagate");
    }
    
    public static void main(String[] args)
    {
        Driver d = new Driver();
        Network net =  new Network(1,5,1);
        net.testsNetworkDefinition(1,5,1);
        Network net2 =  new Network(1,9,8,7,3,7);
        net2.testsNetworkDefinition(1,9,8,7,3,7);
        Driver.finishTesting("testsNetworkDefinition");
        net.testForwardPass();
        Network net4 =  new Network(1,3,2,1);
        net4.testBackPropagate();
        
    }
}
