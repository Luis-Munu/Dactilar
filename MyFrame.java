import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
/**
 * Clase usada para abrir la ventana al final de la ejecución mostrando las conversiones que realizamos de la imagen.
 * Es posible que haya varios errores o sea muy ineficiente, ha sido practicamente un experimento.
 */
public class MyFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    /**
     * La base es un JFrame al que añadimos un JPanel.
     * El JPanel es un conjunto de diferentes elementos.
     * Los elementos son JLabels, en estos añadimos imagenes y textos con cierta separacion entre ellos usando bordes.
     */
    public MyFrame(ArrayList<BufferedImage> imagenes, String[] nombres){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(141,254,140));
        Image img;
        for(int i=0;i< imagenes.size();++i){
            JLabel label = new JLabel(nombres[i]);
            img = imagenes.get(i).getScaledInstance(250, 250,Image.SCALE_SMOOTH);
            label.setFont(new Font("", Font.PLAIN, 20));
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.TOP);
            EmptyBorder border = new EmptyBorder(50, 50, 50, 50);
            label.setBorder(border);
            label.setIcon(new ImageIcon(img));
            panel.add(label);
        }
        JFrame frame = new JFrame("Imagenes");
        
        frame.setIconImage(new ImageIcon(imagenes.get(0)).getImage());
        frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}