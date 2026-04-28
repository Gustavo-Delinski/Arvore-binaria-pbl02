public class Main {

    public static void main(String[] args) {
        BinarySearchTree rankingTree = new BinarySearchTree();
        carregarJogadores(rankingTree, "Arvore-binaria-pbl02/players.csv");

        TreeVisualizer visualizador = new TreeVisualizer(rankingTree);
        visualizador.exibir();
    }

    private static void carregarJogadores(BinarySearchTree bst, String arquivo) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(arquivo))) {
            String linha = br.readLine();

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");

                if (partes.length == 2) {
                    String nickname = partes[0].trim();

                    try {
                        int ranking = Integer.parseInt(partes[1].trim());
                        bst.insert(new Player(nickname, ranking));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Nao foi possivel carregar o ficheiro CSV.");
        }
    }
}