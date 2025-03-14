import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    //List<Node> taskHistory = new ArrayList<>();
    Map<Integer, Node> mapOfNodes = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
/*      if (taskHistory.size() == 10) {
            taskHistory.remove(0);
        }
        taskHistory.add(task.copy());*/
        Task taskCopy = task.copy();
        Integer taskId = taskCopy.getId();
        removeFromHistory(taskId);
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
    public void removeFromHistory(int id) {
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
        //return taskHistory;
        ArrayList<Task> listOfTasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            listOfTasks.add(node.task);
            node = node.next;
        }

        return listOfTasks;
    }

/*    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;

         taskHistory.put(task.getId(), newNode);
    }*/

    //public ArrayList<Task> getTasks() {
    //    ArrayList<Task> listOfTasks = new ArrayList<>();
    //    for (Node node : taskHistory.values()) {
    //        listOfTasks.add(node.task);
    //    }
    //}

}
