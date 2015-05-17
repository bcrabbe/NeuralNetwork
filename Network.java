import org.jblas.*;
import java.util.*;

class Network
{
    private InputLayer inputLayer;
    private List<HiddenLayer> hiddenLayers;
    private int[] layerWidths;
    
    
    Network(int...definition)
    {
        layerWidths = definition.clone();
        inputLayer = new InputLayer(layerWidths[0]);
        hiddenLayers = new ArrayList<HiddenLayer>();
        for(int i=1; i<definition.length; ++i) {
            hiddenLayers.add(new HiddenLayer(layerWidths[i],layerWidths[i-1]));
        }
    }
    
    static void tests()
    {

    }

   public static void main(String[] args)
   {
        Network test = new Network();
        test.tests();
      
   }
}
