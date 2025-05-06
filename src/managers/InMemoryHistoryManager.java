package managers;

import taskstructure.Task;
import taskstructure.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node> mapOfNodes = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {

        Task taskCopy = task.copy();
        Integer taskId = taskCopy.getId();
        remove(taskId);
        Node oldTail = tail;
        Node newNode = new Node(oldTail, taskCopy, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        mapOfNodes.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = mapOfNodes.get(id);
        if (node == null) {
            return;
        }
        Node nodePrev = node.prev;
        Node nodeNext = node.next;
        if (nodeNext != null && nodePrev != null) {
            nodeNext.prev = nodePrev;
            nodePrev.next = nodeNext;
        } else if (nodeNext == null && nodePrev != null) {
            nodePrev.next = null;
            tail = nodePrev;
        } else if (nodePrev == null && nodeNext != null) {
            nodeNext.prev = null;
            head = nodeNext;
        } else {
            tail = null;
            head = null;
        }
        mapOfNodes.remove(id);
    }

    @Override
    public List<Task> getHistory() {

        ArrayList<Task> listOfTasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            listOfTasks.add(node.task);
            node = node.next;
        }

        return listOfTasks;
    }


}
