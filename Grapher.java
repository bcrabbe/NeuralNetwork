import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.math.*;

/** Graph drawing object
    plots data in  DataList dataPoints
    
    some drawing code adapted from https://gist.github.com/roooodcastro/6325153
*/
class Grapher extends JPanel
{
    private static final long serialVersionUID = 282891015;
    private int width = 800;
    private int height = 400;
    private int padding = 35;
    private int labelPadding = 25;
    private List<Color> lineColor = new ArrayList<Color>();
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 10;
    private int numberXDivisions = 4;
    private float xScale;
    private float yScale;

    private DataList dataPoints;

    public Grapher(DataList dataPoints)
    {
        setPreferredSize(new Dimension(width, height));
        this.dataPoints = dataPoints;
        lineColor.add(new Color(44, 102, 230, 180));
        lineColor.add(new Color(230, 102, 44, 180));
        lineColor.add(new Color(89, 240, 44, 180));
    }
    
    private float getXScale()
    {
        return ((float)getWidth() - (2*padding) - labelPadding) /
                                                dataPoints.getXRange();
    }
    
    private float getYScale()
    {
        return ((float)getHeight() - (2*padding) - labelPadding) /
                                                dataPoints.getYRange();
    }
    
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        xScale = getXScale();
        yScale = getYScale();
        
        List<List<Point>> plots = dataPoints.transformAndConvertToPointList(
            xScale, (int)(padding+labelPadding-(dataPoints.getMinX()*xScale)),
             -yScale, (dataPoints.getMaxY()*yScale)+padding);
        
        drawAxes(g2);
        plotPoints(g2,plots);
    }

    private void plotPoints(Graphics2D g2,List<List<Point>> plots)
    {
        int plot=0;
        for(List<Point> graphPoints: plots) {
            Stroke oldStroke = g2.getStroke();
            g2.setColor(lineColor.get(plot));
            g2.setStroke(GRAPH_STROKE);
            for (int i = 0; i < graphPoints.size() - 1; i++) {
                int x1 = graphPoints.get(i).x;
                int y1 = graphPoints.get(i).y;
                int x2 = graphPoints.get(i + 1).x;
                int y2 = graphPoints.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }
            plot = plot+1<=lineColor.size() ? plot+1 : 0;
            g2.setStroke(oldStroke);
            g2.setColor(pointColor);
            for (int i = 0; i < graphPoints.size(); i++) {
                int x = graphPoints.get(i).x - pointWidth / 2;
                int y = graphPoints.get(i).y - pointWidth / 2;
                int ovalW = pointWidth;
                int ovalH = pointWidth;
                g2.fillOval(x, y, ovalW, ovalH);
            }
        }
    }
    
    private void drawAxes(Graphics2D g2)
    {
        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding,
            getWidth() - (2*padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);
        drawAxisX(g2);
        drawAxisY(g2);
    }
    
    private void drawAxisX(Graphics2D g2)
    {
            // and for x axis
        for (int i = 0; i <= numberXDivisions; i++) {
            int x0 =  ((i * (getWidth() - padding * 2 - labelPadding)) /
                        numberXDivisions + padding + labelPadding);
            int x1 = x0;
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            g2.setColor(gridColor);
            g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
            g2.setColor(Color.BLACK);
            BigDecimal tickLabel = new BigDecimal(((i/(float)numberXDivisions)*dataPoints.getXRange() +
                dataPoints.getMinX()));
            tickLabel = tickLabel.round(new MathContext(3));
            String xLabel = tickLabel + "";
            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(xLabel);
            g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            g2.drawLine(x0, y0, x1, y1);
        }
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);
    }
    
    private void drawAxisY(Graphics2D g2)
    {
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) /
                                            numberYDivisions + padding + labelPadding);
            int y1 = y0;
            g2.setColor(gridColor);
            g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
            g2.setColor(Color.BLACK);
            BigDecimal tickLabel = new BigDecimal(( (int)((dataPoints.getMinY() + (dataPoints.getMaxY() -
                dataPoints.getMinY()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 );
            tickLabel = tickLabel.round(new MathContext(2));
            String yLabel = tickLabel + "";
            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(yLabel);
            g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            g2.drawLine(x0, y0, x1, y1);
        }
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);

    }
    
    public void update(DataList updatedList)
    {
        dataPoints = updatedList;
        invalidate();
        this.repaint();
    }
}