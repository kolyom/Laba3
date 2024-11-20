import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class line extends JPanel {
    public static class Point {
        double x;
        double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    private BufferedImage image;
    private int centerX = 600;
    private int centerY = 250;
    private int radius = 80;
    private int[] xPoints2 = {100, 150, 200, 150, 100}; // Example polygon vertices
    private int[] yPoints2 = {50, 100, 50, 0, 50};
    private JButton drawLineButton, drawCircleButton, drawPolygonButton, drawBezierButton;
    private JButton fillCircleButton, fillPolygonButton;

    
    private int[][] xPoints;
    private int[][] yPoints;

    private Point p0, p1, p2;
    private final int backgroundColor = Color.WHITE.getRGB(); //Задаем цвет фона
    private void clearImage() {
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
    }

    public static List<Point> drawCircle(double x0, double y0, double r, int numPoints) {
        List<Point> points = new ArrayList<>();
        for (double x = x0 - r; x <= x0 + r; x += 0.1) {
            double y = Math.sqrt(r * r - (x - x0) * (x - x0));
            if (!Double.isNaN(y)) {
                points.add(new Point(x, y0 + y));
                points.add(new Point(x, y0 - y));
            }
        }
        return points;
    }

    private void drawLine(int x0, int y0, int x1, int y1, Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        int x = x0;
        int y = y0;
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = (dx > dy ? dx : -dy) / 2;

        while (true) {
            g2d.fillRect(x, y, 1, 1); ///////////////////////////////////
            if (x == x1 && y == y1)
                break;
            int e2 = err;
            if (e2 > -dx) {
                err -= dy;
                x += sx;
            }
            if (e2 < dy) {
                err += dx;
                y += sy;
            }
        }
    }

    private void drawBezier(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        List<Point> bezierPoints = generateBezierCurve(p0, p1, p2, 100);
        for (int i = 1; i < bezierPoints.size(); i++) {
            drawLine((int) bezierPoints.get(i - 1).x, (int) bezierPoints.get(i - 1).y, (int) bezierPoints.get(i).x,
                    (int) bezierPoints.get(i).y, g2d);
        }
    }

    private List<Point> generateBezierCurve(Point p0, Point p1, Point p2, int numPoints) {
        List<Point> curvePoints = new ArrayList<>();
        for (int i = 0; i <= numPoints; i++) {
            double t = (double) i / numPoints;
            double x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x;
            double y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y;
            curvePoints.add(new Point(x, y));
        }
        return curvePoints;
    }

    private void floodFillCircle(BufferedImage img, int x, int y, int fillColor, int boundaryColor) {
        int width = img.getWidth();
        int height = img.getHeight();

        if (x < 0 || x >= width || y < 0 || y >= height || img.getRGB(x, y) != boundaryColor) {
            return;
        }

        Queue<Point> queue = new LinkedList<>();
        queue.offer(new Point(x, y));

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int currentX = (int) current.x;
            int currentY = (int) current.y;

            if (currentX < 0 || currentX >= width || currentY < 0 || currentY >= height || img.getRGB(currentX, currentY) != boundaryColor) {
                continue;
            }

            img.setRGB(currentX, currentY, fillColor);

            queue.offer(new Point(currentX + 1, currentY));
            queue.offer(new Point(currentX - 1, currentY));
            queue.offer(new Point(currentX, currentY + 1));
            queue.offer(new Point(currentX, currentY - 1));
        }
        

    }
    private boolean isPointInPolygon(int x, int y, int[] xPoints, int[] yPoints) {
        int n = xPoints.length;
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = xPoints[i], yi = yPoints[i];
            double xj = xPoints[j], yj = yPoints[j];
            if (((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
        }
        return inside;
    }
    private void floodFillPolygon(BufferedImage img, int x, int y, int fillColor, int[] xPoints, int[] yPoints) {
        int width = img.getWidth();
        int height = img.getHeight();

        if (x < 0 || x >= width || y < 0 || y >= height || img.getRGB(x, y) != backgroundColor) {
            return;
        }

        if (!isPointInPolygon(x, y, xPoints, yPoints)) {
            return;
        }

        Queue<Point> queue = new LinkedList<>();
        queue.offer(new Point(x, y));

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int currentX = (int) current.x;
            int currentY = (int) current.y;

            if (currentX < 0 || currentX >= width || currentY < 0 || currentY >= height || img.getRGB(currentX, currentY) != backgroundColor) {
                continue;
            }

            if (isPointInPolygon(currentX, currentY, xPoints, yPoints)) {
                img.setRGB(currentX, currentY, fillColor);
                queue.offer(new Point(currentX + 1, currentY));
                queue.offer(new Point(currentX - 1, currentY));
                queue.offer(new Point(currentX, currentY + 1));
                queue.offer(new Point(currentX, currentY - 1));
            }
        }
    }



    public line() {
        image = new BufferedImage(1200, 800, BufferedImage.TYPE_INT_ARGB); // Create the image
        clearImage();
        xPoints = new int[1][];
        yPoints = new int[1][];
        xPoints[0] = new int[]{centerX - 100, centerX - 50, centerX + 50, centerX + 100, centerX + 50, centerX - 50};
        yPoints[0] = new int[]{centerY + 150, centerY + 200, centerY + 200, centerY + 150, centerY + 100, centerY + 100};
        p0 = new Point(centerX + 200, centerY + 150);
        p1 = new Point(centerX + 250, centerY);
        p2 = new Point(centerX + 350, centerY + 150);
        drawLineButton = new JButton("Линия");
        drawCircleButton = new JButton("Окружность");
        drawPolygonButton = new JButton("Шестиугольник");
        drawBezierButton = new JButton("Безье");
        fillCircleButton = new JButton("Закрасить круг");
        fillPolygonButton = new JButton("Закрасить многоугольник");

        drawLineButton.addActionListener(e -> {
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            drawLine(100, 100, 200, 200, g2d);
            g2d.dispose();
            repaint();
        });
        drawCircleButton.addActionListener(e -> {
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            List<Point> circlePoints = drawCircle(centerX, centerY, radius, 1000);
            for (int i = 1; i < circlePoints.size(); i++) {
          
                //g2d.drawLine((int) circlePoints.get(circlePoints.size()-1).x, (int) circlePoints.get(circlePoints.size()-1).y, (int) circlePoints.get(0).x, (int) circlePoints.get(0).y);
                drawLine((int) circlePoints.get(i - 1).x, (int) circlePoints.get(i - 1).y, (int) circlePoints.get(i).x, (int) circlePoints.get(i).y, g2d);
                
            }
            g2d.dispose();
            repaint();
            
            
        });
        drawPolygonButton.addActionListener(e -> {
            Graphics g2d = (Graphics2D) image.getGraphics();
            g2d.setColor(Color.RED);
            int[] xPoints = {centerX - 100, centerX - 50, centerX + 50, centerX + 100, centerX + 50, centerX - 50};
            int[] yPoints = {centerY + 150, centerY + 200, centerY + 200, centerY + 150, centerY + 100, centerY + 100};

            for (int i = 1; i < xPoints.length; i++) {
                drawLine(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i], (Graphics2D) g2d);
            }
            // Connect last point to first to close the polygon:
            drawLine(xPoints[xPoints.length - 1], yPoints[xPoints.length - 1], xPoints[0], yPoints[0], (Graphics2D) g2d);
            g2d.dispose();
            repaint();
        });
        drawBezierButton.addActionListener(e -> {
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            drawBezier(g2d);
            g2d.dispose();
            repaint();
        });
        
        fillCircleButton.addActionListener(e -> {
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            floodFillCircle(image, centerX, centerY, Color.RED.getRGB(), Color.BLACK.getRGB());
            g2d.dispose();
            repaint();
        });

        fillPolygonButton.addActionListener(e -> {
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            int fillColor = Color.GREEN.getRGB(); // Example fill color
            floodFillPolygon(image, xPoints2[0], yPoints2[0], fillColor, xPoints2, yPoints2);             g2d.dispose();
            repaint();
        });

        setLayout(new FlowLayout());
        add(drawLineButton);
        add(drawCircleButton);
        add(drawPolygonButton);
        add(drawBezierButton);
        add(fillCircleButton);
        add(fillPolygonButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }


}
