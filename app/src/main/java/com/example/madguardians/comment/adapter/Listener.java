package com.example.madguardians.comment.adapter;

import com.example.madguardians.database.Comments;

public class Listener {

    public interface CommentListener {
        void onCommentTextProvided(String text);
    }

    public interface OnDialogDismissListener {
        void onDialogDismiss();
    }

    public interface OnItemPressedListener {
        void onItemPressed(Comments repliedComment);
    }

    public interface OnViewMorePressedListener{
        void onViewMorePressed();
    }

    public interface OnAdapterItemUpdateListener{
        void onAdapterItemUpdate(boolean showViewMore);
    }

    public interface OnReportListener{
        void onReport(Comments comment);
    }

    public interface OnIssueListener{
        void issueClicked();
    }

    public interface onHelpdeskListener{
        void helpdeskAdded();
    }

}
