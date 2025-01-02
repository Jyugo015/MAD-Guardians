package com.example.madguardians.ui.staff;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Comment;
import com.example.madguardians.database.CommentDao;
import com.example.madguardians.database.Course;
import com.example.madguardians.database.CourseDao;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.Helpdesk;
import com.example.madguardians.database.HelpdeskDao;
import com.example.madguardians.database.Post;
import com.example.madguardians.database.PostDao;
import com.example.madguardians.database.QuizOld;
import com.example.madguardians.database.QuizOldDao;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;

import java.util.ArrayList;
import java.util.List;
//delete havent do
public class Tab1ReportedCommentFragment extends BaseTab1Fragment<Helpdesk> implements RecycleViewReportedCommentAdapter.OnReportedCommentActionListener{
    private HelpdeskDao helpdeskDao;
    public Tab1ReportedCommentFragment (){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpdeskDao = AppDatabase.getDatabase(requireContext()).helpdeskDao();
    }
    @Override
    protected List<Helpdesk> getData() {
        List<Helpdesk> helpdeskReportedCommentList = new ArrayList<>();
        try {
            List<Helpdesk> helpdeskList = helpdeskDao.getAll();
            if (helpdeskList == null || helpdeskList.isEmpty()) {
                Toast.makeText(requireContext(), "No data found in the database.", Toast.LENGTH_SHORT).show();
                return new ArrayList<>();
            }

            for (Helpdesk helpdesk : helpdeskList) {
                if (helpdesk.getCommentId() != null) {
                    helpdeskReportedCommentList.add(helpdesk);
                }
            }

            if (helpdeskReportedCommentList.isEmpty()) {
                Toast.makeText(requireContext(), "No reported comment available.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "An error occurred while fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace(); // Optional: Log for debugging purposes
        }
        return helpdeskReportedCommentList;
    }

    @Override
    protected RecyclerView.Adapter<?> getAdapter(List<Helpdesk> data) {
        return new RecycleViewReportedCommentAdapter(data, requireContext(), this);
    }

    @Override
    public void onReportedDescrClicked(Helpdesk helpdesk, int position) {
        // Handle course title click
        Toast.makeText(requireContext(), "Reported comment clicked: " , Toast.LENGTH_SHORT).show();
        // Add any additional actions here
    }
}
