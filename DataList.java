import java.util.*;
import java.awt.*;
import java.util.List;
/*
    a collection of DataPoints compatible with Grapher
*/
class DataList
{
    List<DataPoint> data;
    int numberOfYs=0;
    
    DataList(int capacity)
    {
        data = new ArrayList<DataPoint>(capacity);
    }
    
    DataList()
    {
        data = new ArrayList<DataPoint>();
    }
    
    int size()
    {
        return data.size();
    }
    
    void add(float x0, float...y0)
    {
        DataPoint newData = new DataPoint(x0, y0);
        if(numberOfYs==0) {
            numberOfYs=newData.getNumberOfYs();
        }
        else if(newData.getNumberOfYs()!=numberOfYs) {
            throw new Error("DataList contains " + numberOfYs +
             " y coordinates per point. " +
             "If you want to add a point with less set the y you wish to ignore NULL");
        }
        data.add(newData);
    }
    
    void add(DataPoint newData)
    {
        if(numberOfYs==0) {
            numberOfYs=newData.getNumberOfYs();
        }
        else if(newData.getNumberOfYs()!=numberOfYs) {
            throw new Error("DataList contains " + numberOfYs +
             " y coordinates per point. " +
             "If you want to add a point with less set the y you wish to ignore NULL");
        }
        data.add(newData);
    }
    
    float getMaxX()
    {
        float maxX = data.get(0).x;
        for(DataPoint point: data) {
            maxX = point.x > maxX ? point.x : maxX;
        }
        return maxX;
    }
    
    float getMinX()
    {
        float minX = data.get(0).x;
        for(DataPoint point: data) {
            minX = point.x < minX ? point.x : minX;
        }
        return minX;
    }
    
    float getXRange()
    {
        return getMaxX()-getMinX();
    }
    
    float getMaxY()
    {
        float maxY = data.get(0).y.get(0);
        for(DataPoint point: data) {
            for(float y_i: point.y) {
                maxY = y_i > maxY ? y_i : maxY;
            }
        }
        return maxY;
    }
    
    float getMinY()
    {
        float minY = data.get(0).y.get(0);
        for(DataPoint point: data) {
            for(float y_i: point.y) {
                minY = y_i < minY ? y_i : minY;
            }
        }
        return minY;
    }
    
    float getYRange()
    {
        return getMaxY()-getMinY();
    }
    
    List<List<Point>> convertToPointList()
    {
        List<List<Point>> pList = new ArrayList<List<Point>>(numberOfYs);
        for(int i=0; i<numberOfYs; ++i) {
            List<Point> xyList = new ArrayList<Point>(data.size());
            for(int p=0; p<data.size(); ++p) {
                xyList.add(new Point((int)data.get(p).x, data.get(p).y.get(i).intValue()));
            }
            pList.add(xyList);
        }
        return pList;
    }
    
    List<List<Point>> transformAndConvertToPointList(float scaleX, float offsetX, float scaleY, float offsetY)
    {
        DataList transformedList = transformDataPoints(scaleX,offsetX,scaleY,offsetY);
        return transformedList.convertToPointList();
    }
    
    DataList transformDataPoints(float scaleX, float offsetX, float scaleY, float offsetY)
    {
        DataList transformedList = new DataList(data.size());
        for(DataPoint dp : data) {
            DataPoint tranformedData = new DataPoint( scaleX*dp.x + offsetX);
            for(float y: dp.y) {
                tranformedData.addY(scaleY*y + offsetY);
            }
            transformedList.add(tranformedData);
        }
        return transformedList;
    }
    
    void transformDataPointsi(float scaleX, float offsetX, float scaleY, float offsetY)
    {
        for(DataPoint dp : data) {
            dp.x = scaleX*dp.x + offsetX;
            for(float y: dp.y) {
                y = scaleY*y + offsetY;
            }
        }
    }
    
}