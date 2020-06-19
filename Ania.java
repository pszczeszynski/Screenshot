import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Rectangle;

import javax.imageio.ImageIO;


class Ania {
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket("localhost", 13085);
        OutputStream outputStream = socket.getOutputStream();

        BufferedImage image = GetScreenShot();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);

        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        outputStream.write(size);
        outputStream.write(byteArrayOutputStream.toByteArray());
        outputStream.flush();
        System.out.println("Flushed: " + System.currentTimeMillis());

        Thread.sleep(1200);
        System.out.println("Closing: " + System.currentTimeMillis());
        socket.close();
    }

    private static BufferedImage GetScreenShot() throws Exception {
        Robot robot = new Robot();            
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(screenRect);
    }
}