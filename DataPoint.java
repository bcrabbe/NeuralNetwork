import java.util.*;
/*
x, y1, y2, y3... 
data point for Grapher 
*/
class DataPoint
{
    float x;
    List<Float> y;
    
    DataPoint(float x0, float...y0)
    {
        y = new ArrayList<Float>();
        x = x0;
        for(float y_i: y0) {
            y.add(new Float(y_i));
        }
    }
    
    void addY(float y0)
    {
        y.add(y0);
    }
    
    int getNumberOfYs()
    {
        return y.size();
    }
    
}
