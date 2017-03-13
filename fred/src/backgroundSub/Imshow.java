package backgroundSub;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by jinhong on 13/03/2017.
 */
public class Imshow {

        public JFrame Window;
        private ImageIcon image;
        private JLabel label;
        private MatOfByte matOfByte;
        private Boolean SizeCustom;
        private int Height;
        private int Width;

        public Imshow(String title) {
            this.Window = new JFrame();
            this.image = new ImageIcon();
            this.label = new JLabel();
            this.matOfByte = new MatOfByte();
            this.label.setIcon(this.image);
            this.Window.getContentPane().add(this.label);
            this.Window.setResizable(false);
            this.Window.setTitle(title);
            this.SizeCustom = Boolean.valueOf(false);
            this.setCloseOption(0);
        }

        public Imshow(String title, int height, int width) {
            this.SizeCustom = Boolean.valueOf(true);
            this.Height = height;
            this.Width = width;
            this.Window = new JFrame();
            this.image = new ImageIcon();
            this.label = new JLabel();
            this.matOfByte = new MatOfByte();
            this.label.setIcon(this.image);
            this.Window.getContentPane().add(this.label);
            this.Window.setResizable(false);
            this.Window.setTitle(title);
            this.setCloseOption(0);
        }

        public void showImage(Mat img) {
            if(this.SizeCustom.booleanValue()) {
                Imgproc.resize(img, img, new Size((double)this.Height, (double)this.Width));
            }

            Imgcodecs.imencode(".jpg", img, this.matOfByte);
            BufferedImage bufImage = null;

            try {
                bufImage = this.toBufferedImage(img);
                this.image.setImage(bufImage);
                this.Window.pack();
                this.label.updateUI();
                this.Window.setVisible(true);
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        }

        public BufferedImage toBufferedImage(Mat m) {
            byte type = 10;
            if(m.channels() > 1) {
                type = 5;
            }

            int bufferSize = m.channels() * m.cols() * m.rows();
            byte[] b = new byte[bufferSize];
            m.get(0, 0, b);
            BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
            byte[] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
            System.arraycopy(b, 0, targetPixels, 0, b.length);
            return image;
        }

        public void setCloseOption(int option) {
            switch (option) {
                case 0:
                    this.Window.setDefaultCloseOperation(3);
                    break;
                case 1:
                    this.Window.setDefaultCloseOperation(1);
                    break;
                default:
                    this.Window.setDefaultCloseOperation(3);
            }

        }
}
