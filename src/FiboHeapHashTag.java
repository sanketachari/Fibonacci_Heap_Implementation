/*
*  This class maintains advanced data structure Max fibonacci heap to store hashtags &
*  its counts. Two methods insert & removeMax can be accessed, to store an element and
*  delete max element from the fibonacci heap.
*
*  Following operations can be performed:
*  1. Remove Max
*  2. Insert
*  3. Cut
*  4. Cascading Cut
*  5. Increase Key
*  6. Pairwise Combine
*
*  Author: Sanket Achari, UFID - 71096329, sanketachari@ufl.edu
*  Date:  13 November 2016
*/
import java.util.HashMap;
import java.util.NoSuchElementException;

public class FiboHeapHashTag {

    // A node which has hashtag of maximum count
    private Node  max = null;

    // Used for checking presence of hashtag.
    private HashMap<String, Node> hashtagMap = new HashMap<>();


    /*   Maintains list of roots of fibonacci heap
     *   Input Argument : Node which has to be added to the root list of
     *                    fibonacci heap
     *   Returns: nothing
     */
    private void addToRootList(Node x){

        // Check if heap is empty
        if (max == null){

            max = x;
            max.right = max;
            max.left = max;
        }

        // If heap is not empty then insert new node to the left of max
        else {

            max.left.right = x;
            x.left = max.left;
            x.right = max;
            max.left = x;

            if (max.data < x.data)
                max = x;
        }
    }

    /*   Method which performs increase key operation of fibonacci heap
     *   If child's count element is greater than parent's count element
     *   then cut that chile and insert into root list.
     *   Input Argument : Node whose count has to be increased,
     *                    increase by number
     *   Returns: nothing
     */
    private void increaseKey(Node x, int k){

        // Check validity of number k by which node's count has to be increased.
        try{
            if (k < 0)
                throw new Exception("Invalid increase Key");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        x.data += k;
        Node y = x.parent;

        if (y != null && x.data > y.data) {

            // Cut the child and perform cascading cut
            cut(x, y);
            cascadingCut(y);
        }

        if (max.data < x.data)
            max = x;
    }

    /*   Method which performs cut operation of fibonacci heap
     *   Child is cut from its parent and added to the root list of
     *   fibonacci heap.
     *   Input Argument : Node which has to be cut from its parent,
     *                    parent of that node
     *   Returns: nothing
     */
    private void cut(Node current, Node parent){

        current.parent = null;
        current.isChildCut = false;

        // Check if parent has pointer to its child. If yes, then
        // update this pointer to the next child if any, else null
        if (parent.child == current){

            if (current != current.right)
                parent.child = current.right;
            else
                parent.child = null;
        }

        // Maintain the neighbors after the child is cut.
        if (current != current.right){
            current.right.left = current.left;
            current.left.right = current.right;
        }

        current.right = current.left = null;

        // Decrease the degree of parent and add child to the root list
        parent.degree -= 1;
        addToRootList(current);

    }

    /*   Method which performs cascading cut operation of fibonacci heap
     *   Input Argument : Node parent whose child has been cut
     *   Returns: nothing
     */
    private void cascadingCut(Node current){

        Node parent = current.parent;
        if (parent != null){

            // Check if child has been cut previously
            if (current.isChildCut == false)
                current.isChildCut = true;

            // If yes, then cut this node from its parent and check whether
            // parent's any child has been cut previously.
            else{
                cut(current, parent);
                cascadingCut(parent);
            }
        }

    }

    /*   Method which combines the two node
     *   Input Argument : two nodes
     *   Returns: resulting node after the combination
     */
    private Node combine(Node n1, Node n2){

        // Check small and large node, small node has to be
        // added to the child of large node
        Node small = n1, large = n2;

        if (small.data > large.data){
            small= n2;
            large= n1;
        }

        // Make small node child of the large node and increase
        // the degree of large node
        small.parent = large;
        large.degree += 1;

        // If large node doesn't have child no need to add neighbors
        // the child
        if (large.child == null) {
            large.child = small;
            small.left = small.right = small;
        }

        // If large node has any child, update the neighbors of newly
        // added child
        else{
            Node neighbor = large.child;
            neighbor.left.right = small;
            small.left = neighbor.left;

            neighbor.left = small;
            small.right = neighbor;
        }

        return large;
    }

    /*   Method which performs remove Max operation of fibonacci heap
     *   Input Argument : Nothing
     *   Returns: Name of the hashtag and its count in string format
     */
    public String removeMax(){

        // String which will store top hashtag & its count in
        // "hashtag:count" format
        String maxTagDetails;

        if (max == null)
            throw new NoSuchElementException("Heap is empty");

        // Update the hashmap which stores every hashtag
        hashtagMap.remove(max.tag);
        maxTagDetails = max.tag + ":" + max.data;

        // Add children to root list
        Node currentChild = max.child;
        Node nextChild;

        if (currentChild != null){

            while (currentChild.right != max.child ){

                nextChild = currentChild.right;

                currentChild.right = currentChild.left = null;
                currentChild.parent = null;
                addToRootList(currentChild);

                currentChild = nextChild;
            }

            currentChild.right = currentChild.left = null;
            currentChild.parent = null;
            addToRootList(currentChild);
        }

        // Hash Map for comparison of degrees in case of pairwise combine
        HashMap<Integer, Node> degreeCompare = new HashMap<>();

        if (max.right != max){

            Node neighbor = max.right;
            Node nextNeighbor, sameDegreeNode;

            // Perform pairwise combine for same degree nodes of root list
            while (neighbor != max){

                nextNeighbor = neighbor.right;
                neighbor.right = neighbor.left = null;
                while (degreeCompare.containsKey(neighbor.degree)) {

                    sameDegreeNode = degreeCompare.get(neighbor.degree);
                    degreeCompare.remove(neighbor.degree);

                    // Combine nodes
                    neighbor = combine(neighbor, sameDegreeNode);
                }

                degreeCompare.put(neighbor.degree, neighbor);
                neighbor = nextNeighbor;
            }
        }

        // Update the neighbors of new roots
        Node head, previousNeighbor = null;
        max = null;
        int newMaxData = Integer.MIN_VALUE;

        for (int key : degreeCompare.keySet()) {

            head = degreeCompare.get(key);

            if (previousNeighbor == null)
                head.left = head.right = head;
            else {
                head.left = previousNeighbor;
                head.right = previousNeighbor.right;
                previousNeighbor.right.left = head;
                previousNeighbor.right = head;
            }

            if (head.data > newMaxData) {
                newMaxData = head.data;
                max = head;
            }

            previousNeighbor = head;
        }

        return maxTagDetails;
    }

    /*   Method which performs insert operation of fibonacci heap
     *   Input Argument : Hashtag, count of hashtag
     *   Returns: nothing
     */
    public void insert(String tag, int num){

        // Check whether hashtag already present in fibonacci heap
        // If its already present, increase the count of existing
        // hashtag by given number.
        if (hashtagMap.containsKey(tag))
            increaseKey(hashtagMap.get(tag), num);

        // If hashtag is not present in fibonacci heap then create
        // new node of given hashtag and its count, add it to heap
        else{
            Node node = new Node(tag);
            node.data = num;
            addToRootList(node);
            hashtagMap.put(tag, node);
        }
    }
}

/*
*  This class maintains data structure of node which will be used in
*  fibonacci heap. It has following fields
*
*  1. Neighbors
*  2. Parent
*  3. Child
*  4. Degree
*  5. ChildCut
*  6. Hashtag
*  7. Count of hashtag
*/
class Node{
    Node left, right, parent, child;
    String tag;
    boolean isChildCut;
    int degree, data;

    Node(String str){
        left = right = parent = child = null;
        isChildCut = false;
        data = 0;
        tag = str;
        degree = 0;
    }
}
