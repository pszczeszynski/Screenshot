import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.awt.Robot;
import java.awt.Rectangle;
import java.net.ServerSocket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

class Ania {
    private static ServerSocket serverSocket;
    private static OutputStream outputStream;
    private static Socket socket;
    public static void main(String[] args) throws Exception{
        while (true) {
            InitializeSocket();
            BufferedImage screenShot = GetScreenShot();
            // screenShot = resize(screenShot, screenShot.getHeight()/4, screenShot.getWidth()/4);
            int packetSize = 60000;
            SendImage(screenShot, packetSize);
            CloseSocket();
        }
    }


    private static void SendInt(int x) throws Exception{
        byte[] bytes = ByteBuffer.allocate(8).putInt(x).array();
        outputStream.write(bytes);
        outputStream.flush();
    }


    /// Breaks the image into manageable chunks to send over the tcpip
    private static void SendImage(BufferedImage image, int packetSize) throws Exception {
        // first convert the image into bytes
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byte[] allBytes = byteArrayOutputStream.toByteArray();


        // SEND THE IMAGES SPECS
        int totalSize = byteArrayOutputStream.size();
        SendInt(totalSize);
        SendInt(packetSize);


        byte[] currentSending = new byte[packetSize];
        int currentStartingIndex = 0;
        boolean finished = false;
        while (!finished) {
            for (int i = 0; i < packetSize; i ++) {
                if (currentStartingIndex + i >= allBytes.length) {
                    finished = true;
                    break;
                }
                currentSending[i] = allBytes[currentStartingIndex + i];
            }

            outputStream.write(currentSending);
            outputStream.flush();
            System.out.println("Flushed: " + System.currentTimeMillis());

            currentStartingIndex += packetSize;
        }
    }

    private static void InitializeSocket() throws Exception {
        serverSocket = new ServerSocket(13085);
        socket = serverSocket.accept();
        outputStream = socket.getOutputStream();
    }

    private static BufferedImage GetScreenShot() throws Exception {
        Robot robot = new Robot();            
        Rectangle screenRect = new Rectangle(0, 0, 0, 0);
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
        }


        return robot.createScreenCapture(screenRect);
    }

    /// Resizes an image to the specified dimensions
    private static BufferedImage resize(BufferedImage img, int height, int width) {
        BufferedImage outputImage = new BufferedImage(width, height, img.getType());
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
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

    private static BufferedImage deepCopyImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private static void CloseSocket() throws Exception {
        System.out.println("Closing: " + System.currentTimeMillis());
        serverSocket.close();
    }
}