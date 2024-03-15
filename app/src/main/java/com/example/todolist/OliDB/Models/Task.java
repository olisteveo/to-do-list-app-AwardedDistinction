package com.example.todolist.OliDB.Models;

/**
 * Represents a task in the to-do list.
 */
public class Task {
    private int id; // Task ID
    private String taskName; // Name of the task
    private String taskDescription; // Description of the task
    private int completed; // Flag indicating whether the task is completed or not

    // Constructor
    public Task() {
    }

    /**
     * Create a new task table model object.
     *
     * @param taskName        The name of the task.
     * @param taskDescription The description of the task.
     * @param completed       The completion status of the task.
     * @return                The new modeled task object.
     */
    public static Task newFromBasic(String taskName, String taskDescription, int completed)
    {
        Task task = new Task();
        task.setTaskName(taskName);
        task.setTaskDescription(taskDescription);
        task.setCompleted(completed);
        return task;
    }

    public static Task newFromInserted(int id, String taskName, String taskDescription, int completed)
    {
        Task task = newFromBasic(taskName, taskDescription, completed);
        task.setId(id);
        return task;
    }

    // Getters and setters

    /**
     * Gets the ID of the task.
     *
     * @return The task ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the task.
     *
     * @param id The task ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the task.
     *
     * @return The task name.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets the name of the task.
     *
     * @param taskName The task name to set.
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Gets the description of the task.
     *
     * @return The task description.
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * Sets the description of the task.
     *
     * @param taskDescription The task description to set.
     */
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    /**
     * Gets the completion status of the task.
     *
     * @return The completion status of the task.
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * Sets the completion status of the task.
     *
     * @param completed The completion status of the task to set.
     */
    public void setCompleted(int completed) {
        this.completed = completed;
    }

    /**
     * Returns a string representation of the task (its name).
     *
     * @return The task name.
     */
    @Override
    public String toString() {
        return getTaskName();
    }
}
