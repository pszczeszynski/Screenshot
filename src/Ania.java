package src;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.net.ServerSocket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.geom.*;

class Ania {
    public static void main(String[] args) throws Exception{
        while (true) {
            ServerSocket serverSocket = new ServerSocket(13085);
            Socket socket = serverSocket.accept();

            OutputStream outputStream = socket.getOutputStream();


            BufferedImage screenShot = deepCopy(GetScreenShot());
            screenShot = resize(screenShot, screenShot.getHeight()/2, screenShot.getWidth()/2);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(screenShot, "jpg", byteArrayOutputStream);

            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

            outputStream.write(size);

            byte[] rawBytes = byteArrayOutputStream.toByteArray();

            outputStream.write(rawBytes);
            outputStream.flush();
            System.out.println("Flushed: " + System.currentTimeMillis());
            System.out.println("Closing: " + System.currentTimeMillis());
            serverSocket.close();
        }
    }

    private static BufferedImage GetScreenShot() throws Exception {
        Robot robot = new Robot();            
        Rectangle screenRect = new Rectangle(0, 0, 0, 0);
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
        }


        return robot.createScreenCapture(screenRect);
    }

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        BufferedImage outputImage = new BufferedImage(width, height, img.getType());
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();


        // ImageIO.write(outputImage, "jpg", outputImage);

        // Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        // BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Graphics2D g2d = resized.createGraphics();
        // resized.flush();
        // g2d.drawImage(tmp, 0, 0, null);
        // g2d.dispose();
        return outputImage;
    }

    
    public static void DisplayImage(BufferedImage image) throws Exception
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame editorFrame = new JFrame("Image Demo");
                editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
                ImageIcon imageIcon = new ImageIcon(image);
                JLabel jLabel = new JLabel();
                jLabel.setIcon(imageIcon);
                editorFrame.getContentPane().add(jLabel, BorderLayout.CENTER);

                editorFrame.pack();
                editorFrame.setLocationRelativeTo(null);
                editorFrame.setVisible(true);
            }
        });
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}