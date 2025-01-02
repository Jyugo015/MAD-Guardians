package com.example.madguardians.ui.staff;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.madguardians.database.*;
import java.util.ArrayList;
import java.util.List;

public class Tab1ReportedPostFragment extends BaseTab1Fragment<Helpdesk> implements RecycleViewReportedPostAdapter.OnReportedPostActionListener {
    private FirestoreManager firestoreManager;
    private HelpdeskDao helpdeskDao;
    private PostDao postDao;
    private VerPostDao verPostDao;
    private CourseDao courseDao;
    private QuizOldDao quizOldDao;
    private UserDao userDao;
    private IssueDao issueDao;

    public Tab1ReportedPostFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        firestoreManager = new FirestoreManager(database);
        helpdeskDao = database.helpdeskDao();
        postDao = database.postDao();
        verPostDao = database.verPostDao();
        courseDao = database.courseDao();
        quizOldDao = database.quizOldDao();
        userDao = database.userDao();
        issueDao = database.issueDao();
    }

    @Override
    protected List<Helpdesk> getData() {
        List<Helpdesk> helpdeskReportedList = new ArrayList<>();
        try {
            List<Helpdesk> helpdeskList = helpdeskDao.getAll();
            for (Helpdesk helpdesk : helpdeskList) {
                if (helpdesk.getPostId() != null || helpdesk.getCourseId() != null || helpdesk.getQuizId() != null) {
                    helpdeskReportedList.add(helpdesk);
                }
            }

            if (helpdeskReportedList.isEmpty()) {
                showToast("No reported comment available.");
            }
        } catch (Exception e) {
            handleError("An error occurred while fetching data", e);
        }
        return helpdeskReportedList;
    }

    @Override
    protected RecyclerView.Adapter<?> getAdapter(List<Helpdesk> data) {
        return new RecycleViewReportedPostAdapter(data, requireContext(), this);
    }

    @Override
    public void onKeepClicked(Helpdesk helpdesk, int position) {
        try {
            String messageToOwner;
            String messageToReporter;
            NotificationManager notificationManager = new NotificationManager(requireContext());

            if (helpdesk.getPostId() != null) {
                Post post = postDao.getById(helpdesk.getPostId()).getValue();
                messageToOwner = "Your post \"" + post.getTitle() + "\" has been reviewed and kept.";
                notificationManager.sendNotification(post.getUserId(), messageToOwner);

                messageToReporter = "The post you reported has been reviewed and kept: \"" + post.getTitle() + "\".";
                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);

                showToast("Kept post: " + post.getTitle());
            } else if (helpdesk.getCourseId() != null) {
                Course course = courseDao.getById(helpdesk.getCourseId());
                messageToOwner = "Your course \"" + course.getTitle() + "\" has been reviewed and kept.";
                notificationManager.sendNotification(course.getPost1(), messageToOwner);

                messageToReporter = "The course you reported has been reviewed and kept: \"" + course.getTitle() + "\".";
                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);

                showToast("Kept course: " + course.getTitle());
            } else if (helpdesk.getQuizId() != null) {
                QuizOld quizOld = quizOldDao.getByQuizPostId(helpdesk.getQuizId(), helpdesk.getPostId());
                messageToOwner = "Your quiz has been reviewed and kept.";
                notificationManager.sendNotification(quizOld.getPostId(), messageToOwner);

                messageToReporter = "The quiz you reported has been reviewed and kept.";
                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);

                showToast("Kept quiz.");
            } else {
                showToast("No valid data found to keep.");
                return;
            }

            updateHelpdeskStatus(helpdesk, "reviewed");
        } catch (Exception e) {
            handleError("Failed to keep", e);
        }
    }

    @Override
    public void onDeleteClicked(Helpdesk helpdesk, int position) {
        try {
            String messageToOwner;
            String messageToReporter;
            NotificationManager notificationManager = new NotificationManager(requireContext());

            if (helpdesk.getPostId() != null) {
                Post post = postDao.getById(helpdesk.getPostId()).getValue();
                VerPost verPost = verPostDao.getByPostId(helpdesk.getPostId());

                firestoreManager.onDelete("post", post);
                firestoreManager.onDelete("verPost", verPost);

                User reporter = userDao.getById(helpdesk.getUserId());
                Issue issue = issueDao.getById(helpdesk.getIssueId());
                messageToOwner = "User " + reporter.getName() + " reported you for " + issue.getType() + ". Your post \"" + post.getTitle() + "\" has been deleted.";
                notificationManager.sendNotification(post.getUserId(), messageToOwner);

                messageToReporter = "You successfully reported " + userDao.getById(post.getUserId()).getName() + "'s post.";
                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);

                showToast("Deleted post: " + post.getTitle());
            } else if (helpdesk.getCourseId() != null) {
                Course course = courseDao.getById(helpdesk.getCourseId());
                firestoreManager.onDelete("course", course);

                messageToOwner = "Your course \"" + course.getTitle() + "\" has been deleted.";
                notificationManager.sendNotification(course.getPost1(), messageToOwner);

                messageToReporter = "The course you reported has been deleted.";
                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);

                showToast("Deleted course: " + course.getTitle());
            } else if (helpdesk.getQuizId() != null) {
                QuizOld quizOld = quizOldDao.getByQuizPostId(helpdesk.getQuizId(), helpdesk.getPostId());
                firestoreManager.onDelete("quizOld", quizOld);

                messageToOwner = "Your quiz has been deleted.";
                notificationManager.sendNotification(quizOld.getPostId(), messageToOwner);

                messageToReporter = "The quiz you reported has been deleted.";
                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);

                showToast("Deleted quiz.");
            } else {
                showToast("No valid data found to delete.");
                return;
            }

            updateHelpdeskStatus(helpdesk, "reviewed");
        } catch (Exception e) {
            handleError("Failed to delete", e);
        }
    }

    private void updateHelpdeskStatus(Helpdesk helpdesk, String status) {
        helpdesk.setStaffId("");
        helpdesk.setHelpdeskStatus(status);
        firestoreManager.onInsertUpdate("update","helpdesk", helpdesk, requireContext());
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void handleError(String message, Exception e) {
        Toast.makeText(requireContext(), message + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
}