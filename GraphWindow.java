/* Provide a top-level window for the game. */

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;
import java.util.List;

class GraphWindow
{
    private int task, START=0, UPDATE=1;
    private Grapher graph;
    private DataList dataPoints;
    
    GraphWindow(DataList dataPoints)
    {
        this.dataPoints = dataPoints;
        task = START;
        SwingUtilities.invokeLater(this::run);
    }

    void update(DataPoint additionalDataPoint)
    {
        dataPoints.add(additionalDataPoint);
        task = UPDATE;
        try { SwingUtilities.invokeAndWait(this::run); }
        catch (Exception err) { throw new Error(err); }
    }
    
    void update(DataList newDataList)
    {
        dataPoints = newDataList;
        task = UPDATE;
        try { SwingUtilities.invokeAndWait(this::run); }
        catch (Exception err) { throw new Error(err); }
    }

    private void run()
    {
        if (task == START) start();
        else if (task == UPDATE) update();
    }

    private void start()
    {
        JFrame w = new JFrame();
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graph = new Grapher(dataPoints);
        w.add(graph);
        w.pack();
        w.setLocationByPlatform(true);
        w.setVisible(true);
    }

    private void update()
    {
        graph.update(dataPoints);
    }
}