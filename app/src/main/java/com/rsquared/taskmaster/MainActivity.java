package com.rsquared.taskmaster;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// Todo: possibly move from 0-100 selection to 0-10
// Todo: Permanent notification for most important and urgent item (maybe next release)

// Main activity that initializes all items, including database helpers, and sets listeners to
// trigger functions
public class MainActivity extends AppCompatActivity {

  // To track which fragments are visible
  private boolean fragmentAddOrEdit = false;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // taskViewModel holds task information between views, activities, etc.
    TaskViewModel taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

    // Get all the unfinished tasks for display
    taskViewModel.downloadIncompleteTasks();
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Begin program with edit screen
    showHome();

    // Add a custom back button listener with simple switch indicating which screen starting from
    findViewById(R.id.linear_layout_activity_main).getRootView().setFocusableInTouchMode(true);
    findViewById(R.id.linear_layout_activity_main).getRootView().requestFocus();
    findViewById(R.id.linear_layout_activity_main)
        .getRootView()
        .setOnKeyListener(
            (View v, int keyCode, KeyEvent event) -> {
              if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (fragmentAddOrEdit) {
                  showHome();
                  return true;
                }
                return false;
              }
              return false;
            });
  }

  // FRAGMENT CONTROL METHODS

  // Show the "home" screen: task graphics, instructions, and button for adding/editing tasks
  public void showHome() {

    // Prepare transaction by clearing activity of frames and creating new ones
    FragmentTransaction fragmentTransaction = prepareTransaction();

    // Create new fragment instances
    FragmentTaskDraw fragmentTaskDraw = FragmentTaskDraw.newInstance();
    FragmentAddButton fragmentAddButton = FragmentAddButton.newInstance();
    FragmentInstructions fragmentInstructions = FragmentInstructions.newInstance();
    FragmentCalvinQuote fragmentCalvinQuote = FragmentCalvinQuote.newInstance();

    // Keep track of which fragments are showing
    fragmentAddOrEdit = false;

    finalizeTransaction(
        fragmentTransaction, fragmentTaskDraw, fragmentAddButton, fragmentInstructions, fragmentCalvinQuote);
  }

  // Bring up a screen for a new task
  public void addTask() {
    FragmentTransaction fragmentTransaction = prepareTransaction();
    FragmentAddOrModifyTask fragmentAddOrModifyTask = newFragmentAddTask();
    FragmentSmartGoals fragmentSmartGoals = FragmentSmartGoals.newInstance();
    fragmentAddOrEdit = true;
    finalizeTransaction(fragmentTransaction, fragmentAddOrModifyTask, fragmentSmartGoals);
  }

  // Bring up a screen for a new task with importance and urgency already established
  public void addTask(int urgency, int importance) {
    FragmentTransaction fragmentTransaction = prepareTransaction();
    FragmentAddOrModifyTask fragmentAddOrModifyTask = newFragmentAddTask(urgency, importance);
    FragmentSmartGoals fragmentSmartGoals = FragmentSmartGoals.newInstance();
    fragmentAddOrEdit = true;
    finalizeTransaction(fragmentTransaction, fragmentAddOrModifyTask, fragmentSmartGoals);
  }

  // Bring up a screen for editing an existing task
  public void editTask(Task task) {
    FragmentTransaction fragmentTransaction = prepareTransaction();
    FragmentAddOrModifyTask fragmentAddOrModifyTask = newFragmentModifyTask(task);
    FragmentSmartGoals fragmentSmartGoals = FragmentSmartGoals.newInstance();
    fragmentAddOrEdit = true;
    finalizeTransaction(fragmentTransaction, fragmentAddOrModifyTask, fragmentSmartGoals);
  }

  // PIECEMEAL PRIVATE METHODS FOR CHANGING FRAGMENTS (USED BY ABOVE METHODS)

  // Create fragment for adding a new task (urgency and importance not yet set)
  private @NotNull FragmentAddOrModifyTask newFragmentAddTask() {
    Bundle bundle = new Bundle();
    bundle.putByte("isNewTask", (byte) 1);
    bundle.putByte("hasRatings", (byte) 0);
    FragmentAddOrModifyTask fragmentAddOrModifyTask = FragmentAddOrModifyTask.newInstance();
    fragmentAddOrModifyTask.setArguments(bundle);
    return fragmentAddOrModifyTask;
  }

  // Create fragment for adding a new task with pre-determined importance and urgency
  private @NotNull FragmentAddOrModifyTask newFragmentAddTask(int urgency, int importance) {
    Bundle bundle = new Bundle();
    bundle.putByte("isNewTask", (byte) 1);
    bundle.putByte("hasRatings", (byte) 1);
    bundle.putInt("urgency", urgency);
    bundle.putInt("importance", importance);
    FragmentAddOrModifyTask fragmentAddOrModifyTask = FragmentAddOrModifyTask.newInstance();
    fragmentAddOrModifyTask.setArguments(bundle);
    return fragmentAddOrModifyTask;
  }

  // Create fragment for editing an existing task
  private @NotNull FragmentAddOrModifyTask newFragmentModifyTask(Task task) {
    Bundle bundle = new Bundle();
    bundle.putByte("isNewTask", (byte) 0);
    bundle.putParcelable("editTask", task);
    FragmentAddOrModifyTask fragmentAddOrModifyTask = FragmentAddOrModifyTask.newInstance();
    fragmentAddOrModifyTask.setArguments(bundle);
    return fragmentAddOrModifyTask;
  }

  // Create transaction for the above methods
  private @NotNull FragmentTransaction prepareTransaction() {
    ((ViewGroup) findViewById(R.id.linear_layout_placeholder)).removeAllViews();
    FragmentManager fragmentManager = Objects.requireNonNull(getSupportFragmentManager());
    return fragmentManager.beginTransaction();
  }

  // Finish transaction for the above methods
  private void finalizeTransaction(
      @NotNull FragmentTransaction fragmentTransaction, Fragment @NotNull ... fragments) {
    for (Fragment fragment : fragments) {
      fragmentTransaction.add(R.id.linear_layout_placeholder, fragment);
    }
    fragmentTransaction.commit();
  }

  // Swap rotation between landscape and portrait, or none
  // Taken from: https://stackoverflow.com/a/18268446
  @SuppressLint({"SourceLockedOrientationActivity", "SwitchIntDef"})
  public void rotate() {
    switch (getResources().getConfiguration().orientation) {
      case android.content.res.Configuration.ORIENTATION_PORTRAIT:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        } else {
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        break;
      case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        } else {
          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        break;
      case android.content.res.Configuration.ORIENTATION_UNDEFINED:
      default:
        break;
    }
  }
}
