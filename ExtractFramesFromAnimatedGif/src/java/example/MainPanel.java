package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.*;

class MainPanel extends JPanel {
    private MainPanel() {
        super(new BorderLayout());
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(getClass().getResource("duke.running.gif")));
        label.setBorder(BorderFactory.createTitledBorder("duke.running.gif"));

        Box p = Box.createHorizontalBox();
        p.setBorder(BorderFactory.createTitledBorder("Extract frames from Animated GIF"));

        try(InputStream source = getClass().getResourceAsStream("duke.running.gif");
            ImageInputStream iis = ImageIO.createImageInputStream(source)) {
            for(BufferedImage image: loadFromStream(iis)) {
                p.add(new JLabel(new ImageIcon(image)));
            }
            //iis.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        add(label, BorderLayout.WEST);
        add(new JScrollPane(p));
        setPreferredSize(new Dimension(320, 240));
    }
    //OTN Discussion Forums : Reading gif animation frame rates and such?
    //https://forums.oracle.com/forums/thread.jspa?messageID=5386516
    private ArrayList<BufferedImage> loadFromStream(ImageInputStream imageStream) throws IOException{
        ImageReader reader = null;
        Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);
        while(readers.hasNext()) {
            reader = readers.next();
            String metaFormat = reader.getOriginatingProvider().getNativeImageMetadataFormatName();
            if("gif".equalsIgnoreCase(reader.getFormatName()) && !"javax_imageio_gif_image_1.0".equals(metaFormat)) {
                continue;
            }else{
                break;
            }
        }
        if(reader == null) {
            throw new IOException("Can not read image format!");
        }
        boolean isGif = reader.getFormatName().equalsIgnoreCase("gif");
        reader.setInput(imageStream, false, !isGif);
        ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
        for(int i=0;i<reader.getNumImages(true);i++) {
            IIOImage frame = reader.readAll(i, null);
            list.add((BufferedImage)frame.getRenderedImage());
        }
        reader.dispose();
        return list;
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}