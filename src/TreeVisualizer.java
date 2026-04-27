import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TreeVisualizer extends JPanel {

    private static final int NIVEL_VERTICAL = 60;
    private static final int ESPACAMENTO_HORIZONTAL = 8;
    private static final int LARGURA_MINIMA_NO = 70;
    private static final int ALTURA_NO = 30;
    private static final int LARGURA_JANELA = 1200;
    private static final int ALTURA_JANELA = 800;

    private BinarySearchTree bst;
    private DrawNode rootDraw;
    private int xCounter;

    private JTextField campoNick;
    private JTextField campoRank;
    private JLabel statusLabel;

    private JFrame frame;

    private class DrawNode {
        private Node originalNode;
        private int x;
        private int y;
        private DrawNode left;
        private DrawNode right;

        public DrawNode(Node originalNode) {
            this.originalNode = originalNode;
        }
    }

    public TreeVisualizer(BinarySearchTree bst) {
        this.bst = bst;
        configurarPainel();
        calcularCoordenadas();
    }

    private void configurarPainel() {
        setBackground(new Color(245, 245, 245));
        setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    private void calcularCoordenadas() {
        if (bst == null || bst.getRoot() == null) {
            rootDraw = null;
            setPreferredSize(new Dimension(800, 400));
            return;
        }

        rootDraw = construirArvoreDesenho(bst.getRoot(), 1);

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FontMetrics fm = img.getGraphics().getFontMetrics(getFont());

        xCounter = 50;
        atribuirCoordenadasEmOrdem(rootDraw, fm);

        int alturaTotal = bst.getHeight() * NIVEL_VERTICAL + 100;
        int larguraTotal = xCounter + 100;

        setPreferredSize(new Dimension(larguraTotal, alturaTotal));
    }

    private DrawNode construirArvoreDesenho(Node no, int profundidade) {
        if (no == null) {
            return null;
        }

        DrawNode drawNode = new DrawNode(no);
        drawNode.y = profundidade * NIVEL_VERTICAL;

        drawNode.left = construirArvoreDesenho(no.getLeft(), profundidade + 1);
        drawNode.right = construirArvoreDesenho(no.getRight(), profundidade + 1);

        return drawNode;
    }

    private void atribuirCoordenadasEmOrdem(DrawNode drawNode, FontMetrics fm) {
        if (drawNode == null) {
            return;
        }

        atribuirCoordenadasEmOrdem(drawNode.left, fm);

        String texto = drawNode.originalNode.getPlayer().getNickname();
        int larguraTexto = fm.stringWidth(texto);
        int larguraNo = Math.max(LARGURA_MINIMA_NO, larguraTexto + 30);

        xCounter += larguraNo / 2;
        drawNode.x = xCounter;
        xCounter += larguraNo / 2 + ESPACAMENTO_HORIZONTAL;

        atribuirCoordenadasEmOrdem(drawNode.right, fm);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (rootDraw != null) {
            desenharLinhas(g, rootDraw);
            desenharNos(g, rootDraw);
        }
    }

    private void desenharLinhas(Graphics g, DrawNode drawNode) {
        if (drawNode == null) {
            return;
        }

        g.setColor(Color.DARK_GRAY);

        if (drawNode.left != null) {
            g.drawLine(drawNode.x, drawNode.y + 15, drawNode.left.x, drawNode.left.y - 15);
            desenharLinhas(g, drawNode.left);
        }

        if (drawNode.right != null) {
            g.drawLine(drawNode.x, drawNode.y + 15, drawNode.right.x, drawNode.right.y - 15);
            desenharLinhas(g, drawNode.right);
        }
    }

    private void desenharNos(Graphics g, DrawNode drawNode) {
        if (drawNode == null) {
            return;
        }

        desenharNos(g, drawNode.left);
        desenharNos(g, drawNode.right);

        String texto = drawNode.originalNode.getPlayer().getNickname();
        FontMetrics fm = g.getFontMetrics();

        int larguraTexto = fm.stringWidth(texto);
        int alturaTexto = fm.getAscent();
        int larguraNo = Math.max(LARGURA_MINIMA_NO, larguraTexto + 30);

        g.setColor(Color.WHITE);
        g.fillOval(drawNode.x - larguraNo / 2, drawNode.y - ALTURA_NO / 2, larguraNo, ALTURA_NO);

        g.setColor(Color.BLACK);
        g.drawOval(drawNode.x - larguraNo / 2, drawNode.y - ALTURA_NO / 2, larguraNo, ALTURA_NO);

        g.drawString(texto, drawNode.x - larguraTexto / 2, drawNode.y + alturaTexto / 4);
    }

    private void atualizarArvore() {
        calcularCoordenadas();
        revalidate();
        repaint();
    }

    private JPanel criarPainelControles() {
        JPanel painel = new JPanel();

        painel.add(new JLabel("Nickname:"));
        campoNick = new JTextField(10);
        painel.add(campoNick);

        painel.add(new JLabel("Ranking:"));
        campoRank = new JTextField(5);
        painel.add(campoRank);

        JButton btnInserir = new JButton("Inserir");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnRemover = new JButton("Remover");


        painel.add(btnInserir);
        painel.add(btnBuscar);
        painel.add(btnRemover);


        btnInserir.addActionListener(e -> inserirJogador());
        btnBuscar.addActionListener(e -> buscarJogador());
        btnRemover.addActionListener(e -> removerJogador());


        return painel;
    }

    private void inserirJogador() {
        try {
            String nick = campoNick.getText().trim();
            int rank = Integer.parseInt(campoRank.getText().trim());

            if (nick.isEmpty()) {
                statusLabel.setText("Digite um nickname.");
                return;
            }

            bst.insert(new Player(nick, rank));
            statusLabel.setText("Jogador inserido com sucesso.");
            atualizarArvore();

        } catch (NumberFormatException e) {
            statusLabel.setText("Ranking invalido.");
        }
    }

    private void buscarJogador() {
        String nick = campoNick.getText().trim();

        Player jogador = bst.getPlayer(nick);

        if (jogador != null) {
            statusLabel.setText("Encontrado: " + jogador.getNickname() + " | Ranking: " + jogador.getRanking());
        } else {
            statusLabel.setText("Jogador nao encontrado.");
        }
    }

    private void removerJogador() {
        String nick = campoNick.getText().trim();

        if (bst.remove(nick) != null) {
            statusLabel.setText("Jogador removido com sucesso.");
            atualizarArvore();
        } else {
            statusLabel.setText("Jogador nao encontrado para remocao.");
        }
    }

    public void exibir() {
        frame = new JFrame("Ranking");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(this);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        frame.add(criarPainelControles(), BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);

        statusLabel = new JLabel("Sistema iniciado.");
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.setSize(LARGURA_JANELA, ALTURA_JANELA);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}