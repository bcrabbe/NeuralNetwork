import org.jblas.*;
import java.util.*;

class Trainer
{
    private Network net;
    private int inputWidth;
    private int trainingBatchSize=10000;
    private int validationSetSize=20;
    private float weightDecay=0;
    private float momentum=(float)0.9;
    private float learningRate=(float)0.001;

    Trainer(Network net, int inputWidth)
    {
        this.net = net;
        this.inputWidth = inputWidth;
    }
    
    void trainNetwork()
    {
        int batchNumber=1;
        while(batchNumber<10000) {
            presentTrainingBatch();
            float validationError = measureValidationError();
            System.out.println("Batch number = " + batchNumber +
                               ". Validation error = " + validationError);
            ++batchNumber;
        }
    }
    
    private float measureValidationError()
    {
        float validationError = 0;
        for(int i=1; i<=validationSetSize; ++i) {
            List<FloatMatrix> example = getExample();
            FloatMatrix netOutput = net.computeFowardPass(example.get(0));
            validationError += computeExampleLoss(netOutput, example.get(1));
        }
        return validationError/(float)validationSetSize;
    }
    
    private void presentTrainingBatch()
    {
        List<FloatMatrix> deltaW_l = new ArrayList<FloatMatrix>();
        List<FloatMatrix> deltaB_l = new ArrayList<FloatMatrix>();
        for(int i=1; i<=trainingBatchSize; ++i) {
            List<FloatMatrix> example = getExample();
            List<List<FloatMatrix>> gradLoss_wrtWandB = presentTrainingExample(example.get(0), example.get(1));
            List<FloatMatrix> gradLoss_wrtW = gradLoss_wrtWandB.get(0);
            List<FloatMatrix> gradLoss_wrtB = gradLoss_wrtWandB.get(1);
            if(i==1) {
                deltaW_l = Driver.copySizingInMatrixList(gradLoss_wrtW);
                deltaB_l = Driver.copySizingInMatrixList(gradLoss_wrtB);
                net.initialisePreviousUpdates(gradLoss_wrtW,gradLoss_wrtB);
            }
            //accumulate each update gradLoss_wrtW/B in deltaW_l
            Driver.addFloatMatrixListsi(deltaW_l, gradLoss_wrtW);
            Driver.addFloatMatrixListsi(deltaB_l, gradLoss_wrtB);
        }
        Driver.scalarMultiplyFloatMatrixListsi(deltaW_l, (1/(float)trainingBatchSize));
        Driver.scalarMultiplyFloatMatrixListsi(deltaB_l, (1/(float)trainingBatchSize));
        net.updateParameters(deltaW_l, deltaB_l, weightDecay, momentum, learningRate);
    }
    
    private List<List<FloatMatrix>> presentTrainingExample(FloatMatrix input, FloatMatrix label)
    {
        FloatMatrix netOutput = net.computeFowardPass(input);
        return net.backpropagate(netOutput, label);
    }
    
    private float computeExampleLoss(FloatMatrix netOutput, FloatMatrix expectedOutput)
    {
        netOutput.subi(expectedOutput);
        return (float)0.5*netOutput.dot(netOutput);
    }
    
    //returns a 2 floatMatricies, first is the input, 2nd is the expected output
    private List<FloatMatrix> getExample()
    {
        return generateRandomSinSample();
    }
    
    private List<FloatMatrix> generateRandomSinSample()
    {
        List<FloatMatrix> example = new ArrayList<FloatMatrix>();
    
        float randomX = Driver.randomNumberGen.nextFloat()*(float)Math.PI-((float)Math.PI/2);
        float randomY = (float)Math.sin(randomX);
        /*FloatMatrix(int newRows, int newColumns, float... newData)
          Create a new matrix with newRows rows, newColumns columns using newData> as the data.
        */
        example.add(new FloatMatrix(1,1,randomX));
        example.add(new FloatMatrix(1,1,randomY));
        return example;
    }
    
 
    
    
}