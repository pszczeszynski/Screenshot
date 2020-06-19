import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.*;
import javax.swing.*;

public class Mom {
    private static Socket socket;
    private static InputStream inputStream;
    public static void main(String[] args) throws Exception {
        System.out.println("Running mom...");
        InitializeSocket(args[0]);
        System.out.println("Listening...");

        BufferedImage image = ReadImageFromSocket();
        

        DisplayImage(image);

        socket.close();
    }

    public static BufferedImage ReadImageFromSocket() {
        try {
            int totalImageSize = ReadIntFromSocket();
            int imageChunkSize = ReadIntFromSocket();
            System.out.println("receiving image, total size: " + totalImageSize + 
                " chunk size: " + imageChunkSize);
    
            byte[] receivedBytesStitched = new byte[totalImageSize];
            byte[] receivedChunk = new byte[imageChunkSize];
            int currentInsertionPoint = 0;
            while (currentInsertionPoint + imageChunkSize < totalImageSize) {
                int numBytesRead = inputStream.read(receivedChunk);
                System.arraycopy(receivedChunk, 0, receivedBytesStitched, currentInsertionPoint, imageChunkSize);
                if (numBytesRead < 0) {
                    break;
                }
                System.out.println("received bytes!");
                currentInsertionPoint += imageChunkSize;
            }

            System.out.println("finished reading bytes");
            
    
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(receivedBytesStitched));
            System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
            return image;
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        return null;
    }

    public static int ReadIntFromSocket() throws Exception {
        byte[] bytes = new byte[8];
        inputStream.read(bytes);
        return ByteBuffer.wrap(bytes).asIntBuffer().get();
    }

    public static void InitializeSocket(String ip) throws Exception {
        socket = new Socket(ip, 13085);
        inputStream = socket.getInputStream();
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
}