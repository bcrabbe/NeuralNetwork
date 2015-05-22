import org.jblas.*;
import java.util.*;

class Network
{
    private InputLayer inputLayer;
    private List<HiddenLayer> hiddenLayers;
    private int[] layerWidths;
    //private Trainer trainer;
    
    Network(int...definition)
    {
        layerWidths = definition.clone();
        inputLayer = new InputLayer(layerWidths[0]);
        hiddenLayers = new ArrayList<HiddenLayer>();
        for(int i=1; i<definition.length; ++i) {
            hiddenLayers.add(new HiddenLayer(i, layerWidths[i], layerWidths[i-1]));
        }
    }
    
    FloatMatrix computeFowardPass(FloatMatrix input)
    {
        //FloatMatrix input = inputLayer.getInput();
        //System.out.println("input: " + input.toString());
        int i=1;
        for(HiddenLayer l: hiddenLayers) {
            input = l.getOutput(input);
            //System.out.println("layer " + i + ": " + input.toString());
            ++i;
        }
        return input;
    }
    
    void testsNetworkDefinition(int... definition)
    {
        System.out.println("Network initialised with layers: " + Arrays.toString(layerWidths) );
       
        System.out.println("\n\nLAYER 0");
        Driver.is(inputLayer.getWidth(), definition[0],
        "checking the sizes of the layers and the number of inputs they should recieve correct");
        int i=1;
        for(HiddenLayer l: hiddenLayers) {
            System.out.println("\n\nLAYER " + i);

            Driver.is(l.getWidth(),definition[i],"has correct number of neurons");
            Driver.is(l.getNumberOfInputs(),definition[i-1],"has the correct number of connections");
            ++i;
        }
        System.out.println("\n\nLAYER 0");
        Driver.is(inputLayer.getInput().length, definition[0],
        "is the size of the InputLayer.getInput() returned vector correct");
        Driver.is(hiddenLayers.get(0).getNumberOfInputs(), inputLayer.getInput().length,
        "is the size of the input vector the size expected by the first layer.");
        
        System.out.println("\n\nLAYER 1");
        FloatMatrix input = inputLayer.getInput();
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
        
      //  System.out.println("forward pass: " + computeFowardPass().toString());
    }

   public static void main(String[] args)
   {
        Driver d = new Driver();
        Network net =  new Network(1,5,1);
        net.testsNetworkDefinition(1,5,1);
       
        Network net2 =  new Network(1,9,8,7,3,7);
        net2.testsNetworkDefinition(1,9,8,7,3,7);
        Driver.finishTesting("testsNetworkDefinition");
   }
}
