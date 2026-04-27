import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TreeVisualizer extends JPanel {
    private BinarySearchTree bst;
    private DrawNode rootDraw;
    private int xCounter;

    private class DrawNode {
        Node originalNode;
        int x, y;
        DrawNode left, right;

        DrawNode(Node originalNode) {
            this.originalNode = originalNode;
        }
    }

    public TreeVisualizer(BinarySearchTree bst) {
        this.bst = bst;
        setBackground(new Color(245, 245, 245));
        setFont(new Font("SansSerif", Font.BOLD, 12));
        calcularCoordenadas();
    }

    private void calcularCoordenadas() {
        if (bst == null || bst.getRoot() == null) return;

        rootDraw = construirArvoreDesenho(bst.getRoot(), 1);

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FontMetrics fm = img.getGraphics().getFontMetrics(getFont());

        xCounter = 50;
        atribuirCoordenadasEmOrdem(rootDraw, fm);

        int alturaTotal = bst.getHeight() * 80 + 100;
        int larguraTotal = xCounter + 100;

        setPreferredSize(new Dimension(larguraTotal, alturaTotal));
    }

    private DrawNode construirArvoreDesenho(Node no, int profundidade) {
        if (no == null) return null;
        DrawNode dn = new DrawNode(no);
        dn.y = profundidade * 80;
        dn.left = construirArvoreDesenho(no.getLeft(), profundidade + 1);
        dn.right = construirArvoreDesenho(no.getRight(), profundidade + 1);
        return dn;
    }

    private void atribuirCoordenadasEmOrdem(DrawNode dn, FontMetrics fm) {
        if (dn == null) return;

        atribuirCoordenadasEmOrdem(dn.left, fm);

        String texto = dn.originalNode.getPlayer().getNickname();
        int larguraTexto = fm.stringWidth(texto);
        int larguraNo = Math.max(70, larguraTexto + 30);

        xCounter += larguraNo / 2;
        dn.x = xCounter;
        xCounter += larguraNo / 2 + 20;

        atribuirCoordenadasEmOrdem(dn.right, fm);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rootDraw != null) {
            desenharLinhas(g, rootDraw);
            desenharNos(g, rootDraw);
        }
    }

    private void desenharLinhas(Graphics g, DrawNode dn) {
        if (dn == null) return;

        g.setColor(Color.DARK_GRAY);
        if (dn.left != null) {
            g.drawLine(dn.x, dn.y + 15, dn.left.x, dn.left.y - 15);
            desenharLinhas(g, dn.left);
        }
        if (dn.right != null) {
            g.drawLine(dn.x, dn.y + 15, dn.right.x, dn.right.y - 15);
            desenharLinhas(g, dn.right);
        }
    }

    private void desenharNos(Graphics g, DrawNode dn) {
        if (dn == null) return;

        desenharNos(g, dn.left);
        desenharNos(g, dn.right);

        String texto = dn.originalNode.getPlayer().getNickname();
        FontMetrics fm = g.getFontMetrics();
        int larguraTexto = fm.stringWidth(texto);
        int alturaTexto = fm.getAscent();
        int larguraNo = Math.max(70, larguraTexto + 30);
        int alturaNo = 30;

        g.setColor(Color.WHITE);
        g.fillOval(dn.x - larguraNo / 2, dn.y - alturaNo / 2, larguraNo, alturaNo);

        g.setColor(Color.BLACK);
        g.drawOval(larguraNo / 2, dn.y - alturaNo / 2, larguraNo, alturaNo);

        g.drawString(texto, dn.x - larguraTexto / 2, dn.y + alturaTexto / 4);
    }

    public static void exibir() {
        JFrame frame = new JFrame("Visualizador de Ranking ABB");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        TreeVisualizer painel = new TreeVisualizer(Main.bstCompartilhada);
        JScrollPane scroll = new JScrollPane(painel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        frame.add(scroll);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}