public class BinarySearchTree {
    private Node root;

    public BinarySearchTree() {
        this.root = null;
    }

    public Node getRoot() {
        return root;
    }

    public void insert(Player j) {
        root = insert(root, j);
    }

    private Node insert(Node current, Player j) {
        if (current == null) {
            return new Node(j);
        }
        if (j.getRanking() < current.getPlayer().getRanking()) {
            current.setLeft(insert(current.getLeft(), j));
        } else if (j.getRanking() > current.getPlayer().getRanking()) {
            current.setRight(insert(current.getRight(), j));
        }
        return current;
    }

    public boolean search(String name) {
        return searchNode(root, name) != null;
    }

    public Player getPlayer(String name) {
        Node n = searchNode(root, name);
        if (n != null) {
            return n.getPlayer();
        }
        return null;
    }

    private Node searchNode(Node current, String name) {
        if (current == null) {
            return null;
        }
        if (current.getPlayer().getNickname().equals(name)) {
            return current;
        }
        Node leftSearch = searchNode(current.getLeft(), name);
        if (leftSearch != null) {
            return leftSearch;
        }
        return searchNode(current.getRight(), name);
    }

    public Player remove(String name) {
        Node toRemove = searchNode(root, name);
        if (toRemove == null) {
            return null;
        }
        Player j = toRemove.getPlayer();
        root = remove(root, j.getRanking());
        return j;
    }

    private Node remove(Node current, int ranking) {
        if (current == null) {
            return null;
        }
        if (ranking < current.getPlayer().getRanking()) {
            current.setLeft(remove(current.getLeft(), ranking));
        } else if (ranking > current.getPlayer().getRanking()) {
            current.setRight(remove(current.getRight(), ranking));
        } else {
            if (current.getLeft() == null && current.getRight() == null) {
                return null;
            }
            if (current.getRight() == null) {
                return current.getLeft();
            }
            if (current.getLeft() == null) {
                return current.getRight();
            }
            int smallestValue = findSmallestValue(current.getRight());
            Node smallestNode = searchByRanking(current.getRight(), smallestValue);
            current.setPlayer(smallestNode.getPlayer());
            current.setRight(remove(current.getRight(), smallestValue));
        }
        return current;
    }

    private int findSmallestValue(Node root) {
        return root.getLeft() == null ? root.getPlayer().getRanking() : findSmallestValue(root.getLeft());
    }

    private Node searchByRanking(Node current, int ranking) {
        if (current == null) {
            return null;
        }
        if (ranking == current.getPlayer().getRanking()) {
            return current;
        }
        return ranking < current.getPlayer().getRanking() ? searchByRanking(current.getLeft(), ranking) : searchByRanking(current.getRight(), ranking);
    }

    public int getHeight() {
        return getHeight(root);
    }

    private int getHeight(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(getHeight(node.getLeft()), getHeight(node.getRight()));
    }
}