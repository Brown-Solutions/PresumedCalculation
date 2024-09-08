package org.brownsolutions.data;

import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

@Getter
public class FileQueue {

    private final Queue<String> queue;

    public FileQueue() {
        this.queue = new LinkedList<String>();
    }

    public void enqueue(String filePath) {
        this.queue.add(filePath);
    }

    public String dequeue() {
        return this.queue.poll();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }
}
