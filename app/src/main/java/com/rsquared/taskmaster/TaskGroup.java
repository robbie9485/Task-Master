package com.rsquared.taskmaster;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

// This class for task groups, and is much like the task class
public class TaskGroup {

  // PRIVATE MEMBERS

  private final ArrayList<Task> tasks = new ArrayList<>();
  private String label;
  private int averageImportance;
  private int averageUrgency;
  private TaskGraphic taskGraphic;

  // CONSTRUCTORS

  // Initialize with an undetermined mixture of tasks and task groups
  public TaskGroup(@NotNull Task... tasks) {
    for (Task task : tasks) {
      addTask(task);
    }
  }

  // SETTER METHODS

  // Create a list of groups
  public void addTask(@NotNull Task newTask) {
    boolean addTask = true;
    for (Task task : tasks) {
      if (newTask == task) {
        addTask = false;
        break;
      }
    }
    if (addTask) {
      tasks.add(newTask);
      combine();
    }
  }

  // Get average urgency and importance from individual tasks, and sort by importance
  private void combine() {
    averageImportance = 0;
    averageUrgency = 0;
    for (Task task : tasks) {
      averageImportance += task.getImportance();
      averageUrgency += task.getUrgency();
    }
    averageImportance = averageImportance / tasks.size();
    averageUrgency = averageUrgency / tasks.size();

    Collections.sort(
        tasks,
        (Task t1, Task t2) -> {
          return t2.getImportance() - t1.getImportance(); // Descending
        });

    label = tasks.size() + " tasks";
  }

  public void setTaskGraphic(TaskGraphic newTaskGraphic) {
    taskGraphic = newTaskGraphic;
  }

  // GETTER METHODS

  public ArrayList<Task> getTasks() {
    return tasks;
  }

  public String getLabel() {
    return label;
  }

  public int getImportance() {
    return averageImportance;
  }

  public int getUrgency() {
    return averageUrgency;
  }

  public TaskGraphic getTaskGraphic() {
    return taskGraphic;
  }

  public boolean isTaskInGroup(Task task) {
    for (Task checkTask : tasks) {
      if (task == checkTask) {
        return true;
      }
    }
    return false;
  }
}
