import org.jblas.*;
import java.util.*;

class Trainer
{
    private Network net;
    private int inputWidth;
    private int trainingBatchSize=50;
    private int validationSetSize=20;
    private int trainingSetSize=2000;

    private float weightDecay=(float)0;
    private float momentum=(float)0.9;
    private float learningRate=(float)0.005;

    private float minX = (float)-Math.PI,
                  maxX = (float)Math.PI;
    List<List<FloatMatrix>> validationSet;
    List<List<FloatMatrix>> trainingSet;

    
    Trainer(Network net, int inputWidth)
    {
        this.net = net;
        this.inputWidth = inputWidth;
        validationSet = makeDataSet(validationSetSize);
        //trainingSet = makeDataSet(trainingSetSize);
    }
    
    void trainNetwork()
    {
        int batchNumber=1;
        while(batchNumber<1000000) {
            //net.printNetworkWeights();
            presentTrainingBatch();
            System.out.println("Biggest update was " + net.getMaxPreviousUpdate());
            //net.printNetworkDetails();
            float validationError = measureValidationError();
            System.out.println("Batch number = " + batchNumber +
                               ". Validation error = " + validationError);
            ++batchNumber;
        }
    }
    
    private void printValidationResult(FloatMatrix input, FloatMatrix output,
                                       FloatMatrix label)
    {
        System.out.println("x: " + input.toString() + " h(x): " + output.toString() + " y: " + label.toString());
    }
    
    private float measureValidationError()
    {
        float validationError = 0;
        for(int i=0; i<validationSetSize; ++i) {
            List<FloatMatrix> example = validationSet.get(i);
            FloatMatrix netOutput = net.computeFowardPass(example.get(0));
            //printValidationResult(example.get(0), netOutput, example.get(1));
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
    
        float randomX = Driver.randomNumberGen.nextFloat()*(float)Math.PI*2-((float)Math.PI);
        float randomY = (float)Math.sin(randomX);
        /*FloatMatrix(int newRows, int newColumns, float... newData)
          Create a new matrix with newRows rows, newColumns columns using newData> as the data.
        */
        example.add(new FloatMatrix(1,1,randomX));
        example.add(new FloatMatrix(1,1,randomY));
        return example;
    }
    
    List<List<FloatMatrix>> makeDataSet(int size)
    {
        float stepSize = (maxX-minX)/(float)(size-1);
        //a list of examples, each examples contains [input, label]
        List<List<FloatMatrix>> dataSet =
        new ArrayList<List<FloatMatrix>>(size);
        
        for(int i=0; i<size; ++i) {
            float x = minX + (i*stepSize);
            float y = testFunction(x);
            FloatMatrix input = new FloatMatrix(1);
            input.put(0,0,x);
            FloatMatrix label = new FloatMatrix(1);
            label.put(0,0,y);
            List<FloatMatrix> example = new ArrayList<FloatMatrix>(2);
            example.add(input);
            example.add(label);
            dataSet.add(example);
        }
        return dataSet;
    }
    
    private float testFunction(float x)
    {
        return (float)Math.sin(x);
    }
    
    void testValidationSet()
    {
        for(List<FloatMatrix> example: validationSet ) {
            Driver.printMatrixDetails("input",example.get(0));
            Driver.printMatrixDetails("label",example.get(1));
            Driver.is(example.get(0).get(0,0)<maxX && example.get(0).get(0,0)>minX,
                    true, "is input between minx and maxx");
            Driver.is(example.get(1).get(0,0),
                    testFunction(example.get(0).get(0,0)), "is label testFunction(input)");
        }
    }
    
    void testExampleMaker()
    {
        List<FloatMatrix> example = getExample();
        Driver.printMatrixDetails("input",example.get(0));
        Driver.is(example.get(0).get(0,0)<=maxX && example.get(0).get(0,0)>=minX,
                    true, "is input between minx and maxx");
        Driver.printMatrixDetails("label",example.get(1));

        Driver.is(example.get(1).get(0,0),
                    testFunction(example.get(0).get(0,0)), "is label testFunction(input)");
    
    }
    
    void tests()
    {
        testExampleMaker();
        testValidationSet();
        Driver.finishTesting("Input maker tests");
    }

    public static void main(String[] args)
    {
        Driver d = new Driver();
        Network net4 =  new Network(1,3,2,1);
        Trainer t = new Trainer(net4,1);
        t.tests();
    }
    
}