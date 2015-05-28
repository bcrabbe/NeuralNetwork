import org.jblas.*;
import java.util.*;
/**
Like a hiddenLayer but different formula for computeDeltas
*/

class OutputLayer extends HiddenLayer
{
    OutputLayer(int layerNumber, int numberOfNeurons, int numberOfInputs)
    {
        super(layerNumber, numberOfNeurons, numberOfInputs);
    }
    
    FloatMatrix computeDeltas(FloatMatrix networkOutput, FloatMatrix expectedOutput)
    {
        //d = (y-a):
        deltas = expectedOutput.sub(networkOutput);
        //d = -d bullet f'(z)
        deltas.muli(-1).muli(fDashOfActivationZ);
        return deltas.dup();
    }
    
    private void testComputeDeltasFinalLayer()
    {
        fDashOfActivationZ = FloatMatrix.rand(numberOfNeurons);
        FloatMatrix networkOutput = FloatMatrix.rand(numberOfNeurons, 1);
        FloatMatrix expectedOutput = FloatMatrix.rand(numberOfNeurons, 1);

        FloatMatrix deltas = computeDeltas(networkOutput, expectedOutput);
        Driver.printMatrixDetails("deltas", deltas);
        Driver.is(deltas.rows, numberOfNeurons, "does delta Matrix have correct rows (number of neurons)");
        Driver.is(deltas.columns, 1, "does delta Matrix have correct columns");
    }
    
    private void tests()
    {
        testComputeDeltasFinalLayer();
        Driver.finishTesting("testComputeDeltasFinalLayer");
    }

    public static void main(String[] args)
    {
        OutputLayer l = new OutputLayer(1, 2, 3);
        l.tests();
        OutputLayer l2 = new OutputLayer(1, 19, 21);
        l2.tests();
    }
}